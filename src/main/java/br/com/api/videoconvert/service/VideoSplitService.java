package br.com.api.videoconvert.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.api.videoconvert.model.VideoDocument;
import br.com.api.videoconvert.model.VideoQueue;
import br.com.api.videoconvert.model.enums.Notification;
import br.com.api.videoconvert.model.enums.VideoStatus;
import br.com.api.videoconvert.mongo.repository.VideoMongoRepository;
import br.com.api.videoconvert.sqs.sender.NotificationSender;
import lombok.extern.log4j.Log4j2;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

@Service
@Log4j2
public class VideoSplitService {

	@Autowired
	private S3UploaderService s3Uploader;

	@Autowired
	private VideoMongoRepository videoMongoRepository;

	@Autowired
	private NotificationSender notificationSender;

	@Transactional
	public void splitVideo(VideoQueue videoQueue) {
		try {
		    log.info("Processo iniciado:");
		    
		    atualizarStatus(videoQueue.getId(), VideoStatus.PROCESSING);
		    
		    Path videoPath = downloadVideoFromS3(videoQueue.getUrl());

		    Path outputFolder = Files.createTempDirectory("frames_output_");
		    String outputPattern = outputFolder.resolve("frame_%03d.jpg").toString();
		    double interval = getTempoParticao(videoQueue.getId());

		    FFmpeg ffmpeg = new FFmpeg();
		    
		    FFmpegBuilder builder = new FFmpegBuilder()
		            .setInput(videoPath.toString())
		            .addOutput(outputPattern)
		            .setFormat("image2")
		            .setVideoFilter("fps=1/" + interval + ",scale=640:-1")
		            .done();

		    ffmpeg.run(builder);

		    log.info("------ Extração de frames finalizada.------");

		    createZipImages(outputFolder.toString(), videoQueue);

		    log.info("------ Processo finalizado. ------");
		    atualizarStatus(videoQueue.getId(), VideoStatus.COMPLETED);

		    Files.deleteIfExists(videoPath);

		} catch (Exception e) {
		    log.error("Erro durante o processo: {}", e.getMessage(), e);
		    atualizarStatus(videoQueue.getId(), VideoStatus.FAILED);
		    Notification notificacao = criarNotificacao(videoQueue.getId(), e);
		    notificationSender.send(notificacao);
		}
	}

	private Notification criarNotificacao(String id, Exception e) {
		VideoDocument videoDocument = getVideoDocument(id);
		Notification notification = new  Notification();
		notification.setEmail(videoDocument.getClientId());
		notification.setStatus(VideoStatus.FAILED.toString());
		notification.setMessage(e.getMessage());
		notification.setUserId(videoDocument.getClientId());
		notification.setSubject("Erro no processamento");
		notification.setVideoId(id);
		
		return notification;
	}

	private void createZipImages(String outputFolder, VideoQueue videoQueue) throws IOException {
	    log.info(" ------- Iniciando criação arquivo zip ------ ");

	    Path tempZipDir = Files.createTempDirectory("zip_output_");
	    Path destinationZipFilePath = tempZipDir.resolve("frames.zip");

	    try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(destinationZipFilePath))) {
	        zos.setLevel(Deflater.BEST_COMPRESSION);

	        Files.walk(Paths.get(outputFolder))
	                .filter(Files::isRegularFile)
	                .filter(path -> !path.getFileName().toString().endsWith(".zip"))
	                .forEach(path -> {
	                    ZipEntry zipEntry = new ZipEntry(Paths.get(outputFolder).relativize(path).toString());
	                    try {
	                        zos.putNextEntry(zipEntry);
	                        Files.copy(path, zos);
	                        zos.closeEntry();
	                    } catch (Exception e) {
	                        log.error("Erro ao adicionar arquivo ao zip: {}", e.getMessage(), e);
	                        atualizarStatus(videoQueue.getId(), VideoStatus.FAILED);
	            		    Notification notificacao = criarNotificacao(videoQueue.getId(), e);
	            		    notificationSender.send(notificacao);
	                    }
	                });
	    }

	    log.info("Arquivo ZIP criado em: {}", destinationZipFilePath.toAbsolutePath());

	    uploadZip(outputFolder, videoQueue, destinationZipFilePath.toString());

	    try {
	        Files.deleteIfExists(destinationZipFilePath);
	        Files.deleteIfExists(tempZipDir);
	        log.info("Arquivos temporários removidos.");
	    } catch (IOException e) {
	        log.warn("Não foi possível excluir arquivos temporários: {}", e.getMessage());
	    }
	}

	public void uploadZip(String outputFolder, VideoQueue videoQueue, String destinationZipFilePath) {
		try {
			
			String keyname = "videos/images" + videoQueue.getId()+ LocalDateTime.now() + ".zip";
			
			s3Uploader.uploadFile(keyname, Paths.get(destinationZipFilePath));
			log.info("Upload para o S3 concluído com sucesso.");
			
			StringBuilder sb = new StringBuilder();
			
			String urlZip = sb.append("https://video-storage-zip-bucket-ter1.s3.us-east-1.amazonaws.com/")
					 			.append(keyname).toString();
			
			atualizarUrlZip(videoQueue.getId(), urlZip);

			deleteDirectoryRecursively(Paths.get(outputFolder));
			Files.deleteIfExists(Paths.get(destinationZipFilePath));
			log.info("Arquivos temporários deletados com sucesso.");
		} catch (Exception e) {
			log.error("Erro ao fazer upload para o S3: {}", e.getMessage(), e);
			atualizarStatus(videoQueue.getId(), VideoStatus.FAILED);
		    Notification notificacao = criarNotificacao(videoQueue.getId(), e);
		    notificationSender.send(notificacao);
		}
	}

	private void deleteDirectoryRecursively(Path path) throws IOException {
		if (Files.exists(path)) {
			Files.walk(path)
					.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.forEach(File::delete);
		}
	}

	public void atualizarUrlZip(String id, String url) {
		videoMongoRepository.findById(id).ifPresent(video -> {
			video.setUrlZip(url);
			videoMongoRepository.save(video);
		});
	}
	
	public void atualizarStatus(String id, VideoStatus novoStatus) {
		videoMongoRepository.findById(id).ifPresent(video -> {
			video.setStatus(novoStatus.toString());
			videoMongoRepository.save(video);
		});
	}
	
	public int getTempoParticao(String id) {
		Optional<VideoDocument> optionalVideo = videoMongoRepository.findById(id);
		
		if(optionalVideo.isPresent()) {
			return optionalVideo.get().getSecondsPartition();
		}
		return 20;
	}
	
	private VideoDocument getVideoDocument(String id) {
		Optional<VideoDocument> optionalVideo = videoMongoRepository.findById(id);
		
		if(optionalVideo.isPresent()) {
			return optionalVideo.get();
		}
		return new VideoDocument() ;
	}

	public Path downloadVideoFromS3(String url) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
		HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

		Path tempFile = Files.createTempFile("video_", ".mp4");
		Files.copy(response.body(), tempFile, StandardCopyOption.REPLACE_EXISTING);

		return tempFile;
	}
}

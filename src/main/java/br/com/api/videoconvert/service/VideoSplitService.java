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
import java.util.Comparator;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.api.videoconvert.model.VideoQueue;
import br.com.api.videoconvert.model.VideoRequest;
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
	private  NotificationSender notificationSender;
	
	@Transactional
	public void splitVideo(VideoQueue videoQueue) {
		
        try {
            log.info("Processo iniciado:");
            
            atualizarStatus(videoQueue.getId(), VideoStatus.PROCESSING);
                        
            Path videoPath = downloadVideoFromS3(videoQueue.getUrl());
            String outputFolder = "src/main/resources/images/";
            Files.createDirectories(Paths.get(outputFolder));

           // FFmpeg ffmpeg = new FFmpeg("ffmpeg");
            String outputPattern = outputFolder + "frame_%03d.jpg";
            
       
           // String videoPath = "src/main/resources/video/Marvel_DOTNET_CSHARP.mp4";
          //  String outputFolder = "src/main/resources/images/";

           // Files.createDirectories(Paths.get(outputFolder));

            double interval = 400.0;

            FFmpeg ffmpeg = new FFmpeg("src/main/resources/ffmfiles/ffmpeg.exe");            
            
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(videoPath.toString())
                    .addOutput(outputPattern)
                    .setFormat("image2")
                    .setVideoFilter("fps=1/" + interval + ",scale=640:-1")
                    .done();

            ffmpeg.run(builder);

            log.info("------ Extração de frames finalizada.------");
            
            createZipImages(outputFolder, videoQueue);

            log.info("------ Processo finalizado. ------");
            atualizarStatus(videoQueue.getId(), VideoStatus.COMPLETED);

            
        } catch (Exception  e) {
            atualizarStatus(videoQueue.getId(), VideoStatus.FAILED);
            log.error("Erro durante o processo: {}" , e.getMessage());
        }
	}

	private void createZipImages(String outputFolder, VideoQueue videoQueue) throws IOException {
		
        log.info(" ------- Iniciando criação arquivo zip ------ ");

	    String destinationZipFilePath = "src/main/resources/zips/images.zip";

	    Files.createDirectories(Paths.get("src/main/resources/zips/"));

	    try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(destinationZipFilePath)))) {
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
	                } catch (IOException e) {
	                    atualizarStatus(videoQueue.getId(), VideoStatus.FAILED);
	                    log.error("Erro ao adicionar arquivo ao zip: {} ", e.getMessage());
	                }
	            });
	    }

	    log.info("Arquivo ZIP criado em: {}", destinationZipFilePath);
	    
        try {
            s3Uploader.uploadFile("videos/images.zip", Paths.get(destinationZipFilePath));
            log.info("Upload para o S3 concluído com sucesso.");
            
            deleteDirectoryRecursively(Paths.get(outputFolder));
            Files.deleteIfExists(Paths.get(destinationZipFilePath));
            log.info("Arquivos temporários deletados com sucesso.");
        } catch (Exception e) {
            atualizarStatus(videoQueue.getId(), VideoStatus.FAILED);
            log.error("Erro ao fazer upload para o S3: {}", e.getMessage());
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
	
	public void atualizarStatus(String id, VideoStatus novoStatus) {
		videoMongoRepository.findById(id).ifPresent(video -> {
	        video.setStatus(novoStatus.toString());
	        videoMongoRepository.save(video);
	    });
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

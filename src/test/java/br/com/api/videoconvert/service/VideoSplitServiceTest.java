package br.com.api.videoconvert.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.api.videoconvert.model.VideoDocument;
import br.com.api.videoconvert.model.VideoQueue;
import br.com.api.videoconvert.mongo.repository.VideoMongoRepository;
import br.com.api.videoconvert.sqs.sender.NotificationSender;

@SpringBootTest
@ActiveProfiles("test")
class VideoSplitServiceTest {
	
	@Spy
    @InjectMocks
    private VideoSplitService videoSplitService;

    @Mock
    private S3UploaderService s3UploaderService;

    @Mock
    private VideoMongoRepository videoMongoRepository;

    @Mock
    private NotificationSender notificationSender;

    private VideoQueue videoQueue;

    private VideoDocument videoDocument;
     @Mock
    private S3UploaderService s3Uploader;
     

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        videoQueue = new VideoQueue();
        videoQueue.setId("123");
        videoQueue.setUrl("https://meu-video-fake.mp4");

        videoDocument = new VideoDocument();
        videoDocument.setId("123");
        videoDocument.setClientId("cliente@teste.com");
        videoDocument.setSecondsPartition(2);

        when(videoMongoRepository.findById("123")).thenReturn(Optional.of(videoDocument));

        Path fakeVideo = Files.createTempFile("fake", ".mp4");
        Files.write(fakeVideo, "video".getBytes());

        doReturn(fakeVideo).when(videoSplitService).downloadVideoFromS3(anyString());
    }


    @Test
    void testSplitVideo_falha_no_download() throws Exception {
        doThrow(new RuntimeException("Erro simulado")).when(videoSplitService).downloadVideoFromS3(anyString());

        videoSplitService.splitVideo(videoQueue);

        verify(notificationSender).send(any());
        verify(videoMongoRepository, atLeastOnce()).save(any());
    }
    
    @Test
    void shouldCreateZipImagesSuccessfully() throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
        Path tempDir = Files.createTempDirectory("test_frames");
        Path frame1 = Files.createFile(tempDir.resolve("frame_001.jpg"));
        Path frame2 = Files.createFile(tempDir.resolve("frame_002.jpg"));

        Files.writeString(frame1, "fake data");
        Files.writeString(frame2, "fake data");

        VideoQueue videoQueue = new VideoQueue();
        videoQueue.setId("video123");

        VideoSplitService spyService = Mockito.spy(videoSplitService);

        doNothing().when(spyService).uploadZip(anyString(), any(VideoQueue.class), anyString());

        Method method = VideoSplitService.class.getDeclaredMethod("createZipImages", String.class, VideoQueue.class);
        method.setAccessible(true);
        method.invoke(spyService, tempDir.toString(), videoQueue);

        verify(spyService, times(1)).uploadZip(anyString(), eq(videoQueue), anyString());

        Files.deleteIfExists(frame1);
        Files.deleteIfExists(frame2);
        Files.deleteIfExists(tempDir);
    }
    
    @Test
    void shouldUploadZipSuccessfully() throws Exception {
        Path outputFolder = Files.createTempDirectory("test_output_folder");
        Path zipFile = Files.createTempFile("test_frames", ".zip");
        String destinationZipFilePath = zipFile.toString();

        VideoQueue videoQueue = new VideoQueue();
        videoQueue.setId("video123");

        VideoDocument videoDoc = new VideoDocument();
        videoDoc.setId("video123");
        videoDoc.setClientId("client123");

        when(videoMongoRepository.findById("video123")).thenReturn(Optional.of(videoDoc));

        doNothing().when(s3Uploader).uploadFile(anyString(), any(Path.class));

        videoSplitService.uploadZip(outputFolder.toString(), videoQueue, destinationZipFilePath);

        verify(s3Uploader, times(1)).uploadFile(anyString(), eq(Paths.get(destinationZipFilePath)));
        verify(videoMongoRepository, times(1)).findById("video123");
        verify(videoMongoRepository, times(1)).save(any(VideoDocument.class));

        Files.deleteIfExists(zipFile);
        Files.deleteIfExists(outputFolder);
    }
    
}

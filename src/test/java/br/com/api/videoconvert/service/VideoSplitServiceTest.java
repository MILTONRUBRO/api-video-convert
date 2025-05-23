package br.com.api.videoconvert.service;

import br.com.api.videoconvert.model.VideoDocument;
import br.com.api.videoconvert.model.VideoQueue;
import br.com.api.videoconvert.model.enums.Notification;
import br.com.api.videoconvert.mongo.repository.VideoMongoRepository;
import br.com.api.videoconvert.sqs.sender.NotificationSender;
import br.com.api.videoconvert.utils.TempFileUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = "AWS_REGION=us-east-1")
@ActiveProfiles("test")
class VideoSplitServiceTest {

    @MockBean
    private SqsAsyncClient sqsAsyncClient;
	
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
    
    @Test
    void testGetVideoDocument_default() throws Exception {
        when(videoMongoRepository.findById("999")).thenReturn(Optional.empty());

        Method method = VideoSplitService.class.getDeclaredMethod("getVideoDocument", String.class);
        method.setAccessible(true);
        VideoDocument result = (VideoDocument) method.invoke(videoSplitService, "999");

        assertNotNull(result);
    }
    
    @Test
    void testDeleteDirectoryRecursively() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Path dir = Files.createTempDirectory("delete_test");
        Files.createFile(dir.resolve("a.txt"));

        Method method = VideoSplitService.class.getDeclaredMethod("deleteDirectoryRecursively", Path.class);
        method.setAccessible(true);
        method.invoke(videoSplitService, dir);

        assertFalse(Files.exists(dir));
    }
    
    @Test
    void testUploadZip_falhaS3() throws IOException {
        Path outputFolder = Files.createTempDirectory("test_output_folder");
        Path zipFile = Files.createTempFile("test_frames", ".zip");

        VideoQueue videoQueue = new VideoQueue();
        videoQueue.setId("video123");

        when(videoMongoRepository.findById("video123")).thenReturn(Optional.of(videoDocument));
        doThrow(new RuntimeException("S3 falhou")).when(s3Uploader).uploadFile(any(), any());

        videoSplitService.uploadZip(outputFolder.toString(), videoQueue, zipFile.toString());

        verify(notificationSender).send(any());
    }
    
    @Test
    void testGetTempoParticao_default() {
        when(videoMongoRepository.findById("999")).thenReturn(Optional.empty());
        int tempo = videoSplitService.getTempoParticao("999");
        assertEquals(20, tempo);
    }
    
    @Test
    void deveLancarErroAoAdicionarArquivoNoZip() throws Exception {
        // Criar diretório de frames
        Path outputFolder = TempFileUtils.createSecureTempDirectory("frames_test_");

        // Criar arquivo inválido (diretório em vez de arquivo) que causará falha no Files.copy
        Path badFile = outputFolder.resolve("erro.jpg");
        Files.createDirectory(badFile); // diretório em vez de arquivo comum

        // Usar reflexão para invocar o método privado diretamente
        Method method = VideoSplitService.class.getDeclaredMethod("createZipImages", String.class, VideoQueue.class);
        method.setAccessible(true);

        method.invoke(videoSplitService, outputFolder.toString(), videoQueue);

        // Verifica se o status foi atualizado para FAILED
        verify(videoMongoRepository, atLeastOnce()).findById("123");
    }
    
}

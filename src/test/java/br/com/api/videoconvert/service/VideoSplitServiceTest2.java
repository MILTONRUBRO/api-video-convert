package br.com.api.videoconvert.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.api.videoconvert.model.VideoDocument;
import br.com.api.videoconvert.model.VideoQueue;
import br.com.api.videoconvert.mongo.repository.VideoMongoRepository;
import br.com.api.videoconvert.sqs.sender.NotificationSender;

@ExtendWith(MockitoExtension.class)
public class VideoSplitServiceTest2 {

    @Mock
    private HttpClient httpClient;

    @Mock
    private VideoMongoRepository videoMongoRepository;

    @Mock
    private NotificationSender notificationSender;

    @Mock
    private S3UploaderService s3Uploader;

    @InjectMocks
    private VideoSplitService videoSplitService;

    @Test
    void testSplitVideo_Success() throws Exception {
        // Arrange
        String videoUrl = "http://example.com/video.mp4";
        VideoQueue videoQueue = new VideoQueue();
        videoQueue.setId("videoId");
        videoQueue.setUrl(videoUrl);
        when(videoMongoRepository.findById(anyString())).thenReturn(Optional.of(new VideoDocument()));

        videoSplitService.splitVideo(videoQueue);

        verify(videoMongoRepository, times(2)).save(any()); 

    }


    @Test
    void testDownloadVideoFromS3() throws Exception {
        String videoUrl = "http://example.com/video.mp4";

        Path downloadedFile = videoSplitService.downloadVideoFromS3(videoUrl);
        assertNotNull(downloadedFile, "O arquivo de vídeo não foi baixado.");
        assertTrue(Files.exists(downloadedFile), "O arquivo de vídeo não existe no sistema.");
    }
}

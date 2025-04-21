package br.com.api.videoconvert.consumer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.api.videoconvert.model.VideoQueue;
import br.com.api.videoconvert.service.VideoSplitService;

class VideoConsumerTest {

    @Mock
    private VideoSplitService videoSplitService;

    @InjectMocks
    private VideoConsumer videoConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReceive() {
        VideoQueue video = new VideoQueue();
        videoConsumer.receive(video);
        verify(videoSplitService, times(1)).splitVideo(video);
    }

}

package br.com.api.videoconvert.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.api.videoconvert.model.VideoQueue;
import br.com.api.videoconvert.service.VideoSplitService;

class VideoControllerTest {
	@Mock
	private VideoSplitService videoSplitService;

	@InjectMocks
	private VideoController videoController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCreateImages() {
		VideoQueue request = new VideoQueue();

		ResponseEntity<Void> response = videoController.createImages(request);

		verify(videoSplitService, times(1)).splitVideo(request);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

}

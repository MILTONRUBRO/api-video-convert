package br.com.api.videoconvert.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class VideoQueueTest {

    @Test
    void testGettersAndSetters() {
        // Criar objeto de teste
        VideoQueue videoQueue = new VideoQueue();
        videoQueue.setId("123");
        videoQueue.setUrl("http://video.url");

        // Validar os Getters
        assertEquals("123", videoQueue.getId());
        assertEquals("http://video.url", videoQueue.getUrl());

        // Validar os Setters
        videoQueue.setId("456");
        videoQueue.setUrl("http://new.url");

        assertEquals("456", videoQueue.getId());
        assertEquals("http://new.url", videoQueue.getUrl());
    }

    @Test
    void testEqualsAndHashCode() {
        // Criar dois objetos iguais
        VideoQueue video1 = new VideoQueue("123", "http://video.url");
        VideoQueue video2 = new VideoQueue("123", "http://video.url");

        // Criar um objeto diferente
        VideoQueue video3 = new VideoQueue("456", "http://different.url");

        // Validar igualdade
        assertEquals(video1, video2);
        assertNotEquals(video1, video3);

        // Validar hashCode
        assertEquals(video1.hashCode(), video2.hashCode());
        assertNotEquals(video1.hashCode(), video3.hashCode());
    }

    @Test
    void testToString() {
        // Criar objeto
        VideoQueue videoQueue = new VideoQueue("123", "http://video.url");

        // Validar saída do toString
        String expectedString = "VideoQueue(id=123, url=http://video.url)";
        assertEquals(expectedString, videoQueue.toString());
    }
}
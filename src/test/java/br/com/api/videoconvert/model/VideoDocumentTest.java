package br.com.api.videoconvert.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class VideoDocumentTest {

    @Test
    void testGettersAndSetters() {
        // Criar objeto de teste
        VideoDocument videoDocument = new VideoDocument();
        videoDocument.setId("123");
        videoDocument.setTitle("Título do Vídeo");
        videoDocument.setUrl("http://video.url");
        videoDocument.setClientId("cliente123");
        videoDocument.setStatus("Pendente");
        videoDocument.setSecondsPartition(10);
        videoDocument.setCreatedAt(LocalDateTime.of(2025, 4, 24, 10, 0));
        videoDocument.setUrlZip("http://zip.url");

        // Validar os Getters
        assertEquals("123", videoDocument.getId());
        assertEquals("Título do Vídeo", videoDocument.getTitle());
        assertEquals("http://video.url", videoDocument.getUrl());
        assertEquals("cliente123", videoDocument.getClientId());
        assertEquals("Pendente", videoDocument.getStatus());
        assertEquals(10, videoDocument.getSecondsPartition());
        assertEquals(LocalDateTime.of(2025, 4, 24, 10, 0), videoDocument.getCreatedAt());
        assertEquals("http://zip.url", videoDocument.getUrlZip());
    }

    @Test
    void testEqualsAndHashCode() {
        // Criar dois objetos iguais
        VideoDocument video1 = new VideoDocument();
        video1.setId("123");
        video1.setTitle("Título do Vídeo");
        video1.setUrl("http://video.url");
        video1.setClientId("cliente123");
        video1.setStatus("Pendente");
        video1.setSecondsPartition(10);
        video1.setCreatedAt(LocalDateTime.of(2025, 4, 24, 10, 0));
        video1.setUrlZip("http://zip.url");

        VideoDocument video2 = new VideoDocument();
        video2.setId("123");
        video2.setTitle("Título do Vídeo");
        video2.setUrl("http://video.url");
        video2.setClientId("cliente123");
        video2.setStatus("Pendente");
        video2.setSecondsPartition(10);
        video2.setCreatedAt(LocalDateTime.of(2025, 4, 24, 10, 0));
        video2.setUrlZip("http://zip.url");

        // Criar objeto diferente
        VideoDocument video3 = new VideoDocument();
        video3.setId("456");

        // Verificar igualdade
        assertEquals(video1, video2);
        assertNotEquals(video1, video3);

        // Verificar hashCode
        assertEquals(video1.hashCode(), video2.hashCode());
        assertNotEquals(video1.hashCode(), video3.hashCode());
    }

    @Test
    void testToString() {
        // Criar objeto
        VideoDocument videoDocument = new VideoDocument();
        videoDocument.setId("123");
        videoDocument.setTitle("Título do Vídeo");
        videoDocument.setUrl("http://video.url");
        videoDocument.setClientId("cliente123");
        videoDocument.setStatus("Pendente");
        videoDocument.setSecondsPartition(10);
        videoDocument.setCreatedAt(LocalDateTime.of(2025, 4, 24, 10, 0));
        videoDocument.setUrlZip("http://zip.url");

        // Validar saída do toString
        String expectedString = "VideoDocument(id=123, title=Título do Vídeo, url=http://video.url, clientId=cliente123, status=Pendente, secondsPartition=10, createdAt=2025-04-24T10:00, urlZip=http://zip.url)";
        assertEquals(expectedString, videoDocument.toString());
    }
}
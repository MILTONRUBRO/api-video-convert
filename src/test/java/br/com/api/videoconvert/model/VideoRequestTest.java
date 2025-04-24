package br.com.api.videoconvert.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class VideoRequestTest {

    @Test
    void testGettersAndSetters() {
        // Criar objeto de teste
        VideoRequest videoRequest = new VideoRequest(1L, "Título do Vídeo", "Descrição do Vídeo", "Pendente");

        // Validar os Getters
        assertEquals(1L, videoRequest.getId());
        assertEquals("Título do Vídeo", videoRequest.getTitulo());
        assertEquals("Descrição do Vídeo", videoRequest.getDescricao());
        assertEquals("Pendente", videoRequest.getStatus());

        // Validar os Setters
        videoRequest.setId(2L);
        videoRequest.setTitulo("Novo Título");
        videoRequest.setDescricao("Nova Descrição");
        videoRequest.setStatus("Concluído");

        assertEquals(2L, videoRequest.getId());
        assertEquals("Novo Título", videoRequest.getTitulo());
        assertEquals("Nova Descrição", videoRequest.getDescricao());
        assertEquals("Concluído", videoRequest.getStatus());
    }

    @Test
    void testEqualsAndHashCode() {
        // Criar dois objetos iguais
        VideoRequest video1 = new VideoRequest(1L, "Título", "Descrição", "Status");
        VideoRequest video2 = new VideoRequest(1L, "Título", "Descrição", "Status");

        // Criar um objeto diferente
        VideoRequest video3 = new VideoRequest(2L, "Outro Título", "Outra Descrição", "Outro Status");

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
        VideoRequest videoRequest = new VideoRequest(1L, "Título", "Descrição", "Status");

        // Validar saída do toString
        String expectedString = "VideoRequest(id=1, titulo=Título, descricao=Descrição, status=Status)";
        assertEquals(expectedString, videoRequest.toString());
    }
}
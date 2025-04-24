package br.com.api.videoconvert.model.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class NotificationTest {

    @Test
    void testGettersAndSetters() {
        // Criar objeto de teste
        Notification notification = new Notification();
        notification.setUserId("user123");
        notification.setVideoId("video123");
        notification.setStatus("Enviado");
        notification.setMessage("Processamento concluído");
        notification.setSubject("Atualização de Status");
        notification.setEmail("user@example.com");

        // Validar Getters
        assertEquals("user123", notification.getUserId());
        assertEquals("video123", notification.getVideoId());
        assertEquals("Enviado", notification.getStatus());
        assertEquals("Processamento concluído", notification.getMessage());
        assertEquals("Atualização de Status", notification.getSubject());
        assertEquals("user@example.com", notification.getEmail());
    }

    @Test
    void testEqualsAndHashCode() {
        // Criar dois objetos iguais
        Notification notification1 = new Notification("user123", "video123", "Enviado", "Processamento concluído", "Atualização de Status", "user@example.com");
        Notification notification2 = new Notification("user123", "video123", "Enviado", "Processamento concluído", "Atualização de Status", "user@example.com");

        // Criar objeto diferente
        Notification notification3 = new Notification("user456", "video456", "Erro", "Falha no processamento", "Erro no Status", "another@example.com");

        // Validar igualdade
        assertEquals(notification1, notification2);
        assertNotEquals(notification1, notification3);

        // Validar hashCode
        assertEquals(notification1.hashCode(), notification2.hashCode());
        assertNotEquals(notification1.hashCode(), notification3.hashCode());
    }

    @Test
    void testToString() {
        // Criar objeto
        Notification notification = new Notification("user123", "video123", "Enviado", "Processamento concluído", "Atualização de Status", "user@example.com");

        // Validar saída do toString
        String expectedString = "Notification(userId=user123, videoId=video123, status=Enviado, message=Processamento concluído, subject=Atualização de Status, email=user@example.com)";
        assertEquals(expectedString, notification.toString());
    }
}
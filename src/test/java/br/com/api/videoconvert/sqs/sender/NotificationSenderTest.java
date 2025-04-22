package br.com.api.videoconvert.sqs.sender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.api.videoconvert.model.enums.Notification;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

class NotificationSenderTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NotificationSender notificationSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSend() throws Exception {
        Notification notification = new Notification();
        notification.setStatus("FAILED");
        String messageBody = "Mensagem de teste";

        when(objectMapper.writeValueAsString(notification)).thenReturn(messageBody);
        notificationSender.send(notification);

        verify(sqsClient, times(1)).sendMessage(any(SendMessageRequest.class));
    }
    
    @Test
    void testSendNotificationThrowsExceptionWhenObjectMapperFails() throws Exception {
        // Arrange
        Notification notification = new Notification();
        
        when(objectMapper.writeValueAsString(notification)).thenThrow(new RuntimeException("Erro na conversão"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            notificationSender.send(notification);
        });

        assertEquals("Erro ao enviar notificação para SQS", exception.getMessage());
        assertTrue(exception.getCause() instanceof RuntimeException);
    }

    @Test
    void testSendNotificationThrowsExceptionWhenSqsClientFails() throws Exception {
        Notification notification = new Notification();
        String messageBody = "{\"title\":\"test\",\"message\":\"message\"}";

        when(objectMapper.writeValueAsString(notification)).thenReturn(messageBody);

        doThrow(new RuntimeException("Erro ao enviar mensagem para SQS")).when(sqsClient).sendMessage(any(SendMessageRequest.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            notificationSender.send(notification);
        });

        assertEquals("Erro ao enviar notificação para SQS", exception.getMessage());
        assertTrue(exception.getCause() instanceof RuntimeException);
    }

}

package br.com.api.videoconvert.sqs.sender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.api.videoconvert.model.enums.Notification;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@RequiredArgsConstructor
public class NotificationSender {
	
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${sqs.notification.queue-url}")
    private String queueUrl;

    public void send(Notification notification) {
        try {
            String messageBody = objectMapper.writeValueAsString(notification);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();

            sqsClient.sendMessage(request);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar notificação para SQS", e);
        }
    }

}

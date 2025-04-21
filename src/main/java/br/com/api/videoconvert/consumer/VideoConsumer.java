package br.com.api.videoconvert.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.api.videoconvert.model.VideoQueue;
import br.com.api.videoconvert.service.VideoSplitService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class VideoConsumer {
	
	@Autowired
	private  VideoSplitService  videoSplitService;
	
    @SqsListener("${sqs.queue.url}")
    public void receive(@Payload @Valid @NotNull VideoQueue video) {
        log.info("Mensagem recebida da fila: {}", video);

        try {
        	videoSplitService.splitVideo(video);
            log.info("--------------  Video processado com sucesso  -------------- ");
        } catch (Exception e) {
            log.error("Erro ao processar o video {}", video, e);
        }
    }

}

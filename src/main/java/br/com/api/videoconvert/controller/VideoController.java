package br.com.api.videoconvert.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.api.videoconvert.model.VideoQueue;
import br.com.api.videoconvert.service.VideoSplitService;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class VideoController {
	
	@Autowired
	private VideoSplitService videoSplitService;
	
	@PostMapping
	public ResponseEntity<Void> createImages(@RequestBody VideoQueue request) {
		log.info("REQUEST: {}", request);
		
        Instant inicio = Instant.now(); 

		videoSplitService.splitVideo(request);
		
        Instant fim = Instant.now();
        Duration duracao = Duration.between(inicio, fim); 

        log.info("Tempo de execução: {} segundos", duracao.getSeconds());
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}

package br.com.api.videoconvert.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoRequest {
	private Long id;
	private String titulo;
	private String descricao;
	private String status;
}

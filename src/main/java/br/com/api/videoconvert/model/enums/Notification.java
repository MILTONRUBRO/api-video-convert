package br.com.api.videoconvert.model.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
	private String userId;
	private String videoId;
	private String status;
	private String message;
	private String subject;
	private String email;
}



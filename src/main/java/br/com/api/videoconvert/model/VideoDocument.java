package br.com.api.videoconvert.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "videos")
@Data
@NoArgsConstructor
public class VideoDocument {
	
    @Id
    private String id;
    private String title;
    private String url;
    private String clientId;
    private String status;
    private LocalDateTime createdAt;

}

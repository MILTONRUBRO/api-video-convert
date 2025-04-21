package br.com.api.videoconvert.mongo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.api.videoconvert.model.VideoDocument;

public interface VideoMongoRepository extends MongoRepository<VideoDocument, String> {
	 
	List<VideoDocument> findByClientId(String clientId);

}

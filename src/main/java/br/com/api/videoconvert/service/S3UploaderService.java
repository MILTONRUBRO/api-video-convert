package br.com.api.videoconvert.service;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import br.com.api.videoconvert.config.AwsProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3UploaderService {
	
    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public S3UploaderService(AwsProperties awsProperties) {
        this.awsProperties = awsProperties;

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                awsProperties.getAccessKey(),
                awsProperties.getSecretKey()
        );

        this.s3Client = S3Client.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public void uploadFile(String keyName, Path filePath) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(awsProperties.getBucket())
                .key(keyName)
                .build();

         s3Client.putObject(request, RequestBody.fromFile(filePath));
    }

}

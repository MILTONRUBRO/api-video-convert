package br.com.api.videoconvert.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
@RequiredArgsConstructor
public class AwsSqsConfig {
	
    private final AwsProperties awsProperties;

    @Bean
    public SqsClient sqsClient() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                awsProperties.getAccessKey(),
                awsProperties.getSecretKey()
        );

        return SqsClient.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}

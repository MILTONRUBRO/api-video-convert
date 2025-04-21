package br.com.api.videoconvert.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "aws")
@Data
public class AwsProperties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;
}

package br.com.api.videoconvert.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class AwsPropertiesTest {

    @TestConfiguration
    @EnableConfigurationProperties(AwsProperties.class)
    static class Config {
        @Bean
        public AwsProperties awsProperties() {
            AwsProperties properties = new AwsProperties();
            properties.setAccessKey("test-access-key");
            properties.setSecretKey("test-secret-key");
            properties.setRegion("us-east-1");
            properties.setBucket("test-bucket");
            return properties;
        }
    }

    @Test
    void testAwsPropertiesLoading() {
        AwsProperties awsProperties = new Config().awsProperties();

        assertEquals("test-access-key", awsProperties.getAccessKey());
        assertEquals("test-secret-key", awsProperties.getSecretKey());
        assertEquals("us-east-1", awsProperties.getRegion());
        assertEquals("test-bucket", awsProperties.getBucket());
    }
    
    @Test
    void testLombokGeneratedMethods() {
        AwsProperties awsProperties = new AwsProperties();
        awsProperties.setAccessKey("test-access-key");
        awsProperties.setSecretKey("test-secret-key");
        awsProperties.setRegion("us-east-1");
        awsProperties.setBucket("test-bucket");

        assertEquals("test-access-key", awsProperties.getAccessKey());
        assertEquals("test-secret-key", awsProperties.getSecretKey());
        assertEquals("us-east-1", awsProperties.getRegion());
        assertEquals("test-bucket", awsProperties.getBucket());

        AwsProperties otherProperties = new AwsProperties();
        otherProperties.setAccessKey("test-access-key");
        otherProperties.setSecretKey("test-secret-key");
        otherProperties.setRegion("us-east-1");
        otherProperties.setBucket("test-bucket");

        assertEquals(awsProperties, otherProperties);
        assertEquals(awsProperties.hashCode(), otherProperties.hashCode());

        String expectedString = "AwsProperties(accessKey=test-access-key, secretKey=test-secret-key, region=us-east-1, bucket=test-bucket)";
        assertEquals(expectedString, awsProperties.toString());
    }

}

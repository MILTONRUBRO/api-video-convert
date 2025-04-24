package br.com.api.videoconvert.service;

import br.com.api.videoconvert.config.AwsProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class S3UploaderServiceTest {

    @Mock
    private AwsProperties awsProperties;

    @Mock
    private S3Client s3Client;

    private S3UploaderService uploaderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Configura o mock com valores válidos
        when(awsProperties.getAccessKey()).thenReturn("fake-access");
        when(awsProperties.getSecretKey()).thenReturn("fake-secret");
        when(awsProperties.getRegion()).thenReturn("us-east-1");
        when(awsProperties.getBucket()).thenReturn("test-bucket");

        // Instancia manualmente o service com mock de awsProperties
        uploaderService = new S3UploaderService(awsProperties);

        // Injeta o mock de S3Client (substituindo o real criado no construtor)
        ReflectionTestUtils.setField(uploaderService, "s3Client", s3Client);
    }

    @Test
    void testUploadFile() throws Exception {
        Path tempFile = Files.createTempFile("test-upload", ".txt");
        Files.writeString(tempFile, "conteudo fake");

        uploaderService.uploadFile("meu-arquivo.txt", tempFile);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest request = requestCaptor.getValue();
        assertEquals("meu-arquivo.txt", request.key());
        assertEquals("test-bucket", request.bucket());

        Files.deleteIfExists(tempFile);
    }
}

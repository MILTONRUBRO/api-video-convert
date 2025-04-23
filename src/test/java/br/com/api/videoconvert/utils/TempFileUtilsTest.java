package br.com.api.videoconvert.utils;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TempFileUtilsTest {

    @Test
    void testCreateSecureTempDirectory() throws IOException {
        // Cria um diretório temporário seguro
        Path tempDir = TempFileUtils.createSecureTempDirectory("secure_dir_");

        // Verifica se o diretório foi criado
        assertTrue(Files.exists(tempDir));

        // Verifica as permissões no caso de sistemas Unix-like
        if (SystemUtils.IS_OS_UNIX) {
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(tempDir);
            assertTrue(permissions.contains(PosixFilePermission.OWNER_READ));
            assertTrue(permissions.contains(PosixFilePermission.OWNER_WRITE));
            assertTrue(permissions.contains(PosixFilePermission.OWNER_EXECUTE));
        }
    }

    @Test
    void testCreateSecureTempFile() throws IOException {
        // Cria um arquivo temporário seguro
        Path tempFile = TempFileUtils.createSecureTempFile("secure_file_", ".tmp");

        // Verifica se o arquivo foi criado
        assertTrue(Files.exists(tempFile));

        // Verifica as permissões no caso de sistemas Unix-like
        if (SystemUtils.IS_OS_UNIX) {
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(tempFile);
            assertTrue(permissions.contains(PosixFilePermission.OWNER_READ));
            assertTrue(permissions.contains(PosixFilePermission.OWNER_WRITE));
            assertTrue(permissions.contains(PosixFilePermission.OWNER_EXECUTE));
        } else {
            // No Windows, permissões podem não ser modificáveis, então não verificamos isso
            assertTrue(Files.exists(tempFile));
        }
    }
}

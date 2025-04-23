package br.com.api.videoconvert.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

class TempFileUtilsTest {

    @Test
    void testCreateSecureTempDirectory() throws IOException {
        Path tempDir = TempFileUtils.createSecureTempDirectory("secure_dir_");

        assertTrue(Files.exists(tempDir));

        if (SystemUtils.IS_OS_UNIX) {
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(tempDir);
            assertTrue(permissions.contains(PosixFilePermission.OWNER_READ));
            assertTrue(permissions.contains(PosixFilePermission.OWNER_WRITE));
            assertTrue(permissions.contains(PosixFilePermission.OWNER_EXECUTE));
        }
    }

    @Test
    void testCreateSecureTempFile() throws IOException {
        Path tempFile = TempFileUtils.createSecureTempFile("secure_file_", ".tmp");

        assertTrue(Files.exists(tempFile));

        if (SystemUtils.IS_OS_UNIX) {
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(tempFile);
            assertTrue(permissions.contains(PosixFilePermission.OWNER_READ));
            assertTrue(permissions.contains(PosixFilePermission.OWNER_WRITE));
            assertTrue(permissions.contains(PosixFilePermission.OWNER_EXECUTE));
        }
    }
}

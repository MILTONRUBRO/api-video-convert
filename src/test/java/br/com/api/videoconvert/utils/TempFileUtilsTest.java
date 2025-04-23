package br.com.api.videoconvert.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import org.junit.jupiter.api.Test;

class TempFileUtilsTest {

    @Test
    void testCreateSecureTempDirectory() throws IOException {
        Path dir = TempFileUtils.createSecureTempDirectory("test_dir_");

        assertNotNull(dir);
        assertTrue(Files.exists(dir));
        assertTrue(Files.isDirectory(dir));

        if (Files.getFileStore(dir).supportsFileAttributeView(PosixFileAttributeView.class)) {
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(dir);
            assertTrue(permissions.contains(PosixFilePermission.OWNER_READ));
            assertTrue(permissions.contains(PosixFilePermission.OWNER_WRITE));
            assertTrue(permissions.contains(PosixFilePermission.OWNER_EXECUTE));
            assertEquals(3, permissions.size(), "Deve conter apenas permissões do dono");
        }

        Files.deleteIfExists(dir);
    }

    @Test
    void testCreateSecureTempFile() throws IOException {
        Path file = TempFileUtils.createSecureTempFile("test_file_", ".tmp");

        assertNotNull(file);
        assertTrue(Files.exists(file));
        assertTrue(Files.isRegularFile(file));

        Files.deleteIfExists(file);
    }
}

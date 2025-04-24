package br.com.api.videoconvert.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class TempFileUtilsTest {

    @Test
    void testCreateSecureTempDirectory_Windows() throws IOException {
        String originalOs = System.getProperty("os.name");
        System.setProperty("os.name", "Windows 10");

        Path dir = TempFileUtils.createSecureTempDirectory("testDir_");
        assertNotNull(dir);
        assertTrue(Files.exists(dir));
        assertTrue(Files.isDirectory(dir));

        Files.deleteIfExists(dir);
        System.setProperty("os.name", originalOs);
    }

    @Test
    void testCreateSecureTempFile_Windows() throws IOException {
        String originalOs = System.getProperty("os.name");
        System.setProperty("os.name", "Windows 10");

        Path file = TempFileUtils.createSecureTempFile("testFile_", ".tmp");
        assertNotNull(file);
        assertTrue(Files.exists(file));
        assertTrue(Files.isRegularFile(file));

        Files.deleteIfExists(file);
        System.setProperty("os.name", originalOs);
    }

    @Test
    void testCreateSecureTempDirectory_UnixLike() throws IOException {
        String originalOs = System.getProperty("os.name");
        System.setProperty("os.name", "Linux");

        Path dir = TempFileUtils.createSecureTempDirectory("testDir_");
        assertNotNull(dir);
        assertTrue(Files.exists(dir));
        assertTrue(Files.isDirectory(dir));

        Files.deleteIfExists(dir);
        System.setProperty("os.name", originalOs);
    }

    @Test
    void testCreateSecureTempFile_UnixLike() throws IOException {
        String originalOs = System.getProperty("os.name");
        System.setProperty("os.name", "Linux");

        Path file = TempFileUtils.createSecureTempFile("testFile_", ".tmp");
        assertNotNull(file);
        assertTrue(Files.exists(file));
        assertTrue(Files.isRegularFile(file));

        Files.deleteIfExists(file);
        System.setProperty("os.name", originalOs);
    }
}
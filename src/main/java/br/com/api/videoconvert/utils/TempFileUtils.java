package br.com.api.videoconvert.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;

public class TempFileUtils {

    public static Path createSecureTempDirectory(String prefix) throws IOException {
        Path dir = Files.createTempDirectory(prefix);
        if (Files.getFileStore(dir).supportsFileAttributeView(PosixFileAttributeView.class)) {
            Files.setPosixFilePermissions(dir, PosixFilePermissions.fromString("rwx------"));
        }
        return dir;
    }

    public static Path createSecureTempFile(String prefix, String suffix) throws IOException {
        Path file = Files.createTempFile(prefix, suffix);
        if (Files.getFileStore(file).supportsFileAttributeView(PosixFileAttributeView.class)) {
            Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------"));
        }
        return file;
    }
}

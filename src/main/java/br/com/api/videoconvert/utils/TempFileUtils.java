package br.com.api.videoconvert.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

public class TempFileUtils {

    public static Path createSecureTempDirectory(String prefix) throws IOException {
        if (System.getProperty("os.name").toLowerCase().contains("nix")
                || System.getProperty("os.name").toLowerCase().contains("nux")
                || System.getProperty("os.name").toLowerCase().contains("mac")) {
            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(
                    PosixFilePermissions.fromString("rwx------"));
            return Files.createTempDirectory(prefix, attr); // Compliant para Unix-like
        } else {
            Path dir = Files.createTempDirectory(prefix);  // Compliant para Windows
            File file = dir.toFile();
            file.setReadable(true, true);
            file.setWritable(true, true);
            file.setExecutable(true, true);
            return dir;
        }
    }

    public static Path createSecureTempFile(String prefix, String suffix) throws IOException {
        if (System.getProperty("os.name").toLowerCase().contains("nix")
                || System.getProperty("os.name").toLowerCase().contains("nux")
                || System.getProperty("os.name").toLowerCase().contains("mac")) {
            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(
                    PosixFilePermissions.fromString("rw-------"));
            return Files.createTempFile(prefix, suffix, attr); // Compliant para Unix-like
        } else {
            Path file = Files.createTempFile(prefix, suffix);  // Compliant para Windows
            File f = file.toFile();
            f.setReadable(true, true);
            f.setWritable(true, true);
            return file;
        }
    }
}

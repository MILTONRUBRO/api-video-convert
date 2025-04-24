package br.com.api.videoconvert.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

public class TempFileUtils {

    public static Path createSecureTempDirectory(String prefix) throws IOException {
        if (isUnixLike() && supportsPosix()) {
            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(
                    PosixFilePermissions.fromString("rwx------"));
            return Files.createTempDirectory(prefix, attr);
        } else {
            Path dir = Files.createTempDirectory(prefix);
            File file = dir.toFile();
            file.setReadable(true, true);
            file.setWritable(true, true);
            file.setExecutable(true, true);
            return dir;
        }
    }

    public static Path createSecureTempFile(String prefix, String suffix) throws IOException {
        if (isUnixLike() && supportsPosix()) {
            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(
                    PosixFilePermissions.fromString("rw-------"));
            return Files.createTempFile(prefix, suffix, attr);
        } else {
            Path file = Files.createTempFile(prefix, suffix);
            File f = file.toFile();
            f.setReadable(true, true);
            f.setWritable(true, true);
            return file;
        }
    }

    static boolean isUnixLike() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("nix") || os.contains("nux") || os.contains("mac");
    }

    static boolean supportsPosix() {
        return FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
    }
}

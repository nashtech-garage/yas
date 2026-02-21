package com.yas.media.repository;

import com.yas.media.config.FilesystemConfig;
import com.yas.media.exception.MediaFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FileSystemRepository {

    private static final String DIRECTORY_DOES_NOT_EXIST = "Directory %s does not exist.";

    private final FilesystemConfig filesystemConfig;

    public String persistFile(String filename, byte[] content) throws IOException {

        File directory = new File(filesystemConfig.getDirectory());
        checkExistingDirectory(directory);
        checkPermissions(directory);

        Path filePath = buildFilePath(filename);
        Files.write(filePath, content);
        log.info("File saved: {}", filename);
        return filePath.toString();
    }

    @SneakyThrows
    public InputStream getFile(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IllegalStateException(String.format(DIRECTORY_DOES_NOT_EXIST, filesystemConfig.getDirectory()));
        }

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new MediaFileException("Failed to read file: " + filePath, e);
        }
    }

    private Path buildFilePath(String filename) {
        // Validate the filename
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename");
        }
        // Normalize the path
        Path filePath = Paths.get(filesystemConfig.getDirectory(), filename).toAbsolutePath().normalize();

        // Ensure the file is within the base directory
        if (!filePath.startsWith(filesystemConfig.getDirectory())) {
            throw new IllegalArgumentException("Invalid file path");
        }
        return filePath;
    }

    private void checkExistingDirectory(File directory) {
        if (!directory.exists()) {
            throw new IllegalStateException(String.format(DIRECTORY_DOES_NOT_EXIST, filesystemConfig.getDirectory()));
        }
    }

    private void checkPermissions(File directory) {
        if (!directory.canRead() || !directory.canWrite()) {
            throw new IllegalStateException("Directory " + directory.getAbsolutePath() + " is not accessible.");
        }
    }
}

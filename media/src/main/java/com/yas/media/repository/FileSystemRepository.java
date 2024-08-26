package com.yas.media.repository;

import com.yas.media.config.FilesystemConfig;
import java.io.File;
import java.io.IOException;
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

    private final FilesystemConfig filesystemConfig;

    public String persistFile(String fileName, byte[] content) throws IOException {

        File directory = new File(filesystemConfig.getDirectory());
        checkExistingDirectory(directory);
        checkPermissions(directory);

        // check file existing or not
        Path path = Paths.get(filesystemConfig.getDirectory(), fileName);
        Files.write(path, content);
        log.info("File saved: {}", fileName);
        return path.toString();
    }

    @SneakyThrows
    public byte[] getFile(String filePath) {
      Path path = Paths.get(filePath);

      if (!Files.exists(path)) {
        throw new IllegalStateException("Directory " + filesystemConfig.getDirectory() + " does not exist.");
      }
      return Files.readAllBytes(path);
    }

    private void checkExistingDirectory(File directory) {
        if (!directory.exists()) {
          throw new IllegalStateException("Directory " + filesystemConfig.getDirectory() + " does not exist.");
        }
    }

    private void checkPermissions(File directory) {
        if (!directory.canRead() || !directory.canWrite()) {
          throw new IllegalStateException("Directory " + directory.getAbsolutePath() + " is not accessible.");
        }
    }
}

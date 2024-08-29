package com.yas.media;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.yas.media.config.FilesystemConfig;
import com.yas.media.repository.FileSystemRepository;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.Assert;

public class FileSystemRepositoryTest {

    @Mock
    private FilesystemConfig filesystemConfig;

    @Mock
    private File file;

    @InjectMocks
    private FileSystemRepository fileSystemRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPersistFile() throws IOException {
        String directoryPath = "test-media";
        String filename = "testFile.txt";
        byte[] content = "test content".getBytes();
        Path filePath = Paths.get(directoryPath, filename);

        when(filesystemConfig.getDirectory()).thenReturn(directoryPath);

        assertThrows(IllegalStateException.class, () -> fileSystemRepository.persistFile(filename, content));
    }

    @Test
    public void testGetFile() throws IOException {
        String directoryPath = "testDirectory";
        String filename = "testFile.txt";
        String filePathStr = Paths.get(directoryPath, filename).toString();
        byte[] content = "test content".getBytes();

        when(filesystemConfig.getDirectory()).thenReturn(directoryPath);

        Path filePath = Paths.get(filePathStr);
        Files.createDirectories(filePath.getParent()); // Ensure parent directories are created
        Files.write(filePath, content); // Write content to file

        // Test getting the file
        try (InputStream inputStream = fileSystemRepository.getFile(filePathStr)) {
            byte[] fileContent = inputStream.readAllBytes();
            Assert.isTrue(new String(fileContent).equals("test content"), "File content should match");
        }
    }

    @Test
    public void testPersistFileDirectoryDoesNotExist() {
        String directoryPath = "nonExistentDirectory";
        String filename = "testFile.txt";
        byte[] content = "test content".getBytes();

        when(filesystemConfig.getDirectory()).thenReturn(directoryPath);

        Executable executable = () -> fileSystemRepository.persistFile(filename, content);
        Assert.isTrue(assertThrows(IllegalStateException.class, executable).getMessage().contains("Directory " + directoryPath + " does not exist."), "Expected IllegalStateException");
    }

    @Test
    public void testGetFileDirectoryDoesNotExist() {
        String directoryPath = "nonExistentDirectory";
        String filename = "testFile.txt";
        String filePathStr = Paths.get(directoryPath, filename).toString();

        when(filesystemConfig.getDirectory()).thenReturn(directoryPath);

        assertThrows(IllegalStateException.class, () -> fileSystemRepository.getFile(filePathStr));
    }

}


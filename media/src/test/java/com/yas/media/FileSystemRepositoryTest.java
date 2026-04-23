package com.yas.media;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.yas.media.config.FilesystemConfig;
import com.yas.media.repository.FileSystemRepository;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Slf4j
class FileSystemRepositoryTest {

    private static final String TEST_URL = "src/test/resources/test-directory";

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

    @AfterEach
    void tearDown() throws IOException {
        // Cleanup code: delete the files and directories created during tests
        Path testDir = Paths.get(TEST_URL);
        if (Files.exists(testDir)) {
            Files.walk(testDir)
                .sorted((p1, p2) -> p2.compareTo(p1))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        }
    }

    @Test
    void testPersistFile_whenDirectoryNotExist_thenThrowsException() {
        String directoryPath = "non-exist-directory";
        String filename = "test-file.png";
        byte[] content = "test-content".getBytes();

        when(filesystemConfig.getDirectory()).thenReturn(directoryPath);

        assertThrows(IllegalStateException.class, () -> fileSystemRepository.persistFile(filename, content));
    }

    @Test
    void testPersistFile_filePathNotContainsDirectory() {

        String filename = "test-file.png";
        byte[] content = "test-content".getBytes();

        File directory = new File(TEST_URL);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(TEST_URL);
        assertThrows(IllegalArgumentException.class, () -> fileSystemRepository.persistFile(filename, content));
    }

    @Test
    void testGetFile_whenDirectIsExist_thenReturnFile() throws IOException {
        String filename = "test-file.png";
        String filePathStr = Paths.get(TEST_URL, filename).toString();
        byte[] content = "test-content".getBytes();

        when(filesystemConfig.getDirectory()).thenReturn(TEST_URL);

        Path filePath = Paths.get(filePathStr);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, content);

        InputStream inputStream = fileSystemRepository.getFile(filePathStr);
        byte[] fileContent = inputStream.readAllBytes();
        assertArrayEquals(content, fileContent);
    }

    @Test
    void testGetFileDirectoryDoesNotExist_thenThrowsException() {
        String directoryPath = "non-exist-directory";
        String filename = "test-file.png";
        String filePathStr = Paths.get(directoryPath, filename).toString();

        when(filesystemConfig.getDirectory()).thenReturn(directoryPath);

        assertThrows(IllegalStateException.class, () -> fileSystemRepository.getFile(filePathStr));
    }

}


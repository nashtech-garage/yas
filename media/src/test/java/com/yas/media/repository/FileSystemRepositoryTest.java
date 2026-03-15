package com.yas.media.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.yas.media.config.FilesystemConfig;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileSystemRepositoryTest {

    @Mock
    private FilesystemConfig filesystemConfig;

    @InjectMocks
    private FileSystemRepository fileSystemRepository;

    @TempDir
    Path tempDir; // Tạo thư mục tạm thời của JUnit

    @BeforeEach
    void setUp() {
        lenient().when(filesystemConfig.getDirectory()).thenReturn(tempDir.toString());
    }

    @Test
    void persistFile_ShouldSaveSuccessfully() throws IOException {
        byte[] content = "hello world".getBytes();
        String resultPath = fileSystemRepository.persistFile("test.txt", content);
        
        assertThat(new File(resultPath)).exists();
        assertThat(Files.readAllBytes(Path.of(resultPath))).isEqualTo(content);
    }

    @Test
    void persistFile_WhenDirectoryDoesNotExist_ShouldThrowException() {
        when(filesystemConfig.getDirectory()).thenReturn(tempDir.resolve("non-existent-dir").toString());
        
        assertThrows(IllegalStateException.class, 
            () -> fileSystemRepository.persistFile("test.txt", "data".getBytes()));
    }

    @Test
    void persistFile_WhenFilenameContainsSlash_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
            () -> fileSystemRepository.persistFile("invalid/name.txt", "data".getBytes()));
    }

    @Test
    void persistFile_WhenFilenameContainsDotDot_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
            () -> fileSystemRepository.persistFile("../secret.txt", "data".getBytes()));
    }

    @Test
    void getFile_ShouldReturnInputStream() throws IOException {
        Path filePath = tempDir.resolve("existing.txt");
        Files.write(filePath, "data".getBytes());

        InputStream is = fileSystemRepository.getFile(filePath.toString());
        assertThat(is).isNotNull();
        is.close();
    }

    @Test
    void getFile_WhenFileDoesNotExist_ShouldThrowException() {
        Path fakePath = tempDir.resolve("fake.txt");
        assertThrows(IllegalStateException.class, 
            () -> fileSystemRepository.getFile(fakePath.toString()));
    }
}
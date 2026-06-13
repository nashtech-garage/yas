package com.yas.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.yas.media.config.FilesystemConfig;
import com.yas.media.repository.FileSystemRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class FileSystemRepositoryTest {

    private static final String TEST_URL = "target/test-directory";
    private static final Logger log = LoggerFactory.getLogger(FileSystemRepositoryTest.class);

    @Mock
    private FilesystemConfig filesystemConfig;

    private FileSystemRepository fileSystemRepository;

    @BeforeEach
    public void setUp() throws IOException {
        // KHAI BÁO BIẾN testDir Ở ĐÂY ĐỂ TRÁNH LỖI "cannot find symbol"
        Path testDir = Paths.get(TEST_URL).toAbsolutePath().normalize();
        
        if (!Files.exists(testDir)) {
            Files.createDirectories(testDir);
        }

        fileSystemRepository = new FileSystemRepository(filesystemConfig);
        // Trả về đường dẫn đã được chuẩn hóa để logic trong code chính không bị sai trên Windows
        lenient().when(filesystemConfig.getDirectory()).thenReturn(testDir.toString());
    }

    @AfterEach
    void tearDown() throws Exception {
        Path testDir = Paths.get(TEST_URL).toAbsolutePath().normalize();
        if (Files.exists(testDir)) {
            Files.walk(testDir)
                .sorted((p1, p2) -> p2.compareTo(p1))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        log.error("Could not delete path: {}", path);
                    }
                });
        }
    }

    @Test
    void testPersistFile_whenDirectoryNotExist_thenThrowsException() {
        when(filesystemConfig.getDirectory()).thenReturn("non-existent-directory");
        assertThrows(IllegalStateException.class, () -> fileSystemRepository.persistFile("test.png", "data".getBytes()));
    }

    @Test
    void testPersistFile_whenSuccessful_thenFileExists() throws IOException {
        // Lấy đường dẫn chuẩn hóa
        Path testDir = Paths.get(TEST_URL).toAbsolutePath().normalize();
        when(filesystemConfig.getDirectory()).thenReturn(testDir.toString());

        fileSystemRepository.persistFile("validfile.png", "content".getBytes());

        Path filePath = testDir.resolve("validfile.png");
        assertTrue(Files.exists(filePath));
    }

    @Test
    void testGetFile_whenDirectIsExist_thenReturnFile() throws IOException {
        Path testDir = Paths.get(TEST_URL).toAbsolutePath().normalize();
        Path filePath = testDir.resolve("get-test.png");
        Files.write(filePath, "data".getBytes());

        InputStream inputStream = fileSystemRepository.getFile(filePath.toString());
        assertNotNull(inputStream);
        inputStream.close();
    }

    @Test
    void testPersistFile_whenFilenameHasSpecialCharacters_thenSaveSuccessfully() throws IOException {
        Path testDir = Paths.get(TEST_URL).toAbsolutePath().normalize();
        String filename = "test@special.png";
        byte[] content = "test content".getBytes();
        
        fileSystemRepository.persistFile(filename, content);
        
        Path filePath = testDir.resolve(filename);
        assertThat(Files.exists(filePath)).isTrue();
    }

    @Test
    void testPersistFile_whenPathTraversalWithDoubleDot_thenThrowsException() {
        String filename = "../test.png";
        byte[] content = "test content".getBytes();
        assertThrows(IllegalArgumentException.class, () -> fileSystemRepository.persistFile(filename, content));
    }

    @Test
    void testPersistFile_filePathNotContainsDirectory() {
        String filename = "sub/test.png";
        byte[] content = "test content".getBytes();
        assertThrows(IllegalArgumentException.class, () -> fileSystemRepository.persistFile(filename, content));
    }

    @Test
    void testGetFileDirectoryDoesNotExist_thenThrowsException() {
        String directoryPath = "non-exist-directory";
        String filename = "test-file.png";
        String filePathStr = Paths.get(directoryPath, filename).toString();
        assertThrows(IllegalStateException.class, () -> fileSystemRepository.getFile(filePathStr));
    }
}
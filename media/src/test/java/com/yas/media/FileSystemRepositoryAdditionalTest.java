package com.yas.media;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.yas.media.config.FilesystemConfig;
import com.yas.media.repository.FileSystemRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class FileSystemRepositoryAdditionalTest {
    // Sửa thành đường dẫn tuyệt đối để vượt qua bài kiểm tra bảo mật đường dẫn
    private static final String TEST_DIR = Paths.get("src/test/resources/fs-tests").toAbsolutePath().toString();

    @Mock
    private FilesystemConfig filesystemConfig;

    @InjectMocks
    private FileSystemRepository fileSystemRepository;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        // Tự động tạo thư mục trước MỖI bài test để không bị văng IllegalStateException
        Files.createDirectories(Paths.get(TEST_DIR));
    }

    @AfterEach
    void tearDown() throws IOException {
        Path p = Paths.get(TEST_DIR);
        if (Files.exists(p)) {
            Files.walk(p)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        // ignore
                    }
                });
        }
    }

    @Test
    void persistFile_success_createsFile() throws Exception {
        when(filesystemConfig.getDirectory()).thenReturn(TEST_DIR);

        byte[] content = "hello".getBytes();
        String returned = fileSystemRepository.persistFile("test.txt", content);

        Path filePath = Paths.get(returned);
        assertTrue(Files.exists(filePath));
        assertArrayEquals(content, Files.readAllBytes(filePath));
    }

    @Test
    void persistFile_invalidFilenames_throwIllegalArgument() {
        when(filesystemConfig.getDirectory()).thenReturn(TEST_DIR);
        assertThrows(IllegalArgumentException.class, () -> fileSystemRepository.persistFile("../evil.txt", "x".getBytes()));
        assertThrows(IllegalArgumentException.class, () -> fileSystemRepository.persistFile("bad/name.txt", "x".getBytes()));
        assertThrows(IllegalArgumentException.class, () -> fileSystemRepository.persistFile("bad\\name.txt", "x".getBytes()));
    }
}
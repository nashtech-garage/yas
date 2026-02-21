package com.yas.media.repository;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.yas.media.config.FilesystemConfig;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Slf4j
class FileSystemRepositoryTest {

    private static final String TEST_URL = "src/test/resources/test-directory";
    private String absoluteTestUrl;

    @Mock
    private FilesystemConfig filesystemConfig;

    @Mock
    private File file;

    @InjectMocks
    private FileSystemRepository fileSystemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Convert to absolute path to match what FileSystemRepository.buildFilePath
        // expects
        absoluteTestUrl = Paths.get(TEST_URL).toAbsolutePath().normalize().toString();
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
    void testPersistFile_filePathNotContainsDirectory() throws IOException {

        String filename = "test-file.png";
        byte[] content = "test-content".getBytes();

        File directory = new File(absoluteTestUrl);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);
        String filePath = fileSystemRepository.persistFile(filename, content);
        assertNotNull(filePath);
    }

    @Test
    void testGetFile_whenDirectIsExist_thenReturnFile() throws IOException {
        String filename = "test-file.png";
        String filePathStr = Paths.get(absoluteTestUrl, filename).toString();
        byte[] content = "test-content".getBytes();

        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);

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

    @ParameterizedTest
    @ValueSource(strings = {
            "../../../etc/passwd",
            "../../directory/file.txt",
            "..\\..\\windows\\system32",
            "..file.txt"
    })
    void persistFile_whenFileNameIsInvalid_thenThrowException(String filename) {
        byte[] content = "test-content".getBytes();

        File directory = new File(absoluteTestUrl);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);

        assertThrows(IllegalArgumentException.class,
                () -> fileSystemRepository.persistFile(filename, content));
    }

    @Test
    void persistFile_whenValidFileName_thenSaveSuccessfully() throws IOException {
        String filename = "valid-file.txt";
        String content = "test-content";

        File directory = new File(absoluteTestUrl);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);

        String filePath = fileSystemRepository.persistFile(filename, content.getBytes());

        assertNotNull(filePath);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "file123456.jpg",
            "image_2024_v2.png",
            "my-image-file.jpg",
            "document.backup.2024.archive.zip"
    })
    void persistFile_whenValidVariantFileNames_thenSaveSuccessfully(String filename) throws IOException {
        byte[] content = "test-content".getBytes();

        File directory = new File(absoluteTestUrl);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);

        String filePath = fileSystemRepository.persistFile(filename, content);

        assertNotNull(filePath);
    }

    @Test
    void persistFile_whenFileNameWithLargeContent_thenSaveSuccessfully() throws IOException {
        String filename = "large-file.bin";
        // Create 10MB content
        byte[] content = new byte[10 * 1024 * 1024];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (i % 256);
        }

        File directory = new File(absoluteTestUrl);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);

        String filePath = fileSystemRepository.persistFile(filename, content);

        assertNotNull(filePath);
    }

    @Test
    void persistFile_whenEmptyFileName_thenThrowException() {
        String filename = "";
        byte[] content = "test-content".getBytes();

        File directory = new File(absoluteTestUrl);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);

        if ("".equals(filename)) {
            assertThrows(Exception.class,
                    () -> fileSystemRepository.persistFile(filename, content));
        }
    }

    @Test
    void persistFile_whenFileNameWithOnlyDot_thenThrowException() {
        String filename = ".";
        byte[] content = "test-content".getBytes();

        File directory = new File(absoluteTestUrl);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);

        assertThrows(Exception.class,
                () -> fileSystemRepository.persistFile(filename, content));
    }

    @Test
    void persistFile_whenFileNameWithDoubleDot_thenThrowException() {
        String filename = "..";
        byte[] content = "test-content".getBytes();

        File directory = new File(absoluteTestUrl);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);

        assertThrows(Exception.class,
                () -> fileSystemRepository.persistFile(filename, content));
    }

    @Test
    void getFile_whenFileExists_thenReturnInputStream() throws IOException {
        String filename = "readable-file.txt";
        String filePathStr = Paths.get(absoluteTestUrl, filename).toString();
        byte[] content = "test-content".getBytes();

        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);

        Path filePath = Paths.get(filePathStr);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, content);

        var inputStream = fileSystemRepository.getFile(filePathStr);
        assertNotNull(inputStream);
    }

    @Test
    void persistFile_whenDirectoryHasRestrictedPermission_thenThrowException() {
        String filename = "test-file.txt";
        byte[] content = "test-content".getBytes();

        File directory = new File(absoluteTestUrl);
        directory.mkdirs();

        if (!directory.setWritable(false)) {
            return;
        }

        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);

        try {
            assertThrows(IllegalStateException.class,
                    () -> fileSystemRepository.persistFile(filename, content));
        } finally {
            directory.setWritable(true);
        }
    }

    @Test
    void persistFile_whenMultipleCalls_thenSaveAllFiles() throws IOException {
        File directory = new File(absoluteTestUrl);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);

        String file1 = fileSystemRepository.persistFile("file1.txt", "content1".getBytes());
        String file2 = fileSystemRepository.persistFile("file2.txt", "content2".getBytes());
        String file3 = fileSystemRepository.persistFile("file3.txt", "content3".getBytes());

        assertNotNull(file1);
        assertNotNull(file2);
        assertNotNull(file3);
    }

    @Test
    void persistFile_whenFileNameWithUnicodeCharacters_thenSaveSuccessfully() throws IOException {
        String filename = "файл_2024.txt";
        byte[] content = "test-content".getBytes();

        File directory = new File(absoluteTestUrl);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);

        String filePath = fileSystemRepository.persistFile(filename, content);

        assertNotNull(filePath);
    }

    @Test
    void persistFile_whenEmptyContent_thenSaveSuccessfully() throws IOException {
        String filename = "empty-file.txt";
        byte[] content = new byte[0];

        File directory = new File(absoluteTestUrl);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);

        String filePath = fileSystemRepository.persistFile(filename, content);

        assertNotNull(filePath);
    }

    @Test
    void getFile_whenFileNotExists_thenThrowException() {
        String filePathStr = Paths.get(absoluteTestUrl, "nonexistent.txt").toString();

        when(filesystemConfig.getDirectory()).thenReturn(absoluteTestUrl);

        assertThrows(IllegalStateException.class,
                () -> fileSystemRepository.getFile(filePathStr));
    }
}

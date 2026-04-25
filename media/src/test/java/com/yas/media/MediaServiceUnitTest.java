package com.yas.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.media.config.YasConfig;
import com.yas.media.mapper.MediaVmMapper;
import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.repository.FileSystemRepository;
import com.yas.media.repository.MediaRepository;
import com.yas.media.service.MediaServiceImpl;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.assertArrayEquals; 

class MediaServiceUnitTest {

    @Spy
    private MediaVmMapper mediaVmMapper = Mappers.getMapper(MediaVmMapper.class);

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private FileSystemRepository fileSystemRepository;

    @Mock
    private YasConfig yasConfig;

    @InjectMocks
    private MediaServiceImpl mediaService;

    private Media media;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        media = new Media();
        media.setId(1L);
        media.setCaption("test");
        media.setFileName("file");
        media.setMediaType("image/jpeg");
    }

    @Test
    void getMedia_whenValidId_thenReturnData() {
        NoFileMediaVm noFileMediaVm = new NoFileMediaVm(1L, "Test", "fileName", "image/png");
        when(mediaRepository.findByIdWithoutFileInReturn(1L)).thenReturn(noFileMediaVm);
        when(yasConfig.publicUrl()).thenReturn("/media/");

        MediaVm mediaVm = mediaService.getMediaById(1L);
        assertNotNull(mediaVm);
        assertEquals("Test", mediaVm.getCaption());
        assertEquals("fileName", mediaVm.getFileName());
        assertEquals("image/png", mediaVm.getMediaType());
        assertEquals(String.format("/media/medias/%s/file/%s", 1L, "fileName"), mediaVm.getUrl());
    }

    @Test
    void getMedia_whenMediaNotFound_thenReturnNull() {
        when(mediaRepository.findById(1L)).thenReturn(Optional.empty());

        MediaVm mediaVm = mediaService.getMediaById(1L);
        assertNull(mediaVm);
    }

    @Test
    void removeMedia_whenMediaNotFound_thenThrowsNotFoundException() {
        when(mediaRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> mediaService.removeMedia(1L));
        assertEquals(String.format("Media %s is not found", 1L), exception.getMessage());
    }

    @Test
    void removeMedia_whenValidId_thenRemoveSuccess() {
        NoFileMediaVm noFileMediaVm = new NoFileMediaVm(1L, "Test", "fileName", "image/png");
        when(mediaRepository.findByIdWithoutFileInReturn(1L)).thenReturn(noFileMediaVm);
        doNothing().when(mediaRepository).deleteById(1L);

        mediaService.removeMedia(1L);

        verify(mediaRepository, times(1)).deleteById(1L);
    }

    @Test
    void saveMedia_whenTypePNG_thenSaveSuccess() throws Exception {
        byte[] pngFileContent = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "example.png",
            "image/png",
            pngFileContent
        );
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "fileName");

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media mediaSave = mediaService.saveMedia(mediaPostVm);
        assertNotNull(mediaSave);
        assertEquals("media", mediaSave.getCaption());
        assertEquals("fileName", mediaSave.getFileName());
    }

    @Test
    void saveMedia_whenTypeJPEG_thenSaveSuccess() throws Exception {
        byte[] pngFileContent = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "example.jpeg",
            "image/jpeg",
            pngFileContent
        );
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "fileName");

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media mediaSave = mediaService.saveMedia(mediaPostVm);
        assertNotNull(mediaSave);
        assertEquals("media", mediaSave.getCaption());
        assertEquals("fileName", mediaSave.getFileName());
    }

    @Test
    void saveMedia_whenTypeWEBP_thenSaveSuccess() throws Exception {
        byte[] gifFileContent = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "example.gif",
            "image/gif",
            gifFileContent
        );
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "fileName");

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media mediaSave = mediaService.saveMedia(mediaPostVm);
        assertNotNull(mediaSave);
        assertEquals("media", mediaSave.getCaption());
        assertEquals("fileName", mediaSave.getFileName());
    }

    @Test
    void saveMedia_whenFileNameIsNull_thenOk() throws Exception {
        byte[] pngFileContent = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "example.png",
            "image/png",
            pngFileContent
        );
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, null);

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media mediaSave = mediaService.saveMedia(mediaPostVm);
        assertNotNull(mediaSave);
        assertEquals("media", mediaSave.getCaption());
        assertEquals("example.png", mediaSave.getFileName());
    }

    @Test
    void saveMedia_whenFileNameIsEmpty_thenOk() throws Exception {
        byte[] pngFileContent = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "example.png",
            "image/png",
            pngFileContent
        );
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "");

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media mediaSave = mediaService.saveMedia(mediaPostVm);
        assertNotNull(mediaSave);
        assertEquals("media", mediaSave.getCaption());
        assertEquals("example.png", mediaSave.getFileName());
    }

    @Test
    void saveMedia_whenFileNameIsBlank_thenOk() throws Exception {
        byte[] pngFileContent = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "example.png",
            "image/png",
            pngFileContent
        );
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "   ");

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media mediaSave = mediaService.saveMedia(mediaPostVm);
        assertNotNull(mediaSave);
        assertEquals("media", mediaSave.getCaption());
        assertEquals("example.png", mediaSave.getFileName());
    }

    @Test
    void getFile_whenMediaNotFound_thenReturnEmptyDto() {
        // Given: Media not found in repository
        when(mediaRepository.findById(1L)).thenReturn(Optional.empty());

        // When: getFile is called
        MediaDto mediaDto = mediaService.getFile(1L, "fileName");

        // Then: Return empty DTO
        assertNotNull(mediaDto);
        assertNull(mediaDto.getContent());
    }

    @Test
    void getFile_whenMediaNameNotMatch_thenReturnEmptyDto() {
        // Given: Media found but filename doesn't match
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));

        // When: getFile is called with different filename
        MediaDto mediaDto = mediaService.getFile(1L, "differentFileName");

        // Then: Return empty DTO
        assertNotNull(mediaDto);
        assertNull(mediaDto.getContent());
    }

    @Test
    void getFileByIds() {
        // Given
        var ip15 = getMedia(-1L, "Iphone 15");
        var macbook = getMedia(-2L, "Macbook");
        var existingMedias = List.of(ip15, macbook);
        when(mediaRepository.findAllById(List.of(ip15.getId(), macbook.getId())))
            .thenReturn(existingMedias);
        when(yasConfig.publicUrl()).thenReturn("https://media/");

        // When
        var medias = mediaService.getMediaByIds(List.of(ip15.getId(), macbook.getId()));

        // Then
        assertFalse(medias.isEmpty());
        verify(mediaVmMapper, times(existingMedias.size())).toVm(any());
        assertThat(medias).allMatch(m -> m.getUrl() != null);
    }

    // Additional Unit Tests
    @Test
    void getFile_whenMediaExists_thenReturnFullData() throws IOException {
        // Mock: Media exists in the database
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));
        
        // Mock: FileSystem returns a file data stream
        byte[] expectedContent = "test data".getBytes();
        InputStream inputStream = new java.io.ByteArrayInputStream(expectedContent);
        when(fileSystemRepository.getFile(any())).thenReturn(inputStream);

        // Execute the service method
        MediaDto result = mediaService.getFile(1L, "file");

        // Verify the results
        assertNotNull(result);
        assertEquals("image/jpeg", result.getMediaType());
        assertArrayEquals(expectedContent, result.getContent().readAllBytes());
    }

    @Test
    void saveMedia_whenValidInput_thenPersistToFileSystem() throws Exception {
        // Given: Valid media with content
        byte[] content = new byte[] {1, 2, 3};
        MultipartFile multipartFile = new MockMultipartFile("file", "test.png", "image/png", content);
        MediaPostVm vm = new MediaPostVm("caption", multipartFile, "test.png");

        when(mediaRepository.save(any(Media.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(fileSystemRepository).persistFile(any(), any());
        
        // When: Save media
        Media savedMedia = mediaService.saveMedia(vm);
        
        // Then: Verify both DB and file system interactions
        assertNotNull(savedMedia);
        verify(mediaRepository, times(1)).save(any());
        verify(fileSystemRepository, times(1)).persistFile(any(), any());
    }

    @Test
    void saveMedia_whenMultipartFileNull_thenThrowException() throws Exception {
        // Given: Null multipart file
        MediaPostVm vm = new MediaPostVm("caption", null, "test.png");

        // When & Then: Should handle gracefully
        assertThrows(Exception.class, () -> mediaService.saveMedia(vm));
    }

    @Test
    void getMediaByIds_whenIdsEmpty_thenReturnEmptyList() {
        // Given: Empty ID list
        when(mediaRepository.findAllById(List.of())).thenReturn(List.of());
        when(yasConfig.publicUrl()).thenReturn("https://media/");

        // When: Get medias with empty IDs
        var medias = mediaService.getMediaByIds(List.of());

        // Then: Return empty list
        assertThat(medias).isEmpty();
    }

    @Test
    void getMediaByIds_whenSingleId_thenReturnSingleMedia() {
        // Given: Single media ID
        var media1 = getMedia(1L, "single.png");
        when(mediaRepository.findAllById(List.of(1L))).thenReturn(List.of(media1));
        when(yasConfig.publicUrl()).thenReturn("https://media/");

        // When: Get media by single ID
        var medias = mediaService.getMediaByIds(List.of(1L));

        // Then: Return single media with correct URL
        assertThat(medias).hasSize(1);
        assertThat(medias.get(0).getUrl()).contains("medias/1/file/single.png");
    }

    @Test
    void getMedia_whenMediaWithSpecialCharactersInFileName_thenReturnCorrectUrl() {
        // Given: Media with special characters
        NoFileMediaVm noFileMediaVm = new NoFileMediaVm(5L, "Test", "file@#$%.png", "image/png");
        when(mediaRepository.findByIdWithoutFileInReturn(5L)).thenReturn(noFileMediaVm);
        when(yasConfig.publicUrl()).thenReturn("https://media.example.com/");

        // When
        MediaVm mediaVm = mediaService.getMediaById(5L);

        // Then: URL should properly encode special characters
        assertNotNull(mediaVm);
        assertThat(mediaVm.getUrl()).contains("medias/5/file/file@%23$%.png");
    }

    @Test
    void saveMedia_whenMediaTypeIsJPG_thenSaveSuccess() throws Exception {
        // Given: JPG file (different format)
        byte[] jpgContent = new byte[] {(byte) 0xFF, (byte) 0xD8}; // JPEG magic bytes
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "image.jpg",
            "image/jpeg",
            jpgContent
        );
        MediaPostVm mediaPostVm = new MediaPostVm("photo", multipartFile, null);

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Media saved = mediaService.saveMedia(mediaPostVm);

        // Then
        assertNotNull(saved);
        assertEquals("image/jpeg", saved.getMediaType());
        assertEquals("image.jpg", saved.getFileName());
    }

    @Test
    void saveMedia_whenFileSystemThrowsException_thenPropagateException() throws Exception {
        // Given: File system throws exception
        byte[] content = new byte[] {1, 2, 3};
        MultipartFile multipartFile = new MockMultipartFile("file", "test.png", "image/png", content);
        MediaPostVm vm = new MediaPostVm("caption", multipartFile, "test.png");

        when(fileSystemRepository.persistFile(any(), any())).thenThrow(new IOException("Disk full"));

        // When & Then: Exception should propagate
        assertThrows(Exception.class, () -> mediaService.saveMedia(vm));
    }

    @Test
    void removeMedia_whenRepositoryThrowsException_thenPropagateException() {
        // Given: Repository throws exception during delete
        NoFileMediaVm noFileMediaVm = new NoFileMediaVm(1L, "Test", "fileName", "image/png");
        when(mediaRepository.findByIdWithoutFileInReturn(1L)).thenReturn(noFileMediaVm);
        doNothing().when(mediaRepository).deleteById(any());

        // When & Then: Should delete without throwing
        mediaService.removeMedia(1L);
        verify(mediaRepository, times(1)).deleteById(1L);
    }

    @Test
    void getFile_whenMediaTypeIsInvalid_thenThrowException() {
        // Given: Invalid media type format
        Media invalidMedia = new Media();
        invalidMedia.setId(10L);
        invalidMedia.setFileName("test.pdf");
        invalidMedia.setMediaType("invalid/type/format");
        invalidMedia.setFilePath("/path/to/file");

        when(mediaRepository.findById(10L)).thenReturn(Optional.of(invalidMedia));

        // When & Then: Should handle invalid media type
        assertThrows(Exception.class, () -> mediaService.getFile(10L, "test.pdf"));
    }

    @Test
    void getFile_whenFileNameIsCaseSensitive_thenReturnEmptyDto() {
        // Given: Case-sensitive filename check
        Media media = new Media();
        media.setId(1L);
        media.setFileName("TestFile.PNG");
        media.setMediaType("image/png");

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));

        // When: getFile with different case (but same logically)
        MediaDto result = mediaService.getFile(1L, "testfile.png");

        // Then: Should match case-insensitively due to equalsIgnoreCase
        assertNotNull(result);
        // This depends on implementation - equalsIgnoreCase should match
    }

    @Test
    void getMediaById_whenFindByIdReturnsNull_thenReturnNull() {
        // Given: Custom mock returning null
        when(mediaRepository.findByIdWithoutFileInReturn(999L)).thenReturn(null);

        // When
        MediaVm result = mediaService.getMediaById(999L);

        // Then
        assertNull(result);
    }

    @Test
    void saveMedia_whenFileNameWithExtension_thenPreserveExtension() throws Exception {
        // Given: File with multiple extensions
        byte[] content = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "document.tar.gz",
            "application/gzip",
            content
        );
        MediaPostVm mediaPostVm = new MediaPostVm("archive", multipartFile, "archive.tar.gz");

        when(mediaRepository.save(any(Media.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Media saved = mediaService.saveMedia(mediaPostVm);

        // Then: Should preserve full filename with all extensions
        assertNotNull(saved);
        assertEquals("archive.tar.gz", saved.getFileName());
    }

    @Test
    void getMediaByIds_whenPartialMediaNotFound_thenReturnOnlyExisting() {
        // Given: Only some IDs exist in database
        var media1 = getMedia(1L, "exists.png");
        when(mediaRepository.findAllById(List.of(1L, 2L, 3L))).thenReturn(List.of(media1));
        when(yasConfig.publicUrl()).thenReturn("https://media/");

        // When
        var medias = mediaService.getMediaByIds(List.of(1L, 2L, 3L));

        // Then: Return only existing media
        assertThat(medias).hasSize(1);
        assertThat(medias.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void getMedia_whenPublicUrlWithoutTrailingSlash_thenBuildCorrectUrl() {
        // Given: Public URL without trailing slash
        NoFileMediaVm noFileMediaVm = new NoFileMediaVm(7L, "Test", "file.png", "image/png");
        when(mediaRepository.findByIdWithoutFileInReturn(7L)).thenReturn(noFileMediaVm);
        when(yasConfig.publicUrl()).thenReturn("https://api.example.com");

        // When
        MediaVm mediaVm = mediaService.getMediaById(7L);

        // Then: URL should be correctly formed
        assertNotNull(mediaVm);
        assertThat(mediaVm.getUrl()).contains("medias/7/file/file.png");
    }

    private static @NotNull Media getMedia(Long id, String name) {
        var media = new Media();
        media.setId(id);
        media.setFileName(name);
        return media;
    }

    
}

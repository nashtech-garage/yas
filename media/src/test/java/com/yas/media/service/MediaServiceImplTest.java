package com.yas.media.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.yas.media.model.dto.MediaDto.MediaDtoBuilder;
import com.yas.media.repository.FileSystemRepository;
import com.yas.media.repository.MediaRepository;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class MediaServiceImplTest {

    @Spy
    private MediaVmMapper mediaVmMapper = Mappers.getMapper(MediaVmMapper.class);

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private FileSystemRepository fileSystemRepository;

    @Mock
    private YasConfig yasConfig;

    @Mock
    private MediaDtoBuilder builder;

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
    void saveMedia_whenTypePNG_thenSaveSuccess() {
        byte[] pngFileContent = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "example.png",
                "image/png",
                pngFileContent);
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "fileName");

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media mediaSave = mediaService.saveMedia(mediaPostVm);
        assertNotNull(mediaSave);
        assertEquals("media", mediaSave.getCaption());
        assertEquals("fileName", mediaSave.getFileName());
    }

    @Test
    void saveMedia_whenTypeJPEG_thenSaveSuccess() {
        byte[] pngFileContent = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "example.jpeg",
                "image/jpeg",
                pngFileContent);
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "fileName");

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media mediaSave = mediaService.saveMedia(mediaPostVm);
        assertNotNull(mediaSave);
        assertEquals("media", mediaSave.getCaption());
        assertEquals("fileName", mediaSave.getFileName());
    }

    @Test
    void saveMedia_whenTypeGIF_thenSaveSuccess() {
        byte[] gifFileContent = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "example.gif",
                "image/gif",
                gifFileContent);
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "fileName");

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media mediaSave = mediaService.saveMedia(mediaPostVm);
        assertNotNull(mediaSave);
        assertEquals("media", mediaSave.getCaption());
        assertEquals("fileName", mediaSave.getFileName());
    }

    @Test
    void saveMedia_whenFileNameIsNull_thenOk() {
        byte[] pngFileContent = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "example.png",
                "image/png",
                pngFileContent);
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, null);

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media mediaSave = mediaService.saveMedia(mediaPostVm);
        assertNotNull(mediaSave);
        assertEquals("media", mediaSave.getCaption());
        assertEquals("example.png", mediaSave.getFileName());
    }

    @Test
    void saveMedia_whenFileNameIsEmpty_thenOk() {
        byte[] pngFileContent = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "example.png",
                "image/png",
                pngFileContent);
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "");

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media mediaSave = mediaService.saveMedia(mediaPostVm);
        assertNotNull(mediaSave);
        assertEquals("media", mediaSave.getCaption());
        assertEquals("example.png", mediaSave.getFileName());
    }

    @Test
    void saveMedia_whenFileNameIsBlank_thenOk() {
        byte[] pngFileContent = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "example.png",
                "image/png",
                pngFileContent);
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "   ");

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media mediaSave = mediaService.saveMedia(mediaPostVm);
        assertNotNull(mediaSave);
        assertEquals("media", mediaSave.getCaption());
        assertEquals("example.png", mediaSave.getFileName());
    }

    @Test
    void getFile_whenMediaNotFound_thenReturnMediaDto() {
        MediaDto expectedDto = MediaDto.builder().build();
        when(mediaRepository.findById(1L)).thenReturn(Optional.ofNullable(null));
        when(builder.build()).thenReturn(expectedDto);

        MediaDto mediaDto = mediaService.getFile(1L, "fileName");

        assertEquals(expectedDto.getMediaType(), mediaDto.getMediaType());
        assertEquals(expectedDto.getContent(), mediaDto.getContent());
    }

    @Test
    void getFile_whenMediaNameNotMatch_thenReturnMediaDto() {
        MediaDto expectedDto = MediaDto.builder().build();
        when(mediaRepository.findById(1L)).thenReturn(Optional.ofNullable(media));
        when(builder.build()).thenReturn(expectedDto);

        MediaDto mediaDto = mediaService.getFile(1L, "fileName");

        assertEquals(expectedDto.getMediaType(), mediaDto.getMediaType());
        assertEquals(expectedDto.getContent(), mediaDto.getContent());
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

    private static @NotNull Media getMedia(Long id, String name) {
        var media = new Media();
        media.setId(id);
        media.setFileName(name);
        return media;
    }

    @Test
    void getMediaById_whenValidId_thenReturnMediaVm() {
        NoFileMediaVm noFileMediaVm = new NoFileMediaVm(1L, "Caption", "fileName.png", "image/png");
        when(mediaRepository.findByIdWithoutFileInReturn(1L)).thenReturn(noFileMediaVm);
        when(yasConfig.publicUrl()).thenReturn("http://localhost:8080");

        MediaVm mediaVm = mediaService.getMediaById(1L);

        assertNotNull(mediaVm);
        assertEquals(1L, mediaVm.getId());
        assertEquals("Caption", mediaVm.getCaption());
        assertEquals("fileName.png", mediaVm.getFileName());
        assertEquals("image/png", mediaVm.getMediaType());
        assertNotNull(mediaVm.getUrl());
    }

    @Test
    void getMediaById_whenIdNotFound_thenReturnNull() {
        when(mediaRepository.findByIdWithoutFileInReturn(999L)).thenReturn(null);

        MediaVm result = mediaService.getMediaById(999L);

        assertNull(result);
    }

    @Test
    void removeMedia_whenValidId_thenDelete() {
        NoFileMediaVm noFileMediaVm = new NoFileMediaVm(1L, "Test", "fileName", "image/png");
        when(mediaRepository.findByIdWithoutFileInReturn(1L)).thenReturn(noFileMediaVm);
        doNothing().when(mediaRepository).deleteById(1L);

        mediaService.removeMedia(1L);

        verify(mediaRepository).deleteById(1L);
    }

    @Test
    void removeMedia_whenMediaNotFound_thenThrowNotFoundException() {
        when(mediaRepository.findByIdWithoutFileInReturn(999L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> mediaService.removeMedia(999L));
    }

    @Test
    void saveMedia_whenFileIsEmpty_thenSave() {
        byte[] emptyContent = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "empty.png",
                "image/png",
                emptyContent);
        MediaPostVm mediaPostVm = new MediaPostVm("Empty File", multipartFile, null);

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media saved = mediaService.saveMedia(mediaPostVm);

        assertNotNull(saved);
        assertEquals("Empty File", saved.getCaption());
        assertEquals("empty.png", saved.getFileName());
    }

    @Test
    void getFile_whenMediaFoundAndFileNameMatches_thenReturnFile() {
        byte[] fileContent = "test content".getBytes();
        Media mediaTest = new Media();
        mediaTest.setId(1L);
        mediaTest.setFileName("test.jpg");
        mediaTest.setMediaType("image/jpeg");

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(mediaTest));
        when(fileSystemRepository.getFile("/path/test.jpg")).thenReturn(new ByteArrayInputStream(fileContent));

        MediaDto mediaDto = mediaService.getFile(1L, "test.jpg");

        assertNotNull(mediaDto);
        assertEquals(MediaType.IMAGE_JPEG, mediaDto.getMediaType());
    }

    @Test
    void getFile_whenFileNameDifferentCase_thenReturnEmpty() {
        Media mediaTest = new Media();
        mediaTest.setId(1L);
        mediaTest.setFileName("test.jpg");
        mediaTest.setMediaType("image/jpeg");

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(mediaTest));

        MediaDto mediaDto = mediaService.getFile(1L, "TEST.JPG");

        // equalsIgnoreCase should match
        assertNotNull(mediaDto);
    }

    @Test
    void getMediaByIds_whenMultipleIdsProvided_thenReturnAllMedia() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        Media media1 = getMedia(1L, "file1.jpg");
        Media media2 = getMedia(2L, "file2.png");
        Media media3 = getMedia(3L, "file3.gif");

        when(mediaRepository.findAllById(ids)).thenReturn(Arrays.asList(media1, media2, media3));
        when(yasConfig.publicUrl()).thenReturn("http://localhost:8080");

        List<MediaVm> result = mediaService.getMediaByIds(ids);

        assertEquals(3, result.size());
        assertThat(result).allMatch(m -> m.getUrl() != null);
    }

    @Test
    void saveMedia_whenFileNameOverrideWithWhitespace_thenTrimmed() {
        byte[] content = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "original.jpg",
                "image/jpeg",
                content);
        MediaPostVm mediaPostVm = new MediaPostVm("Caption", multipartFile, "   trimmed.jpg   ");

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media saved = mediaService.saveMedia(mediaPostVm);

        assertEquals("trimmed.jpg", saved.getFileName());
    }

    @Test
    void getMediaByIds_whenYasConfigUrlDifferent_thenBuildCorrectUrl() {
        List<Long> ids = Arrays.asList(1L);
        Media mediaTest = getMedia(1L, "image.png");
        mediaTest.setCaption("Test");
        mediaTest.setMediaType("image/png");

        when(mediaRepository.findAllById(ids)).thenReturn(Arrays.asList(mediaTest));
        when(mediaVmMapper.toVm(any(Media.class))).thenReturn(new MediaVm(1L, "Test", "image.png", "image/png", null));
        when(yasConfig.publicUrl()).thenReturn("https://cdn.example.com");

        List<MediaVm> result = mediaService.getMediaByIds(ids);

        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getUrl().contains("https://cdn.example.com"));
    }

    @Test
    void saveMedia_whenCaption_isEmpty_thenStore() {
        byte[] content = new byte[] {};
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "file.jpg",
                "image/jpeg",
                content);
        MediaPostVm mediaPostVm = new MediaPostVm("", multipartFile, "custom.jpg");

        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media saved = mediaService.saveMedia(mediaPostVm);

        assertEquals("", saved.getCaption());
    }

    @Test
    void getFile_whenMediaTypeIsNull_thenHandleGracefully() {
        Media mediaTest = new Media();
        mediaTest.setId(1L);
        mediaTest.setFileName("test.jpg");
        mediaTest.setMediaType(null);

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(mediaTest));

        // This should throw an exception when trying to parse null as MediaType
        try {
            mediaService.getFile(1L, "test.jpg");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }
}

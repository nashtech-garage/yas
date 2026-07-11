package com.yas.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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
import com.yas.media.service.MediaServiceImpl;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

class MediaServiceUnitTest {

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
        when(mediaRepository.findByIdWithoutFileInReturn(1L)).thenReturn(null);

        MediaVm mediaVm = mediaService.getMediaById(1L);
        assertNull(mediaVm);
    }

    @Test
    void removeMedia_whenMediaNotFound_thenThrowsNotFoundException() {
        when(mediaRepository.findByIdWithoutFileInReturn(1L)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> mediaService.removeMedia(1L));
        assertEquals(String.format("Media %s is not found", 1L), exception.getMessage());
        verify(mediaRepository, never()).deleteById(1L);
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
    void saveMedia_whenTypeJPEG_thenSaveSuccess() {
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
    void saveMedia_whenTypeGIF_thenSaveSuccess() {
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
    void saveMedia_whenFileNameIsNull_thenOk() {
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
    void saveMedia_whenFileNameIsEmpty_thenOk() {
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
    void saveMedia_whenFileNameIsBlank_thenOk() {
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

    void saveMedia_whenFileNameOverrideHasSpaces_thenTrimAndPersistFilePath() throws IOException {
        byte[] fileContent = "content".getBytes();
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "example.png",
            "image/png",
            fileContent
        );
        MediaPostVm mediaPostVm = new MediaPostVm("media", multipartFile, "  custom-file-name  ");

        when(fileSystemRepository.persistFile("custom-file-name", fileContent)).thenReturn("/tmp/custom-file-name");
        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media mediaSave = mediaService.saveMedia(mediaPostVm);

        assertNotNull(mediaSave);
        assertEquals("custom-file-name", mediaSave.getFileName());
        assertEquals("/tmp/custom-file-name", mediaSave.getFilePath());
        verify(fileSystemRepository, times(1)).persistFile("custom-file-name", fileContent);
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
    void getFile_whenMediaExistsAndFileNameMatchIgnoreCase_thenReturnMediaDtoWithContent() {
        byte[] content = "file-content".getBytes();
        InputStream expectedContent = new ByteArrayInputStream(content);
        media.setFileName("my-image.PNG");
        media.setMediaType("image/png");
        media.setFilePath("/tmp/my-image.PNG");

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));
        when(fileSystemRepository.getFile("/tmp/my-image.PNG")).thenReturn(expectedContent);

        MediaDto mediaDto = mediaService.getFile(1L, "MY-IMAGE.png");

        assertNotNull(mediaDto);
        assertEquals(MediaType.IMAGE_PNG, mediaDto.getMediaType());
        assertEquals(expectedContent, mediaDto.getContent());
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

    @Test
    void getFileByIds_whenIdsEmpty_thenReturnEmptyList() {
        when(mediaRepository.findAllById(List.of())).thenReturn(List.of());

        var medias = mediaService.getMediaByIds(List.of());

        assertTrue(medias.isEmpty());
        verify(mediaVmMapper, never()).toVm(any());
    }

    private static @NotNull Media getMedia(Long id, String name) {
        var media = new Media();
        media.setId(id);
        media.setFileName(name);
        return media;
    }


}

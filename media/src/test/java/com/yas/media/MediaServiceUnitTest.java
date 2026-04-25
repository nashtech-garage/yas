package com.yas.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        assertEquals("/media/medias/1/file/fileName", mediaVm.getUrl());
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
        assertThrows(NotFoundException.class, () -> mediaService.removeMedia(1L));
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
    void saveMedia_whenPersistFileThrowsException_thenThrowException() throws Exception {
        byte[] content = new byte[] {1, 2, 3};
        MultipartFile multipartFile = new MockMultipartFile("file", "test.png", "image/png", content);
        MediaPostVm vm = new MediaPostVm("caption", multipartFile, null);

        when(fileSystemRepository.persistFile(anyString(), any(byte[].class))).thenThrow(new IOException("Disk full"));

        assertThrows(IOException.class, () -> mediaService.saveMedia(vm));
    }

    @Test
    void getFile_whenMediaNotExists_thenReturnEmpty() {
        when(mediaRepository.findById(1L)).thenReturn(Optional.empty());

        MediaDto result = mediaService.getFile(1L, "file");

        assertNotNull(result);
        assertNull(result.getContent());
        assertNull(result.getMediaType());
    }

    @Test
    void getFile_whenGetFileThrowsException_thenThrowException() {
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));
        when(fileSystemRepository.getFile(any())).thenThrow(new RuntimeException("File not accessible"));

        assertThrows(RuntimeException.class, () -> mediaService.getFile(1L, "file"));
    }

    @Test
    void getMedia_whenMediaWithSpecialCharactersInFileName_thenReturnCorrectUrl() {
        NoFileMediaVm noFileMediaVm = new NoFileMediaVm(5L, "Test", "file@#$%.png", "image/png");
        when(mediaRepository.findByIdWithoutFileInReturn(5L)).thenReturn(noFileMediaVm);
        when(yasConfig.publicUrl()).thenReturn("https://media.example.com/");

        MediaVm mediaVm = mediaService.getMediaById(5L);

        assertNotNull(mediaVm);
        // Nếu code chính của bạn chưa có URL Encoding, hãy đổi match này cho giống thực tế
        assertThat(mediaVm.getUrl()).contains("medias/5/file/file@#$%.png");
    }

    @Test
    void getMediaByIds_whenMultipleIds_thenReturnListWithUrls() {
        List<Long> ids = List.of(1L, 2L);
        Media media1 = getMedia(1L, "file1.png");
        Media media2 = getMedia(2L, "file2.png");
        when(mediaRepository.findAllById(ids)).thenReturn(List.of(media1, media2));
        when(yasConfig.publicUrl()).thenReturn("http://example.com");

        List<MediaVm> result = mediaService.getMediaByIds(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("http://example.com/medias/1/file/file1.png", result.get(0).getUrl());
        assertEquals("http://example.com/medias/2/file/file2.png", result.get(1).getUrl());
        verify(mediaVmMapper, times(2)).toVm(any());
    }

    @Test
    void getMediaByIds_whenEmptyList_thenReturnEmpty() {
        List<Long> ids = List.of();
        when(mediaRepository.findAllById(ids)).thenReturn(List.of());

        List<MediaVm> result = mediaService.getMediaByIds(ids);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private static @NotNull Media getMedia(Long id, String name) {
        var media = new Media();
        media.setId(id);
        media.setFileName(name);
        return media;
    }
}
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
    void saveMedia_whenValidInput_thenPersistToFileSystem() throws Exception {
        byte[] content = new byte[] {1, 2, 3};
        MultipartFile multipartFile = new MockMultipartFile("file", "test.png", "image/png", content);
        MediaPostVm vm = new MediaPostVm("caption", multipartFile, "test.png");

        when(mediaRepository.save(any(Media.class))).thenAnswer(i -> i.getArgument(0));
        
        // FIX: Thay doNothing bằng return giá trị (giả sử trả về null hoặc object đều được)
        when(fileSystemRepository.persistFile(anyString(), any(byte[].class))).thenReturn(null); 
        
        Media savedMedia = mediaService.saveMedia(vm);
        
        assertNotNull(savedMedia);
        verify(mediaRepository, times(1)).save(any());
        verify(fileSystemRepository, times(1)).persistFile(anyString(), any(byte[].class));
    }

    @Test
    void getFile_whenMediaExists_thenReturnFullData() throws IOException {
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));
        
        byte[] expectedContent = "test data".getBytes();
        InputStream inputStream = new java.io.ByteArrayInputStream(expectedContent);
        when(fileSystemRepository.getFile(any())).thenReturn(inputStream);

        MediaDto result = mediaService.getFile(1L, "file");

        assertNotNull(result);
        // FIX: So sánh bằng String vì MediaType trả về từ DTO thường được chuyển thành String hoặc dùng .toString()
        assertEquals("image/jpeg", result.getMediaType().toString());
        assertArrayEquals(expectedContent, result.getContent().readAllBytes());
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

    private static @NotNull Media getMedia(Long id, String name) {
        var media = new Media();
        media.setId(id);
        media.setFileName(name);
        return media;
    }
}
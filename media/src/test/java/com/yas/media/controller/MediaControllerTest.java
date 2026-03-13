package com.yas.media.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.yas.media.controller.MediaController;
import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaController mediaController;

    private Media media;
    private MediaVm mediaVm;

    @BeforeEach
    void setUp() {
        media = new Media();
        media.setId(1L);
        media.setCaption("Test Media");
        media.setFileName("test.png");
        media.setMediaType(MediaType.IMAGE_PNG_VALUE);

        mediaVm = new MediaVm(1L, "test.png", "Test Media", MediaType.IMAGE_PNG_VALUE, "url");
    }

    @Test
    void create_ShouldReturnOk() {
        MediaPostVm postVm = mock(MediaPostVm.class);
        when(mediaService.saveMedia(any())).thenReturn(media);

        ResponseEntity<Object> response = mediaController.create(postVm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(NoFileMediaVm.class);
    }

    @Test
    void delete_ShouldReturnNoContent() {
        doNothing().when(mediaService).removeMedia(1L);

        ResponseEntity<Void> response = mediaController.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(mediaService).removeMedia(1L);
    }

    @Test
    void get_WhenFound_ShouldReturnOk() {
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        ResponseEntity<MediaVm> response = mediaController.get(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void get_WhenNotFound_ShouldReturnNotFound() {
        when(mediaService.getMediaById(1L)).thenReturn(null);

        ResponseEntity<MediaVm> response = mediaController.get(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getByIds_WhenFound_ShouldReturnOk() {
        when(mediaService.getMediaByIds(List.of(1L))).thenReturn(List.of(mediaVm));

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(List.of(1L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getByIds_WhenEmpty_ShouldReturnNotFound() {
        when(mediaService.getMediaByIds(List.of(1L))).thenReturn(List.of());

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(List.of(1L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // @Test
    // void getFile_ShouldReturnFileWithHeaders() {
    //     InputStream is = new ByteArrayInputStream("test data".getBytes());
    //     MediaDto mediaDto = new MediaDto(is, MediaType.IMAGE_PNG);
    //     when(mediaService.getFile(1L, "test.png")).thenReturn(mediaDto);

    //     ResponseEntity<InputStreamResource> response = mediaController.getFile(1L, "test.png");

    //     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    //     assertThat(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).get(0))
    //         .contains("attachment; filename=\"test.png\"");
    //     assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_PNG);
    // }
}
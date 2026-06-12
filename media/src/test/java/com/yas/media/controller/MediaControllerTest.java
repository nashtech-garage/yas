package com.yas.media.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
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
        media.setCaption("test caption");
        media.setFileName("test.png");
        media.setMediaType(MediaType.IMAGE_PNG_VALUE);

        mediaVm = new MediaVm(1L, "test caption", "test.png", MediaType.IMAGE_PNG_VALUE, "/url");
    }

    @Test
    void create_shouldReturnNoFileMediaVm() {
        MediaPostVm postVm = new MediaPostVm("caption", null, "test.png");
        when(mediaService.saveMedia(any())).thenReturn(media);

        ResponseEntity<Object> response = mediaController.create(postVm);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void delete_shouldReturnNoContent() {
        doNothing().when(mediaService).removeMedia(1L);

        ResponseEntity<Void> response = mediaController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void get_whenFound_shouldReturnMediaVm() {
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        ResponseEntity<MediaVm> response = mediaController.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mediaVm, response.getBody());
    }

    @Test
    void get_whenNotFound_shouldReturnNotFound() {
        when(mediaService.getMediaById(1L)).thenReturn(null);

        ResponseEntity<MediaVm> response = mediaController.get(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getByIds_whenFound_shouldReturnList() {
        when(mediaService.getMediaByIds(any())).thenReturn(List.of(mediaVm));

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(List.of(1L));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getByIds_whenNotFound_shouldReturnNotFound() {
        when(mediaService.getMediaByIds(any())).thenReturn(List.of());

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(List.of(1L));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getFile_shouldReturnInputStreamResource() {
        MediaDto mediaDto = MediaDto.builder()
            .mediaType(MediaType.IMAGE_PNG)
            .content(new ByteArrayInputStream("data".getBytes()))
            .build();
        when(mediaService.getFile(1L, "test.png")).thenReturn(mediaDto);

        ResponseEntity<InputStreamResource> response = mediaController.getFile(1L, "test.png");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

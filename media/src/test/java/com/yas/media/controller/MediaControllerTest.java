package com.yas.media.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;
import java.io.ByteArrayInputStream;
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
    private MediaDto mediaDto;

    @BeforeEach
    void setUp() {
        media = new Media();
        media.setId(1L);
        media.setCaption("test caption");
        media.setFileName("test.png");
        media.setMediaType("image/png");

        mediaVm = new MediaVm(1L, "test caption", "test.png", "image/png", "url");

        mediaDto = MediaDto.builder()
                .content(new ByteArrayInputStream("test data".getBytes()))
                .mediaType(MediaType.IMAGE_PNG)
                .build();
    }

    @Test
    void create_ValidRequest_ReturnsOk() {
        MediaPostVm mediaPostVm = new MediaPostVm("test caption", null, "test.png");
        when(mediaService.saveMedia(any(MediaPostVm.class))).thenReturn(media);

        ResponseEntity<Object> response = mediaController.create(mediaPostVm);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        NoFileMediaVm body = (NoFileMediaVm) response.getBody();
        assertEquals(1L, body.id());
        assertEquals("test caption", body.caption());
        assertEquals("test.png", body.fileName());
    }

    @Test
    void delete_ValidId_ReturnsNoContent() {
        doNothing().when(mediaService).removeMedia(1L);

        ResponseEntity<Void> response = mediaController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void get_ValidId_ReturnsMedia() {
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        ResponseEntity<MediaVm> response = mediaController.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("test caption", response.getBody().getCaption());
    }

    @Test
    void get_InvalidId_ReturnsNotFound() {
        when(mediaService.getMediaById(99L)).thenReturn(null);

        ResponseEntity<MediaVm> response = mediaController.get(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getByIds_ValidIds_ReturnsMediaList() {
        when(mediaService.getMediaByIds(List.of(1L))).thenReturn(List.of(mediaVm));

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(List.of(1L));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
    }

    @Test
    void getByIds_InvalidIds_ReturnsNotFound() {
        when(mediaService.getMediaByIds(any())).thenReturn(List.of());

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(List.of(99L));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getFile_ValidId_ReturnsFile() {
        when(mediaService.getFile(1L, "test.png")).thenReturn(mediaDto);

        ResponseEntity<InputStreamResource> response = mediaController.getFile(1L, "test.png");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
        assertEquals("attachment; filename=\"test.png\"", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
    }
}

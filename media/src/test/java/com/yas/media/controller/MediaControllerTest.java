package com.yas.media.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

class MediaControllerTest {

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaController mediaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_whenValidRequest_thenReturnOk() {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[]{});
        MediaPostVm mediaPostVm = new MediaPostVm("caption", file, "fileName");
        Media media = new Media();
        media.setId(1L);
        media.setCaption("caption");
        media.setFileName("test.png");
        media.setMediaType("image/png");

        when(mediaService.saveMedia(any())).thenReturn(media);

        ResponseEntity<Object> response = mediaController.create(mediaPostVm);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void delete_whenValidId_thenReturnNoContent() {
        doNothing().when(mediaService).removeMedia(1L);

        ResponseEntity<Void> response = mediaController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void get_whenMediaExists_thenReturnOk() {
        MediaVm mediaVm = new MediaVm(1L, "caption", "fileName", "image/png", "url");
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        ResponseEntity<MediaVm> response = mediaController.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mediaVm, response.getBody());
    }

    @Test
    void get_whenMediaNotExists_thenReturnNotFound() {
        when(mediaService.getMediaById(1L)).thenReturn(null);

        ResponseEntity<MediaVm> response = mediaController.get(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getByIds_whenMediasExist_thenReturnOk() {
        MediaVm mediaVm = new MediaVm(1L, "caption", "fileName", "image/png", "url");
        when(mediaService.getMediaByIds(List.of(1L))).thenReturn(List.of(mediaVm));

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(List.of(1L));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getByIds_whenMediasNotExist_thenReturnNotFound() {
        when(mediaService.getMediaByIds(List.of(1L))).thenReturn(List.of());

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(List.of(1L));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getFile_whenValidRequest_thenReturnOk() {
        MediaDto mediaDto = MediaDto.builder()
            .content(new ByteArrayInputStream(new byte[]{}))
            .mediaType(MediaType.IMAGE_PNG)
            .build();
        when(mediaService.getFile(1L, "fileName")).thenReturn(mediaDto);

        ResponseEntity<InputStreamResource> response = mediaController.getFile(1L, "fileName");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}

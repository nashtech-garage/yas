package com.yas.media.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaController mediaController;

    @Test
    void create_shouldReturnOkWithNoFileMediaVm() {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});
        MediaPostVm postVm = new MediaPostVm("caption", file, null);
        Media media = media(1L, "caption", "test.jpg", "image/jpeg");
        when(mediaService.saveMedia(postVm)).thenReturn(media);

        ResponseEntity<Object> response = mediaController.create(postVm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        NoFileMediaVm body = (NoFileMediaVm) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.id()).isEqualTo(1L);
        assertThat(body.caption()).isEqualTo("caption");
        assertThat(body.fileName()).isEqualTo("test.jpg");
        assertThat(body.mediaType()).isEqualTo("image/jpeg");
    }

    @Test
    void delete_shouldReturnNoContent() {
        ResponseEntity<Void> response = mediaController.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(mediaService).removeMedia(1L);
    }

    @Test
    void get_shouldReturnMedia_whenExists() {
        MediaVm mediaVm = new MediaVm(1L, "caption", "test.jpg", "image/jpeg", "http://url/test.jpg");
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        ResponseEntity<MediaVm> response = mediaController.get(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getCaption()).isEqualTo("caption");
        assertThat(response.getBody().getFileName()).isEqualTo("test.jpg");
    }

    @Test
    void get_shouldReturnNotFound_whenMediaNotExists() {
        when(mediaService.getMediaById(1L)).thenReturn(null);

        ResponseEntity<MediaVm> response = mediaController.get(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getByIds_shouldReturnMediaList_whenExists() {
        MediaVm mediaVm = new MediaVm(1L, "caption", "test.jpg", "image/jpeg", "http://url/test.jpg");
        List<MediaVm> medias = List.of(mediaVm);
        when(mediaService.getMediaByIds(List.of(1L))).thenReturn(medias);

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(List.of(1L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void getByIds_shouldReturnNotFound_whenEmpty() {
        when(mediaService.getMediaByIds(List.of(1L))).thenReturn(List.of());

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(List.of(1L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getFile_shouldReturnFileContent() {
        byte[] content = new byte[]{1, 2, 3};
        MediaDto mediaDto = MediaDto.builder()
            .content(new ByteArrayInputStream(content))
            .mediaType(org.springframework.http.MediaType.IMAGE_JPEG)
            .build();
        when(mediaService.getFile(1L, "test.jpg")).thenReturn(mediaDto);

        ResponseEntity<InputStreamResource> response = mediaController.getFile(1L, "test.jpg");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentDisposition().toString())
            .contains("test.jpg");
        assertThat(response.getHeaders().getContentType())
            .isEqualTo(org.springframework.http.MediaType.IMAGE_JPEG);
    }

    private static Media media(Long id, String caption, String fileName, String mediaType) {
        Media m = new Media();
        m.setId(id);
        m.setCaption(caption);
        m.setFileName(fileName);
        m.setMediaType(mediaType);
        return m;
    }
}

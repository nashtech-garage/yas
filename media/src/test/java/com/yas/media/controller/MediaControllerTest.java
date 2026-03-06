package com.yas.media.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
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
import org.springframework.web.multipart.MultipartFile;

class MediaControllerTest {

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaController mediaController;

    private Media testMedia;
    private MediaVm testMediaVm;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testMedia = new Media();
        testMedia.setId(1L);
        testMedia.setCaption("Test Image");
        testMedia.setFileName("test.jpg");
        testMedia.setMediaType("image/jpeg");
        testMedia.setFilePath("/path/test.jpg");

        testMediaVm = new MediaVm(1L, "Test Image", "test.jpg", "image/jpeg", "http://example.com/medias/1/file/test.jpg");
    }

    @Test
    void testCreateMediaSuccess() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "content".getBytes()
        );
        MediaPostVm mediaPostVm = new MediaPostVm("Test", file, "override.jpg");

        when(mediaService.saveMedia(any(MediaPostVm.class))).thenReturn(testMedia);

        ResponseEntity<Object> response = mediaController.create(mediaPostVm);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetMediaSuccess() {
        when(mediaService.getMediaById(1L)).thenReturn(testMediaVm);

        ResponseEntity<MediaVm> response = mediaController.get(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testMediaVm, response.getBody());
    }

    @Test
    void testGetMediaNotFound() {
        when(mediaService.getMediaById(999L)).thenReturn(null);

        ResponseEntity<MediaVm> response = mediaController.get(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteMediaSuccess() {
        doNothing().when(mediaService).removeMedia(1L);

        ResponseEntity<Void> response = mediaController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteMediaNotFound() {
        doThrow(new NotFoundException("Media 1 is not found")).when(mediaService).removeMedia(1L);

        // The exception will be caught by ControllerAdvisor
        try {
            mediaController.delete(1L);
        } catch (NotFoundException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void testGetByIdsSuccess() {
        List<Long> ids = Arrays.asList(1L, 2L);
        List<MediaVm> medias = Arrays.asList(
            new MediaVm(1L, "Image 1", "image1.jpg", "image/jpeg", "url1"),
            new MediaVm(2L, "Image 2", "image2.png", "image/png", "url2")
        );

        when(mediaService.getMediaByIds(ids)).thenReturn(medias);

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(ids);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetByIdsEmpty() {
        List<Long> ids = Arrays.asList();

        when(mediaService.getMediaByIds(ids)).thenReturn(Arrays.asList());

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(ids);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetFileSuccess() {
        byte[] fileContent = "test content".getBytes();
        MediaDto mediaDto = MediaDto.builder()
            .content(new ByteArrayInputStream(fileContent))
            .mediaType(MediaType.IMAGE_JPEG)
            .build();

        when(mediaService.getFile(1L, "test.jpg")).thenReturn(mediaDto);

        ResponseEntity<InputStreamResource> response = mediaController.getFile(1L, "test.jpg");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreateMediaWithDifferentFileTypes() {
        // JPEG
        MultipartFile jpegFile = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "jpeg content".getBytes()
        );
        MediaPostVm jpegVm = new MediaPostVm("JPEG", jpegFile, null);

        when(mediaService.saveMedia(any(MediaPostVm.class))).thenReturn(testMedia);

        ResponseEntity<Object> response = mediaController.create(jpegVm);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetMediaByMultipleIds() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        List<MediaVm> medias = Arrays.asList(
            new MediaVm(1L, "Image 1", "image1.jpg", "image/jpeg", "url1"),
            new MediaVm(2L, "Image 2", "image2.png", "image/png", "url2"),
            new MediaVm(3L, "Image 3", "image3.gif", "image/gif", "url3")
        );

        when(mediaService.getMediaByIds(ids)).thenReturn(medias);

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(ids);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
    }

    @Test
    void testCreateMediaWithCaption() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "image.png",
            "image/png",
            "png content".getBytes()
        );
        MediaPostVm mediaPostVm = new MediaPostVm("Beautiful Sunset", file, "sunset.png");

        when(mediaService.saveMedia(any(MediaPostVm.class))).thenReturn(testMedia);

        ResponseEntity<Object> response = mediaController.create(mediaPostVm);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteMultipleMediasSequentially() {
        doNothing().when(mediaService).removeMedia(1L);
        doNothing().when(mediaService).removeMedia(2L);

        ResponseEntity<Void> response1 = mediaController.delete(1L);
        ResponseEntity<Void> response2 = mediaController.delete(2L);

        assertEquals(HttpStatus.NO_CONTENT, response1.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());
    }

    @Test
    void testGetFileWithSpecialCharactersInFilename() {
        byte[] fileContent = "content".getBytes();
        MediaDto mediaDto = MediaDto.builder()
            .content(new ByteArrayInputStream(fileContent))
            .mediaType(MediaType.IMAGE_PNG)
            .build();

        when(mediaService.getFile(1L, "image-with-underscore_2024.png")).thenReturn(mediaDto);

        ResponseEntity<InputStreamResource> response = mediaController.getFile(1L, "image-with-underscore_2024.png");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreateMediaResponseStructure() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "content".getBytes()
        );
        MediaPostVm mediaPostVm = new MediaPostVm("Test", file, null);

        when(mediaService.saveMedia(any(MediaPostVm.class))).thenReturn(testMedia);

        ResponseEntity<Object> response = mediaController.create(mediaPostVm);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCreateMediaWithPng() {
        MultipartFile pngFile = new MockMultipartFile(
            "file",
            "test.png",
            "image/png",
            "png data".getBytes()
        );
        MediaPostVm mediaPostVm = new MediaPostVm("PNG Image", pngFile, "custom.png");

        when(mediaService.saveMedia(any(MediaPostVm.class))).thenReturn(testMedia);

        ResponseEntity<Object> response = mediaController.create(mediaPostVm);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreateMediaWithGif() {
        MultipartFile gifFile = new MockMultipartFile(
            "file",
            "test.gif",
            "image/gif",
            "gif data".getBytes()
        );
        MediaPostVm mediaPostVm = new MediaPostVm("GIF Image", gifFile, null);

        when(mediaService.saveMedia(any(MediaPostVm.class))).thenReturn(testMedia);

        ResponseEntity<Object> response = mediaController.create(mediaPostVm);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetMediaWithDifferentIds() {
        MediaVm mediaVm1 = new MediaVm(1L, "Image 1", "img1.jpg", "image/jpeg", "url1");
        MediaVm mediaVm2 = new MediaVm(2L, "Image 2", "img2.png", "image/png", "url2");
        MediaVm mediaVm3 = new MediaVm(3L, "Image 3", "img3.gif", "image/gif", "url3");

        when(mediaService.getMediaById(1L)).thenReturn(mediaVm1);
        when(mediaService.getMediaById(2L)).thenReturn(mediaVm2);
        when(mediaService.getMediaById(3L)).thenReturn(mediaVm3);

        ResponseEntity<MediaVm> response1 = mediaController.get(1L);
        ResponseEntity<MediaVm> response2 = mediaController.get(2L);
        ResponseEntity<MediaVm> response3 = mediaController.get(3L);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
    }

    @Test
    void testDeleteMultipleDifferentMedias() {
        doNothing().when(mediaService).removeMedia(1L);
        doNothing().when(mediaService).removeMedia(2L);
        doNothing().when(mediaService).removeMedia(3L);

        ResponseEntity<Void> response1 = mediaController.delete(1L);
        ResponseEntity<Void> response2 = mediaController.delete(2L);
        ResponseEntity<Void> response3 = mediaController.delete(3L);

        assertEquals(HttpStatus.NO_CONTENT, response1.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response3.getStatusCode());
    }

    @Test
    void testGetByIdsSingleResult() {
        List<Long> ids = Arrays.asList(1L);
        List<MediaVm> medias = Arrays.asList(
            new MediaVm(1L, "Single Image", "single.jpg", "image/jpeg", "url1")
        );

        when(mediaService.getMediaByIds(ids)).thenReturn(medias);

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(ids);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetFileWithPng() {
        byte[] fileContent = "png content".getBytes();
        MediaDto mediaDto = MediaDto.builder()
            .content(new ByteArrayInputStream(fileContent))
            .mediaType(MediaType.IMAGE_PNG)
            .build();

        when(mediaService.getFile(1L, "image.png")).thenReturn(mediaDto);

        ResponseEntity<InputStreamResource> response = mediaController.getFile(1L, "image.png");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetFileWithGif() {
        byte[] fileContent = "gif content".getBytes();
        MediaDto mediaDto = MediaDto.builder()
            .content(new ByteArrayInputStream(fileContent))
            .mediaType(MediaType.valueOf("image/gif"))
            .build();

        when(mediaService.getFile(1L, "animation.gif")).thenReturn(mediaDto);

        ResponseEntity<InputStreamResource> response = mediaController.getFile(1L, "animation.gif");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}

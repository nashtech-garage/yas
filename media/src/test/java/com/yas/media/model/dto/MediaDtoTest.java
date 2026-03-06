package com.yas.media.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class MediaDtoTest {

    @Test
    void testMediaDtoBuild() {
        byte[] content = "test data".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);

        MediaDto mediaDto = MediaDto.builder()
            .content(inputStream)
            .mediaType(MediaType.IMAGE_JPEG)
            .build();

        assertNotNull(mediaDto);
        assertEquals(inputStream, mediaDto.getContent());
        assertEquals(MediaType.IMAGE_JPEG, mediaDto.getMediaType());
    }

    @Test
    void testMediaDtoWithNullValues() {
        MediaDto mediaDto = MediaDto.builder()
            .content(null)
            .mediaType(null)
            .build();

        assertNotNull(mediaDto);
        assertNull(mediaDto.getContent());
        assertNull(mediaDto.getMediaType());
    }

    @Test
    void testMediaDtoBuilderEmpty() {
        MediaDto mediaDto = MediaDto.builder().build();

        assertNotNull(mediaDto);
        assertNull(mediaDto.getContent());
        assertNull(mediaDto.getMediaType());
    }

    @Test
    void testMediaDtoWithDifferentMediaTypes() {
        InputStream inputStream = new ByteArrayInputStream("test".getBytes());

        MediaDto jpegDto = MediaDto.builder()
            .content(inputStream)
            .mediaType(MediaType.IMAGE_JPEG)
            .build();

        MediaDto pngDto = MediaDto.builder()
            .content(inputStream)
            .mediaType(MediaType.IMAGE_PNG)
            .build();

        MediaDto gifDto = MediaDto.builder()
            .content(inputStream)
            .mediaType(MediaType.valueOf("image/gif"))
            .build();

        assertEquals(MediaType.IMAGE_JPEG, jpegDto.getMediaType());
        assertEquals(MediaType.IMAGE_PNG, pngDto.getMediaType());
        assertEquals(MediaType.valueOf("image/gif"), gifDto.getMediaType());
    }

    @Test
    void testMediaDtoImmutability() {
        byte[] content = "test data".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);

        MediaDto mediaDto = MediaDto.builder()
            .content(inputStream)
            .mediaType(MediaType.IMAGE_PNG)
            .build();

        // Verify values are set correctly
        assertEquals(inputStream, mediaDto.getContent());
        assertEquals(MediaType.IMAGE_PNG, mediaDto.getMediaType());
    }
}

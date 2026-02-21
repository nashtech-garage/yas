package com.yas.media.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MediaTest {

    private Media media;

    @BeforeEach
    void setUp() {
        media = new Media();
    }

    @Test
    void testSetAndGetId() {
        long id = 1L;
        media.setId(id);
        assertEquals(id, media.getId());
    }

    @Test
    void testSetAndGetCaption() {
        String caption = "Test Caption";
        media.setCaption(caption);
        assertEquals(caption, media.getCaption());
    }

    @Test
    void testSetAndGetFileName() {
        String fileName = "test-file.jpg";
        media.setFileName(fileName);
        assertEquals(fileName, media.getFileName());
    }

    @Test
    void testSetAndGetFilePath() {
        String filePath = "/path/to/file.jpg";
        media.setFilePath(filePath);
        assertEquals(filePath, media.getFilePath());
    }

    @Test
    void testSetAndGetMediaType() {
        String mediaType = "image/jpeg";
        media.setMediaType(mediaType);
        assertEquals(mediaType, media.getMediaType());
    }

    @Test
    void testMediaInitialization() {
        assertNull(media.getId());
        assertNull(media.getCaption());
        assertNull(media.getFileName());
        assertNull(media.getFilePath());
        assertNull(media.getMediaType());
    }

    @Test
    void testMediaWithAllFields() {
        long id = 1L;
        String caption = "Test Image";
        String fileName = "image.png";
        String filePath = "/upload/image.png";
        String mediaType = "image/png";

        media.setId(id);
        media.setCaption(caption);
        media.setFileName(fileName);
        media.setFilePath(filePath);
        media.setMediaType(mediaType);

        assertEquals(id, media.getId());
        assertEquals(caption, media.getCaption());
        assertEquals(fileName, media.getFileName());
        assertEquals(filePath, media.getFilePath());
        assertEquals(mediaType, media.getMediaType());
    }
}

package com.yas.media.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MediaVmTest {

    @Test
    void testMediaVmConstructor() {
        Long id = 1L;
        String caption = "Test Image";
        String fileName = "image.jpg";
        String mediaType = "image/jpeg";
        String url = "http://example.com/medias/1/file/image.jpg";

        MediaVm mediaVm = new MediaVm(id, caption, fileName, mediaType, url);

        assertEquals(id, mediaVm.getId());
        assertEquals(caption, mediaVm.getCaption());
        assertEquals(fileName, mediaVm.getFileName());
        assertEquals(mediaType, mediaVm.getMediaType());
        assertEquals(url, mediaVm.getUrl());
    }

    @Test
    void testMediaVmSetters() {
        MediaVm mediaVm = new MediaVm(1L, "Original", "file.jpg", "image/jpeg", "url");

        Long newId = 2L;
        String newCaption = "Updated Caption";
        String newFileName = "newfile.png";
        String newMediaType = "image/png";
        String newUrl = "http://example.com/medias/2/file/newfile.png";

        mediaVm.setId(newId);
        mediaVm.setCaption(newCaption);
        mediaVm.setFileName(newFileName);
        mediaVm.setMediaType(newMediaType);
        mediaVm.setUrl(newUrl);

        assertEquals(newId, mediaVm.getId());
        assertEquals(newCaption, mediaVm.getCaption());
        assertEquals(newFileName, mediaVm.getFileName());
        assertEquals(newMediaType, mediaVm.getMediaType());
        assertEquals(newUrl, mediaVm.getUrl());
    }

    @Test
    void testMediaVmWithNullValues() {
        MediaVm mediaVm = new MediaVm(null, null, null, null, null);

        assertEquals(null, mediaVm.getId());
        assertEquals(null, mediaVm.getCaption());
        assertEquals(null, mediaVm.getFileName());
        assertEquals(null, mediaVm.getMediaType());
        assertEquals(null, mediaVm.getUrl());
    }

    @Test
    void testMediaVmWithEmptyStrings() {
        MediaVm mediaVm = new MediaVm(1L, "", "", "", "");

        assertEquals(1L, mediaVm.getId());
        assertEquals("", mediaVm.getCaption());
        assertEquals("", mediaVm.getFileName());
        assertEquals("", mediaVm.getMediaType());
        assertEquals("", mediaVm.getUrl());
    }

    @Test
    void testMediaVmGetters() {
        Long id = 3L;
        String caption = "Photo";
        String fileName = "photo.gif";
        String mediaType = "image/gif";
        String url = "http://cdn.example.com/image";

        MediaVm mediaVm = new MediaVm(id, caption, fileName, mediaType, url);

        assertNotNull(mediaVm.getId());
        assertNotNull(mediaVm.getCaption());
        assertNotNull(mediaVm.getFileName());
        assertNotNull(mediaVm.getMediaType());
        assertNotNull(mediaVm.getUrl());
    }
}

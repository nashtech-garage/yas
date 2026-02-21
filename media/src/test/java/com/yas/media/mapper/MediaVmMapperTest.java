package com.yas.media.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.media.model.Media;
import com.yas.media.viewmodel.MediaVm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class MediaVmMapperTest {

    private MediaVmMapper mediaVmMapper;

    @BeforeEach
    void setUp() {
        mediaVmMapper = Mappers.getMapper(MediaVmMapper.class);
    }

    @Test
    void testMapperCreation() {
        assertNotNull(mediaVmMapper);
    }

    @Test
    void testMediaToMediaVm() {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("Test Caption");
        media.setFileName("test.jpg");
        media.setMediaType("image/jpeg");
        media.setFilePath("/path/to/file");

        MediaVm mediaVm = mediaVmMapper.toVm(media);

        assertNotNull(mediaVm);
        assertEquals(media.getId(), mediaVm.getId());
        assertEquals(media.getCaption(), mediaVm.getCaption());
        assertEquals(media.getFileName(), mediaVm.getFileName());
        assertEquals(media.getMediaType(), mediaVm.getMediaType());
    }

    @Test
    void testMediaToMediaVmWithNullFields() {
        Media media = new Media();
        media.setId(1L);

        MediaVm mediaVm = mediaVmMapper.toVm(media);

        assertNotNull(mediaVm);
        assertEquals(1L, mediaVm.getId());
        assertNull(mediaVm.getCaption());
        assertNull(mediaVm.getFileName());
        assertNull(mediaVm.getMediaType());
    }

    @Test
    void testMediaVmToMedia() {
        MediaVm mediaVm = new MediaVm(1L, "Caption", "file.jpg", "image/jpeg", "http://url");

        Media media = mediaVmMapper.toModel(mediaVm);

        assertNotNull(media);
        assertEquals(mediaVm.getId(), media.getId());
        assertEquals(mediaVm.getCaption(), media.getCaption());
        assertEquals(mediaVm.getFileName(), media.getFileName());
        assertEquals(mediaVm.getMediaType(), media.getMediaType());
    }

    @Test
    void testMediaVmToMediaWithNullUrl() {
        MediaVm mediaVm = new MediaVm(2L, "Image", "image.png", "image/png", null);

        Media media = mediaVmMapper.toModel(mediaVm);

        assertNotNull(media);
        assertEquals(2L, media.getId());
        assertEquals("Image", media.getCaption());
        assertEquals("image.png", media.getFileName());
        assertEquals("image/png", media.getMediaType());
    }

    @Test
    void testPartialUpdateMedia() {
        Media originalMedia = new Media();
        originalMedia.setId(1L);
        originalMedia.setCaption("Original Caption");
        originalMedia.setFileName("original.jpg");
        originalMedia.setMediaType("image/jpeg");
        originalMedia.setFilePath("/path/original");

        MediaVm updateVm = new MediaVm(1L, "Updated Caption", null, "image/png", "url");

        mediaVmMapper.partialUpdate(originalMedia, updateVm);

        assertEquals(1L, originalMedia.getId());
        assertEquals("Updated Caption", originalMedia.getCaption());
        // Since FileName is null in updateVm, it shouldn't update due to IGNORE
        // strategy
        assertEquals("image/png", originalMedia.getMediaType());
    }

    @Test
    void testMapperWithMultipleMedias() {
        Media[] medias = new Media[3];
        for (int i = 0; i < 3; i++) {
            Media media = new Media();
            media.setId((long) i);
            media.setCaption("Caption " + i);
            media.setFileName("file" + i + ".jpg");
            media.setMediaType("image/jpeg");
            medias[i] = media;
        }

        for (Media media : medias) {
            MediaVm vm = mediaVmMapper.toVm(media);
            assertNotNull(vm);
            assertEquals(media.getId(), vm.getId());
        }
    }

    @Test
    void testMediaToVmFilPathNotMapped() {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("Test");
        media.setFileName("file.jpg");
        media.setMediaType("image/jpeg");
        media.setFilePath("/secure/path/file.jpg");

        MediaVm mediaVm = mediaVmMapper.toVm(media);

        assertNotNull(mediaVm);
        assertEquals(1L, mediaVm.getId());
        assertNull(mediaVm.getUrl()); // URL is not auto-mapped
    }

    @Test
    void testMapperConsistency() {
        Media media1 = new Media();
        media1.setId(1L);
        media1.setCaption("Test1");
        media1.setFileName("file1.jpg");
        media1.setMediaType("image/jpeg");

        Media media2 = new Media();
        media2.setId(1L);
        media2.setCaption("Test1");
        media2.setFileName("file1.jpg");
        media2.setMediaType("image/jpeg");

        MediaVm vm1 = mediaVmMapper.toVm(media1);
        MediaVm vm2 = mediaVmMapper.toVm(media2);

        assertEquals(vm1.getId(), vm2.getId());
        assertEquals(vm1.getCaption(), vm2.getCaption());
        assertEquals(vm1.getFileName(), vm2.getFileName());
    }

    @Test
    void testMapperWithAllFieldsPopulated() {
        Media media = new Media();
        media.setId(10L);
        media.setCaption("Complete Image");
        media.setFileName("complete.jpg");
        media.setMediaType("image/jpeg");
        media.setFilePath("/path/to/complete.jpg");

        MediaVm mediaVm = mediaVmMapper.toVm(media);

        assertNotNull(mediaVm);
        assertEquals(10L, mediaVm.getId());
        assertEquals("Complete Image", mediaVm.getCaption());
        assertEquals("complete.jpg", mediaVm.getFileName());
        assertEquals("image/jpeg", mediaVm.getMediaType());
    }

    @Test
    void testMapperWithEmptyStrings() {
        Media media = new Media();
        media.setId(5L);
        media.setCaption("");
        media.setFileName("");
        media.setMediaType("");

        MediaVm mediaVm = mediaVmMapper.toVm(media);

        assertNotNull(mediaVm);
        assertEquals(5L, mediaVm.getId());
        assertEquals("", mediaVm.getCaption());
        assertEquals("", mediaVm.getFileName());
        assertEquals("", mediaVm.getMediaType());
    }

    @Test
    void testMediaVmWithLongCaption() {
        String longCaption = "A".repeat(1000);
        MediaVm mediaVm = new MediaVm(1L, longCaption, "file.jpg", "image/jpeg", "url");

        Media media = mediaVmMapper.toModel(mediaVm);

        assertEquals(longCaption, media.getCaption());
    }

    @Test
    void testMultipleMediaMapping() {
        for (int i = 1; i <= 5; i++) {
            Media media = new Media();
            media.setId((long) i);
            media.setCaption("Image " + i);
            media.setFileName("image" + i + ".jpg");
            media.setMediaType("image/jpeg");

            MediaVm mediaVm = mediaVmMapper.toVm(media);

            assertEquals((long) i, mediaVm.getId());
            assertEquals("Image " + i, mediaVm.getCaption());
        }
    }

    @Test
    void testPartialUpdateWithAllNullFields() {
        Media originalMedia = new Media();
        originalMedia.setId(1L);
        originalMedia.setCaption("Original");
        originalMedia.setFileName("original.jpg");
        originalMedia.setMediaType("image/jpeg");

        MediaVm updateVm = new MediaVm(1L, null, null, null, "url");

        mediaVmMapper.partialUpdate(originalMedia, updateVm);

        // Original values should remain unchanged because update fields are null
        assertEquals("Original", originalMedia.getCaption());
        assertEquals("original.jpg", originalMedia.getFileName());
        assertEquals("image/jpeg", originalMedia.getMediaType());
    }

    @Test
    void testMapperWithDifferentMediaTypes() {
        String[] mediaTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};

        for (String mediaType : mediaTypes) {
            Media media = new Media();
            media.setId(1L);
            media.setCaption("Test");
            media.setFileName("test");
            media.setMediaType(mediaType);

            MediaVm mediaVm = mediaVmMapper.toVm(media);

            assertEquals(mediaType, mediaVm.getMediaType());
        }
    }
}

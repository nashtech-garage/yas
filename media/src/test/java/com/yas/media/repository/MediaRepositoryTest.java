package com.yas.media.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.media.model.Media;
import com.yas.media.viewmodel.NoFileMediaVm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class MediaRepositoryTest {

    @Autowired
    private MediaRepository mediaRepository;

    private Media testMedia;

    @BeforeEach
    void setUp() {
        testMedia = new Media();
        testMedia.setCaption("Test Image");
        testMedia.setFileName("test.jpg");
        testMedia.setMediaType("image/jpeg");
        testMedia.setFilePath("/path/to/test.jpg");
    }

    @Test
    void testSaveMedia() {
        Media saved = mediaRepository.save(testMedia);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Test Image", saved.getCaption());
        assertEquals("test.jpg", saved.getFileName());
    }

    @Test
    void testFindMediaById() {
        Media saved = mediaRepository.save(testMedia);

        Media found = mediaRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Test Image", found.getCaption());
    }

    @Test
    void testDeleteMedia() {
        Media saved = mediaRepository.save(testMedia);
        Long id = saved.getId();

        mediaRepository.deleteById(id);

        Media deleted = mediaRepository.findById(id).orElse(null);
        assertNull(deleted);
    }

    @Test
    void testUpdateMedia() {
        Media saved = mediaRepository.save(testMedia);

        saved.setCaption("Updated Caption");
        saved.setFileName("updated.jpg");

        Media updated = mediaRepository.save(saved);

        assertEquals("Updated Caption", updated.getCaption());
        assertEquals("updated.jpg", updated.getFileName());
    }

    @Test
    void testSaveMultipleMedias() {
        Media media1 = new Media();
        media1.setCaption("Image 1");
        media1.setFileName("image1.jpg");
        media1.setMediaType("image/jpeg");
        media1.setFilePath("/path/image1.jpg");

        Media media2 = new Media();
        media2.setCaption("Image 2");
        media2.setFileName("image2.png");
        media2.setMediaType("image/png");
        media2.setFilePath("/path/image2.png");

        Media saved1 = mediaRepository.save(media1);
        Media saved2 = mediaRepository.save(media2);

        assertNotNull(saved1.getId());
        assertNotNull(saved2.getId());
        assertEquals(2, mediaRepository.count());
    }

    @Test
    void testFindAllByIds() {
        Media saved1 = mediaRepository.save(testMedia);

        Media media2 = new Media();
        media2.setCaption("Image 2");
        media2.setFileName("image2.png");
        media2.setMediaType("image/png");
        media2.setFilePath("/path/image2.png");
        Media saved2 = mediaRepository.save(media2);

        var result = mediaRepository.findAllById(java.util.List.of(saved1.getId(), saved2.getId()));

        assertEquals(2, result.size());
    }

    @Test
    void testFindAllByEmptyIds() {
        var result = mediaRepository.findAllById(java.util.List.of());

        assertEquals(0, result.size());
    }

    @Test
    void testMediaWithDifferentMediaTypes() {
        Media jpegMedia = new Media();
        jpegMedia.setCaption("JPEG Image");
        jpegMedia.setFileName("image.jpg");
        jpegMedia.setMediaType("image/jpeg");
        jpegMedia.setFilePath("/path/image.jpg");

        Media pngMedia = new Media();
        pngMedia.setCaption("PNG Image");
        pngMedia.setFileName("image.png");
        pngMedia.setMediaType("image/png");
        pngMedia.setFilePath("/path/image.png");

        Media savedJpeg = mediaRepository.save(jpegMedia);
        Media savedPng = mediaRepository.save(pngMedia);

        assertEquals("image/jpeg", savedJpeg.getMediaType());
        assertEquals("image/png", savedPng.getMediaType());
    }

    @Test
    void testFindByIdWithoutFileInReturn_whenMediaExists_thenReturnNoFileMediaVm() {
        Media saved = mediaRepository.save(testMedia);

        NoFileMediaVm result = mediaRepository.findByIdWithoutFileInReturn(saved.getId());

        assertNotNull(result);
        assertEquals(saved.getId(), result.id());
        assertEquals("Test Image", result.caption());
        assertEquals("test.jpg", result.fileName());
        assertEquals("image/jpeg", result.mediaType());
    }

    @Test
    void testFindByIdWithoutFileInReturn_whenMediaNotExists_thenReturnNull() {
        NoFileMediaVm result = mediaRepository.findByIdWithoutFileInReturn(9999L);

        assertNull(result);
    }

    @Test
    void testSaveAndUpdateMedia() {
        Media saved = mediaRepository.save(testMedia);
        Long savedId = saved.getId();

        Media toUpdate = mediaRepository.findById(savedId).orElse(null);
        assertNotNull(toUpdate);
        toUpdate.setCaption("Updated Caption");
        
        Media updated = mediaRepository.save(toUpdate);

        assertEquals(savedId, updated.getId());
        assertEquals("Updated Caption", updated.getCaption());
    }

    @Test
    void testFindAllById() {
        Media media1 = new Media();
        media1.setCaption("Image 1");
        media1.setFileName("image1.jpg");
        media1.setMediaType("image/jpeg");
        
        Media media2 = new Media();
        media2.setCaption("Image 2");
        media2.setFileName("image2.png");
        media2.setMediaType("image/png");

        Media savedMedia1 = mediaRepository.save(media1);
        Media savedMedia2 = mediaRepository.save(media2);

        java.util.List<Media> results = mediaRepository.findAllById(java.util.Arrays.asList(savedMedia1.getId(), savedMedia2.getId()));

        assertEquals(2, results.size());
    }

    @Test
    void testMediaWithNullCaption() {
        Media mediaWithNullCaption = new Media();
        mediaWithNullCaption.setCaption(null);
        mediaWithNullCaption.setFileName("test.jpg");
        mediaWithNullCaption.setMediaType("image/jpeg");
        mediaWithNullCaption.setFilePath("/path/test.jpg");

        Media saved = mediaRepository.save(mediaWithNullCaption);

        assertNotNull(saved);
        assertNull(saved.getCaption());
    }

    @Test
    void testMediaWithLongCaption() {
        Media mediaWithLongCaption = new Media();
        String longCaption = "A".repeat(200);
        mediaWithLongCaption.setCaption(longCaption);
        mediaWithLongCaption.setFileName("test.jpg");
        mediaWithLongCaption.setMediaType("image/jpeg");
        mediaWithLongCaption.setFilePath("/path/test.jpg");

        Media saved = mediaRepository.save(mediaWithLongCaption);

        assertEquals(longCaption, saved.getCaption());
    }
}

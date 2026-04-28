package com.yas.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.media.model.Media;
import org.junit.jupiter.api.Test;

class MediaCoverageTest {

    @Test
    void testMediaModel() {
        Media media = new Media();
        media.setId(100L);
        media.setCaption("Sample Caption");
        media.setFileName("sample.png");
        media.setMediaType("image/png");
        media.setData(new byte[]{1, 2, 3});

        assertEquals(100L, media.getId());
        assertEquals("Sample Caption", media.getCaption());
        assertEquals("sample.png", media.getFileName());
        assertEquals("image/png", media.getMediaType());
        assertNotNull(media.getData());
    }
}

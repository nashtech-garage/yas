package com.yas.media.mapper;

import com.yas.media.model.Media;
import com.yas.media.viewmodel.MediaVm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaVmMapperTest {

    private final MediaVmMapper mapper = new MediaVmMapperImpl();

    @Test
    void testToVm_WithNullMedia() {
        assertNull(mapper.toVm(null));
    }

    @Test
    void testToVm_WithEmptyMedia() {
        Media media = new Media();
        MediaVm vm = mapper.toVm(media);
        
        assertNotNull(vm);
        assertNull(vm.getId());
        assertNull(vm.getCaption());
        assertNull(vm.getFileName());
        assertNull(vm.getMediaType());
    }

    @Test
    void testToModel_WithNullVm() {
        assertNull(mapper.toModel(null));
    }

    @Test
    void testToModel_WithValidVm() {
        MediaVm vm = new MediaVm(1L, "Test Caption", "test.png", "image/png", "/url");
        Media model = mapper.toModel(vm);

        assertNotNull(model);
        assertEquals(1L, model.getId());
        assertEquals("Test Caption", model.getCaption());
        assertEquals("test.png", model.getFileName());
        assertEquals("image/png", model.getMediaType());
    }

    @Test
    void testPartialUpdate_WithNullVm() {
        Media media = new Media();
        media.setCaption("Old Caption");
        
        mapper.partialUpdate(media, null);
        assertEquals("Old Caption", media.getCaption());
    }

    @Test
    void testPartialUpdate_WithValidVm_IgnoresNullFields() {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("Old Caption");
        media.setFileName("old.png");

        MediaVm updateVm = new MediaVm(null, "New Caption", null, null, null);
        
        mapper.partialUpdate(media, updateVm);

        assertEquals(1L, media.getId());
        assertEquals("old.png", media.getFileName());
        assertEquals("New Caption", media.getCaption());
    }
}
package com.yas.media.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.media.model.Media;
import com.yas.media.viewmodel.MediaVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MediaVmMapperTest {

    // Không cần dùng @Mock, khởi tạo trực tiếp class luôn cho nhanh
    private MediaVmMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new MediaVmMapperImpl();
    }

    // --- 1. Test hàm toModel ---
    @Test
    void toModel_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toModel(null));
    }

    @Test
    void toModel_shouldMapCorrectly_whenInputIsNotNull() {
        MediaVm vm = new MediaVm(1L, "caption test", "file.png", "image/png", "url");
        Media model = mapper.toModel(vm);

        assertEquals(1L, model.getId());
        assertEquals("caption test", model.getCaption());
        assertEquals("file.png", model.getFileName());
    }

    // --- 2. Test hàm toVm ---
    @Test
    void toVm_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toVm(null));
    }

    @Test
    void toVm_shouldMapCorrectly_whenInputIsNotNull() {
        Media model = new Media();
        model.setId(2L);
        model.setCaption("model caption");
        model.setFileName("model.png");
        model.setMediaType("image/png");

        MediaVm vm = mapper.toVm(model);

        assertEquals(2L, vm.getId());
        assertEquals("model caption", vm.getCaption());
        assertEquals("model.png", vm.getFileName());
    }

    // --- 3. Test hàm partialUpdate ---
    @Test
    void partialUpdate_shouldDoNothing_whenInputIsNull() {
        Media model = new Media();
        model.setId(5L);
        
        mapper.partialUpdate(model, null);
        
        // ID vẫn giữ nguyên là 5L vì không có gì được update
        assertEquals(5L, model.getId()); 
    }

    @Test
    void partialUpdate_shouldUpdateFields_whenInputIsNotNull() {
        Media model = new Media();
        model.setId(10L);
        model.setCaption("old caption");

        MediaVm vm = new MediaVm(20L, "new caption", "new.png", "image/png", null);
        
        mapper.partialUpdate(model, vm);

        // ID và caption phải bị ghi đè bởi dữ liệu mới từ vm
        assertEquals(20L, model.getId());
        assertEquals("new caption", model.getCaption());
        assertEquals("new.png", model.getFileName());
    }
}
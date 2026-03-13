package com.yas.media.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.media.model.Media;
import com.yas.media.viewmodel.MediaVm;
import org.junit.jupiter.api.Test;

class MediaVmMapperTest {

    private final MediaVmMapperImpl mapper = new MediaVmMapperImpl();

    @Test
    void testToVm() {
        Media model = new Media();
        model.setId(1L);
        model.setFileName("test.png");
        
        MediaVm vm = mapper.toVm(model);
        
        assertThat(vm).isNotNull();
        assertThat(vm.getId()).isEqualTo(1L);
        assertThat(vm.getFileName()).isEqualTo("test.png");
        assertThat(mapper.toVm(null)).isNull();
    }

    @Test
    void testToModel() {
        // Nếu MediaVm của bạn không có constructor all-args, hãy dùng setter nhé
        MediaVm vm = new MediaVm(null, null, null, null, null);
        vm.setId(1L);
        vm.setFileName("test.png");
        vm.setCaption("caption");
        vm.setMediaType("image/png");
        vm.setUrl("url");
        
        Media model = mapper.toModel(vm);
        
        assertThat(model).isNotNull();
        assertThat(model.getId()).isEqualTo(1L);
        assertThat(mapper.toModel(null)).isNull();
    }
}
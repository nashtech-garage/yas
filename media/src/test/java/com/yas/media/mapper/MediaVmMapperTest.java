package com.yas.media.mapper;

import com.yas.media.model.Media;
import com.yas.media.viewmodel.MediaVm;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MediaVmMapperTest {

    private final MediaVmMapper mapper = new MediaVmMapperImpl();

    @Test
    void toVm_ShouldMapCorrectly() {
        Media media = new Media();
        media.setId(100L);
        media.setCaption("Test");

        MediaVm vm = mapper.toVm(media);

        assertThat(vm).isNotNull();
        assertThat(vm.getId()).isEqualTo(100L);
    }

    @Test
    void toModel_ShouldMapCorrectly() {
        // Giả lập MediaVm vì chúng ta chưa chắc chắn về Constructor của nó
        MediaVm vm = mock(MediaVm.class);
        when(vm.getId()).thenReturn(200L);

        Media media = mapper.toModel(vm);

        assertThat(media).isNotNull();
        assertThat(media.getId()).isEqualTo(200L);
    }

    @Test
    void shouldReturnNull_WhenInputIsNull() {
        assertThat(mapper.toVm((Media) null)).isNull();
        assertThat(mapper.toModel((MediaVm) null)).isNull();
    }
}
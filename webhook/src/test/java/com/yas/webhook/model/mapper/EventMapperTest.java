package com.yas.webhook.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.webhook.model.Event;
import com.yas.webhook.model.enums.EventName;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class EventMapperTest {

    private final EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Test
    void toEventVm_ShouldMapCorrectly() {
        Event event = new Event();
        event.setId(1L);
        event.setName(EventName.ON_PRODUCT_UPDATED);

        EventVm eventVm = eventMapper.toEventVm(event);

        assertThat(eventVm).isNotNull();
        assertThat(eventVm.getId()).isEqualTo(1L);
        assertThat(eventVm.getName()).isEqualTo(EventName.ON_PRODUCT_UPDATED);
    }

    @Test
    void toEventVm_WhenNull_ShouldReturnNull() {
        EventVm eventVm = eventMapper.toEventVm(null);
        assertThat(eventVm).isNull();
    }
}

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
    void toEventVmShouldMapEventFields() {
        Event event = new Event();
        event.setId(1L);
        event.setName(EventName.ON_PRODUCT_UPDATED);

        EventVm result = eventMapper.toEventVm(event);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(EventName.ON_PRODUCT_UPDATED);
    }
}

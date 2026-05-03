package com.yas.webhook.model.mapper;

import com.yas.webhook.model.Event;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {

    @Test
    void testEventMapper() {
        EventMapper mapper = Mappers.getMapper(EventMapper.class);

        assertNull(mapper.toEventVm(null));

        Event event = new Event();
        event.setId(1L);

        EventVm vm = mapper.toEventVm(event);
        assertNotNull(vm);
        assertEquals(1L, vm.getId());
    }
}
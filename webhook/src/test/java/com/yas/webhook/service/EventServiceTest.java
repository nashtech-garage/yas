package com.yas.webhook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.yas.webhook.model.Event;
import com.yas.webhook.model.mapper.EventMapper;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.repository.EventRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    EventRepository eventRepository;
    @Mock
    EventMapper eventMapper;
    @InjectMocks
    EventService eventService;

    @Test
    void test_findAllEvents_shouldReturnEvents() {

        Event event = new Event();
        EventVm eventVm = EventVm.builder().build();

        when(eventRepository.findAll(Sort.by(Sort.Direction.DESC, "id")))
            .thenReturn(List.of(event));
        when(eventMapper.toEventVm(event)).thenReturn(eventVm);

        List<EventVm> eventVms = eventService.findAllEvents();
        assertNotNull(eventVms);
        assertEquals(1, eventVms.size());
    }
}

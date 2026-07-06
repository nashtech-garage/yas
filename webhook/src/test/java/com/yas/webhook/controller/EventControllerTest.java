package com.yas.webhook.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.service.EventService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    EventService eventService;

    @InjectMocks
    EventController eventController;

    @Test
    void listWebhooksShouldReturnAllEvents() {
        List<EventVm> events = List.of(EventVm.builder().id(1L).build());
        when(eventService.findAllEvents()).thenReturn(events);

        ResponseEntity<List<EventVm>> response = eventController.listWebhooks();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(events);
    }
}

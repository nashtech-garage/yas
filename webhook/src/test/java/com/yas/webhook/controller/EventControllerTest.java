package com.yas.webhook.controller;

import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerTest {

    private MockMvc mockMvc;
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = Mockito.mock(EventService.class);
        EventController eventController = new EventController(eventService);
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    void listEvents_shouldReturnOk() throws Exception {
        List<EventVm> response = Collections.emptyList();
        when(eventService.findAllEvents()).thenReturn(response);

        mockMvc.perform(get("/backoffice/events"))
                .andExpect(status().isOk());
    }
}

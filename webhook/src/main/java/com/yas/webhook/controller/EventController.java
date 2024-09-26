package com.yas.webhook.controller;

import com.yas.webhook.config.constants.ApiConstant;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.service.EventService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.EVENT_URL)
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventVm>> listWebhooks() {
        return ResponseEntity.ok(eventService.findAllEvents());
    }

}

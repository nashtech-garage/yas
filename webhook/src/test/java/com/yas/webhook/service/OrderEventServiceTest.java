package com.yas.webhook.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.Event;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.enums.EventName;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderEventServiceTest {

    @Mock
    EventRepository eventRepository;
    @Mock
    WebhookEventNotificationRepository webhookEventNotificationRepository;
    @Mock
    WebhookService webhookService;
    @Mock
    WebhookApi webHookApi;
    @InjectMocks
    OrderEventService orderEventService;

    @Test
    void test_onOrderEvent_shouldNotException() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("op", "c");
        objectNode.set("after", objectMapper.createObjectNode());

        Event event = new Event();
        WebhookEvent webhookEvent = new WebhookEvent();
        Webhook webhook = new Webhook();
        List<WebhookEvent> webhookEvents = List.of(webhookEvent);

        event.setWebhookEvents(webhookEvents);
        webhookEvent.setWebhook(webhook);

        WebhookEventNotification notification = new WebhookEventNotification();
        notification.setWebhookEventId(1L);

        when(eventRepository.findByName(EventName.ON_ORDER_CREATED)).thenReturn(Optional.of(event));
        when(webhookEventNotificationRepository.save(any(WebhookEventNotification.class))).thenReturn(notification);

        orderEventService.onOrderEvent(objectNode);

        verify(webhookEventNotificationRepository).save(any(WebhookEventNotification.class));
    }
}

package com.yas.webhook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.yas.webhook.config.constants.MessageCode;
import com.yas.webhook.config.exception.NotFoundException;
import com.yas.webhook.model.Event;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.enumeration.EventName;
import com.yas.webhook.model.enumeration.Operation;
import com.yas.webhook.repository.EventRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductEventService {

    private final EventRepository eventRepository;
    private final WebhookService webhookService;

    public void onProductEvent(JsonNode updatedEvent) {
        String operation = updatedEvent.get("op").asText();
        if (!Objects.equals(operation, Operation.UPDATE.getName())) {
            return;
        }
        Event event = eventRepository.findByName(EventName.ON_PRODUCT_UPDATED)
                .orElseThrow(() -> new NotFoundException(MessageCode.EVENT_NOT_FOUND, EventName.ON_PRODUCT_UPDATED));
        List<WebhookEvent> hookEvents = event.getWebhookEvents();
        hookEvents.forEach(hookEvent -> {
            String url = hookEvent.getWebhook().getPayloadUrl();
            String secret = hookEvent.getWebhook().getSecret();
            JsonNode payload = updatedEvent.get("after");

            webhookService.notifyToWebhook(url, secret, payload);
        });
    }

}

package com.yas.webhook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.yas.webhook.config.constants.MessageCode;
import com.yas.webhook.config.exception.NotFoundException;
import com.yas.webhook.model.Event;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.enums.EventName;
import com.yas.webhook.model.enums.Operation;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventService extends AbstractWebhookEventNotificationService {

    private final EventRepository eventRepository;
    private final WebhookService webhookService;
    private final WebhookEventNotificationRepository webhookEventNotificationRepository;

    public void onOrderEvent(JsonNode updatedEvent) {
        Optional<EventName> optionalEventName = getEventName(updatedEvent);
        if (optionalEventName.isPresent()) {
            Event event = eventRepository.findByName(optionalEventName.get())
                    .orElseThrow(() -> new NotFoundException(MessageCode.EVENT_NOT_FOUND, optionalEventName.get()));
            List<WebhookEvent> hookEvents = event.getWebhookEvents();
            hookEvents.forEach(hookEvent -> {
                JsonNode payload = updatedEvent.get("after");
                Long notificationId = super.persistNotification(hookEvent.getId(), payload);

                WebhookEventNotificationDto dto = super.createNotificationDto(hookEvent, payload,
                    notificationId);
                webhookService.notifyToWebhook(dto);
            });
        }
    }

    private Optional<EventName> getEventName(JsonNode updatedEvent) {
        String operation = updatedEvent.get("op").asText();
        if (Objects.equals(operation, Operation.CREATE.getName())) {
            return Optional.of(EventName.ON_ORDER_CREATED);
        }
        if (Objects.equals(operation, Operation.UPDATE.getName())) {
            String orderStatusBefore = updatedEvent.path("before").get("order_status").asText();
            String orderStatusAfter = updatedEvent.path("after").get("order_status").asText();
            if (!orderStatusBefore.equals(orderStatusAfter)) {
                return Optional.of(EventName.ON_ORDER_STATUS_UPDATED);
            }
        }
        return Optional.empty();
    }

    @Override
    protected WebhookEventNotificationRepository getWebhookEventNotificationRepository() {
        return webhookEventNotificationRepository;
    }
}

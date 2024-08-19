package com.yas.webhook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.yas.webhook.config.constants.MessageCode;
import com.yas.webhook.config.exception.NotFoundException;
import com.yas.webhook.model.Event;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.enums.EventName;
import com.yas.webhook.model.enums.NotificationStatus;
import com.yas.webhook.model.enums.Operation;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductEventService extends AbstractWebhookEventNotificationService {

    private final EventRepository eventRepository;
    private final WebhookService webhookService;
    private final WebhookEventNotificationRepository webhookEventNotificationRepository;

    public void onProductEvent(JsonNode updatedEvent) {
        String operation = updatedEvent.get("op").asText();
        if (!Objects.equals(operation, Operation.UPDATE.getName())) {
            return;
        }
        Event event = eventRepository.findByName(EventName.ON_PRODUCT_UPDATED)
                .orElseThrow(() -> new NotFoundException(MessageCode.EVENT_NOT_FOUND, EventName.ON_PRODUCT_UPDATED));
        List<WebhookEvent> hookEvents = event.getWebhookEvents();
        hookEvents.forEach(hookEvent -> {
            JsonNode payload = updatedEvent.get("after");
            Long notificationId = super.persistNotification(hookEvent.getId(), payload);

            WebhookEventNotificationDto dto = super.createNotificationDto(hookEvent, payload,
                notificationId);
            webhookService.notifyToWebhook(dto);
        });
    }

    @Override
    protected WebhookEventNotificationRepository getWebhookEventNotificationRepository() {
        return webhookEventNotificationRepository;
    }
}

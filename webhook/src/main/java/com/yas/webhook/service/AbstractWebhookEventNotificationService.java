package com.yas.webhook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.enums.NotificationStatus;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import java.time.ZonedDateTime;

abstract class AbstractWebhookEventNotificationService {

    protected abstract WebhookEventNotificationRepository getWebhookEventNotificationRepository();

    protected Long persistNotification(Long webhookEventId, JsonNode payload) {
        WebhookEventNotification notification = new WebhookEventNotification();
        notification.setWebhookEventId(webhookEventId);
        notification.setPayload(payload.toString());
        notification.setNotificationStatus(NotificationStatus.NOTIFYING);
        notification.setCreatedOn(ZonedDateTime.now());
        WebhookEventNotification persistedNotification = getWebhookEventNotificationRepository().save(notification);
        return persistedNotification.getId();
    }

    protected WebhookEventNotificationDto createNotificationDto(WebhookEvent webhookEvent, JsonNode payload,
        Long notificationId) {
        String url = webhookEvent.getWebhook().getPayloadUrl();
        String secret = webhookEvent.getWebhook().getSecret();

        return WebhookEventNotificationDto.builder()
            .secret(secret)
            .payload(payload)
            .url(url)
            .notificationId(notificationId)
            .build();
    }
}

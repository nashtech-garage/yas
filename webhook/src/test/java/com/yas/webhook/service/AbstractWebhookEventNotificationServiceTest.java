package com.yas.webhook.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.enums.NotificationStatus;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractWebhookEventNotificationServiceTest {

    @Mock
    private WebhookEventNotificationRepository webhookEventNotificationRepository;

    private AbstractWebhookEventNotificationService service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new AbstractWebhookEventNotificationService() {
            @Override
            protected WebhookEventNotificationRepository getWebhookEventNotificationRepository() {
                return webhookEventNotificationRepository;
            }
        };
    }

    @Test
    void persistNotification_ShouldSaveAndReturnId() throws Exception {
        JsonNode payload = objectMapper.readTree("{\"key\":\"value\"}");
        WebhookEventNotification savedNotification = new WebhookEventNotification();
        savedNotification.setId(10L);

        when(webhookEventNotificationRepository.save(any(WebhookEventNotification.class))).thenAnswer(invocation -> {
            WebhookEventNotification notification = invocation.getArgument(0);
            assertThat(notification.getWebhookEventId()).isEqualTo(5L);
            assertThat(notification.getPayload()).isEqualTo("{\"key\":\"value\"}");
            assertThat(notification.getNotificationStatus()).isEqualTo(NotificationStatus.NOTIFYING);
            assertThat(notification.getCreatedOn()).isNotNull();
            return savedNotification;
        });

        Long id = service.persistNotification(5L, payload);

        assertThat(id).isEqualTo(10L);
    }

    @Test
    void createNotificationDto_ShouldReturnDto() throws Exception {
        JsonNode payload = objectMapper.readTree("{\"key\":\"value\"}");
        Webhook webhook = new Webhook();
        webhook.setPayloadUrl("http://example.com/webhook");
        webhook.setSecret("my-secret");

        WebhookEvent webhookEvent = new WebhookEvent();
        webhookEvent.setWebhook(webhook);

        WebhookEventNotificationDto dto = service.createNotificationDto(webhookEvent, payload, 10L);

        assertThat(dto.getSecret()).isEqualTo("my-secret");
        assertThat(dto.getPayload()).isEqualTo(payload);
        assertThat(dto.getUrl()).isEqualTo("http://example.com/webhook");
        assertThat(dto.getNotificationId()).isEqualTo(10L);
    }
}

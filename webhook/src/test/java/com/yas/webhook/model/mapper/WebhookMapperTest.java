package com.yas.webhook.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class WebhookMapperTest {

    private final WebhookMapper webhookMapper = Mappers.getMapper(WebhookMapper.class);

    @Test
    void toWebhookVmShouldMapPublicFields() {
        Webhook webhook = webhook(1L);

        WebhookVm result = webhookMapper.toWebhookVm(webhook);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPayloadUrl()).isEqualTo("https://example.test");
        assertThat(result.getContentType()).isEqualTo("application/json");
        assertThat(result.getIsActive()).isTrue();
    }

    @Test
    void toWebhookEventVmsShouldReturnEmptyListForNullOrEmptyEvents() {
        assertThat(webhookMapper.toWebhookEventVms(null)).isEmpty();
        assertThat(webhookMapper.toWebhookEventVms(List.of())).isEmpty();
    }

    @Test
    void toWebhookEventVmsShouldMapEventIds() {
        WebhookEvent webhookEvent = new WebhookEvent();
        webhookEvent.setEventId(5L);

        List<EventVm> result = webhookMapper.toWebhookEventVms(List.of(webhookEvent));

        assertThat(result).extracting(EventVm::getId).containsExactly(5L);
    }

    @Test
    void toWebhookListGetVmShouldMapContentAndPageMetadata() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Webhook> page = new PageImpl<>(List.of(webhook(1L)), pageRequest, 1);

        WebhookListGetVm result = webhookMapper.toWebhookListGetVm(page, 0, 10);

        assertThat(result.getWebhooks()).hasSize(1);
        assertThat(result.getPageNo()).isZero();
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isLast()).isTrue();
    }

    @Test
    void toCreatedWebhookShouldMapPostBody() {
        WebhookPostVm request = new WebhookPostVm(
            "https://example.test",
            "secret",
            "application/json",
            true,
            List.of(EventVm.builder().id(5L).build())
        );

        Webhook result = webhookMapper.toCreatedWebhook(request);

        assertThat(result.getPayloadUrl()).isEqualTo("https://example.test");
        assertThat(result.getSecret()).isEqualTo("secret");
        assertThat(result.getContentType()).isEqualTo("application/json");
        assertThat(result.getIsActive()).isTrue();
        assertThat(result.getWebhookEvents()).isNull();
    }

    @Test
    void toUpdatedWebhookShouldApplyMutableFields() {
        Webhook existing = webhook(1L);
        WebhookPostVm request = new WebhookPostVm("https://new.test", "new-secret", "ignored", false, List.of());

        Webhook result = webhookMapper.toUpdatedWebhook(existing, request);

        assertThat(result).isSameAs(existing);
        assertThat(result.getPayloadUrl()).isEqualTo("https://new.test");
        assertThat(result.getSecret()).isEqualTo("new-secret");
        assertThat(result.getContentType()).isEqualTo("application/json");
        assertThat(result.getIsActive()).isFalse();
    }

    @Test
    void toWebhookDetailVmShouldMapEventsButHideSecret() {
        Webhook webhook = webhook(1L);
        WebhookEvent webhookEvent = new WebhookEvent();
        webhookEvent.setEventId(5L);
        webhook.setWebhookEvents(List.of(webhookEvent));

        WebhookDetailVm result = webhookMapper.toWebhookDetailVm(webhook);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPayloadUrl()).isEqualTo("https://example.test");
        assertThat(result.getSecret()).isNull();
        assertThat(result.getEvents()).extracting(EventVm::getId).containsExactly(5L);
    }

    private static Webhook webhook(Long id) {
        Webhook webhook = new Webhook();
        webhook.setId(id);
        webhook.setPayloadUrl("https://example.test");
        webhook.setSecret("secret");
        webhook.setContentType("application/json");
        webhook.setIsActive(true);
        webhook.setWebhookEvents(List.of());
        return webhook;
    }
}

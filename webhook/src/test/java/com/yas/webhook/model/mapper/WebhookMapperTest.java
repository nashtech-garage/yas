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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class WebhookMapperTest {

    private final WebhookMapper webhookMapper = Mappers.getMapper(WebhookMapper.class);

    @Test
    void toWebhookVm_ShouldMapCorrectly() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        webhook.setPayloadUrl("http://test.com");
        webhook.setContentType("application/json");

        WebhookVm vm = webhookMapper.toWebhookVm(webhook);

        assertThat(vm).isNotNull();
        assertThat(vm.getId()).isEqualTo(1L);
        assertThat(vm.getPayloadUrl()).isEqualTo("http://test.com");
        assertThat(vm.getContentType()).isEqualTo("application/json");
    }

    @Test
    void toWebhookEventVms_ShouldMapCorrectly() {
        WebhookEvent event1 = new WebhookEvent();
        event1.setEventId(10L);
        
        WebhookEvent event2 = new WebhookEvent();
        event2.setEventId(20L);

        List<EventVm> eventVms = webhookMapper.toWebhookEventVms(List.of(event1, event2));

        assertThat(eventVms).hasSize(2);
        assertThat(eventVms.get(0).getId()).isEqualTo(10L);
        assertThat(eventVms.get(1).getId()).isEqualTo(20L);
    }

    @Test
    void toWebhookEventVms_WhenNull_ShouldReturnEmptyList() {
        List<EventVm> eventVms = webhookMapper.toWebhookEventVms(null);
        assertThat(eventVms).isEmpty();
    }

    @Test
    void toWebhookListGetVm_ShouldMapCorrectly() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        Page<Webhook> page = new PageImpl<>(List.of(webhook), PageRequest.of(0, 10), 1);

        WebhookListGetVm listGetVm = webhookMapper.toWebhookListGetVm(page, 0, 10);

        assertThat(listGetVm).isNotNull();
        assertThat(listGetVm.getPageNo()).isZero();
        assertThat(listGetVm.getPageSize()).isEqualTo(10);
        assertThat(listGetVm.getTotalElements()).isEqualTo(1);
        assertThat(listGetVm.getTotalPages()).isEqualTo(1);
        assertThat(listGetVm.isLast()).isTrue();
        assertThat(listGetVm.getWebhooks()).hasSize(1);
    }

    @Test
    void toUpdatedWebhook_ShouldMapCorrectly() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        webhook.setPayloadUrl("old.com");

        WebhookPostVm postVm = new WebhookPostVm("new.com", "secret", "application/json", true, List.of());

        Webhook updated = webhookMapper.toUpdatedWebhook(webhook, postVm);

        assertThat(updated.getId()).isEqualTo(1L);
        assertThat(updated.getPayloadUrl()).isEqualTo("new.com");
        assertThat(updated.getSecret()).isEqualTo("secret");
    }

    @Test
    void toCreatedWebhook_ShouldMapCorrectly() {
        WebhookPostVm postVm = new WebhookPostVm("new.com", "secret", "application/json", true, List.of());

        Webhook created = webhookMapper.toCreatedWebhook(postVm);

        assertThat(created.getPayloadUrl()).isEqualTo("new.com");
        assertThat(created.getSecret()).isEqualTo("secret");
    }

    @Test
    void toWebhookDetailVm_ShouldMapCorrectly() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        webhook.setPayloadUrl("url");
        
        WebhookEvent event = new WebhookEvent();
        event.setEventId(5L);
        webhook.setWebhookEvents(List.of(event));

        WebhookDetailVm detailVm = webhookMapper.toWebhookDetailVm(webhook);

        assertThat(detailVm).isNotNull();
        assertThat(detailVm.getId()).isEqualTo(1L);
        assertThat(detailVm.getPayloadUrl()).isEqualTo("url");
        assertThat(detailVm.getEvents()).hasSize(1);
        assertThat(detailVm.getEvents().get(0).getId()).isEqualTo(5L);
    }
}

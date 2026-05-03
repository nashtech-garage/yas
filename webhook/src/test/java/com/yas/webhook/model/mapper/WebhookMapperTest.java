package com.yas.webhook.model.mapper;

import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebhookMapperTest {

    // Dùng Mappers.getMapper để lấy instance thay vì gọi thẳng class Impl
    private final WebhookMapper mapper = Mappers.getMapper(WebhookMapper.class);

    @Test
    void testToWebhookVm() {
        assertNull(mapper.toWebhookVm(null));
        assertNotNull(mapper.toWebhookVm(new Webhook()));
    }

    @Test
    void testToWebhookEventVms() {
        // Test nhánh null hoặc empty
        assertTrue(mapper.toWebhookEventVms(null).isEmpty());
        assertTrue(mapper.toWebhookEventVms(Collections.emptyList()).isEmpty());

        // Test nhánh có dữ liệu
        WebhookEvent event = new WebhookEvent();
        event.setEventId(100L);
        List<EventVm> vms = mapper.toWebhookEventVms(List.of(event));
        assertEquals(1, vms.size());
        assertEquals(100L, vms.get(0).getId()); 
    }

    @Test
    void testToWebhookListGetVm() {
        Webhook webhook = new Webhook();
        Page<Webhook> page = new PageImpl<>(List.of(webhook), PageRequest.of(0, 10), 1);

        WebhookListGetVm vm = mapper.toWebhookListGetVm(page, 0, 10);
        assertNotNull(vm);
        assertEquals(1, vm.getWebhooks().size());
        assertEquals(0, vm.getPageNo());
        assertEquals(10, vm.getPageSize());
        assertEquals(1, vm.getTotalPages());
        assertEquals(1, vm.getTotalElements());
        assertTrue(vm.isLast()); 
    }

    @Test
    void testToUpdatedWebhook() {
        Webhook webhook = new Webhook();
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("http://test.com");
        postVm.setSecret("secret");
        postVm.setIsActive(true);

        Webhook result = mapper.toUpdatedWebhook(webhook, postVm);
        assertNotNull(result);
        assertEquals("http://test.com", result.getPayloadUrl());
        assertEquals("secret", result.getSecret());
        assertTrue(result.getIsActive());

        // Null checks
        assertNull(mapper.toUpdatedWebhook(null, null));
    }

    @Test
    void testToCreatedWebhook() {
        assertNull(mapper.toCreatedWebhook(null));
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("http://test.com");
        assertNotNull(mapper.toCreatedWebhook(postVm));
    }

    @Test
    void testToWebhookDetailVm() {
        assertNull(mapper.toWebhookDetailVm(null));

        Webhook webhook = new Webhook();
        WebhookEvent event = new WebhookEvent();
        event.setEventId(1L);
        webhook.setWebhookEvents(List.of(event));

        WebhookDetailVm vm = mapper.toWebhookDetailVm(webhook);
        assertNotNull(vm);
        assertEquals(1, vm.getEvents().size()); 
    }
}
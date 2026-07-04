package com.yas.webhook.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

class WebhookMapperTest {

    private WebhookMapper webhookMapper;

    @BeforeEach
    void setUp() {
        webhookMapper = new WebhookMapperImpl();
    }

    @Test
    void toWebhookVm() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        webhook.setPayloadUrl("http://test.com");
        webhook.setContentType("application/json");

        WebhookVm vm = webhookMapper.toWebhookVm(webhook);

        assertNotNull(vm);
        assertEquals(1L, vm.getId());
        assertEquals("http://test.com", vm.getPayloadUrl());
    }

    @Test
    void toWebhookEventVms() {
        WebhookEvent event = new WebhookEvent();
        event.setEventId(2L);

        List<EventVm> vms = webhookMapper.toWebhookEventVms(List.of(event));

        assertNotNull(vms);
        assertEquals(1, vms.size());
        assertEquals(2L, vms.get(0).getId());
    }

    @Test
    void toWebhookEventVms_Empty() {
        List<EventVm> vms = webhookMapper.toWebhookEventVms(null);
        assertTrue(vms.isEmpty());
    }

    @Test
    void toWebhookListGetVm() {
        Page<Webhook> page = mock(Page.class);
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        when(page.stream()).thenReturn(java.util.stream.Stream.of(webhook));
        when(page.getTotalPages()).thenReturn(1);
        when(page.getTotalElements()).thenReturn(1L);
        when(page.isLast()).thenReturn(true);

        WebhookListGetVm vm = webhookMapper.toWebhookListGetVm(page, 0, 10);

        assertNotNull(vm);
        assertEquals(0, vm.getPageNo());
        assertEquals(10, vm.getPageSize());
        assertEquals(1, vm.getTotalPages());
        assertEquals(1L, vm.getTotalElements());
        assertTrue(vm.isLast());
        assertEquals(1, vm.getWebhooks().size());
    }

    @Test
    void toUpdatedWebhook() {
        Webhook webhook = new Webhook();
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("http://new.com");
        postVm.setSecret("secret");
        postVm.setIsActive(true);

        Webhook updated = webhookMapper.toUpdatedWebhook(webhook, postVm);

        assertNotNull(updated);
        assertEquals("http://new.com", updated.getPayloadUrl());
        assertEquals("secret", updated.getSecret());
        assertTrue(updated.getIsActive());
    }

    @Test
    void toCreatedWebhook() {
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("http://new.com");
        postVm.setSecret("secret");
        postVm.setIsActive(true);

        Webhook created = webhookMapper.toCreatedWebhook(postVm);

        assertNotNull(created);
        assertEquals("http://new.com", created.getPayloadUrl());
        assertEquals("secret", created.getSecret());
        assertTrue(created.getIsActive());
    }

    @Test
    void toWebhookDetailVm() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        webhook.setPayloadUrl("http://new.com");
        
        WebhookEvent event = new WebhookEvent();
        event.setEventId(2L);
        webhook.setWebhookEvents(List.of(event));

        WebhookDetailVm vm = webhookMapper.toWebhookDetailVm(webhook);

        assertNotNull(vm);
        assertEquals(1L, vm.getId());
        assertEquals("http://new.com", vm.getPayloadUrl());
        assertEquals(1, vm.getEvents().size());
        assertEquals(2L, vm.getEvents().get(0).getId());
    }
}

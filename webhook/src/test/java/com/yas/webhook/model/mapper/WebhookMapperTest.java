package com.yas.webhook.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

class WebhookMapperTest {

    private WebhookMapper webhookMapper;

    @BeforeEach
    void setUp() {
        webhookMapper = Mappers.getMapper(WebhookMapper.class);
    }

    @Test
    void toWebhookEventVms_shouldMapCorrectly() {
        WebhookEvent event = new WebhookEvent();
        event.setEventId(1L);
        List<WebhookEvent> events = List.of(event);

        List<EventVm> result = webhookMapper.toWebhookEventVms(events);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void toWebhookEventVms_shouldReturnEmptyList_whenInputIsEmpty() {
        List<EventVm> result = webhookMapper.toWebhookEventVms(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void toWebhookListGetVm_shouldMapCorrectly() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        Page<Webhook> page = new PageImpl<>(List.of(webhook));

        WebhookListGetVm result = webhookMapper.toWebhookListGetVm(page, 0, 10);

        assertNotNull(result);
        assertEquals(0, result.getPageNo());
        assertEquals(10, result.getPageSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getWebhooks().size());
    }
}

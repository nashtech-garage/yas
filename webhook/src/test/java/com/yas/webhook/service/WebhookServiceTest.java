package com.yas.webhook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.enums.EventName;
import com.yas.webhook.model.enums.NotificationStatus;
import com.yas.webhook.model.mapper.WebhookMapper;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import com.yas.webhook.repository.WebhookEventRepository;
import com.yas.webhook.repository.WebhookRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    WebhookRepository webhookRepository;
    @Mock
    EventRepository eventRepository;
    @Mock
    WebhookMapper webhookMapper;
    @Mock
    WebhookEventRepository webhookEventRepository;
    @Mock
    WebhookEventNotificationRepository webhookEventNotificationRepository;
    @Mock
    WebhookApi webHookApi;

    @InjectMocks
    WebhookService webhookService;

    @Test
    void test_getPageableWebhooks_shouldReturnWebhookListGetVm() {
        int pageNo = 0;
        int pageSize = 10;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Webhook> webhooks = new PageImpl<>(List.of(new Webhook()));
        WebhookListGetVm webhookListGetVm = WebhookListGetVm.builder()
            .webhooks(List.of())
            .pageNo(0)
            .pageSize(10)
            .totalElements(1L)
            .totalPages(1L)
            .isLast(true)
            .build();

        when(webhookRepository.findAll(pageRequest)).thenReturn(webhooks);
        when(webhookMapper.toWebhookListGetVm(webhooks, pageNo, pageSize)).thenReturn(webhookListGetVm);

        WebhookListGetVm result = webhookService.getPageableWebhooks(pageNo, pageSize);

        assertEquals(webhookListGetVm, result);
    }

    @Test
    void test_findAllWebhooks_shouldReturnWebhookVmList() {
        Webhook webhook = new Webhook();
        WebhookVm webhookVm = new WebhookVm();
        webhookVm.setId(1L);
        webhookVm.setPayloadUrl("url");
        webhookVm.setIsActive(true);

        when(webhookRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(List.of(webhook));
        when(webhookMapper.toWebhookVm(webhook)).thenReturn(webhookVm);

        List<WebhookVm> result = webhookService.findAllWebhooks();

        assertEquals(1, result.size());
        assertEquals(webhookVm, result.get(0));
    }

    @Test
    void test_findById_shouldReturnWebhookDetailVm() {
        Webhook webhook = new Webhook();
        WebhookDetailVm webhookDetailVm = new WebhookDetailVm();
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(webhookDetailVm);

        WebhookDetailVm result = webhookService.findById(1L);

        assertEquals(webhookDetailVm, result);
    }

    @Test
    void test_findById_shouldThrowNotFoundException() {
        when(webhookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> webhookService.findById(1L));
    }

    @Test
    void test_create_shouldReturnWebhookDetailVm() {
        WebhookPostVm webhookPostVm = new WebhookPostVm("url", "secret", "application/json", true, List.of(new EventVm(1L, EventName.ON_PRODUCT_UPDATED)));
        Webhook createdWebhook = new Webhook();
        createdWebhook.setId(1L);
        WebhookDetailVm webhookDetailVm = new WebhookDetailVm();
        com.yas.webhook.model.Event event = new com.yas.webhook.model.Event();

        when(webhookMapper.toCreatedWebhook(webhookPostVm)).thenReturn(createdWebhook);
        when(webhookRepository.save(createdWebhook)).thenReturn(createdWebhook);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(webhookMapper.toWebhookDetailVm(createdWebhook)).thenReturn(webhookDetailVm);

        WebhookDetailVm result = webhookService.create(webhookPostVm);

        assertEquals(webhookDetailVm, result);
        verify(webhookEventRepository).saveAll(any());
    }

    @Test
    void test_create_withoutEvents_shouldReturnWebhookDetailVm() {
        WebhookPostVm webhookPostVm = new WebhookPostVm("url", "secret", "application/json", true, null);
        Webhook createdWebhook = new Webhook();
        createdWebhook.setId(1L);
        WebhookDetailVm webhookDetailVm = new WebhookDetailVm();

        when(webhookMapper.toCreatedWebhook(webhookPostVm)).thenReturn(createdWebhook);
        when(webhookRepository.save(createdWebhook)).thenReturn(createdWebhook);
        when(webhookMapper.toWebhookDetailVm(createdWebhook)).thenReturn(webhookDetailVm);

        WebhookDetailVm result = webhookService.create(webhookPostVm);

        assertEquals(webhookDetailVm, result);
        verify(webhookEventRepository, never()).saveAll(any());
    }

    @Test
    void test_update_shouldUpdateWebhook() {
        Long id = 1L;
        WebhookPostVm webhookPostVm = new WebhookPostVm("url", "secret", "application/json", true, List.of(new EventVm(1L, EventName.ON_PRODUCT_UPDATED)));
        Webhook existedWebhook = new Webhook();
        existedWebhook.setWebhookEvents(List.of());
        Webhook updatedWebhook = new Webhook();
        com.yas.webhook.model.Event event = new com.yas.webhook.model.Event();

        when(webhookRepository.findById(id)).thenReturn(Optional.of(existedWebhook));
        when(webhookMapper.toUpdatedWebhook(existedWebhook, webhookPostVm)).thenReturn(updatedWebhook);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        webhookService.update(webhookPostVm, id);

        verify(webhookRepository).save(updatedWebhook);
        verify(webhookEventRepository).deleteAll(any());
        verify(webhookEventRepository).saveAll(any());
    }

    @Test
    void test_update_shouldThrowNotFoundException() {
        Long id = 1L;
        WebhookPostVm webhookPostVm = new WebhookPostVm("url", "secret", "application/json", true, null);
        when(webhookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> webhookService.update(webhookPostVm, id));
    }

    @Test
    void test_delete_shouldDeleteWebhook() {
        when(webhookRepository.existsById(1L)).thenReturn(true);

        webhookService.delete(1L);

        verify(webhookRepository).deleteById(1L);
        verify(webhookEventRepository).deleteByWebhookId(1L);
    }

    @Test
    void test_delete_shouldThrowNotFoundException() {
        when(webhookRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> webhookService.delete(1L));
    }

    @Test
    @SuppressWarnings("unchecked")
    void test_notifyToWebhook_ShouldNotException() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode payload = objectMapper.createObjectNode();

        WebhookEventNotificationDto notificationDto = WebhookEventNotificationDto
            .builder()
            .notificationId(1L)
            .url("http://test.com")
            .secret("secret")
            .payload(payload)
            .build();

        WebhookEventNotification notification = new WebhookEventNotification();
        when(webhookEventNotificationRepository.findById(notificationDto.getNotificationId()))
            .thenReturn(Optional.of(notification));

        webhookService.notifyToWebhook(notificationDto);

        verify(webhookEventNotificationRepository).save(notification);
        verify(webHookApi).notify(notificationDto.getUrl(), notificationDto.getSecret(), notificationDto.getPayload());
        assertEquals(NotificationStatus.NOTIFIED, notification.getNotificationStatus());
    }

    @Test
    void test_notifyToWebhook_shouldThrowExceptionWhenNotificationNotFound() {
        WebhookEventNotificationDto notificationDto = WebhookEventNotificationDto.builder().notificationId(1L).build();
        when(webhookEventNotificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> webhookService.notifyToWebhook(notificationDto));
    }

    @Test
    void test_initializeWebhookEvents_shouldThrowNotFoundExceptionWhenEventNotFound() {
        WebhookPostVm webhookPostVm = new WebhookPostVm("url", "secret", "application/json", true, List.of(new EventVm(1L, EventName.ON_PRODUCT_UPDATED)));
        Webhook createdWebhook = new Webhook();
        createdWebhook.setId(1L);

        when(webhookMapper.toCreatedWebhook(webhookPostVm)).thenReturn(createdWebhook);
        when(webhookRepository.save(createdWebhook)).thenReturn(createdWebhook);
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> webhookService.create(webhookPostVm));
    }
}

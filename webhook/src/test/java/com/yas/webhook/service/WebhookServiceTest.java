package com.yas.webhook.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.mapper.WebhookMapper;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventRepository;
import com.yas.webhook.repository.WebhookRepository;
import com.yas.webhook.model.Event;
import com.yas.webhook.model.enums.EventName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    WebhookEventNotificationRepository webhookEventNotificationRepository;
    @Mock
    WebhookApi webHookApi;
    @Mock
    WebhookRepository webhookRepository;
    @Mock
    EventRepository eventRepository;
    @Mock
    WebhookEventRepository webhookEventRepository;
    @Mock
    WebhookMapper webhookMapper;

    @InjectMocks
    WebhookService webhookService;

    @Test
    void test_notifyToWebhook_ShouldNotException() {

        WebhookEventNotificationDto notificationDto = WebhookEventNotificationDto
            .builder()
            .notificationId(1L)
            .url("")
            .secret("")
            .build();

        WebhookEventNotification notification = new WebhookEventNotification();
        when(webhookEventNotificationRepository.findById(notificationDto.getNotificationId()))
            .thenReturn(Optional.of(notification));

        webhookService.notifyToWebhook(notificationDto);

        verify(webhookEventNotificationRepository).save(notification);
        verify(webHookApi).notify(notificationDto.getUrl(), notificationDto.getSecret(), notificationDto.getPayload());
    }

    @Test
    void getPageableWebhooks_shouldReturnPage() {
        Webhook webhook = new Webhook();
        Page<Webhook> page = new PageImpl<>(List.of(webhook));
        when(webhookRepository.findAll(any(PageRequest.class))).thenReturn(page);
        
        WebhookListGetVm expectedVm = WebhookListGetVm.builder()
                .webhooks(List.of())
                .pageNo(0)
                .pageSize(10)
                .totalElements(0)
                .totalPages(0)
                .isLast(true)
                .build();
        when(webhookMapper.toWebhookListGetVm(page, 0, 10)).thenReturn(expectedVm);

        WebhookListGetVm result = webhookService.getPageableWebhooks(0, 10);
        
        assertEquals(expectedVm, result);
    }

    @Test
    void findAllWebhooks_shouldReturnList() {
        Webhook webhook = new Webhook();
        when(webhookRepository.findAll(any(Sort.class))).thenReturn(List.of(webhook));
        WebhookVm webhookVm = new WebhookVm();
        webhookVm.setId(1L);
        when(webhookMapper.toWebhookVm(any())).thenReturn(webhookVm);

        List<WebhookVm> result = webhookService.findAllWebhooks();
        
        assertEquals(1, result.size());
    }

    @Test
    void findById_whenExists_shouldReturnWebhook() {
        Webhook webhook = new Webhook();
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
        WebhookDetailVm detailVm = new WebhookDetailVm();
        detailVm.setId(1L);
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(detailVm);

        WebhookDetailVm result = webhookService.findById(1L);
        
        assertNotNull(result);
    }

    @Test
    void findById_whenNotExists_shouldThrowNotFoundException() {
        when(webhookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> webhookService.findById(1L));
    }

    @Test
    void create_shouldSaveAndReturnWebhook() {
        WebhookPostVm postVm = new WebhookPostVm("url", "secret", "secret", true, List.of(new EventVm(1L, EventName.ON_ORDER_CREATED)));
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(webhook);
        when(webhookRepository.save(webhook)).thenReturn(webhook);
        
        Event event = new Event();
        event.setId(1L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        
        WebhookDetailVm expectedVm = new WebhookDetailVm();
        expectedVm.setId(1L);
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(expectedVm);

        WebhookDetailVm result = webhookService.create(postVm);
        
        assertNotNull(result);
        verify(webhookEventRepository).saveAll(any());
    }

    @Test
    void update_whenExists_shouldUpdateAndSave() {
        WebhookPostVm postVm = new WebhookPostVm("url", "secret", "secret", true, List.of(new EventVm(1L, EventName.ON_ORDER_CREATED)));
        Webhook existingWebhook = new Webhook();
        existingWebhook.setWebhookEvents(List.of());
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(existingWebhook));
        
        Webhook updatedWebhook = new Webhook();
        when(webhookMapper.toUpdatedWebhook(existingWebhook, postVm)).thenReturn(updatedWebhook);
        
        Event event = new Event();
        event.setId(1L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        webhookService.update(postVm, 1L);
        
        verify(webhookRepository).save(updatedWebhook);
        verify(webhookEventRepository).saveAll(any());
    }

    @Test
    void delete_whenExists_shouldDelete() {
        when(webhookRepository.existsById(1L)).thenReturn(true);

        webhookService.delete(1L);
        
        verify(webhookEventRepository).deleteByWebhookId(1L);
        verify(webhookRepository).deleteById(1L);
    }
}

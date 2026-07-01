package com.yas.webhook.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import com.yas.webhook.repository.WebhookRepository;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventRepository;
import com.yas.webhook.model.mapper.WebhookMapper;
import static org.mockito.ArgumentMatchers.any;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    WebhookRepository webhookRepository;
    @Mock
    EventRepository eventRepository;
    @Mock
    WebhookEventRepository webhookEventRepository;
    @Mock
    WebhookMapper webhookMapper;
    @Mock
    WebhookEventNotificationRepository webhookEventNotificationRepository;
    @Mock
    WebhookApi webHookApi;

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
    void getPageableWebhooks_ReturnsWebhookListGetVm() {
        int pageNo = 0;
        int pageSize = 10;
        org.springframework.data.domain.Page<com.yas.webhook.model.Webhook> page = mock(org.springframework.data.domain.Page.class);
        when(webhookRepository.findAll(any(org.springframework.data.domain.PageRequest.class))).thenReturn(page);
        com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm expectedVm = mock(com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm.class);
        when(webhookMapper.toWebhookListGetVm(page, pageNo, pageSize)).thenReturn(expectedVm);

        com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm result = webhookService.getPageableWebhooks(pageNo, pageSize);

        org.junit.jupiter.api.Assertions.assertEquals(expectedVm, result);
        verify(webhookRepository).findAll(any(org.springframework.data.domain.PageRequest.class));
    }

    @Test
    void findAllWebhooks_ReturnsWebhookVmList() {
        com.yas.webhook.model.Webhook webhook1 = new com.yas.webhook.model.Webhook();
        com.yas.webhook.model.Webhook webhook2 = new com.yas.webhook.model.Webhook();
        when(webhookRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(java.util.List.of(webhook1, webhook2));
        
        com.yas.webhook.model.viewmodel.webhook.WebhookVm vm1 = new com.yas.webhook.model.viewmodel.webhook.WebhookVm();
        com.yas.webhook.model.viewmodel.webhook.WebhookVm vm2 = new com.yas.webhook.model.viewmodel.webhook.WebhookVm();
        when(webhookMapper.toWebhookVm(webhook1)).thenReturn(vm1);
        when(webhookMapper.toWebhookVm(webhook2)).thenReturn(vm2);

        java.util.List<com.yas.webhook.model.viewmodel.webhook.WebhookVm> result = webhookService.findAllWebhooks();

        org.junit.jupiter.api.Assertions.assertEquals(2, result.size());
        verify(webhookRepository).findAll(any(org.springframework.data.domain.Sort.class));
    }

    @Test
    void findById_WebhookNotFound_ThrowsNotFoundException() {
        when(webhookRepository.findById(1L)).thenReturn(Optional.empty());

        com.yas.commonlibrary.exception.NotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(
            com.yas.commonlibrary.exception.NotFoundException.class,
            () -> webhookService.findById(1L)
        );
        org.junit.jupiter.api.Assertions.assertEquals(com.yas.webhook.config.constants.MessageCode.WEBHOOK_NOT_FOUND, exception.getMessage());
    }

    @Test
    void findById_WebhookExists_ReturnsDetailVm() {
        com.yas.webhook.model.Webhook webhook = new com.yas.webhook.model.Webhook();
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
        com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm detailVm = new com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm();
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(detailVm);

        com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm result = webhookService.findById(1L);

        org.junit.jupiter.api.Assertions.assertEquals(detailVm, result);
    }

    @Test
    void create_WithoutEvents_ReturnsDetailVm() {
        com.yas.webhook.model.viewmodel.webhook.WebhookPostVm postVm = new com.yas.webhook.model.viewmodel.webhook.WebhookPostVm();
        com.yas.webhook.model.Webhook mappedWebhook = new com.yas.webhook.model.Webhook();
        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(mappedWebhook);
        com.yas.webhook.model.Webhook savedWebhook = new com.yas.webhook.model.Webhook();
        savedWebhook.setId(1L);
        when(webhookRepository.save(mappedWebhook)).thenReturn(savedWebhook);
        com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm detailVm = new com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm();
        when(webhookMapper.toWebhookDetailVm(savedWebhook)).thenReturn(detailVm);

        com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm result = webhookService.create(postVm);

        org.junit.jupiter.api.Assertions.assertEquals(detailVm, result);
    }

    @Test
    void create_WithEvents_SavesEventsAndReturnsDetailVm() {
        com.yas.webhook.model.viewmodel.webhook.WebhookPostVm postVm = new com.yas.webhook.model.viewmodel.webhook.WebhookPostVm();
        com.yas.webhook.model.viewmodel.webhook.EventVm eventVm = new com.yas.webhook.model.viewmodel.webhook.EventVm();
        eventVm.setId(2L);
        postVm.setEvents(java.util.List.of(eventVm));

        com.yas.webhook.model.Webhook mappedWebhook = new com.yas.webhook.model.Webhook();
        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(mappedWebhook);
        com.yas.webhook.model.Webhook savedWebhook = new com.yas.webhook.model.Webhook();
        savedWebhook.setId(1L);
        when(webhookRepository.save(mappedWebhook)).thenReturn(savedWebhook);

        when(eventRepository.findById(2L)).thenReturn(Optional.of(new com.yas.webhook.model.Event()));
        when(webhookEventRepository.saveAll(org.mockito.ArgumentMatchers.anyList())).thenReturn(java.util.List.of(new com.yas.webhook.model.WebhookEvent()));

        com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm detailVm = new com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm();
        when(webhookMapper.toWebhookDetailVm(savedWebhook)).thenReturn(detailVm);

        com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm result = webhookService.create(postVm);

        org.junit.jupiter.api.Assertions.assertEquals(detailVm, result);
        verify(webhookEventRepository).saveAll(org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    void update_WebhookNotFound_ThrowsNotFoundException() {
        when(webhookRepository.findById(1L)).thenReturn(Optional.empty());

        com.yas.webhook.model.viewmodel.webhook.WebhookPostVm postVm = new com.yas.webhook.model.viewmodel.webhook.WebhookPostVm();
        org.junit.jupiter.api.Assertions.assertThrows(
            com.yas.commonlibrary.exception.NotFoundException.class,
            () -> webhookService.update(postVm, 1L)
        );
    }

    @Test
    void update_WithEvents_UpdatesWebhookAndEvents() {
        com.yas.webhook.model.Webhook existingWebhook = new com.yas.webhook.model.Webhook();
        existingWebhook.setWebhookEvents(java.util.List.of());
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(existingWebhook));

        com.yas.webhook.model.viewmodel.webhook.WebhookPostVm postVm = new com.yas.webhook.model.viewmodel.webhook.WebhookPostVm();
        com.yas.webhook.model.viewmodel.webhook.EventVm eventVm = new com.yas.webhook.model.viewmodel.webhook.EventVm();
        eventVm.setId(2L);
        postVm.setEvents(java.util.List.of(eventVm));

        com.yas.webhook.model.Webhook updatedWebhook = new com.yas.webhook.model.Webhook();
        when(webhookMapper.toUpdatedWebhook(existingWebhook, postVm)).thenReturn(updatedWebhook);
        
        when(eventRepository.findById(2L)).thenReturn(Optional.of(new com.yas.webhook.model.Event()));

        webhookService.update(postVm, 1L);

        verify(webhookRepository).save(updatedWebhook);
        verify(webhookEventRepository).deleteAll(org.mockito.ArgumentMatchers.anyList());
        verify(webhookEventRepository).saveAll(org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    void delete_WebhookNotFound_ThrowsNotFoundException() {
        when(webhookRepository.existsById(1L)).thenReturn(false);

        org.junit.jupiter.api.Assertions.assertThrows(
            com.yas.commonlibrary.exception.NotFoundException.class,
            () -> webhookService.delete(1L)
        );
    }

    @Test
    void delete_WebhookExists_DeletesWebhookAndEvents() {
        when(webhookRepository.existsById(1L)).thenReturn(true);

        webhookService.delete(1L);

        verify(webhookEventRepository).deleteByWebhookId(1L);
        verify(webhookRepository).deleteById(1L);
    }
}

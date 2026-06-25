package com.yas.webhook.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.mapper.WebhookMapper;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import com.yas.webhook.repository.WebhookEventRepository;
import com.yas.webhook.repository.WebhookRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yas.webhook.model.enums.EventName;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    WebhookRepository webhookRepository;
    @Mock
    EventRepository eventRepository;
    @Mock
    WebhookEventRepository webhookEventRepository;
    @Mock
    WebhookEventNotificationRepository webhookEventNotificationRepository;
    @Mock
    WebhookMapper webhookMapper;
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
    void getPageableWebhooks_ShouldReturnVm() {
        Page<Webhook> page = org.mockito.Mockito.mock(Page.class);
        when(webhookRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(webhookMapper.toWebhookListGetVm(any(), eq(0), eq(10))).thenReturn(WebhookListGetVm.builder().build());

        webhookService.getPageableWebhooks(0, 10);

        verify(webhookRepository).findAll(any(PageRequest.class));
    }

    @Test
    void findAllWebhooks_ShouldReturnList() {
        when(webhookRepository.findAll(any(Sort.class))).thenReturn(java.util.List.of());

        webhookService.findAllWebhooks();

        verify(webhookRepository).findAll(any(Sort.class));
    }

    @Test
    void findById_ShouldReturnVm() {
        Webhook webhook = new Webhook();
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(new WebhookDetailVm());

        webhookService.findById(1L);

        verify(webhookRepository).findById(1L);
    }

    @Test
    void create_ShouldReturnVm() {
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setEvents(java.util.List.of(new EventVm(1L, EventName.ON_PRODUCT_UPDATED)));
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(webhook);
        when(webhookRepository.save(any())).thenReturn(webhook);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(new Event()));
        when(webhookMapper.toWebhookDetailVm(any())).thenReturn(new WebhookDetailVm());

        webhookService.create(postVm);

        verify(webhookRepository).save(any());
        verify(webhookEventRepository).saveAll(any());
    }

    @Test
    void update_ShouldNotException() {
        WebhookPostVm postVm = new WebhookPostVm();
        Webhook webhook = new Webhook();
        webhook.setWebhookEvents(java.util.ArrayList.class.cast(new java.util.ArrayList<WebhookEvent>()));
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
        when(webhookMapper.toUpdatedWebhook(any(), any())).thenReturn(webhook);

        webhookService.update(postVm, 1L);

        verify(webhookRepository).save(webhook);
    }

    @Test
    void delete_ShouldNotException() {
        when(webhookRepository.existsById(1L)).thenReturn(true);

        webhookService.delete(1L);

        verify(webhookRepository).deleteById(1L);
        verify(webhookEventRepository).deleteByWebhookId(1L);
    }
}

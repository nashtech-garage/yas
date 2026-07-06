package com.yas.webhook.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.Event;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
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
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
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
    void getPageableWebhooksShouldDelegateToRepositoryAndMapper() {
        PageRequest expectedPageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageImpl<Webhook> page = new PageImpl<>(List.of(webhook(1L)));
        WebhookListGetVm mapped = WebhookListGetVm.builder().pageNo(0).pageSize(10).build();
        when(webhookRepository.findAll(expectedPageRequest)).thenReturn(page);
        when(webhookMapper.toWebhookListGetVm(page, 0, 10)).thenReturn(mapped);

        webhookService.getPageableWebhooks(0, 10);

        verify(webhookMapper).toWebhookListGetVm(page, 0, 10);
    }

    @Test
    void findAllWebhooksShouldMapAllWebhooks() {
        Webhook webhook = webhook(1L);
        WebhookVm webhookVm = new WebhookVm();
        when(webhookRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(List.of(webhook));
        when(webhookMapper.toWebhookVm(webhook)).thenReturn(webhookVm);

        webhookService.findAllWebhooks();

        verify(webhookMapper).toWebhookVm(webhook);
    }

    @Test
    void findByIdShouldThrowWhenWebhookMissing() {
        when(webhookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> webhookService.findById(1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findByIdShouldMapExistingWebhook() {
        Webhook webhook = webhook(1L);
        WebhookDetailVm detailVm = new WebhookDetailVm();
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(detailVm);

        webhookService.findById(1L);

        verify(webhookMapper).toWebhookDetailVm(webhook);
    }

    @Test
    void createShouldSaveWebhookWithoutEvents() {
        WebhookPostVm request = new WebhookPostVm("https://example.test", "secret", "json", true, List.of());
        Webhook mapped = webhook(null);
        Webhook saved = webhook(1L);
        WebhookDetailVm detailVm = new WebhookDetailVm();
        when(webhookMapper.toCreatedWebhook(request)).thenReturn(mapped);
        when(webhookRepository.save(mapped)).thenReturn(saved);
        when(webhookMapper.toWebhookDetailVm(saved)).thenReturn(detailVm);

        webhookService.create(request);

        verify(webhookEventRepository, never()).saveAll(any());
        verify(webhookMapper).toWebhookDetailVm(saved);
    }

    @Test
    void createShouldSaveWebhookEventsWhenPresent() {
        Event event = new Event();
        event.setId(5L);
        WebhookPostVm request = new WebhookPostVm(
            "https://example.test",
            "secret",
            "json",
            true,
            List.of(EventVm.builder().id(5L).build())
        );
        Webhook mapped = webhook(null);
        Webhook saved = webhook(1L);
        when(webhookMapper.toCreatedWebhook(request)).thenReturn(mapped);
        when(webhookRepository.save(mapped)).thenReturn(saved);
        when(eventRepository.findById(5L)).thenReturn(Optional.of(event));
        when(webhookEventRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(webhookMapper.toWebhookDetailVm(saved)).thenReturn(new WebhookDetailVm());

        webhookService.create(request);

        verify(webhookEventRepository).saveAll(any());
    }

    @Test
    void createShouldThrowWhenEventMissing() {
        WebhookPostVm request = new WebhookPostVm(
            "https://example.test",
            "secret",
            "json",
            true,
            List.of(EventVm.builder().id(5L).build())
        );
        Webhook saved = webhook(1L);
        when(webhookMapper.toCreatedWebhook(request)).thenReturn(webhook(null));
        when(webhookRepository.save(any())).thenReturn(saved);
        when(eventRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> webhookService.create(request))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateShouldThrowWhenWebhookMissing() {
        when(webhookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> webhookService.update(new WebhookPostVm(), 1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateShouldSaveAndReplaceEvents() {
        Webhook existing = webhook(1L);
        WebhookEvent oldEvent = new WebhookEvent();
        existing.setWebhookEvents(List.of(oldEvent));
        WebhookPostVm request = new WebhookPostVm(
            "https://example.test",
            "secret",
            "json",
            true,
            List.of(EventVm.builder().id(5L).build())
        );
        Webhook updated = webhook(1L);
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(webhookMapper.toUpdatedWebhook(existing, request)).thenReturn(updated);
        when(eventRepository.findById(5L)).thenReturn(Optional.of(new Event()));

        webhookService.update(request, 1L);

        verify(webhookRepository).save(updated);
        verify(webhookEventRepository).deleteAll(List.of(oldEvent));
        verify(webhookEventRepository).saveAll(any());
    }

    @Test
    void deleteShouldThrowWhenWebhookMissing() {
        when(webhookRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> webhookService.delete(1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteShouldRemoveWebhookAndEvents() {
        when(webhookRepository.existsById(1L)).thenReturn(true);

        webhookService.delete(1L);

        verify(webhookEventRepository).deleteByWebhookId(1L);
        verify(webhookRepository).deleteById(1L);
    }

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

    private static Webhook webhook(Long id) {
        Webhook webhook = new Webhook();
        webhook.setId(id);
        webhook.setPayloadUrl("https://example.test");
        webhook.setSecret("secret");
        webhook.setIsActive(true);
        webhook.setWebhookEvents(List.of());
        return webhook;
    }
}

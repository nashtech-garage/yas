package com.yas.webhook.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.mapper.WebhookMapper;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    private WebhookRepository webhookRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private WebhookEventRepository webhookEventRepository;
    @Mock
    private WebhookEventNotificationRepository webhookEventNotificationRepository;
    @Mock
    private WebhookMapper webhookMapper;
    @Mock
    private WebhookApi webHookApi;

    @InjectMocks
    private WebhookService webhookService;

    private Webhook webhook;
    private WebhookPostVm webhookPostVm;
    private WebhookDetailVm webhookDetailVm;

    @BeforeEach
    void setUp() {
        webhook = new Webhook();
        webhook.setId(1L);

        webhookPostVm = new WebhookPostVm();
        webhookPostVm.setEvents(List.of()); // Empty list for simple tests

        webhookDetailVm = new WebhookDetailVm(1L, "payloadUrl", "secret", "isActive", null);
    }

    @Test
    void test_notifyToWebhook_ShouldNotException() {
        tools.jackson.databind.ObjectMapper objectMapper = new tools.jackson.databind.ObjectMapper();
        tools.jackson.databind.JsonNode payload = objectMapper.createObjectNode();

        WebhookEventNotificationDto notificationDto = WebhookEventNotificationDto
            .builder()
            .notificationId(1L)
            .url("http://example.com")
            .secret("secret")
            .payload(payload)
            .build();

        WebhookEventNotification notification = new WebhookEventNotification();
        when(webhookEventNotificationRepository.findById(notificationDto.getNotificationId()))
            .thenReturn(Optional.of(notification));

        webhookService.notifyToWebhook(notificationDto);

        verify(webhookEventNotificationRepository).save(notification);
        verify(webHookApi).notify(notificationDto.getUrl(), notificationDto.getSecret(), notificationDto.getPayload());
    }

    @Test
    void findAllWebhooks_ReturnsListOfWebhookVm() {
        when(webhookRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(List.of(webhook));
        WebhookVm webhookVm = new WebhookVm(1L, "payloadUrl");
        when(webhookMapper.toWebhookVm(webhook)).thenReturn(webhookVm);

        List<WebhookVm> result = webhookService.findAllWebhooks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void findById_WhenIdIsValid_ReturnsWebhookDetailVm() {
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(webhookDetailVm);

        WebhookDetailVm result = webhookService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void findById_WhenIdIsInvalid_ThrowsNotFoundException() {
        when(webhookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> webhookService.findById(2L));
    }

    @Test
    void create_WhenValidData_SavesAndReturnsWebhookDetailVm() {
        when(webhookMapper.toCreatedWebhook(webhookPostVm)).thenReturn(webhook);
        when(webhookRepository.save(webhook)).thenReturn(webhook);
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(webhookDetailVm);

        WebhookDetailVm result = webhookService.create(webhookPostVm);

        assertThat(result.id()).isEqualTo(1L);
        verify(webhookRepository).save(webhook);
    }

    @Test
    void update_WhenIdIsValid_UpdatesWebhook() {
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
        when(webhookMapper.toUpdatedWebhook(webhook, webhookPostVm)).thenReturn(webhook);

        webhookService.update(webhookPostVm, 1L);

        verify(webhookRepository).save(webhook);
    }

    @Test
    void update_WhenIdIsInvalid_ThrowsNotFoundException() {
        when(webhookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> webhookService.update(webhookPostVm, 2L));
    }

    @Test
    void delete_WhenIdIsValid_DeletesWebhook() {
        when(webhookRepository.existsById(1L)).thenReturn(true);

        webhookService.delete(1L);

        verify(webhookEventRepository).deleteByWebhookId(1L);
        verify(webhookRepository).deleteById(1L);
    }

    @Test
    void delete_WhenIdIsInvalid_ThrowsNotFoundException() {
        when(webhookRepository.existsById(2L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> webhookService.delete(2L));
    }

    @Test
    void getPageableWebhooks_ReturnsWebhookListGetVm() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Webhook> webhooks = new PageImpl<>(List.of(webhook));
        WebhookListGetVm listGetVm = new WebhookListGetVm(List.of(), 0, 10, 1, 1, true);

        when(webhookRepository.findAll(pageRequest)).thenReturn(webhooks);
        when(webhookMapper.toWebhookListGetVm(webhooks, 0, 10)).thenReturn(listGetVm);

        WebhookListGetVm result = webhookService.getPageableWebhooks(0, 10);

        assertThat(result.pageNo()).isEqualTo(0);
        assertThat(result.totalElements()).isEqualTo(1);
    }
}

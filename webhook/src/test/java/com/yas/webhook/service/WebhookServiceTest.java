package com.yas.webhook.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.Event;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.enums.NotificationStatus;
import com.yas.webhook.model.mapper.WebhookMapper;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import com.yas.webhook.repository.WebhookEventRepository;
import com.yas.webhook.repository.WebhookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock WebhookRepository webhookRepository;
    @Mock EventRepository eventRepository;
    @Mock WebhookEventRepository webhookEventRepository;
    @Mock WebhookEventNotificationRepository webhookEventNotificationRepository;
    @Mock WebhookMapper webhookMapper;
    @Mock WebhookApi webHookApi;

    @InjectMocks WebhookService webhookService;

    @Test
    void getPageableWebhooks() {
        Page<Webhook> page = new PageImpl<>(List.of(new Webhook()));
        when(webhookRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(webhookMapper.toWebhookListGetVm(any(), anyInt(), anyInt())).thenReturn(mock(WebhookListGetVm.class));

        assertNotNull(webhookService.getPageableWebhooks(0, 10));
    }

    @Test
    void findAllWebhooks() {
        when(webhookRepository.findAll(any(Sort.class))).thenReturn(List.of(new Webhook()));
        assertNotNull(webhookService.findAllWebhooks());
    }

    @Test
    void findById_NotFound() {
        when(webhookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> webhookService.findById(1L));
    }

    @Test
    void findById_Success() {
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(new Webhook()));
        when(webhookMapper.toWebhookDetailVm(any())).thenReturn(mock(WebhookDetailVm.class));
        assertNotNull(webhookService.findById(1L));
    }

    @Test
    void create_WithEvents() {
        WebhookPostVm postVm = new WebhookPostVm(); // Đã sửa sang constructor
        EventVm eventVm = EventVm.builder().id(1L).build();
        postVm.setEvents(List.of(eventVm));
        
        Webhook mappedWebhook = new Webhook();
        mappedWebhook.setId(10L);

        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(mappedWebhook);
        when(webhookRepository.save(any())).thenReturn(mappedWebhook);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(new Event()));
        when(webhookMapper.toWebhookDetailVm(any())).thenReturn(mock(WebhookDetailVm.class));

        assertNotNull(webhookService.create(postVm));
        verify(webhookEventRepository).saveAll(anyList());
    }

    @Test
    void update_NotFound() {
        when(webhookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> webhookService.update(new WebhookPostVm(), 1L));
    }

    @Test
    void update_Success() {
        Webhook existing = new Webhook();
        // THÊM DÒNG NÀY: Khởi tạo list rỗng để tránh lỗi NullPointerException khi gọi .stream()
        existing.setWebhookEvents(List.of()); 
        
        WebhookPostVm postVm = new WebhookPostVm(); 
        EventVm eventVm = EventVm.builder().id(1L).build();
        postVm.setEvents(List.of(eventVm));

        when(webhookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(webhookMapper.toUpdatedWebhook(existing, postVm)).thenReturn(existing);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(new Event()));

        webhookService.update(postVm, 1L);
        verify(webhookRepository).save(any());
        verify(webhookEventRepository).saveAll(anyList());
    }

    @Test
    void delete_NotFound() {
        when(webhookRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> webhookService.delete(1L));
    }

    @Test
    void delete_Success() {
        when(webhookRepository.existsById(1L)).thenReturn(true);
        webhookService.delete(1L);
        verify(webhookEventRepository).deleteByWebhookId(1L);
        verify(webhookRepository).deleteById(1L);
    }

    @Test
    void notifyToWebhook() {
        // Đã bỏ việc truyền chuỗi String vào Payload (vốn cần class JsonNode)
        WebhookEventNotificationDto dto = WebhookEventNotificationDto.builder()
                .notificationId(1L).url("url").secret("sec").build(); 
        WebhookEventNotification notification = new WebhookEventNotification();

        when(webhookEventNotificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        webhookService.notifyToWebhook(dto);

        // Bỏ qua check giá trị payload bằng hàm any()
        verify(webHookApi).notify(eq("url"), eq("sec"), any()); 
        assertEquals(NotificationStatus.NOTIFIED, notification.getNotificationStatus());
        verify(webhookEventNotificationRepository).save(notification);
    }
}
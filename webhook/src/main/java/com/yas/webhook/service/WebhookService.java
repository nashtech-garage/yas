package com.yas.webhook.service;

import com.yas.webhook.config.constants.MessageCode;
import com.yas.webhook.config.exception.NotFoundException;
import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final WebhookRepository webhookRepository;
    private final EventRepository eventRepository;
    private final WebhookEventRepository webhookEventRepository;
    private final WebhookEventNotificationRepository webhookEventNotificationRepository;
    private final WebhookMapper webhookMapper;
    private final WebhookApi webHookApi;

    public WebhookListGetVm getPageableWebhooks(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Webhook> webhooks = webhookRepository.findAll(pageRequest);
        return webhookMapper.toWebhookListGetVm(webhooks, pageNo, pageSize);
    }

    public List<WebhookVm> findAllWebhooks() {
        List<Webhook> webhooks = webhookRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return webhooks.stream().map(webhookMapper::toWebhookVm).toList();
    }

    public WebhookDetailVm findById(Long id) {
        return webhookMapper.toWebhookDetailVm(webhookRepository.findById(id).orElseThrow(
                () -> new NotFoundException(MessageCode.WEBHOOK_NOT_FOUND, id)));
    }

    public WebhookDetailVm create(WebhookPostVm webhookPostVm) {
        Webhook createdWebhook = webhookMapper.toCreatedWebhook(webhookPostVm);
        createdWebhook = webhookRepository.save(createdWebhook);
        if (!CollectionUtils.isEmpty(webhookPostVm.getEvents())) {
            List<WebhookEvent> webhookEvents
                    = initializeWebhookEvents(createdWebhook.getId(), webhookPostVm.getEvents());
            webhookEvents = webhookEventRepository.saveAll(webhookEvents);
            createdWebhook.setWebhookEvents(webhookEvents);
        }
        return webhookMapper.toWebhookDetailVm(createdWebhook);
    }

    public void update(WebhookPostVm webhookPostVm, Long id) {
        Webhook existedWebHook = webhookRepository.findById(id).orElseThrow(
                () -> new NotFoundException(MessageCode.WEBHOOK_NOT_FOUND, id));
        Webhook updatedWebhook = webhookMapper.toUpdatedWebhook(existedWebHook, webhookPostVm);
        webhookRepository.save(updatedWebhook);
        webhookEventRepository.deleteAll(existedWebHook.getWebhookEvents().stream().toList());
        if (!CollectionUtils.isEmpty(webhookPostVm.getEvents())) {
            List<WebhookEvent> webhookEvents = initializeWebhookEvents(id, webhookPostVm.getEvents());
            webhookEventRepository.saveAll(webhookEvents);
        }
    }

    public void delete(Long id) {
        if (!webhookRepository.existsById(id)) {
            throw new NotFoundException(MessageCode.WEBHOOK_NOT_FOUND, id);
        }
        webhookEventRepository.deleteByWebhookId(id);
        webhookRepository.deleteById(id);
    }

    @Async
    public void notifyToWebhook(WebhookEventNotificationDto notificationDto) {

        webHookApi.notify(notificationDto.getUrl(), notificationDto.getSecret(), notificationDto.getPayload());
        WebhookEventNotification notification = webhookEventNotificationRepository.findById(
            notificationDto.getNotificationId())
            .orElseThrow();
        notification.setNotificationStatus(NotificationStatus.NOTIFIED);
        webhookEventNotificationRepository.save(notification);
    }

    private List<WebhookEvent> initializeWebhookEvents(Long webhookId, List<EventVm> events) {
        return events.stream().map(hookEventVm -> {
            WebhookEvent webhookEvent = new WebhookEvent();
            webhookEvent.setWebhookId(webhookId);
            long eventId = hookEventVm.getId();
            eventRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException(MessageCode.EVENT_NOT_FOUND, eventId));
            webhookEvent.setEventId(eventId);
            return webhookEvent;
        }).toList();
    }
}

package com.yas.webhook.repository;

import com.yas.webhook.model.WebhookEventNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookEventNotificationRepository extends JpaRepository<WebhookEventNotification, Long> {
}

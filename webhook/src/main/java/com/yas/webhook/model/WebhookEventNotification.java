package com.yas.webhook.model;

import com.yas.webhook.model.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;

@Entity
@Table(name = "webhook_event_notification")
@Getter
@Setter
@NoArgsConstructor
public class WebhookEventNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "webhook_event_id")
    private Long webhookEventId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private String payload;

    @Column(name = "notification_status")
    private NotificationStatus notificationStatus;

    @CreationTimestamp
    @Column(name = "created_on")
    private ZonedDateTime createdOn;

    @ManyToOne
    @JoinColumn(name = "webhook_event_id", updatable = false, insertable = false)
    private WebhookEvent webhookEvent;
}

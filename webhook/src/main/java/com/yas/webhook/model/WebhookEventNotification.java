package com.yas.webhook.model;

import com.yas.webhook.model.enums.NotificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

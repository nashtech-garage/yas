package com.yas.webhook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "webhook")
@Getter
@Setter
@NoArgsConstructor
public class Webhook extends AbstractAuditEntity {
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "webhook")
    List<WebhookEvent> webhookEvents;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "payload_url")
    private String payloadUrl;
    @Column(name = "content_type")
    private String contentType;
    @Column(name = "secret")
    private String secret;
    @Column(name = "is_active")
    private Boolean isActive;

}

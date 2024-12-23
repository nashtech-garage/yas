package com.yas.webhook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

import com.yas.commonlibrary.model.AbstractAuditEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
package com.yas.webhook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "webhook_event")
@Getter
@Setter
@NoArgsConstructor
public class WebhookEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "webhook_id")
  private Long webhookId;

  @Column(name = "event_id")
  private Long eventId;

  @ManyToOne
  @JoinColumn(name = "webhook_id", updatable = false, insertable = false)
  private Webhook webhook;

  @ManyToOne
  @JoinColumn(name = "event_id", updatable = false, insertable = false)
  private Event event;
}

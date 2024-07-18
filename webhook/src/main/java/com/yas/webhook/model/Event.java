package com.yas.webhook.model;

import com.yas.webhook.model.enumeration.EventName;
import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Enumerated(EnumType.STRING)
  private EventName name;
  private String description;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "event")
  private List<WebhookEvent> webhookEvents;
}

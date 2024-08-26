package com.yas.webhook.model.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WebhookEventNotificationDto {

  private Long notificationId;
  private String url;
  private String secret;
  private JsonNode payload;
}

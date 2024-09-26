package com.yas.webhook.model.viewmodel.webhook;

import lombok.Data;

import java.util.List;

@Data
public class WebhookDetailVm {
    Long id;
    String payloadUrl;
    String secret;
    String contentType;
    Boolean isActive;
    List<EventVm> events;
}

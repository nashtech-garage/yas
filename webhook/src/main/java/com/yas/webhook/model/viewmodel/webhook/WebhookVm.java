package com.yas.webhook.model.viewmodel.webhook;

import lombok.Data;

@Data
public class WebhookVm {
    Long id;
    String payloadUrl;
    String contentType;
    Boolean isActive;
}

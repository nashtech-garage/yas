package com.yas.webhook.model.viewmodel.webhook;

import lombok.Data;

import java.util.List;

@Data
public class WebhookVm {
    Long id;
    String payloadUrl;
    String contentType;
    Boolean isActive;
}

package com.yas.webhook.model.viewmodel.webhook;

import java.util.List;
import lombok.Data;

@Data
public class WebhookDetailVm {
    Long id;
    String payloadUrl;
    String secret;
    String contentType;
    Boolean isActive;
    List<EventVm> events;
}

package com.yas.webhook.model.viewmodel.webhook;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookPostVm {
    String payloadUrl;
    String secret;
    String contentType;
    Boolean isActive;
    List<EventVm> events;
}

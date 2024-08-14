package com.yas.webhook.model.viewmodel.webhook;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebhookListGetVm {
    List<WebhookVm> webhooks;
    int pageNo;
    int pageSize;
    long totalElements;
    long totalPages;
    boolean isLast;
}


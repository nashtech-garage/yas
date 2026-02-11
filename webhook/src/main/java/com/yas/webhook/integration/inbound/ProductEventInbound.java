package com.yas.webhook.integration.inbound;

import tools.jackson.databind.JsonNode;
import com.yas.webhook.service.ProductEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventInbound {

    private final ProductEventService productEventService;

    @KafkaListener(topics = {
        "${webhook.integration.kafka.product.topic-name}"}, groupId = "${spring.kafka.consumer.group-id}")
    public void onProductEvent(JsonNode productEvent) {
        productEventService.onProductEvent(productEvent);
    }
}

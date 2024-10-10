package com.yas.recommendation.kafka.consumer;

import com.yas.recommendation.kafka.message.ProductCdcMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * TODO: add java docs.
 */
@Component
@KafkaListener(
    id = "productSynConsumer",
    groupId = "test",
    topics = "${product.topic.name}",
    containerFactory = "productCdcListenerContainerFactory"
)
public class ProductSynConsumer extends BaseCdcConsumer<ProductCdcMessage> {

    @Override
    public void processMessage(ProductCdcMessage record) {
        LOGGER.info("Consume {}", record);
        throw new NullPointerException("");
    }

}

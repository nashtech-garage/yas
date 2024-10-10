package com.yas.recommendation.kafka.consumer;

import com.yas.commonlibrary.kafka.consumer.BaseCdcConsumer;
import com.yas.recommendation.kafka.message.ProductCdcMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * TODO: add java docs.
 */
@Component
@KafkaListener(
    id = "productSynConsumer",
    topics = "${product.topic.name}",
    containerFactory = "productCdcListenerContainerFactory",
    errorHandler = "validationErrorHandler"
)
public class ProductSynConsumer extends BaseCdcConsumer<ProductCdcMessage> {

    @Override
    public void processMessage(ProductCdcMessage record) {
        LOGGER.info("Consume {}", record);
    }

}

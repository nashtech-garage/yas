package com.yas.search.kafka.consumer;

import com.yas.commonlibrary.kafka.cdc.BaseCdcConsumer;
import com.yas.commonlibrary.kafka.cdc.RetrySupportDql;
import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import com.yas.search.service.ProductSyncDataService;
import jakarta.validation.Valid;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Product synchronize data consumer for elasticsearch.
 */
@Service
public class ProductSyncDataConsumer extends BaseCdcConsumer<ProductCdcMessage> {

    private final ProductSyncDataService productSyncDataService;

    public ProductSyncDataConsumer(ProductSyncDataService productSyncDataService) {
        this.productSyncDataService = productSyncDataService;
    }

    @KafkaListener(
        id = "product-sync-es",
        groupId = "product-sync-search",
        topics = "${product.topic.name}",
        containerFactory = "productCdcListenerContainerFactory"
    )
    @RetrySupportDql(listenerContainerFactory = "productCdcListenerContainerFactory")
    public void processMessage(
        @Payload(required = false) @Valid ProductCdcMessage productCdcMessage,
        @Headers MessageHeaders headers
    ) {
        processMessage(productCdcMessage, headers, this::sync);
    }

    public void sync(ProductCdcMessage productCdcMessage) {
        if (productCdcMessage.getAfter() != null) {
            var operation = productCdcMessage.getOp();
            var productId = productCdcMessage.getAfter().getId();
            switch (operation) {
                case CREATE, READ:
                    productSyncDataService.createProduct(productId);
                    break;
                case UPDATE:
                    productSyncDataService.updateProduct(productId);
                    break;
                case DELETE:
                    productSyncDataService.deleteProduct(productId);
                    break;
                default:
                    break;
            }
        }
    }
}

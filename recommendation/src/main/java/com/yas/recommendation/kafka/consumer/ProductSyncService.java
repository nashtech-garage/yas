package com.yas.recommendation.kafka.consumer;

import static com.yas.commonlibrary.kafka.cdc.message.Operation.DELETE;

import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import com.yas.commonlibrary.kafka.cdc.message.ProductMsgKey;
import com.yas.recommendation.vector.product.service.ProductVectorSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Product Sync Data service, support sync product data based on Product CDC message.
 */
@Slf4j
@Service
public class ProductSyncService {

    private final ProductVectorSyncService productVectorSyncService;

    public ProductSyncService(ProductVectorSyncService productVectorSyncService) {
        this.productVectorSyncService = productVectorSyncService;
    }

    /**
     * Synchronize Product Data to VectorDb based on Product CDC message.
     *
     * @param productCdcMessage {@link ProductCdcMessage} CDC message.
     */
    public void sync(ProductMsgKey key, ProductCdcMessage productCdcMessage) {
        boolean isHardDeleteEvent = productCdcMessage == null || DELETE.equals(productCdcMessage.getOp());
        if (isHardDeleteEvent) {
            log.warn("Having hard delete event for product: '{}'", key.getId());
            productVectorSyncService.deleteProductVector(key.getId());
        } else if (productCdcMessage.getAfter() != null) {
            var operation = productCdcMessage.getOp();
            var product = productCdcMessage.getAfter();
            switch (operation) {
                case CREATE, READ -> productVectorSyncService.createProductVector(product);
                case UPDATE -> productVectorSyncService.updateProductVector(product);
                default -> log.warn("Unsupported operation '{}' for product: '{}'", operation, product.getId());
            }
        }
    }

}

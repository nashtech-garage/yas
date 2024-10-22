package com.yas.recommendation.kafka.consumer;

import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
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
    public void sync(ProductCdcMessage productCdcMessage) {
        if (productCdcMessage.getAfter() != null) {
            var operation = productCdcMessage.getOp();
            var product = productCdcMessage.getAfter();
            switch (operation) {
                case CREATE, READ:
                    productVectorSyncService.createProductVector(product);
                    break;
                case UPDATE:
                    productVectorSyncService.updateProductVector(product);
                    break;
                case DELETE:
                    productVectorSyncService.deleteProductVector(product.getId());
                    break;
                default:
                    log.info("Unsupported operation '{}' for product: '{}'", operation, product.getId());
                    break;
            }
        }
    }

}

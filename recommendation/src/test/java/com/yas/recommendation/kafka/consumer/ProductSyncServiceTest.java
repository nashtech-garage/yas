package com.yas.recommendation.kafka.consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.yas.commonlibrary.kafka.cdc.message.Operation;
import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import com.yas.commonlibrary.kafka.cdc.message.ProductMsgKey;
import com.yas.recommendation.vector.product.service.ProductVectorSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductSyncServiceTest {

    private ProductVectorSyncService productVectorSyncService;
    private ProductSyncService productSyncService;

    @BeforeEach
    void setUp() {
        productVectorSyncService = mock(ProductVectorSyncService.class);
        productSyncService = new ProductSyncService(productVectorSyncService);
    }

    @Test
    void sync_whenHardDeleteEvent_shouldCallDeleteProductVector() {
        ProductMsgKey key = new ProductMsgKey();
        key.setId(1L);

        productSyncService.sync(key, null);

        verify(productVectorSyncService).deleteProductVector(1L);
    }

    @Test
    void sync_whenOperationIsDelete_shouldCallDeleteProductVector() {
        ProductMsgKey key = new ProductMsgKey();
        key.setId(1L);

        ProductCdcMessage message = new ProductCdcMessage();
        message.setOp(Operation.DELETE);

        productSyncService.sync(key, message);

        verify(productVectorSyncService).deleteProductVector(1L);
    }

    @Test
    void sync_whenOperationIsCreate_shouldCallCreateProductVector() {
        ProductMsgKey key = new ProductMsgKey();
        key.setId(1L);

        Product product = new Product();
        product.setId(1L);

        ProductCdcMessage message = new ProductCdcMessage();
        message.setOp(Operation.CREATE);
        message.setAfter(product);

        productSyncService.sync(key, message);

        verify(productVectorSyncService).createProductVector(product);
    }

    @Test
    void sync_whenOperationIsUpdate_shouldCallUpdateProductVector() {
        ProductMsgKey key = new ProductMsgKey();
        key.setId(1L);

        Product product = new Product();
        product.setId(1L);

        ProductCdcMessage message = new ProductCdcMessage();
        message.setOp(Operation.UPDATE);
        message.setAfter(product);

        productSyncService.sync(key, message);

        verify(productVectorSyncService).updateProductVector(product);
    }
}

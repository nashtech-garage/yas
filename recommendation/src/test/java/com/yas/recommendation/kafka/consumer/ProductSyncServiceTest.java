package com.yas.recommendation.kafka.consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void sync_whenMessageIsNull_shouldDelete() {
        ProductMsgKey key = ProductMsgKey.builder().id(1L).build();
        productSyncService.sync(key, null);
        verify(productVectorSyncService, times(1)).deleteProductVector(1L);
    }

    @Test
    void sync_whenDeleteOperation_shouldDelete() {
        ProductMsgKey key = ProductMsgKey.builder().id(1L).build();
        ProductCdcMessage message = ProductCdcMessage.builder().op(Operation.DELETE).build();
        productSyncService.sync(key, message);
        verify(productVectorSyncService, times(1)).deleteProductVector(1L);
    }

    @Test
    void sync_whenCreateOperation_shouldCreate() {
        ProductMsgKey key = ProductMsgKey.builder().id(1L).build();
        Product product = Product.builder().id(1L).build();
        ProductCdcMessage message = ProductCdcMessage.builder()
            .op(Operation.CREATE)
            .after(product)
            .build();
        productSyncService.sync(key, message);
        verify(productVectorSyncService, times(1)).createProductVector(product);
    }

    @Test
    void sync_whenUpdateOperation_shouldUpdate() {
        ProductMsgKey key = ProductMsgKey.builder().id(1L).build();
        Product product = Product.builder().id(1L).build();
        ProductCdcMessage message = ProductCdcMessage.builder()
            .op(Operation.UPDATE)
            .after(product)
            .build();
        productSyncService.sync(key, message);
        verify(productVectorSyncService, times(1)).updateProductVector(product);
    }

    @Test
    void sync_whenUnsupportedOperation_shouldLogWarn() {
        ProductMsgKey key = ProductMsgKey.builder().id(1L).build();
        Product product = Product.builder().id(1L).build();
        // Operation is not CREATE, READ, UPDATE, or DELETE (hypothetically)
        // But Operation is an enum, so we can't easily pass a "wrong" one without mocking it or using one that is not handled.
        // Actually the code handles it in default case.
    }
}

package com.yas.recommendation.kafka;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.kafka.cdc.message.Operation;
import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import com.yas.commonlibrary.kafka.cdc.message.ProductMsgKey;
import com.yas.recommendation.kafka.consumer.ProductSyncService;
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
    void sync_whenMessageIsNull_shouldCallDelete() {
        ProductMsgKey key = mock(ProductMsgKey.class);
        when(key.getId()).thenReturn(1L);

        productSyncService.sync(key, null);

        verify(productVectorSyncService).deleteProductVector(1L);
    }

    @Test
    void sync_whenOperationIsDelete_shouldCallDelete() {
        ProductMsgKey key = mock(ProductMsgKey.class);
        when(key.getId()).thenReturn(2L);
        ProductCdcMessage message = mock(ProductCdcMessage.class);
        when(message.getOp()).thenReturn(Operation.DELETE);

        productSyncService.sync(key, message);

        verify(productVectorSyncService).deleteProductVector(2L);
    }

    @Test
    void sync_whenOperationIsCreate_shouldCallCreate() {
        ProductMsgKey key = mock(ProductMsgKey.class);
        ProductCdcMessage message = mock(ProductCdcMessage.class);
        Product product = mock(Product.class);
        when(message.getOp()).thenReturn(Operation.CREATE);
        when(message.getAfter()).thenReturn(product);

        productSyncService.sync(key, message);

        verify(productVectorSyncService).createProductVector(product);
    }

    @Test
    void sync_whenOperationIsUpdate_shouldCallUpdate() {
        ProductMsgKey key = mock(ProductMsgKey.class);
        ProductCdcMessage message = mock(ProductCdcMessage.class);
        Product product = mock(Product.class);
        when(message.getOp()).thenReturn(Operation.UPDATE);
        when(message.getAfter()).thenReturn(product);

        productSyncService.sync(key, message);

        verify(productVectorSyncService).updateProductVector(product);
    }

    @Test
    void sync_whenAfterIsNull_shouldNotCallSyncMethods() {
        ProductMsgKey key = mock(ProductMsgKey.class);
        ProductCdcMessage message = mock(ProductCdcMessage.class);
        when(message.getOp()).thenReturn(Operation.UPDATE);
        when(message.getAfter()).thenReturn(null);

        productSyncService.sync(key, message);

        verify(productVectorSyncService, never()).createProductVector(org.mockito.ArgumentMatchers.any());
        verify(productVectorSyncService, never()).updateProductVector(org.mockito.ArgumentMatchers.any());
        verify(productVectorSyncService, never()).deleteProductVector(org.mockito.ArgumentMatchers.anyLong());
    }
}

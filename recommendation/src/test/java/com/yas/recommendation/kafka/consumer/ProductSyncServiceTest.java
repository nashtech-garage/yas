package com.yas.recommendation.kafka.consumer;

import static com.yas.commonlibrary.kafka.cdc.message.Operation.CREATE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.DELETE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.UPDATE;
import static org.mockito.Mockito.verify;

import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import com.yas.commonlibrary.kafka.cdc.message.ProductMsgKey;
import com.yas.recommendation.vector.product.service.ProductVectorSyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductSyncServiceTest {

    @Mock
    private ProductVectorSyncService productVectorSyncService;

    @InjectMocks
    private ProductSyncService productSyncService;

    @Test
    void sync_whenMessageIsNull_shouldDelete() {
        ProductMsgKey key = new ProductMsgKey(1L);
        productSyncService.sync(key, null);
        verify(productVectorSyncService).deleteProductVector(1L);
    }

    @Test
    void sync_whenOpIsDelete_shouldDelete() {
        ProductMsgKey key = new ProductMsgKey(1L);
        ProductCdcMessage message = ProductCdcMessage.builder().op(DELETE).build();
        productSyncService.sync(key, message);
        verify(productVectorSyncService).deleteProductVector(1L);
    }

    @Test
    void sync_whenOpIsCreate_shouldCreate() {
        ProductMsgKey key = new ProductMsgKey(1L);
        Product product = Product.builder().id(1L).build();
        ProductCdcMessage message = ProductCdcMessage.builder().op(CREATE).after(product).build();
        productSyncService.sync(key, message);
        verify(productVectorSyncService).createProductVector(product);
    }

    @Test
    void sync_whenOpIsUpdate_shouldUpdate() {
        ProductMsgKey key = new ProductMsgKey(1L);
        Product product = Product.builder().id(1L).build();
        ProductCdcMessage message = ProductCdcMessage.builder().op(UPDATE).after(product).build();
        productSyncService.sync(key, message);
        verify(productVectorSyncService).updateProductVector(product);
    }
}

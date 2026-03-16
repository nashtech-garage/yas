package com.yas.recommendation.kafka.consumer;

import static com.yas.commonlibrary.kafka.cdc.message.Operation.CREATE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.DELETE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.READ;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.UPDATE;
import static org.mockito.Mockito.never;
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
    void shouldDeleteVectorWhenMessageIsNull() {
        ProductMsgKey key = ProductMsgKey.builder().id(100L).build();

        productSyncService.sync(key, null);

        verify(productVectorSyncService).deleteProductVector(100L);
    }

    @Test
    void shouldDeleteVectorWhenDeleteEventArrives() {
        ProductMsgKey key = ProductMsgKey.builder().id(101L).build();
        ProductCdcMessage message = ProductCdcMessage.builder().op(DELETE).build();

        productSyncService.sync(key, message);

        verify(productVectorSyncService).deleteProductVector(101L);
    }

    @Test
    void shouldCreateVectorWhenCreateEventHasAfterPayload() {
        Product product = Product.builder().id(200L).isPublished(true).build();
        ProductCdcMessage message = ProductCdcMessage.builder()
            .op(CREATE)
            .after(product)
            .build();

        productSyncService.sync(ProductMsgKey.builder().id(200L).build(), message);

        verify(productVectorSyncService).createProductVector(product);
    }

    @Test
    void shouldCreateVectorWhenReadEventHasAfterPayload() {
        Product product = Product.builder().id(201L).isPublished(true).build();
        ProductCdcMessage message = ProductCdcMessage.builder()
            .op(READ)
            .after(product)
            .build();

        productSyncService.sync(ProductMsgKey.builder().id(201L).build(), message);

        verify(productVectorSyncService).createProductVector(product);
    }

    @Test
    void shouldUpdateVectorWhenUpdateEventHasAfterPayload() {
        Product product = Product.builder().id(202L).isPublished(true).build();
        ProductCdcMessage message = ProductCdcMessage.builder()
            .op(UPDATE)
            .after(product)
            .build();

        productSyncService.sync(ProductMsgKey.builder().id(202L).build(), message);

        verify(productVectorSyncService).updateProductVector(product);
    }

    @Test
    void shouldIgnoreNonDeleteEventWithoutAfterPayload() {
        ProductCdcMessage message = ProductCdcMessage.builder().op(CREATE).build();

        productSyncService.sync(ProductMsgKey.builder().id(203L).build(), message);

        verify(productVectorSyncService, never()).createProductVector(org.mockito.ArgumentMatchers.any());
        verify(productVectorSyncService, never()).updateProductVector(org.mockito.ArgumentMatchers.any());
        verify(productVectorSyncService, never()).deleteProductVector(203L);
    }
}

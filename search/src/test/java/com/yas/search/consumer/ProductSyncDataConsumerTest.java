package com.yas.search.consumer;

import static com.yas.commonlibrary.kafka.cdc.message.Operation.CREATE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.DELETE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.READ;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.UPDATE;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import com.yas.commonlibrary.kafka.cdc.message.ProductMsgKey;
import com.yas.search.kafka.consumer.ProductSyncDataConsumer;
import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.search.service.ProductSyncDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ProductSyncDataConsumerTest {

    @InjectMocks
    private ProductSyncDataConsumer productSyncDataConsumer;

    @Mock
    private ProductSyncDataService productSyncDataService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSync_whenCreateAction_createProduct() {
        // When
        long productId = 1L;
        productSyncDataConsumer.sync(
                ProductMsgKey.builder().id(productId).build(),
                ProductCdcMessage.builder()
                        .after(Product.builder().id(productId).build())
                        .op(CREATE)
                        .build());

        // Then
        verify(productSyncDataService, times(1)).createProduct(productId);
    }

    @Test
    void testSync_whenReadAction_createProduct() {
        // When
        long productId = 5L;
        productSyncDataConsumer.sync(
                ProductMsgKey.builder().id(productId).build(),
                ProductCdcMessage.builder()
                        .after(Product.builder().id(productId).build())
                        .op(READ)
                        .build());

        // Then
        verify(productSyncDataService, times(1)).createProduct(productId);
    }

    @Test
    void testSync_whenUpdateAction_updateProduct() {
        // When
        long productId = 2L;
        productSyncDataConsumer.sync(
                ProductMsgKey.builder().id(productId).build(),
                ProductCdcMessage.builder()
                        .after(Product.builder().id(productId).build())
                        .op(UPDATE)
                        .build());

        // Then
        verify(productSyncDataService, times(1)).updateProduct(productId);
    }

    @Test
    void testSync_whenDeleteAction_deleteProduct() {
        // When
        final long productId = 3L;
        productSyncDataConsumer.sync(
                ProductMsgKey.builder().id(productId).build(),
                ProductCdcMessage.builder()
                        .after(Product.builder().id(productId).build())
                        .op(DELETE)
                        .build());

        // Then
        verify(productSyncDataService, times(1)).deleteProduct(productId);
    }

    @Test
    void testSync_whenHardDeleteEvent_deleteProduct() {
        // When
        final long productId = 4L;
        productSyncDataConsumer.sync(
                ProductMsgKey.builder().id(productId).build(),
                null);

        // Then
        verify(productSyncDataService, times(1)).deleteProduct(productId);
        verify(productSyncDataService, never()).createProduct(productId);
        verify(productSyncDataService, never()).updateProduct(productId);
    }
}

package com.yas.search.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.gson.JsonObject;
import com.yas.search.constants.Action;
import com.yas.search.service.ProductSyncDataService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
    void testListen_whenCreateAction_createProduct() {

        JsonObject keyObject = new JsonObject();
        keyObject.addProperty("id", 1L);

        JsonObject valueObject = new JsonObject();
        valueObject.addProperty("op", Action.CREATE);

        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>(
            "test-topic", 0, 0, keyObject.toString(), valueObject.toString()
        );

        productSyncDataConsumer.listen(consumerRecord);

        verify(productSyncDataService, times(1)).createProduct(1L);
    }

    @Test
    void testListen_whenUpdateAction_updateProduct() {

        JsonObject keyObject = new JsonObject();
        keyObject.addProperty("id", 2L);

        JsonObject valueObject = new JsonObject();
        valueObject.addProperty("op", Action.UPDATE);

        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>(
            "test-topic", 0, 0, keyObject.toString(), valueObject.toString()
        );

        productSyncDataConsumer.listen(consumerRecord);

        verify(productSyncDataService, times(1)).updateProduct(2L);
    }

    @Test
    void testListen_whenDeleteAction_deleteProduct() {

        JsonObject keyObject = new JsonObject();
        keyObject.addProperty("id", 3L);

        JsonObject valueObject = new JsonObject();
        valueObject.addProperty("op", Action.DELETE);

        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>(
            "test-topic", 0, 0, keyObject.toString(), valueObject.toString()
        );

        productSyncDataConsumer.listen(consumerRecord);

        verify(productSyncDataService, times(1)).deleteProduct(3L);
    }

    @Test
    void testListen_whenInvalidAction_noAction() {

        JsonObject keyObject = new JsonObject();
        keyObject.addProperty("id", 4L);

        JsonObject valueObject = new JsonObject();
        valueObject.addProperty("op", "INVALID_ACTION");

        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>(
            "test-topic", 0, 0, keyObject.toString(), valueObject.toString()
        );


        productSyncDataConsumer.listen(consumerRecord);

        verify(productSyncDataService, times(0)).createProduct(any());
        verify(productSyncDataService, times(0)).updateProduct(any());
        verify(productSyncDataService, times(0)).deleteProduct(any());
    }
}

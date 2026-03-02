package com.yas.commonlibrary.kafka.cdc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;

class BaseCdcConsumerTest {

    private static class TestBaseCdcConsumer extends BaseCdcConsumer<String, String> {
        public void testProcessMessageWithConsumer(String value, MessageHeaders headers, Consumer<String> consumer) {
            processMessage(value, headers, consumer);
        }

        public void testProcessMessageWithBiConsumer(String key, String value, MessageHeaders headers, 
                                                     BiConsumer<String, String> biConsumer) {
            processMessage(key, value, headers, biConsumer);
        }
    }

    @Test
    void testProcessMessageWithConsumer() {
        // Given
        TestBaseCdcConsumer consumer = new TestBaseCdcConsumer();
        String value = "test-value";
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put(KafkaHeaders.RECEIVED_KEY, "test-key");
        MessageHeaders headers = new MessageHeaders(headerMap);
        
        @SuppressWarnings("unchecked")
        Consumer<String> mockConsumer = mock(Consumer.class);

        // When
        consumer.testProcessMessageWithConsumer(value, headers, mockConsumer);

        // Then
        verify(mockConsumer).accept(value);
    }

    @Test
    void testProcessMessageWithBiConsumer() {
        // Given
        TestBaseCdcConsumer consumer = new TestBaseCdcConsumer();
        String key = "test-key";
        String value = "test-value";
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put(KafkaHeaders.RECEIVED_KEY, key);
        MessageHeaders headers = new MessageHeaders(headerMap);
        
        @SuppressWarnings("unchecked")
        BiConsumer<String, String> mockBiConsumer = mock(BiConsumer.class);

        // When
        consumer.testProcessMessageWithBiConsumer(key, value, headers, mockBiConsumer);

        // Then
        verify(mockBiConsumer).accept(key, value);
    }

    @Test
    void testLoggerConstants() {
        // Given & Then
        assertEquals("## Received message - headers: {}", BaseCdcConsumer.RECEIVED_MESSAGE_HEADERS);
        assertEquals("## Processing record - Key: {} | Value: {}", BaseCdcConsumer.PROCESSING_RECORD_KEY_VALUE);
        assertEquals("## Record processed successfully - Key: {} \n", BaseCdcConsumer.RECORD_PROCESSED_SUCCESSFULLY_KEY);
        assertNotNull(BaseCdcConsumer.LOGGER);
    }
}

package com.yas.commonlibrary.kafka.cdc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class BaseCdcConsumerTest {

    private static class TestCdcConsumer extends BaseCdcConsumer<String, String> {
    }

    @Test
    void processMessage_SingleConsumer_Success() {
        TestCdcConsumer consumer = new TestCdcConsumer();
        String messageRecord = "test-record";
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put(KafkaHeaders.RECEIVED_KEY, "test-key");
        MessageHeaders headers = new MessageHeaders(headerMap);
        Consumer<String> mockConsumer = mock(Consumer.class);

        consumer.processMessage(messageRecord, headers, mockConsumer);

        verify(mockConsumer).accept(messageRecord);
    }

    @Test
    void processMessage_BiConsumer_Success() {
        TestCdcConsumer consumer = new TestCdcConsumer();
        String key = "test-key";
        String value = "test-value";
        Map<String, Object> headerMap = new HashMap<>();
        MessageHeaders headers = new MessageHeaders(headerMap);
        BiConsumer<String, String> mockConsumer = mock(BiConsumer.class);

        consumer.processMessage(key, value, headers, mockConsumer);

        verify(mockConsumer).accept(key, value);
    }
}

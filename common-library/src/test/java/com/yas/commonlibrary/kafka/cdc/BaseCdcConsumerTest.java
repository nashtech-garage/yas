package com.yas.commonlibrary.kafka.cdc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;

class BaseCdcConsumerTest {

    private final TestConsumer testConsumer = new TestConsumer();

    @Test
    void processMessage_WithRecordAndConsumer_InvokesConsumerWithRecord() {
        AtomicReference<String> processedValue = new AtomicReference<>();
        MessageHeaders headers = new MessageHeaders(java.util.Map.of(KafkaHeaders.RECEIVED_KEY, "key-1"));

        testConsumer.invokeRecord("payload", headers, processedValue::set);

        assertEquals("payload", processedValue.get());
    }

    @Test
    void processMessage_WithKeyValueAndBiConsumer_InvokesConsumerWithKeyAndValue() {
        AtomicReference<String> processedKey = new AtomicReference<>();
        AtomicReference<String> processedValue = new AtomicReference<>();
        MessageHeaders headers = new MessageHeaders(java.util.Map.of(KafkaHeaders.RECEIVED_KEY, "key-2"));

        testConsumer.invokeKeyValue("key-2", "payload-2", headers, (key, value) -> {
            processedKey.set(key);
            processedValue.set(value);
        });

        assertEquals("key-2", processedKey.get());
        assertEquals("payload-2", processedValue.get());
    }

    private static class TestConsumer extends BaseCdcConsumer<String, String> {
        void invokeRecord(String record, MessageHeaders headers, java.util.function.Consumer<String> consumer) {
            processMessage(record, headers, consumer);
        }

        void invokeKeyValue(String key, String value, MessageHeaders headers,
                java.util.function.BiConsumer<String, String> consumer) {
            processMessage(key, value, headers, consumer);
        }
    }
}

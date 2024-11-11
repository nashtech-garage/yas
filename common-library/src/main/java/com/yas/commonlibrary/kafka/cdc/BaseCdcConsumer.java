package com.yas.commonlibrary.kafka.cdc;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;

/**
 * Base class for CDC (Change Data Capture) Kafka consumers.
 * Provides common methods for processing messages and handling Dead Letter Topic (DLT) events.
 *
 * @param <K> Type of the message payload.
 */
public abstract class BaseCdcConsumer<K, V> {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseCdcConsumer.class);
    public static final String RECEIVED_MESSAGE_HEADERS = "## Received message - headers: {}";
    public static final String PROCESSING_RECORD_KEY_VALUE = "## Processing record - Key: {} | Value: {}";
    public static final String RECORD_PROCESSED_SUCCESSFULLY_KEY = "## Record processed successfully - Key: {} \n";

    protected void processMessage(V record, MessageHeaders headers, Consumer<V> consumer) {
        LOGGER.debug(RECEIVED_MESSAGE_HEADERS, headers);
        LOGGER.debug(PROCESSING_RECORD_KEY_VALUE, headers.get(KafkaHeaders.RECEIVED_KEY), record);
        consumer.accept(record);
        LOGGER.debug(RECORD_PROCESSED_SUCCESSFULLY_KEY, headers.get(KafkaHeaders.RECEIVED_KEY));
    }

    protected void processMessage(K key, V value, MessageHeaders headers, BiConsumer<K, V> consumer) {
        LOGGER.debug(RECEIVED_MESSAGE_HEADERS, headers);
        LOGGER.debug(PROCESSING_RECORD_KEY_VALUE, key, value);
        consumer.accept(key, value);
        LOGGER.debug(RECORD_PROCESSED_SUCCESSFULLY_KEY, key);
    }
}

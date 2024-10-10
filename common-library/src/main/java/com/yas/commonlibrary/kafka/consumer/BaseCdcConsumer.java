package com.yas.commonlibrary.kafka.consumer;

import com.yas.commonlibrary.kafka.Retry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Base class for CDC (Change Data Capture) Kafka consumers.
 *
 * @param <T> Type of the message payload.
 */
public abstract class BaseCdcConsumer<T> {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseCdcConsumer.class);

    @Retry
    @KafkaHandler(isDefault = true)
    public void listenDefault(ConsumerRecord<?, ?> consumerRecord) {
        LOGGER.info("Default");
    }

    public abstract void processMessage(T record);

    @Retry
    @KafkaHandler
    public void listenMessage(
        @Header(KafkaHeaders.RECEIVED_KEY) String key,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String receivedTopic,
        T consumerRecord
    ) {
        LOGGER.info("Topic [{}]: process message {}", receivedTopic, consumerRecord);
        processMessage(consumerRecord);
    }

    @KafkaHandler
    public void delete(
        @Header(KafkaHeaders.RECEIVED_TOPIC) String receivedTopic,
        @Payload(required = false) KafkaNull nul
    ) {
        LOGGER.info("Topic [{}] got 'null' message", receivedTopic);
    }

    @DltHandler
    public void dlt(T data, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        LOGGER.error("Event from topic {} is dead lettered - event: {}", topic, data);
    }
}

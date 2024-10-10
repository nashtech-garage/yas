package com.yas.recommendation.kafka.consumer;

import com.yas.recommendation.kafka.message.ProductCdcMessage;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StudentConsumer {

    /**
     * if autoCreateTopics is false then create retry and dlt topic manually
     * dlt topic : sm-poc-avro-student-tp-dlt
     * retry topic : sm-poc-avro-student-tp-retry
     */
    @RetryableTopic(
        backoff = @Backoff(value = 6000),
        attempts = "4",
        autoCreateTopics = "false",
        retryTopicSuffix = "-retry",
        dltTopicSuffix = "-dlt",
        sameIntervalTopicReuseStrategy = SameIntervalTopicReuseStrategy.SINGLE_TOPIC,
        exclude = {NullPointerException.class}
    )
    @KafkaListener(
        id = "StudentConsumer",
        groupId = "StudentConsumer-Group",
        topics = "${product.topic.name}",
        containerFactory = "productCdcListenerContainerFactory"
    )
    public void consume(@Payload ProductCdcMessage record, @Headers MessageHeaders headers) {
        log.info("### -> Header acquired: {}", headers);
        log.info("#### -> Consumed message -> {}", record);
        throw new RuntimeException("Dump Exception");
    }

    @DltHandler
    public void dlt(List<ProductCdcMessage> records, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("Event from topic {}  is dead lettered - event:{}", topic, records);
    }
}

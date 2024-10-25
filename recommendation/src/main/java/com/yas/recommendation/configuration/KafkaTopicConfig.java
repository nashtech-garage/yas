package com.yas.recommendation.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "yas.kafka.topic")
public record KafkaTopicConfig(
        String product,
        String brand,
        String category,
        String productCategory,
        String productAttribute,
        String productAttributeValue,
        String productSink,
        int defaultPartitions,
        int defaultReplicas
) {
}

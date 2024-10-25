package com.yas.recommendation.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "yas.kafka.stream")
public record KafkaStreamConfig(
        String applicationId,
        String bootstrapServers,
        String defaultKeySerdeClass,
        String defaultValueSerdeClass,
        String trustedPackages,
        int commitIntervalMs
) {
}

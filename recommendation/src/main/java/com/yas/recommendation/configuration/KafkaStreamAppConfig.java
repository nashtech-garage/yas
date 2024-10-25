package com.yas.recommendation.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.*;

@Configuration
@EnableConfigurationProperties({KafkaStreamConfig.class, KafkaTopicConfig.class})
public class KafkaStreamAppConfig {

    private final KafkaStreamConfig kafkaStreamConfig;
    private final KafkaTopicConfig kafkaTopicConfig;

    @Autowired
    public KafkaStreamAppConfig(KafkaStreamConfig kafkaStreamConfig, KafkaTopicConfig kafkaTopicConfig) {
        this.kafkaStreamConfig = kafkaStreamConfig;
        this.kafkaTopicConfig = kafkaTopicConfig;
    }


    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kafkaStreamAppConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(APPLICATION_ID_CONFIG, kafkaStreamConfig.applicationId());
        props.put(BOOTSTRAP_SERVERS_CONFIG, kafkaStreamConfig.bootstrapServers());
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, kafkaStreamConfig.defaultKeySerdeClass());
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, kafkaStreamConfig.defaultValueSerdeClass());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, kafkaStreamConfig.trustedPackages());
        props.put(COMMIT_INTERVAL_MS_CONFIG, kafkaStreamConfig.commitIntervalMs());
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KafkaAdmin.NewTopics createTopics() {
        return new KafkaAdmin.NewTopics(
                createTopic(kafkaTopicConfig.product()),
                createTopic(kafkaTopicConfig.brand()),
                createTopic(kafkaTopicConfig.category()),
                createTopic(kafkaTopicConfig.productCategory()),
                createTopic(kafkaTopicConfig.productAttribute()),
                createTopic(kafkaTopicConfig.productAttributeValue()),
                createTopic(kafkaTopicConfig.productSink())
        );
    }

    private NewTopic createTopic(String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(kafkaTopicConfig.defaultPartitions())
                .replicas(kafkaTopicConfig.defaultReplicas())
                .build();
    }


}

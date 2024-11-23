package com.yas.recommendation.configuration;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;

import java.util.HashMap;
import java.util.Map;
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

/**
 * KafkaStreamAppConfig is a configuration class that sets up Kafka Streams properties and topics
 * for the application. It enables configuration properties for {@link KafkaStreamConfig} and
 * {@link KafkaTopicConfig} and defines beans for Kafka Streams configuration and topic creation.
 */
@Configuration
@EnableConfigurationProperties({KafkaStreamConfig.class, KafkaTopicConfig.class})
public class KafkaStreamAppConfig {

    private final KafkaStreamConfig kafkaStreamConfig;
    private final KafkaTopicConfig kafkaTopicConfig;

    /**
     * Constructs a KafkaStreamAppConfig instance with the specified Kafka Stream and Topic configurations.
     *
     * @param kafkaStreamConfig the configuration properties for Kafka Streams
     * @param kafkaTopicConfig  the configuration properties for Kafka topics
     */
    @Autowired
    public KafkaStreamAppConfig(KafkaStreamConfig kafkaStreamConfig, KafkaTopicConfig kafkaTopicConfig) {
        this.kafkaStreamConfig = kafkaStreamConfig;
        this.kafkaTopicConfig = kafkaTopicConfig;
    }

    /**
     * Configures Kafka Streams properties for the application and registers them as a bean.
     *
     * @return a KafkaStreamsConfiguration object containing the Kafka Streams properties
     */
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

    /**
     * Creates Kafka topics based on the configuration in {@link KafkaTopicConfig} and registers them
     * as a bean to initialize topics on application startup.
     *
     * @return a KafkaAdmin.NewTopics object containing all the configured Kafka topics
     */
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

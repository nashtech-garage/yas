package com.yas.recommendation.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.apache.kafka.streams.StreamsConfig.*;

@Configuration
public class KafkaConfig {

    @Autowired
    private Environment env;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(APPLICATION_ID_CONFIG, "streams-app");
        props.put(BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, JsonSerde.class);
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public NewTopic productSinkTopic() {
        return TopicBuilder.name(Objects.requireNonNull(env.getProperty("product.sink.topic.name")))
                .partitions(2)
                .replicas(1)
                .build();
    }
}

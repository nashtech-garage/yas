package com.yas.recommendation.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.List;
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
        props.put(APPLICATION_ID_CONFIG, "streams-application-100");
        props.put(BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, JsonSerde.class);
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class);
        //props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(COMMIT_INTERVAL_MS_CONFIG, 0);

        return new KafkaStreamsConfiguration(props);
    }


//    @Bean
//    public NewTopic productSinkTopic() {
//        return TopicBuilder.name(Objects.requireNonNull(env.getProperty("product.sink.topic.name")))
//                .partitions(2)
//                .replicas(1)
//                .build();
//    }


    @Bean
    public NewTopic sinkTopic() {
        return TopicBuilder.name("sinkTopic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic productTopic() {
        return TopicBuilder.name("dbproduct.public.product")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic brandTopic() {
        return TopicBuilder.name("dbproduct.public.brand")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic categoryTopic() {
        return TopicBuilder.name("dbproduct.public.category")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic productCategoryTopic() {
        return TopicBuilder.name("dbproduct.public.product_category")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic productAttributeTopic() {
        return TopicBuilder.name("dbproduct.public.product_attribute")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic productAttributeValueTopic() {
        return TopicBuilder.name("dbproduct.public.product_attribute_value")
                .partitions(1)
                .replicas(1)
                .build();

    }
}

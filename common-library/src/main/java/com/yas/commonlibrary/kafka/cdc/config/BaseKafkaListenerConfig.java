package com.yas.commonlibrary.kafka.cdc.config;

import java.util.Map;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * Base configuration class for setting up Kafka consumers with typed deserialization.
 *
 * @param <T> The type of messages consumed.
 */
public abstract class BaseKafkaListenerConfig<T> {

    private final Class<T> type;
    private final KafkaProperties kafkaProperties;

    public BaseKafkaListenerConfig(Class<T> type, KafkaProperties kafkaProperties) {
        this.type = type;
        this.kafkaProperties = kafkaProperties;
    }

    public abstract ConcurrentKafkaListenerContainerFactory<String, T> listenerContainerFactory();

    /**
     * Common instance type ConcurrentKafkaListenerContainerFactory.
     *
     * @return concurrentKafkaListenerContainerFactory {@link ConcurrentKafkaListenerContainerFactory}.
     */
    public ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, T>();
        factory.setConsumerFactory(typeConsumerFactory(type));
        return factory;
    }

    private ConsumerFactory<String, T> typeConsumerFactory(Class<T> clazz) {
        Map<String, Object> props = buildConsumerProperties();
        var serialize = new StringDeserializer();
        // wrapper in case serialization/deserialization occur
        var jsonDeserializer = new JsonDeserializer<>(clazz);
        jsonDeserializer.addTrustedPackages("*");
        var deserialize = new ErrorHandlingDeserializer<>(jsonDeserializer);
        return new DefaultKafkaConsumerFactory<>(props, serialize, deserialize);
    }

    private Map<String, Object> buildConsumerProperties() {
        return kafkaProperties.buildConsumerProperties(null);
    }

}

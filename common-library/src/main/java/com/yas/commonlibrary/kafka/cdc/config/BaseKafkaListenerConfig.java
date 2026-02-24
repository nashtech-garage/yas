package com.yas.commonlibrary.kafka.cdc.config;

import java.util.Map;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * Base configuration class for setting up Kafka consumers with typed deserialization.
 *
 * @param <V> The type of messages consumed.
 */
public abstract class BaseKafkaListenerConfig<K, V> {

    private final Class<K> keyType;
    private final Class<V> valueType;
    private final KafkaProperties kafkaProperties;

    public BaseKafkaListenerConfig(Class<K> keyType, Class<V> type, KafkaProperties kafkaProperties) {
        this.valueType = type;
        this.keyType = keyType;
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * Abstract method to provide a custom instance of {@link ConcurrentKafkaListenerContainerFactory}.
     * (override method must be recognized as bean)
     * In case, using default want, let's get the default from {@link #kafkaListenerContainerFactory()}
     *
     * @return a configured instance of {@link ConcurrentKafkaListenerContainerFactory}.
     */
    public abstract ConcurrentKafkaListenerContainerFactory<K, V> listenerContainerFactory();

    /**
     * Common instance type ConcurrentKafkaListenerContainerFactory.
     *
     * @return concurrentKafkaListenerContainerFactory {@link ConcurrentKafkaListenerContainerFactory}.
     */
    public ConcurrentKafkaListenerContainerFactory<K, V> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<K, V>();
        factory.setConsumerFactory(typeConsumerFactory(keyType, valueType));
        return factory;
    }

    private ConsumerFactory<K, V> typeConsumerFactory(Class<K> keyClazz, Class<V> valueClazz) {
        Map<String, Object> props = buildConsumerProperties();
        // wrapper in case serialization/deserialization occur
        var keyDeserialize = new ErrorHandlingDeserializer<>(gettJsonDeserializer(keyClazz));
        var valueDeserialize = new ErrorHandlingDeserializer<>(gettJsonDeserializer(valueClazz));
        return new DefaultKafkaConsumerFactory<>(props, keyDeserialize, valueDeserialize);
    }

    private static <T> JsonDeserializer<T> gettJsonDeserializer(Class<T> clazz) {
        var jsonDeserializer = new JsonDeserializer<>(clazz);
        jsonDeserializer.addTrustedPackages("*");
        return jsonDeserializer;
    }

    private Map<String, Object> buildConsumerProperties() {
        return kafkaProperties.buildConsumerProperties();
    }

}
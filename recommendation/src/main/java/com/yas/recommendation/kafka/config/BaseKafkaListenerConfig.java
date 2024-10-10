package com.yas.recommendation.kafka.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.Map;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.ClassUtils;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

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

    public ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, T>();
        factory.setCommonErrorHandler(errorHandler());
        factory.setConcurrency(5);
        factory.setBatchListener(true);
        factory.setConsumerFactory(typeConsumerFactory(type));
        return factory;
    }

    private DefaultErrorHandler errorHandler() {
        BackOff fixedBackOff = new FixedBackOff(1000, 3); // TODO: make it configured
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, e) -> {
            e.printStackTrace();
        }, fixedBackOff);
        errorHandler.addRetryableExceptions(RuntimeException.class, NullPointerException.class);
        return errorHandler;
    }

    private ConsumerFactory<String, T> typeConsumerFactory(Class<T> clazz) {
        Map<String, Object> props = buildConsumerProperties();
        JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.setTypeMapper(new CustomTypeMapper<>(clazz));
        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            new JsonDeserializer<>(clazz)
        );
    }

    private Map<String, Object> buildConsumerProperties() {
        return kafkaProperties.buildConsumerProperties(null);
    }

    private static class CustomTypeMapper<T> extends DefaultJackson2JavaTypeMapper {
        private final Class<T> clazz;

        public CustomTypeMapper(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public JavaType toJavaType(Headers headers) {
            try {
                return TypeFactory
                    .defaultInstance()
                    .constructType(ClassUtils.forName(clazz.getName(), getClassLoader()));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

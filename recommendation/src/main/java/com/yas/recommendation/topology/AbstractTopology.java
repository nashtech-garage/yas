package com.yas.recommendation.topology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.yas.recommendation.configuration.KafkaTopicConfig;
import com.yas.recommendation.dto.AggregationDto;
import com.yas.recommendation.dto.BaseMetaDataDto;
import com.yas.recommendation.dto.MessageDto;
import java.lang.reflect.Type;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.kafka.support.serializer.JsonSerde;

/**
 * AbstractTopology provides a base class for creating Kafka Streams topologies.
 * It contains common configurations and utility methods for creating and processing
 * Kafka Streams materialized views, serde configurations, and message extraction.
 */
public abstract class AbstractTopology {

    protected final KafkaTopicConfig kafkaTopicConfig;

    /**
     * Constructs an AbstractTopology instance with the specified Kafka topic configuration.
     *
     * @param kafkaTopicConfig Configuration for Kafka topics used in this topology.
     */
    protected AbstractTopology(KafkaTopicConfig kafkaTopicConfig) {
        this.kafkaTopicConfig = kafkaTopicConfig;
    }

    /**
     * Abstract method to be implemented by subclasses, defining the processing logic
     * of the Kafka Streams topology.
     *
     * @param streamsBuilder the builder used to define the Kafka Streams topology
     */
    protected abstract void process(StreamsBuilder streamsBuilder);

    /**
     * Creates a JSON serde for a specified class type.
     *
     * @param clazz the class type for which to create the serde
     * @param <S>   the type of the class
     * @return a serde for the specified class
     */
    protected <S> Serde<S> getSerde(Class<S> clazz) {
        return new JsonSerde<>(clazz);
    }

    /**
     * Creates a JSON serde for a specified type reference.
     *
     * @param typeReference the TypeReference for which to create the serde
     * @param <S>           the type of the reference
     * @return a serde for the specified type reference
     */
    protected <S> Serde<S> getSerde(TypeReference<S> typeReference) {
        return new JsonSerde<>(typeReference);
    }

    /**
     * Creates a serde for messages wrapped in MessageDto, allowing for deserialization
     * of messages with metadata.
     *
     * @param clazz the class type for the MessageDto content
     * @param <T>   the type of the content within MessageDto
     * @return a serde for MessageDto containing the specified content type
     */
    protected <T> Serde<MessageDto<T>> getMessageDtoSerde(Class<T> clazz) {
        TypeReference<MessageDto<T>> typeReference = new TypeReference<>() {
            @Override
            public Type getType() {
                return TypeFactory.defaultInstance()
                        .constructParametricType(MessageDto.class, clazz);
            }
        };
        return getSerde(typeReference);
    }

    /**
     * Creates a serde for aggregation messages wrapped in AggregationDto, allowing
     * for deserialization of aggregated messages with metadata.
     *
     * @param clazzT the class type for the AggregationDto content
     * @param <T>    the type of the content within AggregationDto
     * @return a serde for AggregationDto containing the specified content type
     */
    protected <T> Serde<AggregationDto<T>> getAggregationDtoSerde(Class<T> clazzT) {
        TypeReference<AggregationDto<T>> typeReference = new TypeReference<>() {
            @Override
            public Type getType() {
                return TypeFactory
                        .defaultInstance()
                        .constructParametricType(AggregationDto.class, clazzT);
            }
        };
        return getSerde(typeReference);
    }

    /**
     * Creates a materialized store configuration with specified key and value serdes
     * and caching disabled.
     *
     * @param storeName the name of the store
     * @param keySerde  the serde for the key type
     * @param valueSerde the serde for the value type
     * @param <K>       the type of keys in the store
     * @param <V>       the type of values in the store
     * @return a Materialized configuration for the store
     */
    protected <K, V> Materialized<K, V, KeyValueStore<Bytes, byte[]>> createMaterializedStore(
            String storeName, Serde<K> keySerde, Serde<V> valueSerde) {

        return Materialized.<K, V, KeyValueStore<Bytes, byte[]>>as(storeName)
                .withKeySerde(keySerde)
                .withValueSerde(valueSerde)
                .withCachingDisabled();
    }

    /**
     * Extracts the model from a message, preserving metadata such as operation type
     * and timestamp. Returns null if the message or its content is null.
     *
     * @param message the message containing the model
     * @param <T>     the type of the model within the message
     * @return the extracted model with metadata, or null if the message is empty
     */
    protected <T extends BaseMetaDataDto> T extractModelFromMessage(MessageDto<T> message) {
        if (message == null || message.getAfter() == null) {
            return null;
        }
        T model = message.getAfter();
        model.setOp(message.getOp());
        model.setTs(message.getTs());
        return model;
    }
}

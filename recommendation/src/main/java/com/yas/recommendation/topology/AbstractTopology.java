package com.yas.recommendation.topology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.yas.recommendation.configuration.KafkaTopicConfig;
import com.yas.recommendation.dto.AggregationDTO;
import com.yas.recommendation.dto.BaseMetaDataEntity;
import com.yas.recommendation.dto.MessageDTO;
import com.yas.recommendation.dto.MetaData;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.lang.reflect.Type;

public abstract class AbstractTopology {

    protected final KafkaTopicConfig kafkaTopicConfig;

    public AbstractTopology(KafkaTopicConfig kafkaTopicConfig) {
        this.kafkaTopicConfig = kafkaTopicConfig;
    }

    protected abstract void process(StreamsBuilder streamsBuilder);

    protected <S> Serde<S> getSerde(Class<S> clazz) {
        return new JsonSerde<>(clazz);
    }

    protected <S> Serde<S> getSerde(TypeReference<S> typeReference) {
        return new JsonSerde<>(typeReference);
    }

    protected <T> Serde<MessageDTO<T>> getMessageDTOSerde(Class<T> clazz) {
        TypeReference<MessageDTO<T>> typeReference = new TypeReference<>() {
            @Override
            public Type getType() {
                return TypeFactory.defaultInstance()
                        .constructParametricType(MessageDTO.class, clazz);
            }
        };
        return getSerde(typeReference);
    }

    protected <ID, T> Serde<AggregationDTO<ID, T>> getAggregationDTOSerde(Class<ID> clazzID, Class<T> clazzT) {
        TypeReference<AggregationDTO<ID, T>> typeReference = new TypeReference<>() {
            @Override
            public Type getType() {
                return TypeFactory
                        .defaultInstance()
                        .constructParametricType(AggregationDTO.class, clazzID, clazzT);
            }
        };
        return getSerde(typeReference);
    }


    protected <K, V> Materialized<K, V, KeyValueStore<Bytes, byte[]>> createMaterializedStore(
            String storeName, Serde<K> keySerde, Serde<V> valueSerde) {

        return Materialized.<K, V, KeyValueStore<Bytes, byte[]>>as(storeName)
                .withKeySerde(keySerde)
                .withValueSerde(valueSerde)
                .withCachingDisabled();
    }

    protected <T extends BaseMetaDataEntity> T extractModelFromMessage(MessageDTO<T> message) {
        if (message == null || message.getAfter() == null) {
            return null;
        }
        T model = message.getAfter();
        model.setMetaData(new MetaData(message.getOp(), message.getComingTs()));
        return model;
    }
}

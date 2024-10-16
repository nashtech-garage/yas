package com.yas.recommendation.topology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yas.recommendation.dto.AggregationDTO;
import com.yas.recommendation.dto.MessageDTO;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.kafka.support.serializer.JsonSerde;

public abstract class AbstractTopology {
    protected String sourceTopic;
    protected String sinkTopic;

    public AbstractTopology(String sourceTopic, String sinkTopic) {
        this.sourceTopic = sourceTopic;
        this.sinkTopic = sinkTopic;
    }

    protected abstract void proccess(StreamsBuilder streamsBuilder);

    protected <S> Serde<S> getSerde(Class<S> clazz) {
        return new JsonSerde<>(clazz);
    }

    protected <S> Serde<S> getSerde(TypeReference<S> typeReference) {
        return new JsonSerde<>(typeReference);
    }

    protected <T> Serde<MessageDTO<T>> getMessageDTOSerde(Class<T> clazz) {
        TypeReference<MessageDTO<T>> typeReference = new TypeReference<>() {
            @Override
            public java.lang.reflect.Type getType() {
                return com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                        .constructParametricType(MessageDTO.class, clazz);
            }
        };
        return getSerde(typeReference);
    }

    protected <ID, T> Serde<AggregationDTO<ID, T>> getAggregationDTOSerde(Class<ID> clazzID, Class<T> clazzT) {
        TypeReference<AggregationDTO<ID, T>> typeReference = new TypeReference<>() {
            @Override
            public java.lang.reflect.Type getType() {
                return com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                        .constructParametricType(AggregationDTO.class, clazzID, clazzT);
            }
        };
        return getSerde(typeReference);
    }


    protected  <K, V> Materialized<K, V, KeyValueStore<Bytes, byte[]>> createMaterializedStore(
            String storeName, Serde<K> keySerde, Serde<V> valueSerde) {

        return Materialized.<K, V, KeyValueStore<Bytes, byte[]>>as(storeName)
                .withKeySerde(keySerde)
                .withValueSerde(valueSerde);
    }

    protected <T> boolean isAfterObjectNonNull(MessageDTO<T> message) {
        return message != null && message.getAfter() != null;
    }
}

package com.yas.recommendation.topology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yas.recommendation.dto.*;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.*;

public class KafkaTest {
    private static final String PRODUCT_MATERIALIZED_VIEW = "product-mv";
    private static final String BRAND_MATERIALIZED_VIEW = "brand-mv";
    private static final String PRODUCT_BRAND_MATERIALIZED_VIEW = "product-brand-mv";
    private static final String CATEGORY_MATERIALIZED_VIEW = "category-mv";
    private static final String PRODUCT_CATEGORY_MATERIALIZED_VIEW = "product-category-mv";
    private static final String PRODUCT_CATEGORY_WITH_NAME_MATERIALIZED_VIEW = "product-category-with-name-mv";
    private static final String PRODUCT_CATEGORY_AGGREGATION_MATERIALIZED_VIEW = "product-category-agg-mv";
    private static final String PRODUCT_BRAND_CATEGORY_DETAIL_MATERIALIZED_VIEW = "product-brand-category-detail-mv";
    private static final String PRODUCT_ATTRIBUTE_MATERIALIZED_VIEW = "product-attribute-mv";
    private static final String PRODUCT_ATTRIBUTE_VALUE_MATERIALIZED_VIEW = "product-attribute-value-mv";
    private static final String PRODUCT_ATTRIBUTE_VALUE_WITH_NAME_MATERIALIZED_VIEW = "product-attribute-value-with-name-mv";
    private static final String PRODUCT_ATTRIBUTE_AGGREGATION_MATERIALIZED_VIEW = "product-attribute-agg-mv";
    private static final String PRODUCT_BRAND_CATEGORY_ATTRIBUTE_DETAIL_MATERIALIZED_VIEW = "product-brand-category-attribute-detail-mv";

    public static void main(String[] args) {

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KTable<Long, ProductDTO> productTable = streamsBuilder.stream(
                        "dbproduct.public.product",
                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(ProductDTO.class)))
                .filter((key, value) -> isAfterObjectNonNull(value))
                .selectKey((key, value) -> value.getAfter().getId())
                .mapValues(MessageDTO::getAfter)
                .toTable(createMaterializedStore(PRODUCT_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductDTO.class)));

        KTable<Long, BrandDTO> brandTable = streamsBuilder.stream(
                        "dbproduct.public.brand",
                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(BrandDTO.class))
                )
                .filter((key, value) -> isAfterObjectNonNull(value))
                .selectKey((key, value) -> value.getAfter().getId())
                .mapValues(MessageDTO::getAfter)
                .toTable(createMaterializedStore(BRAND_MATERIALIZED_VIEW, Serdes.Long(), getSerde(BrandDTO.class)));

        KTable<Long, ProductResultDTO> productBrandTable = productTable.leftJoin(
                brandTable,
                ProductDTO::getBrandId,
                KafkaTest::enrichWithProductAndBrandData,
                createMaterializedStore(PRODUCT_BRAND_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductResultDTO.class))
        );

        KTable<Long, CategoryDTO> categoryTable = streamsBuilder.stream(
                        "dbproduct.public.category",
                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(CategoryDTO.class))
                )
                .filter((key, value) -> isAfterObjectNonNull(value))
                .selectKey((key, value) -> value.getAfter().getId())
                .mapValues(MessageDTO::getAfter)
                .toTable(createMaterializedStore(CATEGORY_MATERIALIZED_VIEW, Serdes.Long(), getSerde(CategoryDTO.class)));

        KTable<Long, ProductCategoryDTO> productCategoryTable = streamsBuilder.stream(
                        "dbproduct.public.product_category",
                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(ProductCategoryDTO.class))
                )
                .filter((key, value) -> isAfterObjectNonNull(value))
                .selectKey((key, value) -> value.getAfter().getId())
                .mapValues(MessageDTO::getAfter)
                .toTable(createMaterializedStore(PRODUCT_CATEGORY_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductCategoryDTO.class)));


        KTable<Long, ProductCategoryDTO> productCategoryDetailTable = productCategoryTable
                .leftJoin(
                        categoryTable,
                        ProductCategoryDTO::getCategoryId,
                        (productCategory, category) -> {
                            productCategory.setCategoryName(category.getName());
                            return productCategory;
                        },
                        createMaterializedStore(PRODUCT_CATEGORY_WITH_NAME_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductCategoryDTO.class))
                );

        KTable<Long, AggregationDTO<Long, CategoryDTO>> productCategoryAgg = productCategoryDetailTable
                .toStream()
                .selectKey((key, value) -> value.getProductId())
                .groupByKey()
                .aggregate(
                        AggregationDTO::new,
                        (productId, productCategoryDetail, agg) -> {
                            agg.setJoinId(productId);
                            agg.getAggregationContents().removeIf(category -> productCategoryDetail.getCategoryId().equals(category.getId()));
                            agg.add(new CategoryDTO(productCategoryDetail.getCategoryId(), productCategoryDetail.getCategoryName()));
                            return agg;
                        },
                        createMaterializedStore(PRODUCT_CATEGORY_AGGREGATION_MATERIALIZED_VIEW, Serdes.Long(), getAggregationDTOSerde(Long.class, CategoryDTO.class))
                );


        KTable<Long, ProductResultDTO> addingCategoryDetailTable = productBrandTable
                .leftJoin(
                        productCategoryAgg,
                        (productResult, agg) -> {
                            if (agg != null) {
                                productResult.setCategories(agg.getAggregationContents());
                            }

                            return productResult;
                        },
                        createMaterializedStore(PRODUCT_BRAND_CATEGORY_DETAIL_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductResultDTO.class))
                );


        KTable<Long, ProductAttributeDTO> productAttributeTable = streamsBuilder.stream(
                        "dbproduct.public.product_attribute",
                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(ProductAttributeDTO.class))
                )
                .filter((key, value) -> isAfterObjectNonNull(value))
                .selectKey((key, value) -> value.getAfter().getId())
                .mapValues(MessageDTO::getAfter)
                .toTable(createMaterializedStore(PRODUCT_ATTRIBUTE_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductAttributeDTO.class)));

        KTable<Long, ProductAttributeValueDTO> productAttributeValueTable = streamsBuilder.stream(
                        "dbproduct.public.product_attribute_value",
                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(ProductAttributeValueDTO.class))
                )
                .filter((key, value) -> isAfterObjectNonNull(value))
                .selectKey((key, value) -> value.getAfter().getId())
                .mapValues(MessageDTO::getAfter)
                .toTable(createMaterializedStore(PRODUCT_ATTRIBUTE_VALUE_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductAttributeValueDTO.class)));


        KTable<Long, ProductAttributeValueDTO> productAttributeValueDetailTable = productAttributeValueTable
                .leftJoin(
                        productAttributeTable,
                        ProductAttributeValueDTO::getProductAttributeId,
                        (productAttributeValue, productAttribute) -> {
                            productAttributeValue.setProductAttributeName(productAttribute.getName());
                            return productAttributeValue;
                        },
                        createMaterializedStore(PRODUCT_ATTRIBUTE_VALUE_WITH_NAME_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductAttributeValueDTO.class))
                );


        KTable<Long, AggregationDTO<Long, ProductAttributeDTO>> productAttributeAgg = productAttributeValueDetailTable
                .toStream()
                .selectKey((key, value) -> value.getProductId())
                .groupByKey()
                .aggregate(
                        AggregationDTO::new,
                        (productId, productAttributeValueDetail, agg) -> {
                            agg.setJoinId(productId);
                            agg.getAggregationContents().removeIf(attribute -> productAttributeValueDetail.getProductAttributeId().equals(attribute.getId()));
                            agg.add(new ProductAttributeDTO(productAttributeValueDetail.getProductAttributeId(), productAttributeValueDetail.getProductAttributeName(), productAttributeValueDetail.getValue()));
                            return agg;
                        },
                        createMaterializedStore(PRODUCT_ATTRIBUTE_AGGREGATION_MATERIALIZED_VIEW, Serdes.Long(), getAggregationDTOSerde(Long.class, ProductAttributeDTO.class))
                );

        KTable<Long, ProductResultDTO> addingAttributeDetailTable = addingCategoryDetailTable
                .leftJoin(
                        productAttributeAgg,
                        ProductResultDTO::getId,
                        (productResult, agg) -> {
                            if (agg != null) {
                                productResult.setProductAttributes(agg.getAggregationContents());
                            }
                            return productResult;
                        },
                        createMaterializedStore(PRODUCT_BRAND_CATEGORY_ATTRIBUTE_DETAIL_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductResultDTO.class))
                );


        addingAttributeDetailTable.toStream()
                .map((key, value) -> new KeyValue<>(value.getId(), value))
                .to("sink.test", Produced.with(Serdes.Long(), getSerde(ProductResultDTO.class)));

        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), getKStreamsConfig().asProperties());
        streams.cleanUp(); // only do this in dev - not in prod
        streams.start();

        // print the topology
        System.out.println(streams.toString());

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static <S> Serde<S> getSerde(Class<S> clazz) {
        return new JsonSerde<>(clazz);
    }

    private static <S> Serde<S> getSerde(TypeReference<S> typeReference) {
        return new JsonSerde<>(typeReference);
    }

    private static <T> Serde<MessageDTO<T>> getMessageDTOSerde(Class<T> clazz) {
        TypeReference<MessageDTO<T>> typeReference = new TypeReference<>() {
            @Override
            public java.lang.reflect.Type getType() {
                return com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                        .constructParametricType(MessageDTO.class, clazz);
            }
        };
        return getSerde(typeReference);
    }

    private static <ID, T> Serde<AggregationDTO<ID, T>> getAggregationDTOSerde(Class<ID> clazzID, Class<T> clazzT) {
        TypeReference<AggregationDTO<ID, T>> typeReference = new TypeReference<>() {
            @Override
            public java.lang.reflect.Type getType() {
                return com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                        .constructParametricType(AggregationDTO.class, clazzID, clazzT);
            }
        };
        return getSerde(typeReference);
    }


    private static <K, V> Materialized<K, V, KeyValueStore<Bytes, byte[]>> createMaterializedStore(
            String storeName, Serde<K> keySerde, Serde<V> valueSerde) {

        return Materialized.<K, V, KeyValueStore<Bytes, byte[]>>as(storeName)
                .withKeySerde(keySerde)
                .withValueSerde(valueSerde);
    }

    private static <T> boolean isAfterObjectNonNull(MessageDTO<T> message) {
        return message != null && message.getAfter() != null;
    }

    private static ProductResultDTO enrichWithProductAndBrandData(ProductDTO productRecord, BrandDTO brandRecord) {
        ProductResultDTO productResultDTO = new ProductResultDTO();
        productResultDTO.setId(productRecord.getId());
        productResultDTO.setName(productRecord.getName());
        productResultDTO.setMetaDescription(productRecord.getMetaDescription());
        productResultDTO.setShortDescription(productRecord.getShortDescription());
        productResultDTO.setPrice(productRecord.getPrice());
        productResultDTO.setMetaTitle(productRecord.getMetaTitle());
        productResultDTO.setSpecification(productRecord.getSpecification());
        productResultDTO.setBrandId(brandRecord.getId());
        productResultDTO.setBrandName(brandRecord.getName());
        return productResultDTO;
    }

    static KafkaStreamsConfiguration getKStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(APPLICATION_ID_CONFIG, "streams-app1");
        props.put(BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, JsonSerde.class);
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new KafkaStreamsConfiguration(props);
    }
}

package com.yas.recommendation.topology;

import com.yas.recommendation.configuration.KafkaTopicConfig;
import com.yas.recommendation.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductTopology extends AbstractTopology {

    private static final String PRODUCT_MATERIALIZED_VIEW = "product-mv";
    private static final String BRAND_MATERIALIZED_VIEW = "brand-mv";
    private static final String PRODUCT_BRAND_MATERIALIZED_VIEW = "product-brand-mv";
    private static final String CATEGORY_MATERIALIZED_VIEW = "category-mv";
    private static final String PRODUCT_CATEGORY_MATERIALIZED_VIEW = "product-category-mv";
    private static final String PRODUCT_CATEGORY_NON_NULL_MATERIALIZED_VIEW = "product-category-nonnull-mv";
    private static final String PRODUCT_CATEGORY_WITH_NAME_MATERIALIZED_VIEW = "product-category-with-name-mv";
    private static final String PRODUCT_CATEGORY_AGGREGATION_MATERIALIZED_VIEW = "product-category-agg-mv";
    private static final String PRODUCT_BRAND_CATEGORY_DETAIL_MATERIALIZED_VIEW = "product-brand-category-detail-mv";
    private static final String PRODUCT_ATTRIBUTE_MATERIALIZED_VIEW = "product-attribute-mv";
    private static final String PRODUCT_ATTRIBUTE_VALUE_NONNULL_MATERIALIZED_VIEW = "product-attribute-value-nonnull-mv";
    private static final String PRODUCT_ATTRIBUTE_VALUE_MATERIALIZED_VIEW = "product-attribute-value-mv";
    private static final String PRODUCT_ATTRIBUTE_VALUE_WITH_NAME_MATERIALIZED_VIEW = "product-attribute-value-with-name-mv";
    private static final String PRODUCT_ATTRIBUTE_AGGREGATION_MATERIALIZED_VIEW = "product-attribute-agg-mv";
    private static final String PRODUCT_BRAND_CATEGORY_ATTRIBUTE_DETAIL_MATERIALIZED_VIEW = "product-brand-category-attribute-detail-mv";

    @Autowired
    public ProductTopology(KafkaTopicConfig kafkaTopicConfig) {
        super(kafkaTopicConfig);
    }

    @Override
    protected void process(StreamsBuilder streamsBuilder) {
        KTable<Long, ProductDTO> productTable = createProductTable(streamsBuilder);
        KTable<Long, BrandDTO> brandTable = createBrandTable(streamsBuilder);
        KTable<Long, ProductResultDTO> productBrandTable = joinProductAndBrand(productTable, brandTable);

        KTable<Long, CategoryDTO> categoryTable = createCategoryTable(streamsBuilder);
        KTable<Long, ProductCategoryDTO> productCategoryTable = createProductCategoryTable(streamsBuilder);
        KTable<Long, ProductCategoryDTO> nonNullProductCategoryTable = filterNonNullProductCategory(productCategoryTable);
        KTable<Long, ProductCategoryDTO> productCategoryDetailTable = enrichProductCategoryWithDetails(productCategoryTable, categoryTable);

        KTable<Long, AggregationDTO<Long, CategoryDTO>> productCategoryAgg = aggregateProductCategoryDetails(productCategoryDetailTable, nonNullProductCategoryTable);

        KTable<Long, ProductResultDTO> addingCategoryDetailTable = joinProductWithCategoryDetails(productBrandTable, productCategoryAgg);

        KTable<Long, ProductAttributeDTO> productAttributeTable = createProductAttributeTable(streamsBuilder);
        KTable<Long, ProductAttributeValueDTO> productAttributeValueTable = createProductAttributeValueTable(streamsBuilder);
        KTable<Long, ProductAttributeValueDTO> nonNullProductAttributeValueTable = filterNonNullProductAttributeValue(productAttributeValueTable);
        KTable<Long, ProductAttributeValueDTO> productAttributeValueDetailTable = enrichProductAttributeValueDetails(productAttributeValueTable, productAttributeTable);

        KTable<Long, AggregationDTO<Long, ProductAttributeDTO>> productAttributeAgg = aggregateProductAttributeDetails(productAttributeValueDetailTable, nonNullProductAttributeValueTable);

        KTable<Long, ProductResultDTO> resultTable = joinProductWithAttributeDetails(addingCategoryDetailTable, productAttributeAgg);

        writeToSink(resultTable);
    }

    private KTable<Long, ProductDTO> createProductTable(StreamsBuilder streamsBuilder) {
        return streamsBuilder.stream(
                        kafkaTopicConfig.product(),
                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(ProductDTO.class)))
                .selectKey((key, value) -> key.getId())
                .mapValues(this::extractModelFromMessage)
                .toTable(createMaterializedStore(PRODUCT_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductDTO.class)));
    }

    private KTable<Long, BrandDTO> createBrandTable(StreamsBuilder streamsBuilder) {
        return streamsBuilder.stream(
                        "dbproduct.public.brand",
                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(BrandDTO.class)))
                .selectKey((key, value) -> key.getId())
                .mapValues(this::extractModelFromMessage)
                .toTable(createMaterializedStore(BRAND_MATERIALIZED_VIEW, Serdes.Long(), getSerde(BrandDTO.class)));
    }

    private KTable<Long, ProductResultDTO> joinProductAndBrand(KTable<Long, ProductDTO> productTable, KTable<Long, BrandDTO> brandTable) {
        return productTable.leftJoin(
                brandTable,
                ProductDTO::getBrandId,
                this::enrichWithProductAndBrandData,
                createMaterializedStore(PRODUCT_BRAND_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductResultDTO.class))
        );
    }

    private KTable<Long, CategoryDTO> createCategoryTable(StreamsBuilder streamsBuilder) {
        return streamsBuilder.stream(
                        "dbproduct.public.category",
                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(CategoryDTO.class)))
                .selectKey((key, value) -> key.getId())
                .mapValues(this::extractModelFromMessage)
                .toTable(createMaterializedStore(CATEGORY_MATERIALIZED_VIEW, Serdes.Long(), getSerde(CategoryDTO.class)));
    }

    private KTable<Long, ProductCategoryDTO> createProductCategoryTable(StreamsBuilder streamsBuilder) {
        return streamsBuilder.stream(
                        "dbproduct.public.product_category",
                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(ProductCategoryDTO.class)))
                .selectKey((key, value) -> key.getId())
                .mapValues(this::extractModelFromMessage)
                .toTable(createMaterializedStore(PRODUCT_CATEGORY_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductCategoryDTO.class)));
    }

    private KTable<Long, ProductCategoryDTO> filterNonNullProductCategory(KTable<Long, ProductCategoryDTO> productCategoryTable) {
        return productCategoryTable.toStream()
                .filter((key, value) -> value != null)
                .toTable(createMaterializedStore(PRODUCT_CATEGORY_NON_NULL_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductCategoryDTO.class)));
    }

    private KTable<Long, ProductCategoryDTO> enrichProductCategoryWithDetails(KTable<Long, ProductCategoryDTO> productCategoryTable, KTable<Long, CategoryDTO> categoryTable) {
        return productCategoryTable.leftJoin(
                categoryTable,
                ProductCategoryDTO::getCategoryId,
                (productCategory, category) -> {
                    productCategory.setCategoryName(category.getName());
                    return productCategory;
                },
                createMaterializedStore(PRODUCT_CATEGORY_WITH_NAME_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductCategoryDTO.class))
        );
    }

    private KTable<Long, AggregationDTO<Long, CategoryDTO>> aggregateProductCategoryDetails(KTable<Long, ProductCategoryDTO> productCategoryDetailTable, KTable<Long, ProductCategoryDTO> nonNullProductCategoryTable) {
        return productCategoryDetailTable.toStream()
                .mapValues(v -> v != null ? v : new ProductCategoryDTO(-1L))
                .join(
                        nonNullProductCategoryTable,
                        (productCategoryDetail, nonNullProductCategoryDetail) -> {
                            if (productCategoryDetail.getId().equals(-1L)) {
                                nonNullProductCategoryDetail.setDeleted(true);
                                return nonNullProductCategoryDetail;
                            }
                            return productCategoryDetail;
                        }
                )
                .selectKey((key, value) -> value.getProductId())
                .groupByKey()
                .aggregate(
                        AggregationDTO::new,
                        (productId, productCategoryDetail, agg) -> {
                            agg.setJoinId(productId);
                            if (productCategoryDetail.isDeleted()) {
                                agg.getAggregationContents().removeIf(category -> productCategoryDetail.getCategoryId().equals(category.getId()));
                            } else {
                                agg.getAggregationContents().removeIf(category -> productCategoryDetail.getCategoryId().equals(category.getId()));
                                agg.add(new CategoryDTO(productCategoryDetail.getMetaData(), productCategoryDetail.getCategoryId(), productCategoryDetail.getCategoryName()));
                            }
                            return agg;
                        },
                        createMaterializedStore(PRODUCT_CATEGORY_AGGREGATION_MATERIALIZED_VIEW, Serdes.Long(), getAggregationDTOSerde(Long.class, CategoryDTO.class))
                );
    }

    private KTable<Long, ProductResultDTO> joinProductWithCategoryDetails(KTable<Long, ProductResultDTO> productBrandTable, KTable<Long, AggregationDTO<Long, CategoryDTO>> productCategoryAgg) {
        return productBrandTable.leftJoin(
                productCategoryAgg,
                (productResult, agg) -> {
                    if (agg != null) {
                        productResult.setCategories(agg.getAggregationContents());
                    }
                    return productResult;
                },
                createMaterializedStore(PRODUCT_BRAND_CATEGORY_DETAIL_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductResultDTO.class))
        );
    }

    private KTable<Long, ProductAttributeDTO> createProductAttributeTable(StreamsBuilder streamsBuilder) {
        return streamsBuilder.stream(
                        "dbproduct.public.product_attribute",
                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(ProductAttributeDTO.class)))
                .selectKey((key, value) -> key.getId())
                .mapValues(this::extractModelFromMessage)
                .toTable(createMaterializedStore(PRODUCT_ATTRIBUTE_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductAttributeDTO.class)));
    }

    private KTable<Long, ProductAttributeValueDTO> createProductAttributeValueTable(StreamsBuilder streamsBuilder) {
        return streamsBuilder.stream(
                        "dbproduct.public.product_attribute_value",
                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(ProductAttributeValueDTO.class)))
                .selectKey((key, value) -> key.getId())
                .mapValues(this::extractModelFromMessage)
                .toTable(createMaterializedStore(PRODUCT_ATTRIBUTE_VALUE_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductAttributeValueDTO.class)));
    }

    private KTable<Long, ProductAttributeValueDTO> filterNonNullProductAttributeValue(KTable<Long, ProductAttributeValueDTO> productAttributeValueTable) {
        return productAttributeValueTable.toStream()
                .filter((key, value) -> value != null)
                .toTable(createMaterializedStore(PRODUCT_ATTRIBUTE_VALUE_NONNULL_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductAttributeValueDTO.class)));
    }

    private KTable<Long, ProductAttributeValueDTO> enrichProductAttributeValueDetails(KTable<Long, ProductAttributeValueDTO> productAttributeValueTable, KTable<Long, ProductAttributeDTO> productAttributeTable) {
        return productAttributeValueTable.leftJoin(
                productAttributeTable,
                ProductAttributeValueDTO::getProductAttributeId,
                (productAttributeValue, productAttribute) -> {
                    productAttributeValue.setProductAttributeName(productAttribute.getName());
                    return productAttributeValue;
                },
                createMaterializedStore(PRODUCT_ATTRIBUTE_VALUE_WITH_NAME_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductAttributeValueDTO.class))
        );
    }

    private KTable<Long, AggregationDTO<Long, ProductAttributeDTO>> aggregateProductAttributeDetails(KTable<Long, ProductAttributeValueDTO> productAttributeValueDetailTable, KTable<Long, ProductAttributeValueDTO> nonNullProductAttributeValueTable) {
        return productAttributeValueDetailTable.toStream()
                .mapValues(v -> v != null ? v : new ProductAttributeValueDTO(-1L))
                .join(
                        nonNullProductAttributeValueTable,
                        (productAttributeValueDetail, nonNullProductAttributeValueDetail) -> {
                            if (productAttributeValueDetail.getId().equals(-1L)) {
                                nonNullProductAttributeValueDetail.setDeleted(true);
                                return nonNullProductAttributeValueDetail;
                            }
                            return productAttributeValueDetail;
                        }
                )
                .selectKey((key, value) -> value.getProductId())
                .groupByKey()
                .aggregate(
                        AggregationDTO::new,
                        (productId, productAttributeValueDetail, agg) -> {
                            agg.setJoinId(productId);
                            if (productAttributeValueDetail.isDeleted()) {
                                agg.getAggregationContents().removeIf(attribute -> productAttributeValueDetail.getProductAttributeId().equals(attribute.getId()));
                            } else {
                                agg.getAggregationContents().removeIf(attribute -> productAttributeValueDetail.getProductAttributeId().equals(attribute.getId()));
                                agg.add(new ProductAttributeDTO(productAttributeValueDetail.getMetaData(), productAttributeValueDetail.getProductAttributeId(), productAttributeValueDetail.getProductAttributeName(), productAttributeValueDetail.getValue()));
                            }
                            return agg;
                        },
                        createMaterializedStore(PRODUCT_ATTRIBUTE_AGGREGATION_MATERIALIZED_VIEW, Serdes.Long(), getAggregationDTOSerde(Long.class, ProductAttributeDTO.class))
                );
    }

    private KTable<Long, ProductResultDTO> joinProductWithAttributeDetails(KTable<Long, ProductResultDTO> addingCategoryDetailTable, KTable<Long, AggregationDTO<Long, ProductAttributeDTO>> productAttributeAgg) {
        return addingCategoryDetailTable.leftJoin(
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
    }

    private void writeToSink(KTable<Long, ProductResultDTO> resultTable) {
        resultTable
                .toStream()
                .to(kafkaTopicConfig.productSink(), Produced.with(Serdes.Long(), getSerde(ProductResultDTO.class)));
    }

    private ProductResultDTO enrichWithProductAndBrandData(ProductDTO productRecord, BrandDTO brandRecord) {
        ProductResultDTO productResultDTO = new ProductResultDTO();
        productResultDTO.setBrand(brandRecord);
        productResultDTO.setId(productRecord.getId());
        productResultDTO.setName(productRecord.getName());
        productResultDTO.setMetaDescription(productRecord.getMetaDescription());
        productResultDTO.setShortDescription(productRecord.getShortDescription());
        productResultDTO.setPrice(productRecord.getPrice());
        productResultDTO.setMetaTitle(productRecord.getMetaTitle());
        productResultDTO.setSpecification(productRecord.getSpecification());
        productResultDTO.setBrandId(productRecord.getBrandId());
        productResultDTO.setMetaData(productRecord.getMetaData());
        productResultDTO.setPublished(productRecord.getPublished());
        return productResultDTO;
    }

}

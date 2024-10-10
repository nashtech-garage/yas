package com.yas.recommendation.topology;

import com.yas.recommendation.configuration.KafkaTopicConfig;
import com.yas.recommendation.dto.AggregationDto;
import com.yas.recommendation.dto.BrandDto;
import com.yas.recommendation.dto.CategoryDto;
import com.yas.recommendation.dto.KeyDto;
import com.yas.recommendation.dto.ProductAttributeDto;
import com.yas.recommendation.dto.ProductAttributeValueDto;
import com.yas.recommendation.dto.ProductCategoryDto;
import com.yas.recommendation.dto.ProductDto;
import com.yas.recommendation.dto.ProductResultDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ProductTopology defines a Kafka Streams topology that processes product-related data,
 * joins it with brand, category, and attribute information, and aggregates details
 * for enriched product output. This topology generates various materialized views for
 * different stages of data enrichment and outputs the final processed product details
 * to a Kafka sink topic.
 */
@Component
@Slf4j
public class ProductTopology extends AbstractTopology {

    private static final String PRODUCT_MATERIALIZED_VIEW = "product-mv";
    private static final String BRAND_MATERIALIZED_VIEW = "brand-mv";
    private static final String PRODUCT_BRAND_MATERIALIZED_VIEW = "product-brand-mv";
    private static final String CATEGORY_MATERIALIZED_VIEW = "category-mv";
    private static final String PRODUCT_CATEGORY_MATERIALIZED_VIEW = "product-category-mv";
    private static final String PRODUCT_CATEGORY_NON_NULL_MATERIALIZED_VIEW = "prod-category-nn-mv";
    private static final String PRODUCT_CATEGORY_WITH_NAME_MATERIALIZED_VIEW = "prod-category-with-name-mv";
    private static final String PRODUCT_CATEGORY_AGGREGATION_MATERIALIZED_VIEW = "prod-category-agg-mv";
    private static final String PRODUCT_BRAND_CATEGORY_DETAIL_MATERIALIZED_VIEW = "prod-brand-category-detail-mv";
    private static final String PRODUCT_ATTRIBUTE_MATERIALIZED_VIEW = "prod-attr-mv";
    private static final String PRODUCT_ATTRIBUTE_VALUE_NONNULL_MATERIALIZED_VIEW = "prod-attr-value-nn-mv";
    private static final String PRODUCT_ATTRIBUTE_VALUE_MATERIALIZED_VIEW = "prod-attr-value-mv";
    private static final String PRODUCT_ATTRIBUTE_VALUE_WITH_NAME_MATERIALIZED_VIEW = "prod-attr-value-with-name-mv";
    private static final String PRODUCT_ATTRIBUTE_AGGREGATION_MATERIALIZED_VIEW = "prod-attr-agg-mv";
    private static final String PRODUCT_WITH_ALL_DETAIL_MATERIALIZED_VIEW = "product-with-all-detail-mv";

    @Autowired
    public ProductTopology(KafkaTopicConfig kafkaTopicConfig) {
        super(kafkaTopicConfig);
    }

    @Override
    @Autowired
    protected void process(StreamsBuilder streamsBuilder) {
        KTable<Long, ProductDto> productTable = createProductTable(streamsBuilder);
        KTable<Long, BrandDto> brandTable = createBrandTable(streamsBuilder);
        KTable<Long, ProductResultDto> productBrandTable = joinProductAndBrand(productTable, brandTable);

        KTable<Long, CategoryDto> categoryTable = createCategoryTable(streamsBuilder);

        KTable<Long, ProductCategoryDto> productCategoryTable =
                createProductCategoryTable(streamsBuilder);

        KTable<Long, ProductCategoryDto> nonNullProductCategoryTable =
                filterNonNullProductCategory(productCategoryTable);

        KTable<Long, ProductCategoryDto> productCategoryDetailTable =
                enrichProductCategoryWithDetails(productCategoryTable, categoryTable);

        KTable<Long, AggregationDto<CategoryDto>> productCategoryAgg =
                aggregateProductCategoryDetails(productCategoryDetailTable, nonNullProductCategoryTable);

        KTable<Long, ProductResultDto> addingCategoryDetailTable =
                joinProductWithCategoryDetails(productBrandTable, productCategoryAgg);

        KTable<Long, ProductAttributeDto> productAttributeTable = createProductAttributeTable(streamsBuilder);

        KTable<Long, ProductAttributeValueDto> productAttributeValueTable =
                createProductAttributeValueTable(streamsBuilder);

        KTable<Long, ProductAttributeValueDto> nonNullProductAttributeValueTable =
                filterNonNullProductAttributeValue(productAttributeValueTable);

        KTable<Long, ProductAttributeValueDto> productAttributeValueDetailTable =
                enrichProductAttributeValueDetails(productAttributeValueTable, productAttributeTable);

        KTable<Long, AggregationDto<ProductAttributeDto>> productAttributeAgg =
                aggregateProductAttributeDetails(productAttributeValueDetailTable, nonNullProductAttributeValueTable);

        KTable<Long, ProductResultDto> resultTable =
                joinProductWithAttributeDetails(addingCategoryDetailTable, productAttributeAgg);

        writeToSink(resultTable);
    }

    private KTable<Long, ProductDto> createProductTable(StreamsBuilder streamsBuilder) {
        return streamsBuilder.stream(
                        kafkaTopicConfig.product(),
                        Consumed.with(getSerde(KeyDto.class), getMessageDtoSerde(ProductDto.class)))
                .selectKey((key, value) -> key.getId())
                .mapValues(this::extractModelFromMessage)
                .toTable(createMaterializedStore(PRODUCT_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductDto.class)));
    }

    private KTable<Long, BrandDto> createBrandTable(StreamsBuilder streamsBuilder) {
        return streamsBuilder.stream(
                        kafkaTopicConfig.brand(),
                        Consumed.with(getSerde(KeyDto.class), getMessageDtoSerde(BrandDto.class)))
                .selectKey((key, value) -> key.getId())
                .mapValues(this::extractModelFromMessage)
                .toTable(createMaterializedStore(BRAND_MATERIALIZED_VIEW, Serdes.Long(), getSerde(BrandDto.class)));
    }

    private KTable<Long, ProductResultDto> joinProductAndBrand(
            KTable<Long, ProductDto> productTable, KTable<Long, BrandDto> brandTable) {
        return productTable.leftJoin(
                brandTable,
                ProductDto::getBrandId,
                this::enrichWithProductAndBrandData,
                createMaterializedStore(
                        PRODUCT_BRAND_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductResultDto.class)));
    }

    private KTable<Long, CategoryDto> createCategoryTable(StreamsBuilder streamsBuilder) {
        return streamsBuilder.stream(
                        kafkaTopicConfig.category(),
                        Consumed.with(getSerde(KeyDto.class), getMessageDtoSerde(CategoryDto.class)))
                .selectKey((key, value) -> key.getId())
                .mapValues(this::extractModelFromMessage)
                .toTable(
                        createMaterializedStore(
                                CATEGORY_MATERIALIZED_VIEW, Serdes.Long(), getSerde(CategoryDto.class)));
    }

    private KTable<Long, ProductCategoryDto> createProductCategoryTable(StreamsBuilder streamsBuilder) {
        return streamsBuilder.stream(
                        kafkaTopicConfig.productCategory(),
                        Consumed.with(getSerde(KeyDto.class), getMessageDtoSerde(ProductCategoryDto.class)))
                .selectKey((key, value) -> key.getId())
                .mapValues(this::extractModelFromMessage)
                .toTable(
                        createMaterializedStore(
                                PRODUCT_CATEGORY_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductCategoryDto.class)));
    }

    private KTable<Long, ProductCategoryDto> filterNonNullProductCategory(
            KTable<Long, ProductCategoryDto> productCategoryTable) {
        return productCategoryTable.toStream()
                .filter((key, value) -> value != null)
                .toTable(
                        createMaterializedStore(
                                PRODUCT_CATEGORY_NON_NULL_MATERIALIZED_VIEW,
                                Serdes.Long(),
                                getSerde(ProductCategoryDto.class)));
    }

    private KTable<Long, ProductCategoryDto> enrichProductCategoryWithDetails(
            KTable<Long, ProductCategoryDto> productCategoryTable, KTable<Long, CategoryDto> categoryTable) {
        return productCategoryTable.leftJoin(
                categoryTable,
                ProductCategoryDto::getCategoryId,
                (productCategory, category) -> {
                    productCategory.setCategoryName(category.getName());
                    return productCategory;
                },
                createMaterializedStore(
                        PRODUCT_CATEGORY_WITH_NAME_MATERIALIZED_VIEW,
                        Serdes.Long(),
                        getSerde(ProductCategoryDto.class)));
    }

    private KTable<Long, AggregationDto<CategoryDto>> aggregateProductCategoryDetails(
            KTable<Long, ProductCategoryDto> productCategoryDetailTable,
            KTable<Long, ProductCategoryDto> nonNullProductCategoryTable) {
        return productCategoryDetailTable.toStream()
                .mapValues(v -> v != null ? v : new ProductCategoryDto(-1L))
                .join(
                        nonNullProductCategoryTable,
                        (productCategoryDetail, nonNullProductCategoryDetail) -> {
                            if (productCategoryDetail.getId().equals(-1L)) {
                                nonNullProductCategoryDetail.setDeleted(true);
                                return nonNullProductCategoryDetail;
                            }
                            return productCategoryDetail;
                        })
                .selectKey((key, value) -> value.getProductId())
                .groupByKey()
                .aggregate(
                        AggregationDto::new,
                        (productId, productCategoryDetail, agg) -> {
                            agg.setJoinId(productId);
                            agg.getAggregationContents()
                                    .removeIf(category ->
                                            productCategoryDetail.getCategoryId().equals(category.getId()));

                            if (!productCategoryDetail.isDeleted()) {
                                agg.add(new CategoryDto(
                                        productCategoryDetail.getOp(),
                                        productCategoryDetail.getTs(),
                                        productCategoryDetail.getCategoryId(),
                                        productCategoryDetail.getCategoryName()));
                            }
                            return agg;
                        },
                        createMaterializedStore(
                                PRODUCT_CATEGORY_AGGREGATION_MATERIALIZED_VIEW,
                                Serdes.Long(),
                                getAggregationDtoSerde(CategoryDto.class)));
    }

    private KTable<Long, ProductResultDto> joinProductWithCategoryDetails(
            KTable<Long, ProductResultDto> productBrandTable,
            KTable<Long, AggregationDto<CategoryDto>> productCategoryAgg) {
        return productBrandTable.leftJoin(
                productCategoryAgg,
                (productResult, agg) -> {
                    if (agg != null) {
                        productResult.setCategories(agg.getAggregationContents());
                    }
                    return productResult;
                },
                createMaterializedStore(
                        PRODUCT_BRAND_CATEGORY_DETAIL_MATERIALIZED_VIEW,
                        Serdes.Long(),
                        getSerde(ProductResultDto.class)));
    }

    private KTable<Long, ProductAttributeDto> createProductAttributeTable(StreamsBuilder streamsBuilder) {
        return streamsBuilder.stream(
                        kafkaTopicConfig.productAttribute(),
                        Consumed.with(getSerde(KeyDto.class), getMessageDtoSerde(ProductAttributeDto.class)))
                .selectKey((key, value) -> key.getId())
                .mapValues(this::extractModelFromMessage)
                .toTable(createMaterializedStore(
                        PRODUCT_ATTRIBUTE_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductAttributeDto.class)));
    }

    private KTable<Long, ProductAttributeValueDto> createProductAttributeValueTable(StreamsBuilder streamsBuilder) {
        return streamsBuilder.stream(
                        kafkaTopicConfig.productAttributeValue(),
                        Consumed.with(getSerde(KeyDto.class), getMessageDtoSerde(ProductAttributeValueDto.class)))
                .selectKey((key, value) -> key.getId())
                .mapValues(this::extractModelFromMessage)
                .toTable(createMaterializedStore(
                        PRODUCT_ATTRIBUTE_VALUE_MATERIALIZED_VIEW,
                        Serdes.Long(),
                        getSerde(ProductAttributeValueDto.class)));
    }

    private KTable<Long, ProductAttributeValueDto> filterNonNullProductAttributeValue(
            KTable<Long, ProductAttributeValueDto> productAttributeValueTable) {
        return productAttributeValueTable.toStream()
                .filter((key, value) -> value != null)
                .toTable(createMaterializedStore(
                        PRODUCT_ATTRIBUTE_VALUE_NONNULL_MATERIALIZED_VIEW,
                        Serdes.Long(),
                        getSerde(ProductAttributeValueDto.class)));
    }

    private KTable<Long, ProductAttributeValueDto> enrichProductAttributeValueDetails(
            KTable<Long, ProductAttributeValueDto> productAttributeValueTable,
            KTable<Long, ProductAttributeDto> productAttributeTable) {
        return productAttributeValueTable.leftJoin(
                productAttributeTable,
                ProductAttributeValueDto::getProductAttributeId,
                (productAttributeValue, productAttribute) -> {
                    productAttributeValue.setProductAttributeName(productAttribute.getName());
                    return productAttributeValue;
                },
                createMaterializedStore(
                        PRODUCT_ATTRIBUTE_VALUE_WITH_NAME_MATERIALIZED_VIEW,
                        Serdes.Long(),
                        getSerde(ProductAttributeValueDto.class)));
    }

    private KTable<Long, AggregationDto<ProductAttributeDto>> aggregateProductAttributeDetails(
            KTable<Long, ProductAttributeValueDto> productAttributeValueDetailTable,
            KTable<Long, ProductAttributeValueDto> nonNullProductAttributeValueTable) {
        return productAttributeValueDetailTable.toStream()
                .mapValues(v -> v != null ? v : new ProductAttributeValueDto(-1L))
                .join(
                        nonNullProductAttributeValueTable,
                        (productAttributeValueDetail, nonNullProductAttributeValueDetail) -> {
                            if (productAttributeValueDetail.getId().equals(-1L)) {
                                nonNullProductAttributeValueDetail.setDeleted(true);
                                return nonNullProductAttributeValueDetail;
                            }
                            return productAttributeValueDetail;
                        })
                .selectKey((key, value) -> value.getProductId())
                .groupByKey()
                .aggregate(
                        AggregationDto::new,
                        (productId, productAttributeValueDetail, agg) -> {
                            agg.setJoinId(productId);
                            agg.getAggregationContents()
                                    .removeIf(attribute -> productAttributeValueDetail.getProductAttributeId()
                                            .equals(attribute.getId()));

                            if (!productAttributeValueDetail.isDeleted()) {
                                agg.add(new ProductAttributeDto(
                                        productAttributeValueDetail.getOp(),
                                        productAttributeValueDetail.getTs(),
                                        productAttributeValueDetail.getProductAttributeId(),
                                        productAttributeValueDetail.getProductAttributeName(),
                                        productAttributeValueDetail.getValue()));
                            }
                            return agg;
                        },
                        createMaterializedStore(
                                PRODUCT_ATTRIBUTE_AGGREGATION_MATERIALIZED_VIEW,
                                Serdes.Long(),
                                getAggregationDtoSerde(ProductAttributeDto.class)));
    }

    private KTable<Long, ProductResultDto> joinProductWithAttributeDetails(
            KTable<Long, ProductResultDto> addingCategoryDetailTable,
            KTable<Long, AggregationDto<ProductAttributeDto>> productAttributeAgg) {
        return addingCategoryDetailTable.leftJoin(
                productAttributeAgg,
                ProductResultDto::getId,
                (productResult, agg) -> {
                    if (agg != null) {
                        productResult.setProductAttributes(agg.getAggregationContents());
                    }
                    return productResult;
                },
                createMaterializedStore(
                        PRODUCT_WITH_ALL_DETAIL_MATERIALIZED_VIEW,
                        Serdes.Long(),
                        getSerde(ProductResultDto.class)));
    }

    private void writeToSink(KTable<Long, ProductResultDto> resultTable) {
        resultTable
                .toStream()
                .to(kafkaTopicConfig.productSink(), Produced.with(Serdes.Long(), getSerde(ProductResultDto.class)));
    }

    private ProductResultDto enrichWithProductAndBrandData(ProductDto productRecord, BrandDto brandRecord) {
        ProductResultDto productResultDto = new ProductResultDto();
        productResultDto.setBrand(brandRecord);
        productResultDto.setId(productRecord.getId());
        productResultDto.setName(productRecord.getName());
        productResultDto.setMetaDescription(productRecord.getMetaDescription());
        productResultDto.setShortDescription(productRecord.getShortDescription());
        productResultDto.setPrice(productRecord.getPrice());
        productResultDto.setMetaTitle(productRecord.getMetaTitle());
        productResultDto.setSpecification(productRecord.getSpecification());
        productResultDto.setBrandId(productRecord.getBrandId());
        productResultDto.setOp(productRecord.getOp());
        productResultDto.setTs(productRecord.getTs());
        productResultDto.setPublished(productRecord.getPublished());
        return productResultDto;
    }

}

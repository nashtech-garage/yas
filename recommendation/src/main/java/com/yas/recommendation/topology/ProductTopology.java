package com.yas.recommendation.topology;

import com.yas.recommendation.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductTopology extends AbstractTopology {

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


    public ProductTopology(
            @Value("${product.topic.name}") String sourceTopic,
            @Value("${product.sink.topic.name}") String sinkTopic) {
        super(sourceTopic, sinkTopic);
    }

    @Autowired
    @Override
    protected void proccess(StreamsBuilder streamsBuilder) {

        KTable<Long, ProductDTO> productTable = streamsBuilder.stream(
                        sourceTopic,
                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(ProductDTO.class)))
                .filter((key, value) -> isAfterObjectNonNull(value))
                .selectKey((key, value) -> {
                    log.info("productTable: id={}", key.getId());
                    return value.getAfter().getId();
                })
                .mapValues(MessageDTO::getAfter)
                .toTable(createMaterializedStore(PRODUCT_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductDTO.class)));

        KTable<Long, BrandDTO> brandTable = streamsBuilder.stream(
                        "dbproduct.public.brand",
                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(BrandDTO.class))
                )
                .filter((key, value) -> isAfterObjectNonNull(value))
                .selectKey((key, value) -> {
                    log.info("brandTable: id={}", key.getId());
                    return value.getAfter().getId();
                })
                .mapValues(MessageDTO::getAfter)
                .toTable(createMaterializedStore(BRAND_MATERIALIZED_VIEW, Serdes.Long(), getSerde(BrandDTO.class)));

        KTable<Long, ProductResultDTO> productBrandTable = productTable.leftJoin(
                brandTable,
                ProductDTO::getBrandId,
                this::enrichWithProductAndBrandData, // there is a product with id:18, wtf?
                createMaterializedStore(PRODUCT_BRAND_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductResultDTO.class))
        );

//        KTable<Long, CategoryDTO> categoryTable = streamsBuilder.stream(
//                        "dbproduct.public.category",
//                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(CategoryDTO.class))
//                )
//                .filter((key, value) -> isAfterObjectNonNull(value))
//                .selectKey((key, value) -> value.getAfter().getId())
//                .mapValues(MessageDTO::getAfter)
//                .toTable(createMaterializedStore(CATEGORY_MATERIALIZED_VIEW, Serdes.Long(), getSerde(CategoryDTO.class)));
//
//        KTable<Long, ProductCategoryDTO> productCategoryTable = streamsBuilder.stream(
//                        "dbproduct.public.product_category",
//                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(ProductCategoryDTO.class))
//                )
//                .filter((key, value) -> isAfterObjectNonNull(value))
//                .selectKey((key, value) -> value.getAfter().getId())
//                .mapValues(MessageDTO::getAfter)
//                .toTable(createMaterializedStore(PRODUCT_CATEGORY_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductCategoryDTO.class)));
//
//
//        KTable<Long, ProductCategoryDTO> productCategoryDetailTable = productCategoryTable
//                .leftJoin(
//                        categoryTable,
//                        ProductCategoryDTO::getCategoryId,
//                        (productCategory, category) -> {
//                            productCategory.setCategoryName(category.getName());
//                            return productCategory;
//                        },
//                        createMaterializedStore(PRODUCT_CATEGORY_WITH_NAME_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductCategoryDTO.class))
//                );
//
//        KTable<Long, AggregationDTO<Long, CategoryDTO>> productCategoryAgg = productCategoryDetailTable
//                .toStream()
//                .selectKey((key, value) -> value.getProductId())
//                .groupByKey() // Still have duplicate records, although already group by
//                .aggregate(
//                        AggregationDTO::new,
//                        (productId, productCategoryDetail, agg) -> {
//                            agg.setJoinId(productId);
//                            agg.getAggregationContents().removeIf(category -> productCategoryDetail.getCategoryId().equals(category.getId()));
//                            agg.add(new CategoryDTO(productCategoryDetail.getCategoryId(), productCategoryDetail.getCategoryName()));
//                            return agg;
//                        },
//                        createMaterializedStore(PRODUCT_CATEGORY_AGGREGATION_MATERIALIZED_VIEW, Serdes.Long(), getAggregationDTOSerde(Long.class, CategoryDTO.class))
//                );
//
//        // productBrandTable vs productCategoryAgg are wrong now.
//        KTable<Long, ProductResultDTO> addingCategoryDetailTable = productBrandTable
//                .leftJoin(
//                        productCategoryAgg,
//                        (productResult, agg) -> {
//                            if (agg != null) {
//                                productResult.setCategories(agg.getAggregationContents());
//                            }
//                            return productResult;
//                        },
//                        createMaterializedStore(PRODUCT_BRAND_CATEGORY_DETAIL_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductResultDTO.class))
//                );
//
//        KTable<Long, ProductAttributeDTO> productAttributeTable = streamsBuilder.stream(
//                        "dbproduct.public.product_attribute",
//                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(ProductAttributeDTO.class))
//                )
//                .filter((key, value) -> isAfterObjectNonNull(value))
//                .selectKey((key, value) -> value.getAfter().getId())
//                .mapValues(MessageDTO::getAfter)
//                .toTable(createMaterializedStore(PRODUCT_ATTRIBUTE_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductAttributeDTO.class)));
//
//        KTable<Long, ProductAttributeValueDTO> productAttributeValueTable = streamsBuilder.stream(
//                        "dbproduct.public.product_attribute_value",
//                        Consumed.with(getSerde(KeyDTO.class), getMessageDTOSerde(ProductAttributeValueDTO.class))
//                )
//                .filter((key, value) -> isAfterObjectNonNull(value))
//                .selectKey((key, value) -> value.getAfter().getId())
//                .mapValues(MessageDTO::getAfter)
//                .toTable(createMaterializedStore(PRODUCT_ATTRIBUTE_VALUE_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductAttributeValueDTO.class)));
//
//
//        KTable<Long, ProductAttributeValueDTO> productAttributeValueDetailTable = productAttributeValueTable
//                .leftJoin(
//                        productAttributeTable,
//                        ProductAttributeValueDTO::getProductAttributeId,
//                        (productAttributeValue, productAttribute) -> {
//                            productAttributeValue.setProductAttributeName(productAttribute.getName());
//                            return productAttributeValue;
//                        },
//                        createMaterializedStore(PRODUCT_ATTRIBUTE_VALUE_WITH_NAME_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductAttributeValueDTO.class))
//                );
//
//
//        KTable<Long, AggregationDTO<Long, ProductAttributeDTO>> productAttributeAgg = productAttributeValueDetailTable
//                .toStream()
//                .selectKey((key, value) -> value.getProductId())
//                .groupByKey()
//                .aggregate(
//                        AggregationDTO::new,
//                        (productId, productAttributeValueDetail, agg) -> {
//                            agg.setJoinId(productId);
//                            agg.getAggregationContents().removeIf(attribute -> productAttributeValueDetail.getProductAttributeId().equals(attribute.getId()));
//                            agg.add(new ProductAttributeDTO(productAttributeValueDetail.getProductAttributeId(), productAttributeValueDetail.getProductAttributeName(), productAttributeValueDetail.getValue()));
//                            return agg;
//                        },
//                        createMaterializedStore(PRODUCT_ATTRIBUTE_AGGREGATION_MATERIALIZED_VIEW, Serdes.Long(), getAggregationDTOSerde(Long.class, ProductAttributeDTO.class))
//                );
//
//        KTable<Long, ProductResultDTO> addingAttributeDetailTable = addingCategoryDetailTable
//                .leftJoin(
//                        productAttributeAgg,
//                        ProductResultDTO::getId,
//                        (productResult, agg) -> {
//                            if (agg != null) {
//                                productResult.setProductAttributes(agg.getAggregationContents());
//                            }
//                            return productResult;
//                        },
//                        createMaterializedStore(PRODUCT_BRAND_CATEGORY_ATTRIBUTE_DETAIL_MATERIALIZED_VIEW, Serdes.Long(), getSerde(ProductResultDTO.class))
//                );
//
//
//        addingAttributeDetailTable.toStream()
//                .map((key, value) -> new KeyValue<>(value.getId(), value))
//                .to(sinkTopic, Produced.with(Serdes.Long(), getSerde(ProductResultDTO.class)));
    }

    private ProductResultDTO enrichWithProductAndBrandData(ProductDTO productRecord, BrandDTO brandRecord) {
        log.info("Join product {} with brand id {} - with brand {}", productRecord.getId(), productRecord.getBrandId(), brandRecord.getId());
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


}

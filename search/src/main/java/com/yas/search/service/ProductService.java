package com.yas.search.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import com.yas.search.constant.document_fields.ProductField;
import com.yas.search.constant.enums.ESortType;
import com.yas.search.document.Product;
import com.yas.search.viewmodel.ProductGetVm;
import com.yas.search.viewmodel.ProductListGetVm;
import com.yas.search.viewmodel.ProductNameGetVm;
import com.yas.search.viewmodel.ProductNameListVm;
import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    private final ElasticsearchOperations elasticsearchOperations;

    public ProductService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public ProductListGetVm findProductAdvance(String keyword,
                                               Integer page,
                                               Integer size,
                                               String brand,
                                               String category,
                                               String attribute,
                                               Double minPrice,
                                               Double maxPrice,
                                               ESortType sortType) {
        NativeQueryBuilder nativeQuery = NativeQuery.builder()
                .withAggregation("categories", Aggregation.of(a -> a
                        .terms(ta -> ta.field(ProductField.CATEGORIES))))
                .withAggregation("attributes", Aggregation.of(a -> a
                        .terms(ta -> ta.field(ProductField.ATTRIBUTES))))
                .withAggregation("brands", Aggregation.of(a -> a
                        .terms(ta -> ta.field(ProductField.BRAND))))
                .withQuery(q -> q
                        .bool(b -> b
                                .should(s -> s
                                        .match(m -> m
                                                .field(ProductField.NAME)
                                                .query(keyword)
                                                .fuzziness(Fuzziness.ONE.asString())
                                        )
                                )
                                .should(s -> s
                                        .match(m -> m
                                                .field(ProductField.BRAND)
                                                .query(keyword)
                                                .fuzziness(Fuzziness.ONE.asString())
                                        )
                                )
                        )
                )
                .withPageable(PageRequest.of(page, size));


        nativeQuery.withFilter(f -> f
                .bool(b -> {
                            if (brand != null && !brand.isBlank()) {
                                String[] brands = brand.split(",");
                                for (String brd : brands) {
                                    b.should(s -> s
                                            .term(t -> t
                                                    .field(ProductField.BRAND)
                                                    .value(brd)
                                                    .caseInsensitive(true)
                                            )
                                    );
                                }
                            }
                            if (category != null && !category.isBlank()) {
                                String[] categories = category.split(",");
                                for (String cat : categories) {
                                    b.should(s -> s
                                            .term(t -> t
                                                    .field(ProductField.CATEGORIES)
                                                    .value(cat)
                                                    .caseInsensitive(true)
                                            )
                                    );
                                }
                            }
                            if (minPrice != null && maxPrice == null) {
                                b.must(m -> m.
                                        range(r -> r
                                                .field(ProductField.PRICE)
                                                .from(minPrice.toString())
                                        ));
                            } else if (minPrice == null && maxPrice != null) {
                                b.must(m -> m.
                                        range(r -> r
                                                .field(ProductField.PRICE)
                                                .to(maxPrice.toString())
                                        ));
                            } else if (minPrice != null) {
                                b.must(m -> m.
                                        range(r -> r
                                                .field(ProductField.PRICE)
                                                .from(minPrice.toString())
                                                .to(maxPrice.toString())
                                        ));
                            }
                            if (attribute != null && !attribute.isBlank()) {
                                String[] attributes = attribute.split(",");
                                for (String att : attributes) {
                                    b.should(s -> s
                                            .term(t -> t
                                                    .field(ProductField.ATTRIBUTES)
                                                    .value(att)
                                                    .caseInsensitive(true)
                                            )
                                    );
                                }
                            }
                            return b;
                        }
                )
        );

        if (sortType == ESortType.PRICE_ASC) {
            nativeQuery.withSort(Sort.by(Sort.Direction.ASC, ProductField.PRICE));
        } else if (sortType == ESortType.PRICE_DESC) {
            nativeQuery.withSort(Sort.by(Sort.Direction.DESC, ProductField.PRICE));
        } else {
            nativeQuery.withSort(Sort.by(Sort.Direction.DESC, ProductField.CREATE_ON));
        }

        SearchHits<Product> searchHitsResult = elasticsearchOperations.search(nativeQuery.build(), Product.class);
        SearchPage<Product> productPage = SearchHitSupport.searchPageFor(searchHitsResult, nativeQuery.getPageable());

        List<ProductGetVm> productListVmList = searchHitsResult.stream()
                .map(i -> ProductGetVm.fromModel(i.getContent())).toList();

        return new ProductListGetVm(
                productListVmList,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast(),
                getAggregations(searchHitsResult));
    }

    private Map<String, Map<String, Long>> getAggregations(SearchHits<Product> searchHits) {
        List<org.springframework.data.elasticsearch.client.elc.Aggregation> aggregations = new ArrayList<>();
        if (searchHits.hasAggregations()) {
            ((List<ElasticsearchAggregation>) searchHits.getAggregations().aggregations()) //NOSONAR
                    .forEach(elsAgg -> aggregations.add(elsAgg.aggregation()));
        }

        Map<String, Map<String, Long>> aggregationsMap = new HashMap<>();
        aggregations.forEach(agg -> {
            Map<String, Long> aggregation = new HashMap<>();
            StringTermsAggregate stringTermsAggregate = (StringTermsAggregate) agg.getAggregate()._get();
            List<StringTermsBucket> stringTermsBuckets = (List<StringTermsBucket>) stringTermsAggregate.buckets()._get();
            stringTermsBuckets.forEach(bucket -> aggregation.put(bucket.key()._get().toString(), bucket.docCount()));
            aggregationsMap.put(agg.getName(), aggregation);
        });

        return aggregationsMap;
    }

    public ProductNameListVm autoCompleteProductName(final String keyword) {
        NativeQuery matchQuery = NativeQuery.builder()
                .withQuery(
                        q -> q.matchPhrasePrefix(
                                mPP -> mPP.field("name").query(keyword)
                        )
                )
                .withSourceFilter(new FetchSourceFilter(
                        new String[]{"name"},
                        null)
                )
                .build();
        SearchHits<Product> result = elasticsearchOperations.search(matchQuery, Product.class);
        List<Product> products = result.stream().map(SearchHit::getContent).toList();

        return new ProductNameListVm(products.stream().map(ProductNameGetVm::fromModel).toList());
    }
}

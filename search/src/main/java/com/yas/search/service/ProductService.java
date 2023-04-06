package com.yas.search.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import com.yas.search.constant.enums.ESortType;
import com.yas.search.document.Product;
import com.yas.search.viewmodel.ProductListGetVm;
import com.yas.search.viewmodel.ProductListVm;
import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ElasticsearchOperations elasticsearchOperations;

    public ProductService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public ProductListGetVm findProductAdvance(String keyword,
                                               Integer page,
                                               Integer size,
                                               String category,
                                               String attribute,
                                               Double minPrice,
                                               Double maxPrice,
                                               ESortType sortType) {
        NativeQueryBuilder nativeQuery = NativeQuery.builder()
                .withAggregation("categories", Aggregation.of(a -> a
                        .terms(ta -> ta.field("categories"))))
                .withQuery(q -> q
                        .bool(b -> b
                                .should(s -> s
                                        .fuzzy(m -> m
                                                .field("name")
                                                .value(keyword)
                                                .fuzziness(Fuzziness.ONE.asString())
                                        )
                                )
                                .should(s -> s
                                        .fuzzy(m -> m
                                                .field("brand")
                                                .value(keyword)
                                                .fuzziness(Fuzziness.ONE.asString())
                                        )
                                )
                        )
                )
                .withPageable(PageRequest.of(page, size));


        nativeQuery.withFilter(f -> f
                .bool(b -> {
                            if (category != null && !category.isBlank()) {
                                b.must(m -> m.
                                        term(t -> t
                                                .field("categories")
                                                .value(category)
                                                .caseInsensitive(true)
                                        ));
                            }
                            if (minPrice != null && maxPrice != null) {
                                b.must(m -> m.
                                        range(r -> r
                                                .field("price")
                                                .from(minPrice.toString())
                                                .to(maxPrice.toString())
                                        ));
                            }
                            if (attribute != null && !attribute.isBlank()) {
                                b.must(m -> m
                                        .term(t -> t
                                                .field("attributes")
                                                .value(attribute)
                                                .caseInsensitive(true)
                                        )
                                );
                            }
                            return b;
                        }
                )
        );

        if (sortType == ESortType.PRICE_ASC) {
            nativeQuery.withSort(Sort.by(Sort.Direction.ASC, "price"));
        } else if (sortType == ESortType.PRICE_DESC) {
            nativeQuery.withSort(Sort.by(Sort.Direction.DESC, "price"));
        }

        SearchHits<Product> searchHits = elasticsearchOperations.search(nativeQuery.build(), Product.class);
        SearchPage<Product> productPage = SearchHitSupport.searchPageFor(searchHits, nativeQuery.getPageable());

        List<Aggregate> aggregations = new ArrayList<>();
        if (searchHits.hasAggregations()) {
            List.of(searchHits.getAggregations().aggregations())
                    .forEach(i ->
                            aggregations.add(((org.springframework.data.elasticsearch.client.elc.Aggregation) i).getAggregate())
                    );
        }

        List<ProductListVm> productListVmList = searchHits.stream()
                .map(i -> ProductListVm.fromModel(i.getContent())).toList();
        return new ProductListGetVm(
                productListVmList,
                productPage.getNumber(),
                productPage.getSize(),
                (int) productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast());
    }
}

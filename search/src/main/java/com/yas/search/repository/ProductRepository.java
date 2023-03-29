package com.yas.search.repository;

import com.yas.search.document.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductRepository extends ElasticsearchRepository<Product, Long> {

    @Query("""
            {
                "bool": {
                    "must": [
                        {
                            "nested": {
                                "path": "productCategories",
                                "query": {
                                    "query_string": {
                                        "default_operator": "and",
                                        "fields": [
                                            "productCategories.category.slug"
                                        ],
                                        "query": "?0"
                                    }
                                },
                                "score_mode": "avg"
                            }
                        },
                        {
                            "query_string": {
                                "default_operator": "and",
                                "fields": [
                                    "isVisibleIndividually"
                                ],
                                "query": "true"
                            }
                        },
                        {
                            "query_string": {
                                "default_operator": "and",
                                "fields": [
                                    "isActive"
                                ],
                                "query": "true"
                            }
                        }
                    ]
                }
            }
            """)
    List<Product> findByProductCategoriesCategoryNameAndIsVisibleIndividuallyTrueAndIsActiveTrue(String name);

    @Query(query = """
            {
                "bool": {
                    "should": [
                        {
                            "query_string": {
                                "default_operator": "or",
                                "fields": [
                                    "brand.name",
                                    "name",
                                    "productCategories.name"
                                ],
                                "query": "?0"
                            }
                        },
                        {
                            "nested": {
                                "path": "attributeValues",
                                "query": {
                                    "bool": {
                                        "must": [
                                            {
                                                "query_string": {
                                                    "default_operator": "or",
                                                    "fields": [
                                                        "attributeValues.value",
                                                        "attributeValues.productAttribute.name"
                                                    ],
                                                    "query": "?0"
                                                }
                                            }
                                        ]
                                    }
                                }
                            }
                        }
                    ],
                    "must": [
                        {
                            "match": {
                                "isActive": {
                                    "query": True
                                }
                            }
                        },
                        {
                            "match": {
                                "isVisibleIndividually": True
                            }
                        }
                    ]
                }
            }
            """)
    Page<Product> findByAttributeOrBrandOrNameOrCategory(String keyword, Pageable pageable);
}

package com.yas.search.repository;

import com.yas.search.document.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepository extends ElasticsearchRepository<Product, Long> {

    @Query(query = """
            {
                "bool": {
                    "must": [
                        {
                            "bool": {
                                "should": [
                                    {
                                        "query_string": {
                                            "default_operator": "or",
                                            "fields": [
                                                "brand",
                                                "name",
                                                "categories"
                                            ],
                                            "query": "?0"
                                        }
                                    },
                                    {
                                        "nested": {
                                            "path": "attributes",
                                            "query": {
                                                "bool": {
                                                    "must": [
                                                        {
                                                            "query_string": {
                                                                "default_operator": "or",
                                                                "fields": [
                                                                    "attributes.value",
                                                                    "attributes.name"
                                                                ],
                                                                "query": "?0"
                                                            }
                                                        }
                                                    ]
                                                }
                                            }
                                        }
                                    }
                                ]
                            }
                        },
                        {
                            "bool": {
                                "must": [
                                    {
                                        "match": {
                                            "isActive": {
                                                "query": true
                                            }
                                        }
                                    },
                                    {
                                        "match": {
                                            "isVisibleIndividually": true
                                        }
                                    }
                                ]
                            }
                        }
                    ]
                }
            }
            """)
    Page<Product> findByName(String name, Pageable pageable);

    @Query(query = """
            {
                "bool": {
                    "must": [
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
                                ]
                            }
                        },
                        {
                            "bool": {
                                "must": [
                                    {
                                        "match": {
                                            "isActive": {
                                                "query": true
                                            }
                                        }
                                    },
                                    {
                                        "match": {
                                            "isVisibleIndividually": true
                                        }
                                    }
                                ]
                            }
                        }
                    ]
                }
            }
            """)
    Page<Product> findByAttributeOrBrandOrNameOrCategory(String keyword, Pageable pageable);
}

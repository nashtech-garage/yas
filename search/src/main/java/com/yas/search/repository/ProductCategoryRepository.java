package com.yas.search.repository;

import com.yas.search.document.ProductCategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductCategoryRepository extends ElasticsearchRepository<ProductCategory, Long> {
}

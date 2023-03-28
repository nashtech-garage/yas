package com.yas.search.repository;

import com.yas.search.document.ProductAttribute;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductAttributeRepository extends ElasticsearchRepository<ProductAttribute, Long> {
}

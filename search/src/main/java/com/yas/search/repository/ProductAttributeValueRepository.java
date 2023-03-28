package com.yas.search.repository;

import com.yas.search.document.ProductAttributeValue;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductAttributeValueRepository extends ElasticsearchRepository<ProductAttributeValue, Long> {
}

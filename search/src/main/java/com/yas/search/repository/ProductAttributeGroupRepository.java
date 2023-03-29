package com.yas.search.repository;

import com.yas.search.document.ProductAttributeGroup;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductAttributeGroupRepository extends ElasticsearchRepository<ProductAttributeGroup, Long> {
}
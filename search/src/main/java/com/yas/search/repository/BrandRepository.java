package com.yas.search.repository;

import com.yas.search.document.Brand;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BrandRepository extends ElasticsearchRepository<Brand, Long> {
}
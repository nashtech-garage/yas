package com.yas.search.repository;

import com.yas.search.document.Category;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CategoryRepository extends ElasticsearchRepository<Category, Long> {
}

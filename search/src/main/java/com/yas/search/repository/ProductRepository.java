package com.yas.search.repository;

import com.yas.search.document.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductRepository extends ElasticsearchRepository<Product, Long> {

    List<Product> findAllProductByName(String productName);


}

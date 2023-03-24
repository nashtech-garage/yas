package com.yas.search.repository;

import com.yas.search.document.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

//@Repository
public interface ProductRepository extends ElasticsearchRepository<Product, Long> {

    List<Product> findProductByName(String productName);


}

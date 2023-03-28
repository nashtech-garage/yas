package com.yas.search.repository;

import com.yas.search.document.Product;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductRepository extends ElasticsearchRepository<Product, Long> {

    @Query("{" +
            "\"query\": {" +
            "\"nested\": {" +
            "\"path\": \"product\"," +
            "\"query\": {" +
            "\"bool\": {" +
            "\"must\": [" +
            "{ \"match\": { \"product.name\": ?1 } }" +
            "]" +
            "}" +
            "}" +
            "}" +
            "}" +
            "}")
    List<Product> findAllProductByName(String productName);

    List<Product> findByName(String name);

    List<Product> findByAttributeValuesValueOrBrandName(String name, String name1);

}

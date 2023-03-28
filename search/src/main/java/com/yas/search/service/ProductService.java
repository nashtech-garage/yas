package com.yas.search.service;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.yas.search.document.Product;
import com.yas.search.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findProduct(String name) {
        return productRepository.findAllProductByName(name);
    }

    public List<Product> findProductAdvance(String keyword) {
        return productRepository.findAllProductByName(keyword);
    }
}

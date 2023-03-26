package com.yas.search.service;

import com.yas.search.document.Product;
import com.yas.search.repository.ProductRepository;
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
}

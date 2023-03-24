package com.yas.search.config;

import com.yas.search.document.Product;
import com.yas.search.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Abc {
    private final ProductRepository productRepository;

    public Abc(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Bean
    public void alo() {
        Product product = Product.builder().id(1L).name("hú hú").build();
        productRepository.save(product);
    }
}

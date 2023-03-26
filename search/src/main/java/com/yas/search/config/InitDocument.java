package com.yas.search.config;

import com.yas.search.document.Product;
import com.yas.search.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitDocument {
    private final ProductRepository productRepository;

    public InitDocument(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Bean
    public void init() {
        Product product = Product.builder().id(1L).name("hú hú").build();
        productRepository.save(product);
    }
}

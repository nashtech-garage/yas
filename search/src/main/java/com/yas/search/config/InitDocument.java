package com.yas.search.config;

import com.yas.search.document.Brand;
import com.yas.search.document.Product;
import com.yas.search.repository.BrandRepository;
import com.yas.search.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class InitDocument {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    public InitDocument(ProductRepository productRepository,
                        BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
    }

    @Bean
    public void init() {
        Product product = Product.builder().id(2L).name("hú hú").build();
        Brand brand = Brand.builder().id(1L).name("hu hu").build();
        productRepository.save(product);
        brandRepository.save(brand);
    }
}

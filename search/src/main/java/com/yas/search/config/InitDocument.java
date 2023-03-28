package com.yas.search.config;

import com.yas.search.document.Brand;
import com.yas.search.document.Product;
import com.yas.search.repository.BrandRepository;
import com.yas.search.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
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
        Brand brand1 = Brand.builder().id(1L).name("Brand A").build();
        Brand brand2 = Brand.builder().id(2L).name("Brand A").build();
        brandRepository.saveAll(Arrays.asList(brand1, brand2));

        Product product = Product.builder().id(1L).name("Product test")
                .slug("slug")
                .price(88.88)
                .isPublished(true)
                .isActive(false)
                .thumbnailMediaId(1L)
                .brand(brand1).build();
        productRepository.save(product);
    }
}

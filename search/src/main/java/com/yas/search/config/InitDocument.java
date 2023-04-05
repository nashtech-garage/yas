package com.yas.search.config;

import com.yas.search.document.Product;
import com.yas.search.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class InitDocument {
    private final ProductRepository productRepository;

    public InitDocument(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Bean
    public void init() {
        Product product1 = Product.builder().id(1L)
                .isAllowedToOrder(true)
                .name("Dell XPS 15 9550")
                .slug("dell-xps-15-9550")
                .price(100000.0)
                .brand("Apple")
                .isPublished(true)
                .isActive(true)
                .isVisibleIndividually(true)
                .thumbnailMediaId(1L)
                .categories(List.of("Laptop", "Computer"))
                .build();
        Product product2 = Product.builder().id(2L)
                .name("iPad Pro Wi-Fi 4G 128GB")
                .slug("ipad-pro-wi-fi-4g-128gb-gold")
                .price(100000.0)
                .isPublished(true)
                .isActive(true)
                .isVisibleIndividually(true)
                .thumbnailMediaId(3L)
                .categories(List.of("Phone", "Tablet"))
                .brand("Samsung").build();
        Product product3 = Product.builder().id(3L)
                .name("iPad Pro Wi-Fi 4G 256gb")
                .slug("ipad-pro-wi-fi-4g-256gb-black")
                .price(100000.0)
                .isPublished(true)
                .isActive(true)
                .isVisibleIndividually(true)
                .thumbnailMediaId(3L)
                .categories(List.of("Phone", "Tablet"))
                .brand("Samsung").build();
        productRepository.saveAll(List.of(product1, product2, product3));
    }
}

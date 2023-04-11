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
                .price(16500000.0)
                .brand("Dell")
                .isPublished(true)
                .isVisibleIndividually(true)
                .thumbnailMediaId(8L)
                .categories(List.of("Laptop", "Computer"))
                .build();
        Product product2 = Product.builder().id(2L)
                .name("iPad Pro Wi-Fi 4G")
                .slug("ipad-pro-wi-fi-4g")
                .price(30990000.0)
                .isPublished(true)
                .isVisibleIndividually(true)
                .thumbnailMediaId(11L)
                .categories(List.of("Phone", "Tablet"))
                .brand("Apple").build();
        Product product3 = Product.builder().id(3L)
                .name("iPad Pro Wi-Fi 4G 128GB Gold")
                .slug("ipad-pro-wi-fi-4g-128gb-gold")
                .price(22000000.0)
                .isPublished(true)
                .isVisibleIndividually(true)
                .thumbnailMediaId(11L)
                .categories(List.of("Phone", "Tablet"))
                .brand("Apple").build();
        Product product4 = Product.builder().id(4L)
                .name("iPad Pro Wi-Fi 4G 256GB Gold")
                .slug("ipad-pro-wi-fi-4g-256gb-gold")
                .price(25440000.0)
                .isPublished(true)
                .isVisibleIndividually(true)
                .thumbnailMediaId(11L)
                .categories(List.of("Phone", "Tablet"))
                .brand("Apple").build();
        Product product5 = Product.builder().id(5L)
                .name("iPad Pro Wi-Fi 4G 512GB Gold")
                .slug("ipad-pro-wi-fi-4g-512gb-gold")
                .price(29330000.0)
                .isPublished(true)
                .isVisibleIndividually(true)
                .thumbnailMediaId(11L)
                .categories(List.of("Phone", "Tablet"))
                .brand("Apple").build();
        Product product6 = Product.builder().id(6L)
                .name("iPad Pro Wi-Fi 4G 128GB Black")
                .slug("ipad-pro-wi-fi-4g-128gb-black")
                .price(21990000.0)
                .isPublished(true)
                .isVisibleIndividually(true)
                .thumbnailMediaId(11L)
                .categories(List.of("Phone", "Tablet"))
                .brand("Apple").build();
        Product product7 = Product.builder().id(7L)
                .name("iPad Pro Wi-Fi 4G 256GB Black")
                .slug("ipad-pro-wi-fi-4g-256gb-black")
                .price(24990000.0)
                .isPublished(true)
                .isVisibleIndividually(true)
                .thumbnailMediaId(11L)
                .categories(List.of("Phone", "Tablet"))
                .brand("Apple").build();
        Product product8 = Product.builder().id(8L)
                .name("iPad Pro Wi-Fi 4G 512GB Black")
                .slug("ipad-pro-wi-fi-4g-512gb-black")
                .price(28990000.0)
                .isPublished(true)
                .isVisibleIndividually(true)
                .thumbnailMediaId(11L)
                .categories(List.of("Phone", "Tablet"))
                .brand("Apple").build();
        productRepository.saveAll(List.of(product1, product2, product3, product4, product5, product6, product7, product8));
    }
}

package com.yas.search.config;

import com.yas.search.document.Brand;
import com.yas.search.document.Product;
import com.yas.search.document.ProductAttribute;
import com.yas.search.document.ProductAttributeValue;
import com.yas.search.repository.BrandRepository;
import com.yas.search.repository.ProductAttributeRepository;
import com.yas.search.repository.ProductAttributeValueRepository;
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

    private final ProductAttributeRepository productAttributeRepository;

    private final ProductAttributeValueRepository productAttributeValueRepository;

    public InitDocument(ProductRepository productRepository,
                        BrandRepository brandRepository,
                        ProductAttributeRepository productAttributeRepository,
                        ProductAttributeValueRepository productAttributeValueRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.productAttributeValueRepository = productAttributeValueRepository;
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
        ProductAttribute productAttribute = ProductAttribute.builder().id(1L).name("attr1").build();
        ProductAttributeValue productAttributeValue = ProductAttributeValue.builder().id(1L).value("prod_attr_1").productAttribute(productAttribute).build();
        product.setAttributeValues(List.of(productAttributeValue));

        productAttributeRepository.save(productAttribute);
        productAttributeValueRepository.save(productAttributeValue);
        productRepository.save(product);
    }
}

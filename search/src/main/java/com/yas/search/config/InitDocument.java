package com.yas.search.config;

import com.yas.search.document.*;
import com.yas.search.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class InitDocument {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    private final ProductAttributeRepository productAttributeRepository;


    private final CategoryRepository categoryRepository;

    private final ProductAttributeGroupRepository productAttributeGroupRepository;

    public InitDocument(ProductRepository productRepository,
                        BrandRepository brandRepository,
                        ProductAttributeRepository productAttributeRepository,
                        CategoryRepository categoryRepository,
                        ProductAttributeGroupRepository productAttributeGroupRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.categoryRepository = categoryRepository;
        this.productAttributeGroupRepository = productAttributeGroupRepository;
    }

    @Bean
    public void init() {
        Brand brand1 = Brand.builder().id(1L).name("Apple").slug("apple").build();
        Brand brand2 = Brand.builder().id(2L).name("Samsung").slug("samsung").build();
        brandRepository.saveAll(List.of(brand1, brand2));

        Category category1 = Category.builder()
                .slug("laptop")
                .name("Laptop")
                .isPublished(true)
                .displayOrder((short) 100)
                .id(1L)
                .build();
        Category category2 = Category.builder()
                .slug("iphone")
                .name("Iphone")
                .isPublished(true)
                .displayOrder((short) 100)
                .id(2L)
                .build();
        Category category3 = Category.builder()
                .slug("macbook")
                .name("Macbook")
                .isPublished(true)
                .displayOrder((short) 100)
                .id(3L)
                .build();
        categoryRepository.saveAll(List.of(category1, category2, category3));

        ProductAttributeGroup productAttributeGroup1 = ProductAttributeGroup.builder().name("General").id(1L).build();
        ProductAttributeGroup productAttributeGroup2 = ProductAttributeGroup.builder().name("Screen").id(2L).build();
        ProductAttributeGroup productAttributeGroup3 = ProductAttributeGroup.builder().name("Connectivity").id(3L).build();
        ProductAttributeGroup productAttributeGroup4 = ProductAttributeGroup.builder().name("Camera").id(4L).build();
        productAttributeGroupRepository.saveAll(List.of(
                productAttributeGroup1, productAttributeGroup2, productAttributeGroup3, productAttributeGroup4));

        ProductAttribute productAttribute1 = ProductAttribute.builder().id(1L).name("CPU").productAttributeGroup(productAttributeGroup1).build();
        ProductAttribute productAttribute2 = ProductAttribute.builder().id(2L).name("GPU").productAttributeGroup(productAttributeGroup1).build();
        ProductAttribute productAttribute3 = ProductAttribute.builder().id(3L).name("OS").productAttributeGroup(productAttributeGroup1).build();
        ProductAttribute productAttribute4 = ProductAttribute.builder().id(4L).name("Size").productAttributeGroup(productAttributeGroup2).build();
        ProductAttribute productAttribute5 = ProductAttribute.builder().id(5L).name("Type").productAttributeGroup(productAttributeGroup2).build();
        ProductAttribute productAttribute6 = ProductAttribute.builder().id(6L).name("Bluetooth").productAttributeGroup(productAttributeGroup3).build();
        ProductAttribute productAttribute7 = ProductAttribute.builder().id(7L).name("NFC").productAttributeGroup(productAttributeGroup3).build();
        ProductAttribute productAttribute8 = ProductAttribute.builder().id(8L).name("Main Camera").productAttributeGroup(productAttributeGroup4).build();
        ProductAttribute productAttribute9 = ProductAttribute.builder().id(9L).name("Sub Camera").productAttributeGroup(productAttributeGroup4).build();
        productAttributeRepository.saveAll(
                List.of(
                        productAttribute1,
                        productAttribute2,
                        productAttribute3,
                        productAttribute4,
                        productAttribute5,
                        productAttribute6,
                        productAttribute7,
                        productAttribute8,
                        productAttribute9
                )
        );

        Product product1 = Product.builder().id(1L)
                .isAllowedToOrder(true)
                .name("Dell XPS 15 9550")
                .slug("dell-xps-15-9550")
                .price(100000.0)
                .brand(brand1)
                .isPublished(true)
                .isActive(true)
                .isVisibleIndividually(true)
                .thumbnailMediaId(1L)
                .build();
        Product product2 = Product.builder().id(2L)
                .name("iPad Pro Wi-Fi 4G 128GB")
                .slug("ipad-pro-wi-fi-4g-128gb-gold")
                .price(100000.0)
                .isPublished(true)
                .isActive(true)
                .isVisibleIndividually(true)
                .thumbnailMediaId(3L)
                .brand(brand2).build();
        productRepository.saveAll(List.of(product1, product2));
    }
}

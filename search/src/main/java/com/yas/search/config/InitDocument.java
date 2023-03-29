package com.yas.search.config;

import com.yas.search.document.*;
import com.yas.search.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class InitDocument {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    private final ProductAttributeRepository productAttributeRepository;

    private final ProductAttributeValueRepository productAttributeValueRepository;

    private final ProductCategoryRepository productCategoryRepository;

    private final CategoryRepository categoryRepository;

    public InitDocument(ProductRepository productRepository,
                        BrandRepository brandRepository,
                        ProductAttributeRepository productAttributeRepository,
                        ProductAttributeValueRepository productAttributeValueRepository,
                        ProductCategoryRepository productCategoryRepository,
                        CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.productAttributeValueRepository = productAttributeValueRepository;
        this.categoryRepository = categoryRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    @Bean
    public void init() {
        Brand brand1 = Brand.builder().id(1L).name("Brand A").build();
        Brand brand2 = Brand.builder().id(2L).name("Brand A").build();
        Category category = Category.builder().slug("cat1").name("cat1").isPublished(true).id(1L).build();
        ProductCategory productCategory = ProductCategory.builder().category(category).id(1L).build();
        brandRepository.saveAll(Arrays.asList(brand1, brand2));

        Product product = Product.builder().id(1L).name("Product test")
                .slug("slug")
                .price(88.88)
                .isPublished(true)
                .isActive(true)
                .isVisibleIndividually(true)
                .productCategories(List.of(productCategory))
                .thumbnailMediaId(1L)
                .brand(brand1).build();
        ProductAttribute productAttribute = ProductAttribute.builder().id(1L).name("attr1").build();
        ProductAttributeValue productAttributeValue = ProductAttributeValue.builder().id(1L).value("prod_attr_1").productAttribute(productAttribute).build();
        product.setAttributeValues(List.of(productAttributeValue));

        categoryRepository.save(category);
        productCategoryRepository.save(productCategory);
        productAttributeRepository.save(productAttribute);
        productAttributeValueRepository.save(productAttributeValue);
        productRepository.save(product);
    }
}

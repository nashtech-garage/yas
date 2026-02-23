package com.yas.search.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void testBuilder_whenAllFieldsProvided_thenAllFieldsSet() {
        // Given
        ZonedDateTime now = ZonedDateTime.now();
        List<String> categories = List.of("Electronics", "Audio");
        List<String> attributes = List.of("Bluetooth", "Wireless");

        // When
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .slug("test-product")
                .price(99.99)
                .isPublished(true)
                .isVisibleIndividually(true)
                .isAllowedToOrder(true)
                .isFeatured(false)
                .thumbnailMediaId(100L)
                .brand("Test Brand")
                .categories(categories)
                .attributes(attributes)
                .createdOn(now)
                .build();

        // Then
        assertNotNull(product);
        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("test-product", product.getSlug());
        assertEquals(99.99, product.getPrice());
        assertEquals(true, product.getIsPublished());
        assertEquals(true, product.getIsVisibleIndividually());
        assertEquals(true, product.getIsAllowedToOrder());
        assertEquals(false, product.getIsFeatured());
        assertEquals(100L, product.getThumbnailMediaId());
        assertEquals("Test Brand", product.getBrand());
        assertEquals(2, product.getCategories().size());
        assertEquals(2, product.getAttributes().size());
        assertEquals(now, product.getCreatedOn());
    }

    @Test
    void testSettersAndGetters_whenSettingValues_thenValuesRetrievedCorrectly() {
        // Given
        Product product = new Product();
        ZonedDateTime now = ZonedDateTime.now();

        // When
        product.setId(2L);
        product.setName("Setter Product");
        product.setSlug("setter-product");
        product.setPrice(149.99);
        product.setIsPublished(false);
        product.setIsVisibleIndividually(false);
        product.setIsAllowedToOrder(false);
        product.setIsFeatured(true);
        product.setThumbnailMediaId(200L);
        product.setBrand("Setter Brand");
        product.setCategories(List.of("Category1"));
        product.setAttributes(List.of("Attr1"));
        product.setCreatedOn(now);

        // Then
        assertEquals(2L, product.getId());
        assertEquals("Setter Product", product.getName());
        assertEquals("setter-product", product.getSlug());
        assertEquals(149.99, product.getPrice());
        assertEquals(false, product.getIsPublished());
        assertEquals(false, product.getIsVisibleIndividually());
        assertEquals(false, product.getIsAllowedToOrder());
        assertEquals(true, product.getIsFeatured());
        assertEquals(200L, product.getThumbnailMediaId());
        assertEquals("Setter Brand", product.getBrand());
        assertEquals(1, product.getCategories().size());
        assertEquals(1, product.getAttributes().size());
        assertEquals(now, product.getCreatedOn());
    }

    @Test
    void testNoArgsConstructor_whenCreated_thenObjectNotNull() {
        // When
        Product product = new Product();

        // Then
        assertNotNull(product);
    }

    @Test
    void testAllArgsConstructor_whenAllFieldsProvided_thenAllFieldsSet() {
        // Given
        ZonedDateTime now = ZonedDateTime.now();
        List<String> categories = List.of("Electronics");
        List<String> attributes = List.of("Wireless");

        // When
        Product product = new Product(
                3L,
                "All Args Product",
                "all-args-product",
                199.99,
                true,
                true,
                true,
                true,
                300L,
                "All Args Brand",
                categories,
                attributes,
                now);

        // Then
        assertNotNull(product);
        assertEquals(3L, product.getId());
        assertEquals("All Args Product", product.getName());
        assertEquals("all-args-product", product.getSlug());
        assertEquals(199.99, product.getPrice());
        assertEquals(true, product.getIsPublished());
        assertEquals(true, product.getIsVisibleIndividually());
        assertEquals(true, product.getIsAllowedToOrder());
        assertEquals(true, product.getIsFeatured());
        assertEquals(300L, product.getThumbnailMediaId());
        assertEquals("All Args Brand", product.getBrand());
        assertEquals(1, product.getCategories().size());
        assertEquals(1, product.getAttributes().size());
        assertEquals(now, product.getCreatedOn());
    }

    @Test
    void testBuilder_whenMinimalFields_thenOtherFieldsNull() {
        // When
        Product product = Product.builder()
                .id(4L)
                .name("Minimal Product")
                .build();

        // Then
        assertNotNull(product);
        assertEquals(4L, product.getId());
        assertEquals("Minimal Product", product.getName());
    }
}

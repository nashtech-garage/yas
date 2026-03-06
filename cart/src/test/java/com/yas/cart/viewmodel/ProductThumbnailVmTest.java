package com.yas.cart.viewmodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductThumbnailVmTest {

    @Test
    void testProductThumbnailVmBuilder_withValidData_shouldCreateSuccessfully() {
        // Given & When
        ProductThumbnailVm vm = ProductThumbnailVm.builder()
            .id(1L)
            .name("Product Name")
            .slug("product-slug")
            .thumbnailUrl("http://example.com/thumbnail.jpg")
            .build();

        // Then
        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("Product Name", vm.name());
        assertEquals("product-slug", vm.slug());
        assertEquals("http://example.com/thumbnail.jpg", vm.thumbnailUrl());
    }

    @Test
    void testProductThumbnailVm_withAllArgsConstructor_shouldCreateSuccessfully() {
        // Given & When
        ProductThumbnailVm vm = new ProductThumbnailVm(
            2L,
            "Another Product",
            "another-product",
            "http://example.com/another.jpg"
        );

        // Then
        assertNotNull(vm);
        assertEquals(2L, vm.id());
        assertEquals("Another Product", vm.name());
        assertEquals("another-product", vm.slug());
        assertEquals("http://example.com/another.jpg", vm.thumbnailUrl());
    }

    @Test
    void testProductThumbnailVm_id_shouldReturnCorrectValue() {
        // Given
        ProductThumbnailVm vm = new ProductThumbnailVm(
            100L,
            "Product",
            "product",
            "http://example.com/image.jpg"
        );

        // When & Then
        assertEquals(100L, vm.id());
    }

    @Test
    void testProductThumbnailVm_name_shouldReturnCorrectValue() {
        // Given
        ProductThumbnailVm vm = new ProductThumbnailVm(
            1L,
            "Test Product Name",
            "test-product",
            "http://example.com/test.jpg"
        );

        // When & Then
        assertEquals("Test Product Name", vm.name());
    }

    @Test
    void testProductThumbnailVm_slug_shouldReturnCorrectValue() {
        // Given
        ProductThumbnailVm vm = new ProductThumbnailVm(
            1L,
            "Product",
            "test-slug-value",
            "http://example.com/image.jpg"
        );

        // When & Then
        assertEquals("test-slug-value", vm.slug());
    }

    @Test
    void testProductThumbnailVm_thumbnailUrl_shouldReturnCorrectValue() {
        // Given
        ProductThumbnailVm vm = new ProductThumbnailVm(
            1L,
            "Product",
            "product",
            "http://example.com/specific-thumbnail.jpg"
        );

        // When & Then
        assertEquals("http://example.com/specific-thumbnail.jpg", vm.thumbnailUrl());
    }

    @Test
    void testProductThumbnailVm_withNullValues_shouldCreateSuccessfully() {
        // Given & When
        ProductThumbnailVm vm = new ProductThumbnailVm(
            0L,
            null,
            null,
            null
        );

        // Then
        assertNotNull(vm);
        assertEquals(0L, vm.id());
        assertNull(vm.name());
        assertNull(vm.slug());
        assertNull(vm.thumbnailUrl());
    }

    @Test
    void testProductThumbnailVm_withEmptyStrings_shouldCreateSuccessfully() {
        // Given & When
        ProductThumbnailVm vm = new ProductThumbnailVm(
            1L,
            "",
            "",
            ""
        );

        // Then
        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("", vm.name());
        assertEquals("", vm.slug());
        assertEquals("", vm.thumbnailUrl());
    }
}

package com.yas.search.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.search.model.Product;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class ProductGetVmTest {

    @Test
    void testFromModel_whenValidProduct_thenReturnProductGetVm() {
        // Given
        ZonedDateTime now = ZonedDateTime.now();
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .slug("test-product")
                .thumbnailMediaId(100L)
                .price(99.99)
                .isAllowedToOrder(true)
                .isPublished(true)
                .isFeatured(false)
                .isVisibleIndividually(true)
                .createdOn(now)
                .build();

        // When
        ProductGetVm result = ProductGetVm.fromModel(product);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test Product", result.name());
        assertEquals("test-product", result.slug());
        assertEquals(100L, result.thumbnailId());
        assertEquals(99.99, result.price());
        assertEquals(true, result.isAllowedToOrder());
        assertEquals(true, result.isPublished());
        assertEquals(false, result.isFeatured());
        assertEquals(true, result.isVisibleIndividually());
        assertEquals(now, result.createdOn());
    }

    @Test
    void testFromModel_whenProductWithNullValues_thenReturnProductGetVmWithNulls() {
        // Given
        Product product = Product.builder()
                .id(2L)
                .name("Minimal Product")
                .build();

        // When
        ProductGetVm result = ProductGetVm.fromModel(product);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.id());
        assertEquals("Minimal Product", result.name());
    }

    @Test
    void testRecord_whenCreatedDirectly_thenAllFieldsSet() {
        // Given
        ZonedDateTime now = ZonedDateTime.now();

        // When
        ProductGetVm vm = new ProductGetVm(
                3L,
                "Direct Product",
                "direct-product",
                200L,
                149.99,
                true,
                true,
                true,
                false,
                now);

        // Then
        assertNotNull(vm);
        assertEquals(3L, vm.id());
        assertEquals("Direct Product", vm.name());
        assertEquals("direct-product", vm.slug());
        assertEquals(200L, vm.thumbnailId());
        assertEquals(149.99, vm.price());
        assertEquals(true, vm.isAllowedToOrder());
        assertEquals(true, vm.isPublished());
        assertEquals(true, vm.isFeatured());
        assertEquals(false, vm.isVisibleIndividually());
        assertEquals(now, vm.createdOn());
    }
}

package com.yas.search.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.search.model.Product;
import org.junit.jupiter.api.Test;

class ProductNameGetVmTest {

    @Test
    void testFromModel_whenValidProduct_thenReturnProductNameGetVm() {
        // Given
        Product product = Product.builder()
                .id(1L)
                .name("Test Product Name")
                .slug("test-product-name")
                .build();

        // When
        ProductNameGetVm result = ProductNameGetVm.fromModel(product);

        // Then
        assertNotNull(result);
        assertEquals("Test Product Name", result.name());
    }

    @Test
    void testFromModel_whenProductWithNullName_thenReturnProductNameGetVmWithNull() {
        // Given
        Product product = Product.builder()
                .id(2L)
                .slug("no-name-product")
                .build();

        // When
        ProductNameGetVm result = ProductNameGetVm.fromModel(product);

        // Then
        assertNotNull(result);
        assertEquals(null, result.name());
    }

    @Test
    void testRecord_whenCreatedDirectly_thenNameIsSet() {
        // Given
        String productName = "Direct Product Name";

        // When
        ProductNameGetVm vm = new ProductNameGetVm(productName);

        // Then
        assertNotNull(vm);
        assertEquals(productName, vm.name());
    }

    @Test
    void testRecord_whenCreatedWithEmptyString_thenEmptyStringIsSet() {
        // Given
        String emptyName = "";

        // When
        ProductNameGetVm vm = new ProductNameGetVm(emptyName);

        // Then
        assertNotNull(vm);
        assertEquals("", vm.name());
    }
}

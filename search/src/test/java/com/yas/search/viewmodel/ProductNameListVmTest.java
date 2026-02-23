package com.yas.search.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ProductNameListVmTest {

    @Test
    void testRecord_whenProductNameListProvided_thenListSet() {
        // Given
        ProductNameGetVm name1 = new ProductNameGetVm("Product 1");
        ProductNameGetVm name2 = new ProductNameGetVm("Product 2");
        ProductNameGetVm name3 = new ProductNameGetVm("Product 3");
        List<ProductNameGetVm> productNames = List.of(name1, name2, name3);

        // When
        ProductNameListVm vm = new ProductNameListVm(productNames);

        // Then
        assertNotNull(vm);
        assertNotNull(vm.productNames());
        assertEquals(3, vm.productNames().size());
        assertEquals("Product 1", vm.productNames().get(0).name());
        assertEquals("Product 2", vm.productNames().get(1).name());
        assertEquals("Product 3", vm.productNames().get(2).name());
    }

    @Test
    void testRecord_whenEmptyList_thenEmptyListSet() {
        // Given
        List<ProductNameGetVm> productNames = List.of();

        // When
        ProductNameListVm vm = new ProductNameListVm(productNames);

        // Then
        assertNotNull(vm);
        assertNotNull(vm.productNames());
        assertTrue(vm.productNames().isEmpty());
    }

    @Test
    void testRecord_whenSingleProductName_thenSingleItemInList() {
        // Given
        ProductNameGetVm name = new ProductNameGetVm("Single Product");
        List<ProductNameGetVm> productNames = List.of(name);

        // When
        ProductNameListVm vm = new ProductNameListVm(productNames);

        // Then
        assertNotNull(vm);
        assertEquals(1, vm.productNames().size());
        assertEquals("Single Product", vm.productNames().get(0).name());
    }
}

package com.yas.search.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ProductListGetVmTest {

    @Test
    void testRecord_whenAllFieldsProvided_thenAllFieldsSet() {
        // Given
        ProductGetVm product1 = new ProductGetVm(
                1L, "Product 1", "product-1", 100L, 99.99,
                true, true, false, true, ZonedDateTime.now());
        ProductGetVm product2 = new ProductGetVm(
                2L, "Product 2", "product-2", 200L, 149.99,
                true, true, true, false, ZonedDateTime.now());
        List<ProductGetVm> products = List.of(product1, product2);

        Map<String, Map<String, Long>> aggregations = new HashMap<>();
        Map<String, Long> categoryAgg = new HashMap<>();
        categoryAgg.put("Electronics", 10L);
        categoryAgg.put("Audio", 5L);
        aggregations.put("categories", categoryAgg);

        // When
        ProductListGetVm vm = new ProductListGetVm(
                products, 0, 10, 2, 1, true, aggregations);

        // Then
        assertNotNull(vm);
        assertEquals(2, vm.products().size());
        assertEquals(0, vm.pageNo());
        assertEquals(10, vm.pageSize());
        assertEquals(2, vm.totalElements());
        assertEquals(1, vm.totalPages());
        assertTrue(vm.isLast());
        assertNotNull(vm.aggregations());
        assertEquals(1, vm.aggregations().size());
        assertTrue(vm.aggregations().containsKey("categories"));
    }

    @Test
    void testRecord_whenEmptyProductList_thenEmptyListSet() {
        // Given
        List<ProductGetVm> products = List.of();
        Map<String, Map<String, Long>> aggregations = new HashMap<>();

        // When
        ProductListGetVm vm = new ProductListGetVm(
                products, 0, 10, 0, 0, true, aggregations);

        // Then
        assertNotNull(vm);
        assertTrue(vm.products().isEmpty());
        assertEquals(0, vm.pageNo());
        assertEquals(10, vm.pageSize());
        assertEquals(0, vm.totalElements());
        assertEquals(0, vm.totalPages());
        assertTrue(vm.isLast());
        assertNotNull(vm.aggregations());
        assertTrue(vm.aggregations().isEmpty());
    }

    @Test
    void testRecord_whenNotLastPage_thenIsLastFalse() {
        // Given
        ProductGetVm product = new ProductGetVm(
                1L, "Product", "product", 100L, 99.99,
                true, true, false, true, ZonedDateTime.now());
        List<ProductGetVm> products = List.of(product);
        Map<String, Map<String, Long>> aggregations = new HashMap<>();

        // When
        ProductListGetVm vm = new ProductListGetVm(
                products, 0, 1, 10, 10, false, aggregations);

        // Then
        assertNotNull(vm);
        assertFalse(vm.isLast());
        assertEquals(10, vm.totalPages());
        assertEquals(10, vm.totalElements());
    }

    @Test
    void testRecord_whenMultipleAggregations_thenAllAggregationsPresent() {
        // Given
        List<ProductGetVm> products = List.of();
        Map<String, Map<String, Long>> aggregations = new HashMap<>();

        Map<String, Long> categoryAgg = new HashMap<>();
        categoryAgg.put("Cat1", 5L);
        aggregations.put("categories", categoryAgg);

        Map<String, Long> brandAgg = new HashMap<>();
        brandAgg.put("Brand1", 3L);
        aggregations.put("brands", brandAgg);

        Map<String, Long> attrAgg = new HashMap<>();
        attrAgg.put("Attr1", 7L);
        aggregations.put("attributes", attrAgg);

        // When
        ProductListGetVm vm = new ProductListGetVm(
                products, 0, 10, 0, 0, true, aggregations);

        // Then
        assertNotNull(vm);
        assertEquals(3, vm.aggregations().size());
        assertTrue(vm.aggregations().containsKey("categories"));
        assertTrue(vm.aggregations().containsKey("brands"));
        assertTrue(vm.aggregations().containsKey("attributes"));
    }
}

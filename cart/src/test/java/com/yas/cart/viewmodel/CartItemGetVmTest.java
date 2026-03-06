package com.yas.cart.viewmodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartItemGetVmTest {

    @Test
    void testCartItemGetVmBuilder_withValidData_shouldCreateSuccessfully() {
        // Given & When
        CartItemGetVm vm = CartItemGetVm.builder()
            .customerId("customer123")
            .productId(1L)
            .quantity(5)
            .build();

        // Then
        assertNotNull(vm);
        assertEquals("customer123", vm.customerId());
        assertEquals(1L, vm.productId());
        assertEquals(5, vm.quantity());
    }

    @Test
    void testCartItemGetVm_withAllValues_shouldCreateSuccessfully() {
        // Given & When
        CartItemGetVm vm = new CartItemGetVm("customer456", 2L, 10);

        // Then
        assertNotNull(vm);
        assertEquals("customer456", vm.customerId());
        assertEquals(2L, vm.productId());
        assertEquals(10, vm.quantity());
    }

    @Test
    void testCartItemGetVm_customerId_shouldReturnCorrectValue() {
        // Given
        CartItemGetVm vm = CartItemGetVm.builder()
            .customerId("customer789")
            .productId(3L)
            .quantity(15)
            .build();

        // When & Then
        assertEquals("customer789", vm.customerId());
    }

    @Test
    void testCartItemGetVm_productId_shouldReturnCorrectValue() {
        // Given
        CartItemGetVm vm = CartItemGetVm.builder()
            .customerId("customer123")
            .productId(100L)
            .quantity(20)
            .build();

        // When & Then
        assertEquals(100L, vm.productId());
    }

    @Test
    void testCartItemGetVm_quantity_shouldReturnCorrectValue() {
        // Given
        CartItemGetVm vm = CartItemGetVm.builder()
            .customerId("customer123")
            .productId(1L)
            .quantity(25)
            .build();

        // When & Then
        assertEquals(25, vm.quantity());
    }

    @Test
    void testCartItemGetVm_withNullFields_shouldCreateSuccessfully() {
        // Given & When
        CartItemGetVm vm = new CartItemGetVm(null, null, null);

        // Then
        assertNotNull(vm);
        assertNull(vm.customerId());
        assertNull(vm.productId());
        assertNull(vm.quantity());
    }
}

package com.yas.cart.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    void testCartItemBuilder_shouldCreateValidCartItem() {
        // Given & When
        CartItem cartItem = CartItem.builder()
            .customerId("customer123")
            .productId(1L)
            .quantity(5)
            .build();

        // Then
        assertNotNull(cartItem);
        assertEquals("customer123", cartItem.getCustomerId());
        assertEquals(1L, cartItem.getProductId());
        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    void testCartItemAllArgsConstructor_shouldCreateValidCartItem() {
        // Given & When
        CartItem cartItem = new CartItem("customer456", 2L, 10);

        // Then
        assertNotNull(cartItem);
        assertEquals("customer456", cartItem.getCustomerId());
        assertEquals(2L, cartItem.getProductId());
        assertEquals(10, cartItem.getQuantity());
    }

    @Test
    void testCartItemNoArgsConstructor_shouldCreateEmptyCartItem() {
        // Given & When
        CartItem cartItem = new CartItem();

        // Then
        assertNotNull(cartItem);
    }

    @Test
    void testCartItemSetters_shouldUpdateFields() {
        // Given
        CartItem cartItem = new CartItem();

        // When
        cartItem.setCustomerId("customer789");
        cartItem.setProductId(3L);
        cartItem.setQuantity(15);

        // Then
        assertEquals("customer789", cartItem.getCustomerId());
        assertEquals(3L, cartItem.getProductId());
        assertEquals(15, cartItem.getQuantity());
    }

    @Test
    void testCartItemGetters_shouldReturnCorrectValues() {
        // Given
        CartItem cartItem = CartItem.builder()
            .customerId("customer999")
            .productId(4L)
            .quantity(20)
            .build();

        // When & Then
        assertEquals("customer999", cartItem.getCustomerId());
        assertEquals(4L, cartItem.getProductId());
        assertEquals(20, cartItem.getQuantity());
    }
}

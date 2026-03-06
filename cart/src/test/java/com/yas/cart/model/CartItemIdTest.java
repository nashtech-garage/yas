package com.yas.cart.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartItemIdTest {

    @Test
    void testCartItemIdNoArgsConstructor_shouldCreateEmptyObject() {
        // Given & When
        CartItemId cartItemId = new CartItemId();

        // Then
        assertNotNull(cartItemId);
    }

    @Test
    void testCartItemIdAllArgsConstructor_shouldCreateValidObject() {
        // Given & When
        CartItemId cartItemId = new CartItemId("customer123", 1L);

        // Then
        assertNotNull(cartItemId);
        assertEquals("customer123", cartItemId.getCustomerId());
        assertEquals(1L, cartItemId.getProductId());
    }

    @Test
    void testCartItemIdSetters_shouldUpdateFields() {
        // Given
        CartItemId cartItemId = new CartItemId();

        // When
        cartItemId.setCustomerId("customer456");
        cartItemId.setProductId(2L);

        // Then
        assertEquals("customer456", cartItemId.getCustomerId());
        assertEquals(2L, cartItemId.getProductId());
    }

    @Test
    void testCartItemIdGetters_shouldReturnCorrectValues() {
        // Given
        CartItemId cartItemId = new CartItemId("customer789", 3L);

        // When & Then
        assertEquals("customer789", cartItemId.getCustomerId());
        assertEquals(3L, cartItemId.getProductId());
    }

    @Test
    void testCartItemIdEquals_whenSameValues_shouldReturnTrue() {
        // Given
        CartItemId cartItemId1 = new CartItemId("customer123", 1L);
        CartItemId cartItemId2 = new CartItemId("customer123", 1L);

        // When & Then
        assertEquals(cartItemId1, cartItemId2);
    }

    @Test
    void testCartItemIdEquals_whenDifferentValues_shouldReturnFalse() {
        // Given
        CartItemId cartItemId1 = new CartItemId("customer123", 1L);
        CartItemId cartItemId2 = new CartItemId("customer456", 2L);

        // When & Then
        assertNotEquals(cartItemId1, cartItemId2);
    }

    @Test
    void testCartItemIdEquals_whenSameObject_shouldReturnTrue() {
        // Given
        CartItemId cartItemId = new CartItemId("customer123", 1L);

        // When & Then
        assertEquals(cartItemId, cartItemId);
    }

    @Test
    void testCartItemIdEquals_whenNull_shouldReturnFalse() {
        // Given
        CartItemId cartItemId = new CartItemId("customer123", 1L);

        // When & Then
        assertNotEquals(cartItemId, null);
    }

    @Test
    void testCartItemIdEquals_whenDifferentClass_shouldReturnFalse() {
        // Given
        CartItemId cartItemId = new CartItemId("customer123", 1L);
        String otherObject = "NotACartItemId";

        // When & Then
        assertNotEquals(cartItemId, otherObject);
    }

    @Test
    void testCartItemIdHashCode_whenSameValues_shouldReturnSameHashCode() {
        // Given
        CartItemId cartItemId1 = new CartItemId("customer123", 1L);
        CartItemId cartItemId2 = new CartItemId("customer123", 1L);

        // When & Then
        assertEquals(cartItemId1.hashCode(), cartItemId2.hashCode());
    }

    @Test
    void testCartItemIdHashCode_whenDifferentValues_shouldReturnDifferentHashCode() {
        // Given
        CartItemId cartItemId1 = new CartItemId("customer123", 1L);
        CartItemId cartItemId2 = new CartItemId("customer456", 2L);

        // When & Then
        assertNotEquals(cartItemId1.hashCode(), cartItemId2.hashCode());
    }
}

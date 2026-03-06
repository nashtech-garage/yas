package com.yas.cart.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    void testErrorCodeConstants_shouldHaveCorrectValues() {
        // When & Then
        assertEquals("NOT_FOUND_PRODUCT", Constants.ErrorCode.NOT_FOUND_PRODUCT);
        assertEquals("NOT_EXISTING_ITEM_IN_CART", Constants.ErrorCode.NOT_EXISTING_ITEM_IN_CART);
        assertEquals("NOT_EXISTING_PRODUCT_IN_CART", Constants.ErrorCode.NOT_EXISTING_PRODUCT_IN_CART);
        assertEquals("NON_EXISTING_CART_ITEM", Constants.ErrorCode.NON_EXISTING_CART_ITEM);
        assertEquals("ADD_CART_ITEM_FAILED", Constants.ErrorCode.ADD_CART_ITEM_FAILED);
        assertEquals("DUPLICATED_CART_ITEMS_TO_DELETE", Constants.ErrorCode.DUPLICATED_CART_ITEMS_TO_DELETE);
    }

    @Test
    void testConstants_shouldBeFinalClass() {
        // When & Then
        assertTrue(Modifier.isFinal(Constants.class.getModifiers()));
    }

    @Test
    void testErrorCode_shouldBeFinalClass() {
        // When & Then
        assertTrue(Modifier.isFinal(Constants.ErrorCode.class.getModifiers()));
    }

    @Test
    void testErrorCodeConstants_shouldBePublic() {
        // When & Then
        assertTrue(Modifier.isPublic(Constants.ErrorCode.class.getModifiers()));
    }

    @Test
    void testConstants_shouldBePublic() {
        // When & Then
        assertTrue(Modifier.isPublic(Constants.class.getModifiers()));
    }
}

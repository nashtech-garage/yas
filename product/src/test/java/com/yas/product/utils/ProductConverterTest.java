package com.yas.product.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductConverterTest {

    @Test
    void testToSlug_NormalString() {
        String input = "Samsung Galaxy S24 Ultra";
        assertEquals("samsung-galaxy-s24-ultra", ProductConverter.toSlug(input));
    }

    @Test
    void testToSlug_WithSpecialCharacters() {
        String input = "Apple iPhone 15 Pro Max @ 2024!!!";
        assertEquals("apple-iphone-15-pro-max-2024-", ProductConverter.toSlug(input));
    }

    @Test
    void testToSlug_WithMultipleSpacesAndDashes() {
        String input = "Sony    PlayStation---5";
        assertEquals("sony-playstation-5", ProductConverter.toSlug(input));
    }

    @Test
    void testToSlug_StartsWithDash() {
        String input = "   -Asus ROG Strix-";
        assertEquals("asus-rog-strix-", ProductConverter.toSlug(input));
    }
}
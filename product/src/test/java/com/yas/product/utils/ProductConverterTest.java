package com.yas.product.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ProductConverterTest {

    @Test
    void toSlug_whenMixedCaseAndSymbols_thenReturnNormalizedSlug() {
        String result = ProductConverter.toSlug("  MacBook Pro 16\" 2025!  ");

        assertEquals("macbook-pro-16-2025-", result);
    }

    @Test
    void toSlug_whenLeadingSymbols_thenTrimLeadingDashOnly() {
        String result = ProductConverter.toSlug("   ***iPhone 15 Pro   ");

        assertEquals("iphone-15-pro", result);
    }
}

package com.yas.product.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ProductConverterTest {

    @ParameterizedTest
    @CsvSource({
        "Hello World,       hello-world",
        "iPhone 15 Pro,     iphone-15-pro",
        "Áo sơ mi trắng,   o-s-mi-tr-ng",
        "  trim spaces  ,  trim-spaces",
        "double---dash,     double-dash",
        "normal-slug,       normal-slug",
        "ALL CAPS,          all-caps"
    })
    void toSlug_variousInputs_returnExpectedSlug(String input, String expected) {
        assertThat(ProductConverter.toSlug(input)).isEqualTo(expected.trim());
    }

    @Test
    void toSlug_whenStartsWithDash_thenRemoveLeadingDash() {
        // Input with leading special char that produces leading dash
        String result = ProductConverter.toSlug("!hello");
        assertThat(result).doesNotStartWith("-");
    }

    @Test
    void toSlug_whenAlreadyLowercase_thenReturnUnchanged() {
        assertThat(ProductConverter.toSlug("valid-slug-123")).isEqualTo("valid-slug-123");
    }
}

package com.yas.product.utils;

public class ProductConverter {
    public static String toSlug(String input) {
        String slug = input.trim().toLowerCase().replaceAll("[^a-z0-9\\-]", "-")
                .replaceAll("-{2,}", "-");
        return slug.startsWith("-") ? slug.substring(1) : slug;
    }
}

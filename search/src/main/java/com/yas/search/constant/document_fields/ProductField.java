package com.yas.search.constant.document_fields;

public class ProductField {
    private ProductField() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String NAME = "name";
    public static final String BRAND = "brand";
    public static final String PRICE = "price";
    public static final String CATEGORIES = "categories";
    public static final String ATTRIBUTES = "attributes";
}

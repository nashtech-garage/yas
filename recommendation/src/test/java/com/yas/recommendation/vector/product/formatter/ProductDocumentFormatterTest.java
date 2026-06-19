package com.yas.recommendation.vector.product.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.databind.ObjectMapper;
import com.yas.recommendation.viewmodel.CategoryVm;
import com.yas.recommendation.viewmodel.ProductAttributeValueVm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductDocumentFormatterTest {

    private ProductDocumentFormatter formatter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        formatter = new ProductDocumentFormatter();
        objectMapper = new ObjectMapper();
    }

    @Test
    void format_whenCalled_returnFormattedString() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "iPhone 14");
        entityMap.put("shortDescription", "Latest iPhone");
        entityMap.put("specification", "6.1 inch");
        entityMap.put("price", 999.99);
        entityMap.put("brandName", "Apple");
        
        List<CategoryVm> categories = List.of(new CategoryVm(1L, "Smartphone", null, "slug", null, null, null, true));
        entityMap.put("categories", categories);
        
        List<ProductAttributeValueVm> attributes = List.of(new ProductAttributeValueVm(1L, "Color", "Black"));
        entityMap.put("attributeValues", attributes);
        
        entityMap.put("metaTitle", "iPhone 14");
        entityMap.put("metaKeyword", "iphone, apple");
        entityMap.put("metaDescription", "Buy iPhone 14");

        String template = "{name}| {shortDescription}| {specification}| Price: {price}| {brandName}| {categories}| {metaTitle}| {metaKeyword}| {metaDescription}| {attributeValues}";
        
        String result = formatter.format(entityMap, template, objectMapper);
        
        assertTrue(result.contains("iPhone 14"));
        assertTrue(result.contains("Apple"));
        assertTrue(result.contains("[Smartphone]"));
        assertTrue(result.contains("[Color: Black]"));
    }

    @Test
    void format_whenAttributesAndCategoriesAreNull_returnEmptyBrackets() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "Product");
        entityMap.put("attributeValues", null);
        entityMap.put("categories", null);

        String template = "{name} {categories} {attributeValues}";
        String result = formatter.format(entityMap, template, objectMapper);

        assertEquals("Product [] []", result);
    }
}

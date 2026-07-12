package com.yas.recommendation.vector.product.formatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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
    void format_withAttributesAndCategories_shouldFormatCorrectly() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("id", 1L);
        entityMap.put("name", "Test Product");

        Map<String, Object> attr1 = new HashMap<>();
        attr1.put("id", 1L);
        attr1.put("nameProductAttribute", "Color");
        attr1.put("value", "Red");
        entityMap.put("attributeValues", List.of(attr1));

        Map<String, Object> cat1 = new HashMap<>();
        cat1.put("name", "Electronics");
        entityMap.put("categories", List.of(cat1));

        String template = "Product {name} with categories {categories} and attributes {attributeValues}";
        
        String result = formatter.format(entityMap, template, objectMapper);
        
        assertThat(result).isEqualTo("Product Test Product with categories [Electronics] and attributes [Color: Red]");
    }

    @Test
    void format_withNullAttributesAndCategories_shouldReturnEmptyBrackets() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("id", 1L);
        entityMap.put("name", "Test Product");
        entityMap.put("attributeValues", null);
        entityMap.put("categories", null);

        String template = "Product {name} with categories {categories} and attributes {attributeValues}";
        
        String result = formatter.format(entityMap, template, objectMapper);
        
        assertThat(result).isEqualTo("Product Test Product with categories [] and attributes []");
    }

    @Test
    void format_shouldRemoveHtmlTags() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "<b>Test</b> <p>Product</p>");
        entityMap.put("attributeValues", null);
        entityMap.put("categories", null);

        String template = "Name: {name}";
        
        String result = formatter.format(entityMap, template, objectMapper);
        
        assertThat(result).isEqualTo("Name: Test Product");
    }
}

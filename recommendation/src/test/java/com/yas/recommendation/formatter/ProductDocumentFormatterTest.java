package com.yas.recommendation.formatter;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.recommendation.vector.product.formatter.ProductDocumentFormatter;
import com.yas.recommendation.viewmodel.CategoryVm;
import com.yas.recommendation.viewmodel.ProductAttributeValueVm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class ProductDocumentFormatterTest {

    private ProductDocumentFormatter formatter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        formatter = new ProductDocumentFormatter();
        objectMapper = new ObjectMapper();
    }

    @Test
    void format_withNullAttributeValues_shouldReplaceWithEmptyArray() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("attributeValues", null);
        entityMap.put("categories", null);
        entityMap.put("name", "iPhone 14");
        String template = "Product: {name} Attrs: {attributeValues} Cats: {categories}";

        String result = formatter.format(entityMap, template, objectMapper);

        assertThat(result).contains("Attrs: []");
        assertThat(result).contains("Cats: []");
    }

    @Test
    void format_withAttributeValues_shouldFormatCorrectly() {
        Map<String, Object> entityMap = new HashMap<>();
        List<Map<String, Object>> attributes = List.of(
            Map.of("id", 1L, "nameProductAttribute", "Color", "value", "Black"),
            Map.of("id", 2L, "nameProductAttribute", "Storage", "value", "128GB")
        );
        entityMap.put("attributeValues", attributes);
        entityMap.put("categories", null);
        entityMap.put("name", "iPhone");
        String template = "{attributeValues}";

        String result = formatter.format(entityMap, template, objectMapper);

        assertThat(result).contains("Color: Black");
        assertThat(result).contains("Storage: 128GB");
    }

    @Test
    void format_withCategories_shouldFormatCategoryNames() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("attributeValues", null);
        List<Map<String, Object>> categories = List.of(
            Map.of("id", 1L, "name", "Smartphones"),
            Map.of("id", 2L, "name", "Electronics")
        );
        entityMap.put("categories", categories);
        entityMap.put("name", "iPhone");
        String template = "{categories}";

        String result = formatter.format(entityMap, template, objectMapper);

        assertThat(result).contains("Smartphones");
        assertThat(result).contains("Electronics");
    }

    @Test
    void format_withEmptyAttributeList_shouldReturnEmptyBrackets() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("attributeValues", new ArrayList<>());
        entityMap.put("categories", new ArrayList<>());
        String template = "{attributeValues}|{categories}";

        String result = formatter.format(entityMap, template, objectMapper);

        assertThat(result).isEqualTo("[]|[]");
    }
}

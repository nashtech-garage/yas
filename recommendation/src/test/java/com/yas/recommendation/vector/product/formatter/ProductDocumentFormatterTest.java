package com.yas.recommendation.vector.product.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.yas.recommendation.viewmodel.CategoryVm;
import com.yas.recommendation.viewmodel.ProductAttributeValueVm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class ProductDocumentFormatterTest {

    private final ProductDocumentFormatter formatter = new ProductDocumentFormatter();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void format_shouldFormatAttributesAndCategories() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "Product 1");
        entityMap.put("attributeValues", List.of(Map.of("id", 1L, "nameProductAttribute", "Color", "value", "Red")));
        entityMap.put("categories", List.of(Map.of("id", 1L, "name", "Category 1")));
        
        String template = "{name} {attributeValues} {categories}";
        
        String result = formatter.format(entityMap, template, objectMapper);
        
        assertEquals("Product 1 [Color: Red] [Category 1]", result);
    }
}

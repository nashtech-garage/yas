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
    void shouldFormatCategoriesAttributesAndRemoveHtml() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "<b>iPhone</b>");
        entityMap.put("shortDescription", "<p>Latest model</p>");
        entityMap.put("categories", List.of(
            new CategoryVm(1L, "Phones", null, null, null, null, null, true),
            new CategoryVm(2L, "Apple", null, null, null, null, null, true)
        ));
        entityMap.put("attributeValues", List.of(
            new ProductAttributeValueVm(1L, "Color", "Black"),
            new ProductAttributeValueVm(2L, "Storage", "128GB")
        ));

        String formatted = formatter.format(
            entityMap,
            "{name}| {shortDescription}| {categories}| {attributeValues}",
            objectMapper
        );

        assertEquals(
            "iPhone| Latest model| [Phones, Apple]| [Color: Black, Storage: 128GB]",
            formatted
        );
    }

    @Test
    void shouldUseEmptyBracketsWhenCategoriesOrAttributesAreNull() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "Pixel");
        entityMap.put("categories", null);
        entityMap.put("attributeValues", null);

        String formatted = formatter.format(
            entityMap,
            "{name}| {categories}| {attributeValues}",
            objectMapper
        );

        assertEquals("Pixel| []| []", formatted);
    }
}

package com.yas.recommendation.vector.product.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.recommendation.dto.CategoryDTO;
import com.yas.recommendation.dto.ProductAttributeValueDTO;
import com.yas.recommendation.vector.common.formatter.DocumentFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.text.StringSubstitutor;

public class ProductDocumentFormatter implements DocumentFormatter {

    @Override
    public String format(Map<String, Object> entityMap, String template, ObjectMapper objectMapper) {
        entityMap.compute("attributeValues", (k, attributeValues) -> formatAttributes(attributeValues, objectMapper));
        entityMap.compute("categories", (k, categoriesValues) -> formatCategories(categoriesValues, objectMapper));
        StringSubstitutor sub = new StringSubstitutor(entityMap, "{", "}");
        return removeHtmlTags(sub.replace(template));
    }

    private String formatAttributes(Object attributeValues, ObjectMapper objectMapper) {
        if (attributeValues == null) {
            return "[]";
        }
        List<?> attributeValuesNew = (List<?>) attributeValues;
        List<ProductAttributeValueDTO> productAttributeValueList = attributeValuesNew.stream()
            .map(item -> objectMapper.convertValue(item, ProductAttributeValueDTO.class))
            .toList();

        return productAttributeValueList.stream()
            .map(attr -> attr.nameProductAttribute() + ": " + attr.value())
            .collect(Collectors.joining(", ", "[", "]"));
    }

    private String formatCategories(Object categories, ObjectMapper objectMapper) {
        if (categories == null) {
            return "[]";
        }
        List<?> categoriesNew = (List<?>) categories;
        // Convert each LinkedHashMap to ProductAttributeValueDTO
        List<CategoryDTO> categoriesList = categoriesNew.stream()
            .map(item -> objectMapper.convertValue(item, CategoryDTO.class))
            .toList();

        return categoriesList.stream()
            .map(CategoryDTO::name)
            .collect(Collectors.joining(", ", "[", "]"));
    }

}

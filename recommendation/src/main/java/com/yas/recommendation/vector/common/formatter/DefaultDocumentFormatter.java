package com.yas.recommendation.vector.common.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.recommendation.dto.CategoryDTO;
import com.yas.recommendation.dto.ProductAttributeValueDTO;
import org.apache.commons.text.StringSubstitutor;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DefaultDocumentFormatter implements DocumentFormatter {
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String format(Map<String, Object> entityMap, String template) {
        entityMap.compute("attributeValues", (k, attributeValues) -> formatAttributes(attributeValues));
        entityMap.compute("categories", (k, categoriesValues) -> formatCategories(categoriesValues));
        StringSubstitutor sub = new StringSubstitutor(entityMap, "{", "}");
        return removeHtmlTags(sub.replace(template));
    }

    public static String removeHtmlTags(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return HTML_TAG_PATTERN.matcher(input).replaceAll("").trim();
    }

    public String formatAttributes(Object attributeValues) {
        if (attributeValues == null) return "[]";
        List<?> attributeValuesNew = (List<?>) attributeValues;
        List<ProductAttributeValueDTO> productAttributeValueList = attributeValuesNew.stream()
                .map(item -> objectMapper.convertValue(item, ProductAttributeValueDTO.class))
                .toList();

        return productAttributeValueList.stream()
                .map(attr -> attr.nameProductAttribute() + ": " + attr.value())
                .collect(Collectors.joining(", ", "[", "]"));
    }

    public String formatCategories(Object categories) {
        if (categories == null) return "[]";
        List<?> categoriesNew = (List<?>) categories;
        // Convert each LinkedHashMap to ProductAttributeValueDTO
        List<CategoryDTO> categoriesList = categoriesNew.stream()
                .map(item -> objectMapper.convertValue(item, CategoryDTO.class))
                .toList();

        return categoriesList.stream()
                .map(category -> (String) category.name())
                .collect(Collectors.joining(", ", "[", "]"));
    }

    @Override
    public String format(Map<String, Object> entityMap, String template, ObjectMapper objectMapper) {
        return "";
    }
}

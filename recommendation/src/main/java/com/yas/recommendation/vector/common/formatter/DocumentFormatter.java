package com.yas.recommendation.vector.common.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.regex.Pattern;

public interface DocumentFormatter {

    Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");

    String format(Map<String, Object> entityMap, final String template, ObjectMapper objectMapper);

    default String removeHtmlTags(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return HTML_TAG_PATTERN.matcher(input).replaceAll("").trim();
    }

}

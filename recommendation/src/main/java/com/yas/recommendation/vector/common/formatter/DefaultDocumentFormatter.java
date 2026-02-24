package com.yas.recommendation.vector.common.formatter;

import tools.jackson.databind.ObjectMapper;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

public class DefaultDocumentFormatter implements DocumentFormatter {

    @Override
    public String format(Map<String, Object> entityMap, String template, ObjectMapper objectMapper) {
        StringSubstitutor sub = new StringSubstitutor(entityMap, "{", "}");
        return removeHtmlTags(sub.replace(template));
    }
}
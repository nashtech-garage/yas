package com.yas.recommendation.vector.common.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public interface DocumentFormatter {

    String format(Map<String, String> entityMap, final String template);
    String format(Map<String, Object> entityMap, final String template, ObjectMapper objectMapper);

}

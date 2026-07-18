package com.yas.recommendation.vector.common.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class DefaultDocumentFormatterTest {

    private final DefaultDocumentFormatter formatter = new DefaultDocumentFormatter();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void format_shouldSubstituteAndRemoveHtml() {
        Map<String, Object> entityMap = Map.of("name", "<b>Yas</b>", "price", 100);
        String template = "{name} costs {price}";
        
        String result = formatter.format(entityMap, template, objectMapper);
        
        assertEquals("Yas costs 100", result);
    }
}

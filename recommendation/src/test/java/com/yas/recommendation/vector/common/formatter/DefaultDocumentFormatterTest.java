package com.yas.recommendation.vector.common.formatter;

import org.junit.jupiter.api.Test;
import java.util.Map;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultDocumentFormatterTest {

    @Test
    void testFormat() {
        DefaultDocumentFormatter formatter = new DefaultDocumentFormatter();
        Map<String, Object> entityMap = Map.of("name", "<b>Test</b>", "value", "123");
        String template = "Name: {name}, Value: {value}";
        ObjectMapper mapper = new ObjectMapper();

        String result = formatter.format(entityMap, template, mapper);
        
        // Assuming removeHtmlTags removes HTML tags
        assertEquals("Name: Test, Value: 123", result);
    }
}

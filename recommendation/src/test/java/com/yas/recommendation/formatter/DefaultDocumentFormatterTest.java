package com.yas.recommendation.formatter;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.recommendation.vector.common.formatter.DefaultDocumentFormatter;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class DefaultDocumentFormatterTest {

    private DefaultDocumentFormatter formatter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        formatter = new DefaultDocumentFormatter();
        objectMapper = new ObjectMapper();
    }

    @Test
    void format_shouldReplaceTemplatePlaceholders() {
        Map<String, Object> entityMap = Map.of("name", "iPhone 14", "brand", "Apple");
        String template = "Product: {name} by {brand}";

        String result = formatter.format(entityMap, template, objectMapper);

        assertThat(result).isEqualTo("Product: iPhone 14 by Apple");
    }

    @Test
    void format_shouldStripHtmlTags() {
        Map<String, Object> entityMap = Map.of("description", "<b>Best phone</b>");
        String template = "Desc: {description}";

        String result = formatter.format(entityMap, template, objectMapper);

        assertThat(result).doesNotContain("<b>").doesNotContain("</b>");
        assertThat(result).contains("Best phone");
    }

    @Test
    void format_withNoPlaceholders_shouldReturnTemplateAsIs() {
        Map<String, Object> entityMap = Map.of("name", "iPhone");
        String template = "Static content without placeholders";

        String result = formatter.format(entityMap, template, objectMapper);

        assertThat(result).isEqualTo("Static content without placeholders");
    }

    @Test
    void format_withMissingKey_shouldLeavePlaceholderUnchanged() {
        Map<String, Object> entityMap = Map.of("name", "iPhone");
        String template = "Product: {name} - {missingKey}";

        String result = formatter.format(entityMap, template, objectMapper);

        assertThat(result).contains("iPhone");
        assertThat(result).contains("{missingKey}");
    }
}

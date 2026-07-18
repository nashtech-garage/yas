package com.yas.recommendation.vector.common.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.id.IdGenerator;
import com.yas.recommendation.vector.common.formatter.DocumentFormatter;

class BaseDocumentTest {

    @DocumentMetadata(
        docIdPrefix = "test",
        contentFormat = "{content}",
        documentFormatter = DummyFormatter.class
    )
    static class DummyDocument extends BaseDocument {
    }

    static class DummyFormatter implements DocumentFormatter {
        @Override
        public String format(java.util.Map<String, Object> entityMap, String format, tools.jackson.databind.ObjectMapper objectMapper) {
            return "formatted";
        }
    }

    @Test
    void toDocument_shouldReturnDocument() {
        DummyDocument doc = new DummyDocument();
        doc.setContent("test content");
        doc.setMetadata(Map.of("key", "value"));
        IdGenerator idGenerator = contents -> "id";

        Document result = doc.toDocument(idGenerator);

        assertNotNull(result);
        assertEquals("test content", result.getContent());
        assertEquals("value", result.getMetadata().get("key"));
    }

    @Test
    void toDocument_whenNoAnnotation_shouldThrowException() {
        BaseDocument doc = new BaseDocument() {};
        doc.setContent("test content");
        doc.setMetadata(Map.of());
        IdGenerator idGenerator = contents -> "id";

        assertThrows(IllegalArgumentException.class, () -> doc.toDocument(idGenerator));
    }
}

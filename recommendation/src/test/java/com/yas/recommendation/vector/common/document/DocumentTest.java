package com.yas.recommendation.vector.common.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.recommendation.vector.product.document.ProductDocument;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

class DocumentTest {

    @Test
    void testDefaultIdGenerator() {
        DefaultIdGenerator generator = new DefaultIdGenerator("PREFIX", 1L);
        String id = generator.generateId();
        assertNotNull(id);
        // Deterministic ID
        assertEquals(generator.generateId(), id);
    }

    @Test
    void testProductDocument() {
        ProductDocument doc = new ProductDocument();
        doc.setContent("Content");
        doc.setMetadata(new java.util.HashMap<>(Map.of("key", "value", "type", "P")));

        assertEquals("Content", doc.getContent());
        assertEquals("value", doc.getMetadata().get("key"));

        Document springDoc = doc.toDocument(new DefaultIdGenerator("P", 1L));
        assertEquals("Content", springDoc.getContent());
        assertEquals("P", springDoc.getMetadata().get("type"));
    }
}

package com.yas.recommendation.vector.product.query;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;

class RelatedProductQueryUnitTest {

    @Test
    void testConstructor() {
        VectorStore vectorStore = mock(VectorStore.class);
        RelatedProductQuery query = new RelatedProductQuery(vectorStore);
        assertNotNull(query);
    }
}

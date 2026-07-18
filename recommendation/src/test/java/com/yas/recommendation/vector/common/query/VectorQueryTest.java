package com.yas.recommendation.vector.common.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.recommendation.vector.common.document.BaseDocument;
import com.yas.recommendation.vector.common.document.DocumentMetadata;
import com.yas.recommendation.vector.common.formatter.DefaultDocumentFormatter;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class VectorQueryTest {

    @Mock
    private JdbcVectorService jdbcVectorService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DocumentMetadata(
        docIdPrefix = "TEST",
        contentFormat = "{content}",
        documentFormatter = DefaultDocumentFormatter.class
    )
    static class TestDocument extends BaseDocument {}

    static class TestResult {
        public String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    static class TestVectorQuery extends VectorQuery<TestDocument, TestResult> {
        public TestVectorQuery() {
            super(TestDocument.class, TestResult.class);
        }
    }

    private TestVectorQuery vectorQuery;

    @BeforeEach
    void setUp() {
        vectorQuery = new TestVectorQuery();
        ReflectionTestUtils.setField(vectorQuery, "jdbcVectorService", jdbcVectorService);
        ReflectionTestUtils.setField(vectorQuery, "objectMapper", objectMapper);
    }

    @Test
    void similaritySearch_shouldReturnMappedResults() {
        Document doc = new Document("content", Map.of("name", "Product 1"));
        when(jdbcVectorService.similarityProduct(eq(1L), eq(TestDocument.class))).thenReturn(List.of(doc));

        List<TestResult> results = vectorQuery.similaritySearch(1L);

        assertEquals(1, results.size());
        assertEquals("Product 1", results.get(0).getName());
    }
}

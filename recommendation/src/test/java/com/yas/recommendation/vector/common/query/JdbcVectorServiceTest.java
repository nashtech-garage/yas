package com.yas.recommendation.vector.common.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import tools.jackson.databind.ObjectMapper;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.vector.product.document.ProductDocument;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

class JdbcVectorServiceTest {

    private JdbcTemplate jdbcTemplate;
    private ObjectMapper objectMapper;
    private EmbeddingSearchConfiguration embeddingSearchConfiguration;
    private JdbcVectorService jdbcVectorService;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        objectMapper = mock(ObjectMapper.class);
        embeddingSearchConfiguration = mock(EmbeddingSearchConfiguration.class);
        jdbcVectorService = new JdbcVectorService(jdbcTemplate, objectMapper, embeddingSearchConfiguration);
    }

    @Test
    void similarityProduct_whenCalled_returnDocuments() {
        Long id = 1L;
        Document doc = new Document("content");
        List<Document> expectedDocs = List.of(doc);

        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(DocumentRowMapper.class)))
            .thenReturn(expectedDocs);

        List<Document> actualDocs = jdbcVectorService.similarityProduct(id, ProductDocument.class);

        assertEquals(expectedDocs, actualDocs);
    }
}

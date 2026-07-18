package com.yas.recommendation.vector.common.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.vector.common.document.BaseDocument;
import com.yas.recommendation.vector.common.document.DocumentMetadata;
import com.yas.recommendation.vector.common.formatter.DefaultDocumentFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class JdbcVectorServiceTest {

    @Mock
    private JdbcTemplate jdbcClient;

    @Mock
    private EmbeddingSearchConfiguration embeddingSearchConfiguration;

    private JdbcVectorService jdbcVectorService;

    @DocumentMetadata(
        docIdPrefix = "TEST",
        contentFormat = "{content}",
        documentFormatter = DefaultDocumentFormatter.class
    )
    static class TestDocument extends BaseDocument {}

    @BeforeEach
    void setUp() {
        jdbcVectorService = new JdbcVectorService(jdbcClient, new ObjectMapper(), embeddingSearchConfiguration);
    }

    @Test
    void similarityProduct_shouldCallJdbcTemplate() {
        org.mockito.Mockito.lenient().when(embeddingSearchConfiguration.similarityThreshold()).thenReturn(0.5);
        org.mockito.Mockito.lenient().when(embeddingSearchConfiguration.topK()).thenReturn(5);
        List<Document> expected = List.of(new Document("content"));
        when(jdbcClient.query(anyString(), any(PreparedStatementSetter.class), any(DocumentRowMapper.class)))
            .thenReturn(expected);

        List<Document> result = jdbcVectorService.similarityProduct(1L, TestDocument.class);

        assertEquals(expected, result);
    }
}

package com.yas.recommendation.vector.common.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.vector.product.document.ProductDocument;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

class JdbcVectorServiceTest {

    @Test
    void similarityProduct_shouldQueryConfiguredTableWithGeneratedDocumentIdAndSearchSettings() throws Exception {
        JdbcTemplate jdbcTemplate = org.mockito.Mockito.mock(JdbcTemplate.class);
        JdbcVectorService service = new JdbcVectorService(
                jdbcTemplate,
                new ObjectMapper(),
                new EmbeddingSearchConfiguration(0.42D, 5)
        );
        ReflectionTestUtils.setField(service, "vectorTableName", "custom_vectors");
        Document document = new Document("content");
        when(jdbcTemplate.query(contains("custom_vectors"), any(PreparedStatementSetter.class), any(RowMapper.class)))
                .thenReturn(List.of(document));

        List<Document> results = service.similarityProduct(44L, ProductDocument.class);

        assertThat(results).containsExactly(document);
        ArgumentCaptor<PreparedStatementSetter> setterCaptor = ArgumentCaptor.forClass(PreparedStatementSetter.class);
        verify(jdbcTemplate).query(contains("custom_vectors"), setterCaptor.capture(), any(RowMapper.class));
        PreparedStatement preparedStatement = org.mockito.Mockito.mock(PreparedStatement.class);
        setterCaptor.getValue().setValues(preparedStatement);
        UUID expectedId = UUID.nameUUIDFromBytes("PRODUCT-44".getBytes());
        verify(preparedStatement).setObject(eq(1), eq(expectedId));
        verify(preparedStatement).setObject(eq(2), eq(expectedId));
        verify(preparedStatement).setObject(eq(3), eq(0.42D));
        verify(preparedStatement).setObject(eq(4), eq(5));
    }
}

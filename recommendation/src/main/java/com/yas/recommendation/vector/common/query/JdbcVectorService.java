package com.yas.recommendation.vector.common.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.vector.common.document.BaseDocument;
import com.yas.recommendation.vector.common.document.DocumentMetadata;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.stereotype.Service;

/**
 * Jdbc Vector service support native query vector search for document.
 */
@Service
class JdbcVectorService {

    public static final String DEFAULT_DOCID_PREFIX = "PRODUCT";

    @Value("${spring.ai.vectorstore.pgvector.table-name:vector_store}")
    private String vectorTableName;

    /* Using JdbcTemplate to keep consistency with Spring AI implementation, instead of using JPA, JdbcClient */
    private final JdbcTemplate jdbcClient;
    private final DocumentRowMapper documentRowMapper;
    private final EmbeddingSearchConfiguration embeddingSearchConfiguration;

    public JdbcVectorService(
            JdbcTemplate jdbcClient,
            ObjectMapper objectMapper,
            EmbeddingSearchConfiguration embeddingSearchConfiguration
    ) {
        this.jdbcClient = jdbcClient;
        this.documentRowMapper = new DocumentRowMapper(objectMapper);
        this.embeddingSearchConfiguration = embeddingSearchConfiguration;
    }

    public <D extends BaseDocument> List<Document> similarityProduct(Long id, Class<D> docType) {
        String docIdPrefix = getDocIdPrefix(docType);
        UUID idStr = generateUuid(docIdPrefix, id);

        return jdbcClient.query(getFormattedQuery(), getPreparedStatementSetter(idStr), documentRowMapper);
    }

    private String getDocIdPrefix(Class<?> docType) {
        return Optional.ofNullable(docType)
                .map(dt -> dt.getAnnotation(DocumentMetadata.class))
                .map(DocumentMetadata::docIdPrefix)
                .orElse(DEFAULT_DOCID_PREFIX);
    }

    private UUID generateUuid(String docIdPrefix, Long id) {
        return UUID.nameUUIDFromBytes("%s-%s".formatted(docIdPrefix, id).getBytes());
    }

    private PreparedStatementSetter getPreparedStatementSetter(UUID idStr) {
        return ps -> {
            StatementCreatorUtils.setParameterValue(ps, 1, Integer.MIN_VALUE, idStr);
            StatementCreatorUtils.setParameterValue(ps, 2, Integer.MIN_VALUE, idStr);
            StatementCreatorUtils.setParameterValue(ps, 3, Integer.MIN_VALUE,
                    embeddingSearchConfiguration.similarityThreshold());
            StatementCreatorUtils.setParameterValue(ps, 4, Integer.MIN_VALUE,
                    embeddingSearchConfiguration.topK());
        };
    }

    private String getFormattedQuery() {
        return """
                WITH entity AS (
                    SELECT
                        id,
                        content,
                        metadata,
                        embedding
                    FROM
                        %s
                    WHERE
                        id = ?
                )
                SELECT
                    vs.id,
                    vs.content,
                    vs.metadata,
                    (vs.embedding <=> entity.embedding) AS similarity
                FROM
                    vector_store vs
                JOIN
                    entity ON true
                WHERE vs.id <> ? AND (vs.embedding <=> entity.embedding) > ?
                ORDER BY
                    similarity
                LIMIT ?
                """.formatted(vectorTableName);
    }
}


package com.yas.recommendation.vector.common.query;

import tools.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.RowMapper;

/**
 * Document Row Mapper.
 */
class DocumentRowMapper implements RowMapper<Document> {
    public static final String ID = "id";
    public static final String CONTENT = "content";
    public static final String METADATA = "metadata";
    private final ObjectMapper objectMapper;

    public DocumentRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
        String id = rs.getString(ID);
        String content = rs.getString(CONTENT);
        Map<String, Object> metadata = objectMapper.readValue(rs.getObject(METADATA).toString(), Map.class);
        return new Document(id, content, metadata);
    }
}
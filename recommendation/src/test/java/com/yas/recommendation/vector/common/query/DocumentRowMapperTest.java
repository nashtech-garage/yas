package com.yas.recommendation.vector.common.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import tools.jackson.databind.ObjectMapper;

class DocumentRowMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DocumentRowMapper mapper = new DocumentRowMapper(objectMapper);

    @Test
    void mapRow_shouldMapCorrectly() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("id")).thenReturn("id1");
        when(rs.getString("content")).thenReturn("content1");
        when(rs.getObject("metadata")).thenReturn("{\"key\":\"value\"}");

        Document result = mapper.mapRow(rs, 1);

        assertEquals("id1", result.getId());
        assertEquals("content1", result.getContent());
        assertEquals("value", result.getMetadata().get("key"));
    }
}

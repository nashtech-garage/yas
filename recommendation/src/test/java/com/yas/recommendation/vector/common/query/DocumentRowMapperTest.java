package com.yas.recommendation.vector.common.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import tools.jackson.databind.ObjectMapper;

class DocumentRowMapperTest {

    @Test
    void mapRow_shouldConvertResultSetToDocumentWithMetadata() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(DocumentRowMapper.ID)).thenReturn("doc-1");
        when(resultSet.getString(DocumentRowMapper.CONTENT)).thenReturn("content");
        when(resultSet.getObject(DocumentRowMapper.METADATA)).thenReturn("{\"id\":99,\"name\":\"Phone\"}");

        Document document = new DocumentRowMapper(new ObjectMapper()).mapRow(resultSet, 0);

        assertThat(document.getId()).isEqualTo("doc-1");
        assertThat(document.getContent()).isEqualTo("content");
        assertThat(document.getMetadata()).containsEntry("id", 99).containsEntry("name", "Phone");
    }
}

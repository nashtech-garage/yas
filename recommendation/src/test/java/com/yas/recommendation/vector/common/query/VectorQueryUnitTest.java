package com.yas.recommendation.vector.common.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.viewmodel.RelatedProductVm;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

class VectorQueryUnitTest {

    private JdbcVectorService jdbcVectorService;
    private TestProductQuery query;

    @BeforeEach
    void setUp() {
        jdbcVectorService = org.mockito.Mockito.mock(JdbcVectorService.class);
        query = new TestProductQuery();
        ReflectionTestUtils.setField(query, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(query, "jdbcVectorService", jdbcVectorService);
    }

    @Test
    void similaritySearch_shouldConvertDocumentMetadataToResultTypeAndSkipNullMetadata() {
        Document matched = new Document("doc-1", "content", Map.of(
                "id", 101L,
                "name", "Related phone",
                "brandName", "YAS",
                "slug", "related-phone"
        ));
        Document withoutMetadata = mock(Document.class);
        when(withoutMetadata.getMetadata()).thenReturn(null);
        when(jdbcVectorService.similarityProduct(100L, ProductDocument.class))
                .thenReturn(List.of(matched, withoutMetadata));

        List<RelatedProductVm> results = query.similaritySearch(100L);

        assertThat(results).hasSize(1);
        RelatedProductVm result = results.getFirst();
        assertThat(result.getProductId()).isEqualTo(101L);
        assertThat(result.getName()).isEqualTo("Related phone");
        assertThat(result.getBrand()).isEqualTo("YAS");
        assertThat(result.getSlug()).isEqualTo("related-phone");
    }

    private static class TestProductQuery extends VectorQuery<ProductDocument, RelatedProductVm> {
        TestProductQuery() {
            super(ProductDocument.class, RelatedProductVm.class);
        }
    }
}

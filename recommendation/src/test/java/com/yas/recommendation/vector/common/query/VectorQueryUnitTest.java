package com.yas.recommendation.vector.common.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import tools.jackson.databind.ObjectMapper;
import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.viewmodel.RelatedProductVm;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.test.util.ReflectionTestUtils;

class VectorQueryUnitTest {

    private VectorQuery<ProductDocument, RelatedProductVm> vectorQuery;
    private JdbcVectorService jdbcVectorService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        jdbcVectorService = mock(JdbcVectorService.class);
        objectMapper = mock(ObjectMapper.class);
        
        // Concrete implementation for testing abstract class
        vectorQuery = new VectorQuery<ProductDocument, RelatedProductVm>(ProductDocument.class, RelatedProductVm.class) {};
        
        ReflectionTestUtils.setField(vectorQuery, "jdbcVectorService", jdbcVectorService);
        ReflectionTestUtils.setField(vectorQuery, "objectMapper", objectMapper);
    }

    @Test
    void similaritySearch_whenCalled_returnResultList() {
        Long id = 1L;
        Document doc = new Document("content", Map.of("name", "Product 1"));
        List<Document> documents = List.of(doc);
        RelatedProductVm vm = new RelatedProductVm();
        vm.setName("Product 1");

        when(jdbcVectorService.similarityProduct(eq(id), eq(ProductDocument.class))).thenReturn(documents);
        when(objectMapper.convertValue(any(), eq(RelatedProductVm.class))).thenReturn(vm);

        List<RelatedProductVm> results = vectorQuery.similaritySearch(id);

        assertEquals(1, results.size());
        assertEquals("Product 1", results.get(0).getName());
    }
}

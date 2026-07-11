package com.yas.recommendation.vector.common.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import tools.jackson.databind.ObjectMapper;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.test.util.ReflectionTestUtils;

class SimpleVectorRepositoryUnitTest {

    private SimpleVectorRepository<ProductDocument, ProductDetailVm> repository;
    private VectorStore vectorStore;
    private ObjectMapper objectMapper;
    private EmbeddingSearchConfiguration embeddingSearchConfiguration;

    @BeforeEach
    void setUp() {
        vectorStore = mock(VectorStore.class);
        objectMapper = new ObjectMapper();
        embeddingSearchConfiguration = mock(EmbeddingSearchConfiguration.class);

        repository = new SimpleVectorRepository<ProductDocument, ProductDetailVm>(ProductDocument.class, vectorStore) {
            @Override
            public ProductDetailVm getEntity(Long id) {
                return new ProductDetailVm(id, "Name", null, null, null, null, null, "slug",
                    null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
            }
        };

        ReflectionTestUtils.setField(repository, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(repository, "embeddingSearchConfiguration", embeddingSearchConfiguration);
    }

    @Test
    void add_whenCalled_shouldAddToVectorStore() {
        repository.add(1L);
        verify(vectorStore, times(1)).add(anyList());
    }

    @Test
    void delete_whenCalled_shouldDeleteFromVectorStore() {
        repository.delete(1L);
        verify(vectorStore, times(1)).delete(anyList());
    }

    @Test
    void update_whenCalled_shouldDeleteAndAdd() {
        repository.update(1L);
        verify(vectorStore, times(1)).delete(anyList());
        verify(vectorStore, times(1)).add(anyList());
    }

    @Test
    void search_whenCalled_shouldQueryVectorStore() {
        when(embeddingSearchConfiguration.topK()).thenReturn(5);
        when(embeddingSearchConfiguration.similarityThreshold()).thenReturn(0.7);
        when(vectorStore.similaritySearch(any(org.springframework.ai.vectorstore.SearchRequest.class)))
            .thenReturn(List.of(new Document("result content", Map.of("id", 2L))));

        java.util.List<ProductDocument> results = repository.search(1L);

        org.junit.jupiter.api.Assertions.assertFalse(results.isEmpty());
        assertEquals("result content", results.get(0).getContent());
        verify(vectorStore, times(1)).similaritySearch(any(org.springframework.ai.vectorstore.SearchRequest.class));
    }
}

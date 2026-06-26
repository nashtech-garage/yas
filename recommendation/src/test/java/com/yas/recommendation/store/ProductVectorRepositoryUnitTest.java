package com.yas.recommendation.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.service.ProductService;
import com.yas.recommendation.vector.common.store.SimpleVectorRepository;
import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.vector.product.store.ProductVectorRepository;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

class ProductVectorRepositoryUnitTest {

    private VectorStore vectorStore;
    private ProductService productService;
    private ProductVectorRepository repository;

    @BeforeEach
    void setUp() {
        vectorStore = org.mockito.Mockito.mock(VectorStore.class);
        productService = org.mockito.Mockito.mock(ProductService.class);
        repository = new ProductVectorRepository(vectorStore, productService);
        ReflectionTestUtils.setField(repository, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(repository, "embeddingSearchConfiguration",
                new EmbeddingSearchConfiguration(0.75D, 7));
    }

    @Test
    void add_shouldFetchProductAndStoreFormattedDocument() {
        ProductDetailVm product = productDetail(21L);
        when(productService.getProductDetail(21L)).thenReturn(product);

        repository.add(21L);

        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).add(captor.capture());
        Document document = captor.getValue().getFirst();
        assertThat(document.getId()).isEqualTo(repository.getIdGenerator(21L).generateId());
        assertThat(document.getContent()).contains("Phone", "Price: 199.99", "YAS");
        assertThat(document.getMetadata())
                .containsEntry("id", 21L)
                .containsEntry(SimpleVectorRepository.TYPE_METADATA, ProductDocument.PREFIX_PRODUCT);
    }

    @Test
    void delete_shouldGenerateDocumentIdAndDeleteFromVectorStore() {
        doReturn(Optional.of(true)).when(vectorStore).delete(anyList());

        repository.delete(22L);

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).delete(captor.capture());
        assertThat(captor.getValue()).containsExactly(repository.getIdGenerator(22L).generateId());
    }

    @Test
    void update_shouldDeleteThenAddDocument() {
        ProductDetailVm product = productDetail(23L);
        when(productService.getProductDetail(23L)).thenReturn(product);
        doReturn(Optional.of(true)).when(vectorStore).delete(anyList());

        repository.update(23L);

        verify(vectorStore).delete(anyList());
        verify(vectorStore).add(anyList());
    }

    @Test
    void search_shouldBuildSearchRequestAndConvertDocumentsBackToProductDocuments() {
        ProductDetailVm product = productDetail(24L);
        when(productService.getProductDetail(24L)).thenReturn(product);
        Document similarDocument = new Document("similar content", Map.of("id", 25L, "name", "Similar"));
        doReturn(List.of(similarDocument)).when(vectorStore).similaritySearch(any(SearchRequest.class));

        List<ProductDocument> results = repository.search(24L);

        ArgumentCaptor<SearchRequest> captor = ArgumentCaptor.forClass(SearchRequest.class);
        verify(vectorStore).similaritySearch(captor.capture());
        SearchRequest request = captor.getValue();
        assertThat(request.query).contains("Phone");
        assertThat(request.getTopK()).isEqualTo(7);
        assertThat(request.getSimilarityThreshold()).isEqualTo(0.75D);
        assertThat(request.getFilterExpression().toString()).contains("id");
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getContent()).isEqualTo("similar content");
        assertThat(results.getFirst().getMetadata()).containsEntry("name", "Similar");
    }

    @Test
    void getEntity_shouldDelegateToProductService() {
        ProductDetailVm product = productDetail(26L);
        when(productService.getProductDetail(26L)).thenReturn(product);

        ProductDetailVm result = repository.getEntity(26L);

        assertThat(result).isSameAs(product);
    }

    private static ProductDetailVm productDetail(long id) {
        return new ProductDetailVm(
                id,
                "Phone",
                "Compact",
                "A compact phone",
                "128GB",
                "SKU-%s".formatted(id),
                "GTIN",
                "phone",
                true,
                true,
                false,
                true,
                true,
                199.99D,
                3L,
                Collections.emptyList(),
                "Phone title",
                "phone,compact",
                "Phone meta",
                4L,
                "YAS",
                Collections.emptyList(),
                null,
                null,
                null
        );
    }
}

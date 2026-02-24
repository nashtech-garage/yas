package com.yas.recommendation.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.azure.ai.openai.OpenAIClient;
import com.yas.recommendation.config.KafkaIntegrationTestConfiguration;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.service.ProductService;
import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.vector.product.store.ProductVectorRepository;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@Import(KafkaIntegrationTestConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class ProductVectorRepositoryTest extends BaseVectorRepositoryTest<ProductDocument, ProductDetailVm> {

    @Mock
    private OpenAIClient openAIClient;

    @MockitoBean
    private VectorStore vectorStore;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ProductVectorRepository productVectorRepository;

    @Autowired
    private EmbeddingSearchConfiguration embeddingSearchConf;

    public ProductVectorRepositoryTest() {
        super(ProductDocument.class);
    }

    @DisplayName("When updating document, document must be removed then create new one")
    @Test
    public void testUpdateDocument() {
        testDeleteDocument();
        testAddDocument();
    }

    @DisplayName("When deleting document, provided doc id must be format as metadata defined")
    @Test
    public void testDeleteDocument() {
        // Given
        var productId = 1L;

        // When
        doReturn(Optional.of(true)).when(vectorStore).delete(anyList());
        productVectorRepository.delete(productId);

        // Then
        ArgumentCaptor<List<String>> docIdsCaptor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore, times(1)).delete(docIdsCaptor.capture());
        var expectedId = productVectorRepository.getIdGenerator(productId).generateId();
        assertEquals(
            expectedId,
            docIdsCaptor.getValue().getFirst(),
            "DocId must be generated same as 'IdGenerator' implementation"
        );
    }

    @DisplayName("When performing search similarity, search query must be handle correctly")
    @Test
    public void testSearchDocument() {
        // Given
        var productId = 1L;
        ProductDetailVm searchedProduct = getProductDetailVm(productId);

        // When
        when(productService.getProductDetail(productId)).thenReturn(searchedProduct);

        Document similarDocument = new Document("content", Map.of("id", "2"));
        doReturn(List.of(similarDocument)).when(vectorStore).similaritySearch(any(SearchRequest.class));
        List<ProductDocument> productDocuments = productVectorRepository.search(productId);

        // Then
        ArgumentCaptor<SearchRequest> searchRequestCaptor = ArgumentCaptor.forClass(SearchRequest.class);
        verify(vectorStore).similaritySearch(searchRequestCaptor.capture());
        assertSearchRequest(searchRequestCaptor.getValue(), searchedProduct);

        assertEquals(1, productDocuments.size());
        assertEquals(similarDocument.getContent(), productDocuments.getFirst().getContent());
        assertEquals(similarDocument.getContent(), productDocuments.getFirst().getContent());
        assertEquals(similarDocument.getMetadata().keySet(), productDocuments.getFirst().getMetadata().keySet());
        assertEquals(similarDocument.getMetadata().entrySet(), productDocuments.getFirst().getMetadata().entrySet());
    }

    @DisplayName("When creating document, document must be created as metadata defined")
    @Test
    public void testAddDocument() {
        // Given
        var productId = 1L;
        ProductDetailVm productDetailVm = getProductDetailVm(productId);

        // When
        when(productService.getProductDetail(productId)).thenReturn(productDetailVm);
        doNothing().when(vectorStore).add(anyList());
        productVectorRepository.add(productId);

        // Then
        ArgumentCaptor<List<Document>> docsCaptor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore, times(1)).add(docsCaptor.capture());
        assertFalse(docsCaptor.getValue().isEmpty(), "Document must be added");

        var createdDoc = docsCaptor.getValue().getFirst();
        var expectedId = productVectorRepository.getIdGenerator(productId).generateId();
        assertEquals(
            expectedId,
            createdDoc.getId(),
            "DocId must be generated same as 'IdGenerator' implementation"
        );
        assertDocumentData(createdDoc, productDetailVm);
    }

    private static @NotNull ProductDetailVm getProductDetailVm(long productId) {
        return new ProductDetailVm(
            productId,
            "IPhone 14 Pro",
            "Latest iPhone model",
            "The iPhone 14 Pro comes with the latest technology...",
            "6.1-inch display, A16 Bionic chip, 128GB Storage",
            "IPH14PRO",
            "0123456789012",
            "iphone-14-pro",
            true,
            true,
            true,
            true,
            true,
            999.99,
            101L,
            Collections.emptyList(),
            "iPhone 14 Pro",
            "iPhone, Apple, Smartphone",
            "Buy the latest iPhone 14 Pro...",
            1L,
            "Apple",
            Collections.emptyList(),
            null,
            null,
            null
        );
    }

}

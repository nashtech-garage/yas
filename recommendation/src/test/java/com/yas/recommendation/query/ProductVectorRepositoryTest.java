package com.yas.recommendation.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.azure.ai.openai.OpenAIClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.recommendation.service.ProductService;
import com.yas.recommendation.vector.common.store.SimpleVectorRepository;
import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ProductVectorRepositoryTest {

    @Mock
    private OpenAIClient openAIClient;

    @MockBean
    private VectorStore vectorStore;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpleVectorRepository<ProductDocument, ProductDetailVm> productVectorRepository;

    @Test
    public void testAdd() throws JsonProcessingException {
        // Given
        var productId = 1L;
        ProductDetailVm productDetailVm = getProductDetailVm(productId);

        // When
        when(productService.getProductDetail(productId)).thenReturn(productDetailVm);
        doNothing().when(vectorStore).add(anyList());
        productVectorRepository.add(productId);

        // Then
        ArgumentCaptor<List<Document>> capturingMatcher = ArgumentCaptor.forClass(List.class);
        verify(vectorStore, times(1)).add(capturingMatcher.capture());
        assertFalse(capturingMatcher.getValue().isEmpty(), "Document must be added");

        var createdDoc = capturingMatcher.getValue().getFirst();
        var expectedId = productVectorRepository.getIdGenerator(productId).generateId();
        assertEquals(
            expectedId,
            createdDoc.getId(),
            "Document Id must be generated same as 'IdGenerator' implementation"
        );

        assertNotNull(createdDoc.getMetadata(), "Document's metadata must not be null");
        assertFalse(createdDoc.getMetadata().isEmpty(), "Document's metadata must not be empty");
        var expectedMetadata = objectMapper.convertValue(productDetailVm, Map.class);

        assertEquals(createdDoc.getMetadata().keySet(), expectedMetadata.keySet());
        assertEquals(createdDoc.getMetadata().entrySet(), expectedMetadata.entrySet());
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

package com.yas.recommendation.query;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.recommendation.config.KafkaIntegrationTestConfiguration;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.service.ProductService;
import com.yas.recommendation.vector.product.query.RelatedProductQuery;
import com.yas.recommendation.vector.product.store.ProductVectorRepository;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import com.yas.recommendation.viewmodel.RelatedProductVm;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@Import(KafkaIntegrationTestConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class VectorQueryTest {

    @Autowired
    private JdbcTemplate jdbcClient;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private RelatedProductQuery relatedProductQuery;

    @Autowired
    private ProductVectorRepository productVectorRepository;

    @MockBean
    private EmbeddingModel embeddingModel;

    @MockBean
    private ProductService productService;

    @MockBean
    private EmbeddingSearchConfiguration embeddingSearchConfiguration;

    @AfterEach
    public void tearDown() {
        jdbcClient.execute("DELETE FROM vector_store;");
    }

    @Test
    public void testSimilaritySearch() {
        // Given
        var productId = -1L;
        var similarProductId = -2L;
        ProductDetailVm searchedProduct = getProductDetailVm(productId);
        ProductDetailVm similarProduct = getProductDetailVm(similarProductId);

        // When
        when(embeddingSearchConfiguration.topK()).thenReturn(10);
        when(embeddingSearchConfiguration.similarityThreshold()).thenReturn(-1D); // force to query all data, not depend on vector compare operation
        when(productService.getProductDetail(productId)).thenReturn(searchedProduct);
        when(productService.getProductDetail(similarProductId)).thenReturn(similarProduct);
        when(embeddingModel.embed(any(Document.class))).thenReturn(randomEmbed());
        productVectorRepository.add(productId);
        productVectorRepository.add(similarProductId);
        List<RelatedProductVm> relatedProductVms = relatedProductQuery.similaritySearch(-2L);

        // Then
        assertFalse(relatedProductVms.isEmpty());
    }

    private static float @NotNull [] randomEmbed() {
        int size = 1536;
        float[] floatArray = new float[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            floatArray[i] = random.nextFloat();
        }
        return floatArray;
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

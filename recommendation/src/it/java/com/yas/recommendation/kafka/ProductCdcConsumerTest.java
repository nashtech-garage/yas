package com.yas.recommendation.kafka;

import static com.yas.commonlibrary.kafka.cdc.message.Operation.CREATE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.DELETE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.UPDATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import com.yas.commonlibrary.kafka.cdc.message.ProductMsgKey;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.configuration.KafkaConfiguration;
import com.yas.recommendation.configuration.RecommendationConfig;
import com.yas.recommendation.vector.product.query.RelatedProductQuery;
import com.yas.recommendation.vector.product.service.ProductVectorSyncService;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import com.yas.recommendation.viewmodel.RelatedProductVm;
import common.kafka.CdcConsumerTest;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Integration test class for testing the Product consumer behavior.
 */
@Import(KafkaConfiguration.class)
@PropertySource("classpath:application.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductCdcConsumerTest extends CdcConsumerTest<ProductMsgKey, ProductCdcMessage> {
    public static final String STOREFRONT_PRODUCTS_PATH = "/storefront/products/detail/{id}";
    private static final String PRODUCT_NAME_UPDATE = "IPhone 14 Pro New";
    @Autowired
    private JdbcTemplate jdbcClient;

    @Autowired
    private RecommendationConfig recommendationConfig;

    @MockBean
    private EmbeddingModel embeddingModel;

    @MockBean
    private EmbeddingSearchConfiguration embeddingSearchConfiguration;

    @SpyBean
    private ProductVectorSyncService productVectorSyncService;

    @Autowired
    private RelatedProductQuery relatedProductQuery;

    public ProductCdcConsumerTest() {
        super(ProductMsgKey.class, ProductCdcMessage.class, "dbproduct.public.product");
    }

    @BeforeEach
    public void setup() {
    }

    @AfterEach
    public void tearDown() {
        jdbcClient.execute("DELETE FROM vector_store;");
    }

    @DisplayName("When having product create event, data must sync as create")
    @Test
    public void test_whenHavingCreateEvent_shouldSyncAsCreate()
            throws ExecutionException, InterruptedException, TimeoutException {

        sendEventCreateProduct(1L);

        // Verify consumer
        waitForConsumer(2, 1, 0, 0);
        verify(productVectorSyncService, times(1)).createProductVector(any(Product.class));

        //Verify data
        List<Map<String, Object>> results = findAll();
        assertThat(results).isNotEmpty();
    }

    @DisplayName("When having product create event, but consumer process failed, consumer must perform retry.")
    @Test
    public void test_whenHavingCreateEvent_thenProcessFailed_shouldPerformRetry()
            throws ExecutionException, InterruptedException, TimeoutException {

        // Given
        long productId = 1L;
        ProductDetailVm response = getProductDetailVm(productId);

        // When
        when(embeddingModel.embed(any(Document.class))).thenReturn(randomEmbed());

        // Simulate Product Detail API response
        final URI url = UriComponentsBuilder.fromHttpUrl(recommendationConfig.getApiUrl())
                .path(STOREFRONT_PRODUCTS_PATH)
                .buildAndExpand(productId)
                .toUri();
        simulateHttpRequestWithError(url, new RuntimeException("Missing ResponseSpec.toEntity"), ProductDetailVm.class);

        // Sending CDC Event
        sendMsg(
            ProductMsgKey.builder().id(productId).build(),
            ProductCdcMessage.builder()
                .op(CREATE)
                .after(Product.builder().id(productId).isPublished(true).build())
                .build()
        );

        // Then
        waitForConsumer(2, 1, 4, 6);
        verify(productVectorSyncService, times(4)).createProductVector(any(Product.class));
    }

    @DisplayName("When having product create event, data must sync as create and can search similar product.")
    @Test
    public void test_whenHavingCreateEvent_shouldSyncAsCreate_andSimiliarProduct()
            throws ExecutionException, InterruptedException, TimeoutException {
        var productId = 1L;
        // Given
        ProductDetailVm response = getProductDetailVm(productId);

        // When
        when(embeddingSearchConfiguration.topK()).thenReturn(10);
        when(embeddingSearchConfiguration.similarityThreshold()).thenReturn(-1D); // force to query all data, not depend on vector compare operation
        when(embeddingModel.embed(any(Document.class))).thenReturn(randomEmbed());

        // Simulate Product Detail API response
        final URI url = UriComponentsBuilder.fromHttpUrl(recommendationConfig.getApiUrl())
                .path(STOREFRONT_PRODUCTS_PATH)
                .buildAndExpand(productId)
                .toUri();
        simulateHttpRequestWithResponseToEntity(url, response, ProductDetailVm.class);

        // Sending CDC Event
        sendMsg(
            ProductMsgKey.builder().id(productId).build(),
            ProductCdcMessage.builder()
                .op(CREATE)
                .after(Product.builder().id(productId).isPublished(true).build())
                .build()
        );

        // Then
        waitForConsumer(2, 1, 0, 0);
        verify(productVectorSyncService, times(1)).createProductVector(any(Product.class));

        // Given
        long productId2 = 2L;
        ProductDetailVm response2 = getProductDetailVm(productId2);

        // Simulate Product Detail API response
        final URI url2 = UriComponentsBuilder.fromHttpUrl(recommendationConfig.getApiUrl())
                .path(STOREFRONT_PRODUCTS_PATH)
                .buildAndExpand(productId2)
                .toUri();
        simulateHttpRequestWithResponseToEntity(url2, response2, ProductDetailVm.class);

        // Sending CDC Event
        sendMsg(
            ProductMsgKey.builder().id(productId).build(),
            ProductCdcMessage.builder()
                .op(CREATE)
                .after(Product.builder().id(productId2).isPublished(true).build())
                .build()
        );

        // Verify consumer
        waitForConsumer(2, 1, 0, 0);
        verify(productVectorSyncService, times(2)).createProductVector(any(Product.class));

        //Verify data
        List<Map<String, Object>> results = findAll();
        assertThat(results).isNotEmpty();
        assertEquals(2, results.size());

        // Verify similarSearch
        List<RelatedProductVm> relatedProductVms = relatedProductQuery.similaritySearch(productId2);
        assertFalse(relatedProductVms.isEmpty());
    }

    @DisplayName("When having product update event, data must sync as update")
    @Test
    public void test_whenHavingUpdateEvent_shouldSyncAsUpdate()
            throws ExecutionException, InterruptedException, TimeoutException {

        long productId = 1L;
        sendEventCreateProduct(productId);

        // Verify consumer
        waitForConsumer(2, 1, 0, 0);
        verify(productVectorSyncService, times(1)).createProductVector(any(Product.class));

        //Verify data
        List<Map<String, Object>> results = findAll();
        assertThat(results).isNotEmpty();

        // Given
        ProductDetailVm response = getProductDetailVmUpdate(productId);

        // When
        when(embeddingModel.embed(any(Document.class))).thenReturn(randomEmbed());

        // Simulate Product Detail API response
        final URI url = UriComponentsBuilder.fromHttpUrl(recommendationConfig.getApiUrl())
                .path(STOREFRONT_PRODUCTS_PATH)
                .buildAndExpand(productId)
                .toUri();
        simulateHttpRequestWithResponseToEntity(url, response, ProductDetailVm.class);

        // Sending CDC Event
        sendMsg(
            ProductMsgKey.builder().id(productId).build(),
            ProductCdcMessage.builder()
                .op(UPDATE)
                .after(Product.builder().id(productId).isPublished(true).build())
                .build()
        );

        // Verify consumer
        waitForConsumer(2, 2, 0, 0);
        verify(productVectorSyncService, times(1)).updateProductVector(any(Product.class));

        //Verify data
        results = findAll();
        assertThat(results).isNotEmpty();
        Map<String, Object> firstRow = results.getFirst();
        assertTrue(firstRow.get("content").toString().contains(PRODUCT_NAME_UPDATE), "Content is not correct.");
    }

    @DisplayName("When having product delete event, data must sync as delete")
    @Test
    public void test_whenHavingDeleteEvent_shouldSyncAsDelete()
            throws ExecutionException, InterruptedException, TimeoutException {

        long productId = 1L;
        sendEventCreateProduct(productId);

        // Verify consumer
        waitForConsumer(2, 1, 0, 0);
        verify(productVectorSyncService, times(1)).createProductVector(any(Product.class));

        //Verify data
        List<Map<String, Object>> results = findAll();
        assertThat(results).isNotEmpty();

        // Sending CDC Event
        sendMsg(
            ProductMsgKey.builder().id(productId).build(),
            ProductCdcMessage.builder()
                .op(DELETE)
                .after(Product.builder().id(productId).isPublished(true).build())
                .build()
        );

        // Verify consumer
        waitForConsumer(2, 2, 0, 0);
        verify(productVectorSyncService, times(1)).deleteProductVector((anyLong()));

        //Verify data
        results = findAll();
        assertThat(results).isEmpty();
    }

    private void sendEventCreateProduct(long productId)
            throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        ProductDetailVm response = getProductDetailVm(productId);

        // When
        when(embeddingModel.embed(any(Document.class))).thenReturn(randomEmbed());

        // Simulate Product Detail API response
        final URI url = UriComponentsBuilder.fromHttpUrl(recommendationConfig.getApiUrl())
                .path(STOREFRONT_PRODUCTS_PATH)
                .buildAndExpand(productId)
                .toUri();
        simulateHttpRequestWithResponseToEntity(url, response, ProductDetailVm.class);

        // Sending CDC Event
        sendMsg(
            ProductMsgKey.builder().id(productId).build(),
            ProductCdcMessage.builder()
                .op(CREATE)
                .after(Product.builder().id(productId).isPublished(true).build())
                .build()
        );
    }

    private List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM vector_store;";
        return jdbcClient.queryForList(sql);
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

    private static @NotNull ProductDetailVm getProductDetailVmUpdate(long productId) {
        return new ProductDetailVm(
                productId,
                PRODUCT_NAME_UPDATE,
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

    private static float @NotNull [] randomEmbed() {
        int size = 1536;
        float[] floatArray = new float[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            floatArray[i] = random.nextFloat();
        }
        return floatArray;
    }
}

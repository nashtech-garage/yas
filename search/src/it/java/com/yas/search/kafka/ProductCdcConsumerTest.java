package com.yas.search.kafka;

import static com.yas.commonlibrary.kafka.cdc.message.Operation.CREATE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.DELETE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.UPDATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import tools.jackson.databind.ObjectMapper;
import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import com.yas.commonlibrary.kafka.cdc.message.ProductMsgKey;
import com.yas.search.config.SearchIntegrationTestConfiguration;
import com.yas.search.config.ServiceUrlConfig;
import com.yas.search.repository.ProductRepository;
import com.yas.search.service.ProductSyncDataService;
import com.yas.search.viewmodel.ProductEsDetailVm;
import common.kafka.CdcConsumerTest;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.util.UriComponentsBuilder;

@Import(SearchIntegrationTestConfiguration.class)
@PropertySource("classpath:application.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductCdcConsumerTest extends CdcConsumerTest<ProductMsgKey, ProductCdcMessage> {

    public static final String STOREFRONT_PRODUCTS_ES_PATH = "/storefront/products-es/{id}";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServiceUrlConfig serviceUrlConfig;

    @Autowired
    private ProductRepository productRepository;

    @MockitoSpyBean
    private ProductSyncDataService productSyncDataService;

    public ProductCdcConsumerTest() {
        super(ProductMsgKey.class, ProductCdcMessage.class, "dbproduct.public.product");
    }

    @AfterEach
    public void tearDown() {
        productRepository.deleteAll();
    }

    @DisplayName("When having product create event, data must sync as create")
    @Test
    public void test_whenHavingCreateEvent_shouldSyncAsCreate()
        throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        long productId = 1L;
        ProductEsDetailVm response = getSampleProduct();

        // When
        // Simulate Product Detail API response
        final URI url = UriComponentsBuilder.fromUriString(serviceUrlConfig.product())
            .path(STOREFRONT_PRODUCTS_ES_PATH)
            .buildAndExpand(productId)
            .toUri();
        simulateHttpRequestWithResponse(url, response, ProductEsDetailVm.class);

        // Sending CDC Event
        sendMsg(
            ProductMsgKey.builder().id(productId).build(),
            ProductCdcMessage.builder()
                .op(CREATE)
                .after(Product.builder().id(productId).isPublished(true).build())
                .build()
        );

        // Then
        // Verify consumer
        waitForConsumer(2, 1, 0, 0);
        verify(productSyncDataService, times(1)).createProduct(productId);

        // Verify ES Sync data
        Optional<com.yas.search.model.Product> product = productRepository.findById(productId);
        assertTrue(product.isPresent(), "ElasticSearch must create data accordingly to CDC event.");
    }

    @DisplayName("When having product create event, but consumer process failed, consumer must perform retry.")
    @Test
    public void test_whenHavingCreateEvent_thenProcessFailed_shouldPerformRetry()
        throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        long productId = 1L;

        // When
        // Simulate Product Detail API throw errors
        final URI url = UriComponentsBuilder.fromUriString(serviceUrlConfig.product())
            .path(STOREFRONT_PRODUCTS_ES_PATH)
            .buildAndExpand(productId)
            .toUri();
        simulateHttpRequestWithError(url, new RuntimeException("Invalid Request"), ProductEsDetailVm.class);

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
        verify(productSyncDataService, times(4)).createProduct(productId);
    }

    @DisplayName("When having product update event, data must sync as update")
    @Test
    public void test_whenHavingUpdateEvent_shouldSyncAsUpdate()
        throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        long productId = 1L;
        ProductEsDetailVm response = getSampleProduct();

        // Create existing product
        com.yas.search.model.Product product = getSampleEsProduct(productId);
        productRepository.save(product);

        // When
        // Simulate Product Detail API response
        final URI url = UriComponentsBuilder.fromUriString(serviceUrlConfig.product())
            .path(STOREFRONT_PRODUCTS_ES_PATH)
            .buildAndExpand(productId)
            .toUri();
        simulateHttpRequestWithResponse(url, response, ProductEsDetailVm.class);

        // Sending CDC Event
        sendMsg(
            ProductMsgKey.builder().id(productId).build(),
            ProductCdcMessage.builder()
                .op(UPDATE)
                .after(Product.builder().id(productId).isPublished(true).build())
                .build()
        );

        // Then
        // Verify Consumer
        waitForConsumer(2, 1, 0, 0);
        verify(productSyncDataService, times(1)).updateProduct(productId);
        Optional<com.yas.search.model.Product> updated = productRepository.findById(productId);

        // Verify ES sync data
        assertTrue(updated.isPresent(), "ElasticSearch must have product data.");
        assertEquals(updated.get().getName(), response.name(), "Product name must be updated.");
    }

    @DisplayName("When having product delete event, data must sync as delete")
    @Test
    public void test_whenHavingDeleteEvent_shouldSyncAsDelete()
        throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        long productId = 1L;
        ProductEsDetailVm response = getSampleProduct();

        // Create existing product
        com.yas.search.model.Product product = getSampleEsProduct(productId);
        productRepository.save(product);

        // When
        // Simulate Product Detail API response
        final URI url = UriComponentsBuilder.fromUriString(serviceUrlConfig.product())
            .path(STOREFRONT_PRODUCTS_ES_PATH)
            .buildAndExpand(productId)
            .toUri();
        simulateHttpRequestWithResponse(url, response, ProductEsDetailVm.class);

        // Sending CDC Event
        sendMsg(
            ProductMsgKey.builder().id(productId).build(),
            ProductCdcMessage.builder()
            .op(DELETE)
            .after(Product.builder().id(productId).isPublished(true).build())
            .build()
        );

        // Then
        // Verify Consumer
        waitForConsumer(2, 1, 0, 0);
        verify(productSyncDataService, times(1)).deleteProduct(productId);
        Optional<com.yas.search.model.Product> updated = productRepository.findById(productId);

        // Verify ES sync data
        assertTrue(updated.isEmpty(), "ElasticSearch must remove product data.");
    }

    private static com.yas.search.model.@NotNull Product getSampleEsProduct(long productId) {
        com.yas.search.model.Product product = new com.yas.search.model.Product();
        product.setId(productId);
        product.setName("Modern Speaker");
        return product;
    }

    private static @NotNull ProductEsDetailVm getSampleProduct() {
        return new ProductEsDetailVm(
            1001L,
            "Wireless Bluetooth Speaker",
            "wireless-bluetooth-speaker",
            79.99,
            true,
            true,
            true,
            false,
            501L,
            "SoundWave",
            List.of("Electronics", "Audio"),
            List.of("Bluetooth 5.0", "10-hour battery life")
        );
    }

}
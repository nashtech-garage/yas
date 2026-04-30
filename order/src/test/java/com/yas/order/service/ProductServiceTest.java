package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.viewmodel.order.OrderItemVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.product.ProductCheckoutListVm;
import com.yas.order.viewmodel.product.ProductGetCheckoutListVm;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

class ProductServiceTest {

    private RestClient restClient;
    private ServiceUrlConfig serviceUrlConfig;
    private ProductService productService;

    private RestClient.ResponseSpec responseSpec;

    private static final String PRODUCT_URL = "http://api.yas.local/product";

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        productService = new ProductService(restClient, serviceUrlConfig);
        responseSpec = Mockito.mock(RestClient.ResponseSpec.class);

        setUpSecurityContext("test");
        when(serviceUrlConfig.product()).thenReturn(PRODUCT_URL);
    }

    @Test
    void getProductVariations_whenNormalCase_shouldReturnData() {
        Long productId = 100L;
        URI url = UriComponentsBuilder
                .fromUriString(serviceUrlConfig.product())
                .path("/backoffice/product-variations/{productId}")
                .buildAndExpand(productId)
                .toUri();

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(new ProductVariationVm(10L, "Color Red", "SKU-RED"))));

        List<ProductVariationVm> result = productService.getProductVariations(productId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(10L);
    }

    @Test
    void getProductInfomation_whenNormalCase_shouldReturnMapByProductId() {
        Set<Long> ids = Set.of(100L, 101L);

        URI url = UriComponentsBuilder
                .fromUriString(serviceUrlConfig.product())
                .path("/products")
                .queryParam("ids", ids)
                .queryParam("pageNo", 0)
                .queryParam("pageSize", 10)
                .buildAndExpand()
                .toUri();

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        ProductCheckoutListVm p1 = ProductCheckoutListVm.builder().id(100L).name("A").build();
        ProductCheckoutListVm p2 = ProductCheckoutListVm.builder().id(101L).name("B").build();
        ProductGetCheckoutListVm response = new ProductGetCheckoutListVm(List.of(p1, p2), 0, 10, 2, 1, true);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(response));

        Map<Long, ProductCheckoutListVm> result = productService.getProductInfomation(ids, 0, 10);

        assertThat(result).hasSize(2);
        assertThat(result).containsKeys(100L, 101L);
    }

    @Test
    void getProductInfomation_whenResponseIsNull_shouldThrowNotFoundException() {
        Set<Long> ids = Set.of(100L);

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(null));

        assertThatThrownBy(() -> productService.getProductInfomation(ids, 0, 10))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("PRODUCT_NOT_FOUND");
    }

    @Test
    void subtractProductStockQuantity_whenNormalCase_shouldNotThrow() {
        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(restClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Object.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);

        assertDoesNotThrow(() -> productService.subtractProductStockQuantity(buildOrderVm()));
    }

    @Test
    void handleProductVariationListFallback_whenThrowable_shouldRethrow() {
        RuntimeException error = new RuntimeException("downstream error");

        assertThatThrownBy(() -> productService.handleProductVariationListFallback(error))
                .isSameAs(error);
    }

    @Test
    void handleProductInfomationFallback_whenThrowable_shouldRethrow() {
        RuntimeException error = new RuntimeException("downstream error");

        assertThatThrownBy(() -> productService.handleProductInfomationFallback(error))
                .isSameAs(error);
    }

    @Test
    void handleBodilessFallback_whenThrowable_shouldRethrow() {
        RuntimeException error = new RuntimeException("downstream error");

        assertThatThrownBy(() -> productService.handleBodilessFallback(error))
                .isSameAs(error);
    }

    private OrderVm buildOrderVm() {
        Set<OrderItemVm> items = Set.of(
                new OrderItemVm(
                        1L,
                        100L,
                        "Product A",
                        2,
                        java.math.BigDecimal.valueOf(50_000),
                        "note a",
                        java.math.BigDecimal.ZERO,
                        java.math.BigDecimal.ZERO,
                        java.math.BigDecimal.ZERO,
                        1L
                )
        );

        return new OrderVm(
                1L,
                "customer@example.com",
                null,
                null,
                "note",
                0.0f,
                0.0f,
                2,
                java.math.BigDecimal.valueOf(100_000),
                java.math.BigDecimal.valueOf(10_000),
                "CODE",
                OrderStatus.PENDING,
                DeliveryMethod.GRAB_EXPRESS,
                DeliveryStatus.PREPARING,
                PaymentStatus.PENDING,
                items,
                UUID.randomUUID().toString()
        );
    }
}

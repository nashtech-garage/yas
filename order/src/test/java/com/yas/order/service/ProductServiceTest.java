package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
import java.math.BigDecimal;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

    @Nested
    class GetProductVariationsTest {

        @Test
        void getProductVariations_whenNormalCase_shouldReturnVariations() {
            Long productId = 1L;
            URI url = UriComponentsBuilder
                    .fromHttpUrl(PRODUCT_URL)
                    .path("/backoffice/product-variations/${productId}")
                    .buildAndExpand(productId)
                    .toUri();

            RestClient.RequestHeadersUriSpec requestHeadersUriSpec =
                    Mockito.mock(RestClient.RequestHeadersUriSpec.class);

            List<ProductVariationVm> expected = List.of(
                    new ProductVariationVm(10L, "Variant A", "SKU-A"),
                    new ProductVariationVm(11L, "Variant B", "SKU-B")
            );

            when(restClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(expected));

            List<ProductVariationVm> result = productService.getProductVariations(productId);

            assertNotNull(result);
            assertThat(result).hasSize(2);
            assertThat(result.get(0).id()).isEqualTo(10L);
            assertThat(result.get(1).name()).isEqualTo("Variant B");
        }
    }

    @Nested
    class SubtractProductStockQuantityTest {

        @Test
        void subtractProductStockQuantity_whenNormalCase_shouldNotThrow() {
            OrderVm orderVm = buildOrderVm();

            RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);

            when(restClient.put()).thenReturn(requestBodyUriSpec);
            when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
            when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
            when(requestBodyUriSpec.body(any(Object.class))).thenReturn(requestBodyUriSpec);
            when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);

            productService.subtractProductStockQuantity(orderVm);

            verify(restClient).put();
        }
    }

    @Nested
    class GetProductInformationTest {

        @Test
        void getProductInformation_whenProductsExist_shouldReturnMap() {
            Set<Long> ids = Set.of(1L, 2L);

            RestClient.RequestHeadersUriSpec requestHeadersUriSpec =
                    Mockito.mock(RestClient.RequestHeadersUriSpec.class);

            ProductCheckoutListVm product1 = Mockito.mock(ProductCheckoutListVm.class);
            when(product1.getId()).thenReturn(1L);
            ProductCheckoutListVm product2 = Mockito.mock(ProductCheckoutListVm.class);
            when(product2.getId()).thenReturn(2L);

            ProductGetCheckoutListVm response = new ProductGetCheckoutListVm(
                    List.of(product1, product2), 0, 2, 2, 1, true
            );

            when(restClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(response));

            Map<Long, ProductCheckoutListVm> result =
                    productService.getProductInfomation(ids, 0, 10);

            assertNotNull(result);
            assertThat(result).hasSize(2);
            assertThat(result).containsKey(1L);
            assertThat(result).containsKey(2L);
        }

        @Test
        void getProductInformation_whenResponseIsNull_shouldThrowNotFoundException() {
            Set<Long> ids = Set.of(1L);

            RestClient.RequestHeadersUriSpec requestHeadersUriSpec =
                    Mockito.mock(RestClient.RequestHeadersUriSpec.class);

            when(restClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(null));

            assertThrows(NotFoundException.class,
                    () -> productService.getProductInfomation(ids, 0, 10));
        }

        @Test
        void getProductInformation_whenProductListIsNull_shouldThrowNotFoundException() {
            Set<Long> ids = Set.of(1L);

            RestClient.RequestHeadersUriSpec requestHeadersUriSpec =
                    Mockito.mock(RestClient.RequestHeadersUriSpec.class);

            ProductGetCheckoutListVm response = new ProductGetCheckoutListVm(
                    null, 0, 0, 0, 0, true
            );

            when(restClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(response));

            assertThrows(NotFoundException.class,
                    () -> productService.getProductInfomation(ids, 0, 10));
        }
    }

    @Nested
    class FallbackHandlerTest {

        @Test
        void handleProductVariationListFallback_shouldRethrowException() {
            RuntimeException exception = new RuntimeException("Service unavailable");

            assertThrows(RuntimeException.class,
                    () -> productService.handleProductVariationListFallback(exception));
        }

        @Test
        void handleProductInformationFallback_shouldRethrowException() {
            RuntimeException exception = new RuntimeException("Service unavailable");

            assertThrows(RuntimeException.class,
                    () -> productService.handleProductInfomationFallback(exception));
        }
    }

    private OrderVm buildOrderVm() {
        Set<OrderItemVm> items = new HashSet<>();
        items.add(new OrderItemVm(
                1L, 101L, "Product A", 2, new BigDecimal("29.99"),
                "Note", new BigDecimal("5.00"), new BigDecimal("2.00"),
                new BigDecimal("6.67"), 1001L
        ));

        return new OrderVm(
                1L, "test@example.com", null, null,
                "Note", 5.0f, 10.0f, 1, new BigDecimal("29.99"),
                new BigDecimal("5.00"), "COUPON", OrderStatus.PENDING,
                DeliveryMethod.GRAB_EXPRESS, DeliveryStatus.PREPARING,
                PaymentStatus.PENDING, items, UUID.randomUUID().toString()
        );
    }
}

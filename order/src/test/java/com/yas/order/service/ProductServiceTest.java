// test
package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

// Test snyk
class ProductServiceTest {

        private static final String PRODUCT_URL = "http://api.yas.local/product";

        private RestClient restClient;
        private ServiceUrlConfig serviceUrlConfig;
        private ProductService productService;
        private RestClient.ResponseSpec responseSpec;

        @BeforeEach
        void setUp() {
                restClient = mock(RestClient.class);
                serviceUrlConfig = mock(ServiceUrlConfig.class);
                productService = new ProductService(restClient, serviceUrlConfig);
                responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
                setUpSecurityContext("user-1");
                when(serviceUrlConfig.product()).thenReturn(PRODUCT_URL);
        }

        @Test
        void getProductVariations_whenSuccess_shouldReturnData() {
                URI url = UriComponentsBuilder.fromUriString(PRODUCT_URL)
                                .path("/backoffice/product-variations/{productId}")
                                .buildAndExpand(1L)
                                .toUri();

                RestClient.RequestHeadersUriSpec request = mock(RestClient.RequestHeadersUriSpec.class);
                when(restClient.get()).thenReturn(request);
                when(request.uri(url)).thenReturn(request);
                when(request.headers(any())).thenReturn(request);
                when(request.retrieve()).thenReturn(responseSpec);
                when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                                .thenReturn(ResponseEntity.ok(
                                                Set.of(new ProductVariationVm(10L, "v1", "sku1")).stream().toList()));

                assertThat(productService.getProductVariations(1L)).hasSize(1);
        }

        @Test
        void getProductInfomation_whenSuccess_shouldMapById() {
                URI url = UriComponentsBuilder.fromUriString(PRODUCT_URL)
                                .path("/products")
                                .queryParam("ids", Set.of(1L))
                                .queryParam("pageNo", 0)
                                .queryParam("pageSize", 10)
                                .buildAndExpand()
                                .toUri();

                ProductCheckoutListVm item = ProductCheckoutListVm.builder()
                                .id(1L)
                                .name("name")
                                .price(10.0)
                                .taxClassId(1L)
                                .build();
                ProductGetCheckoutListVm body = new ProductGetCheckoutListVm(
                                java.util.List.of(item), 0, 1, 1, 1, true);

                RestClient.RequestHeadersUriSpec request = mock(RestClient.RequestHeadersUriSpec.class);
                when(restClient.get()).thenReturn(request);
                when(request.uri(url)).thenReturn(request);
                when(request.headers(any())).thenReturn(request);
                when(request.retrieve()).thenReturn(responseSpec);
                when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(body));

                Map<Long, ProductCheckoutListVm> result = productService.getProductInfomation(Set.of(1L), 0, 10);

                assertThat(result).containsKey(1L);
        }

        @Test
        void getProductInfomation_whenResponseMissing_shouldThrowNotFound() {
                RestClient.RequestHeadersUriSpec request = mock(RestClient.RequestHeadersUriSpec.class);
                when(restClient.get()).thenReturn(request);
                when(request.uri(any(URI.class))).thenReturn(request);
                when(request.headers(any())).thenReturn(request);
                when(request.retrieve()).thenReturn(responseSpec);
                when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(null));

                assertThrows(NotFoundException.class, () -> productService.getProductInfomation(Set.of(1L), 0, 10));
        }

        @Test
        void subtractProductStockQuantity_whenSuccess_shouldNotThrow() {
                RestClient.RequestBodyUriSpec requestUriSpec = mock(RestClient.RequestBodyUriSpec.class);
                RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class,
                                Mockito.RETURNS_SELF);
                when(restClient.put()).thenReturn(requestUriSpec);
                when(requestUriSpec.uri(any(URI.class))).thenReturn(requestBodySpec);
                when(requestBodySpec.retrieve()).thenReturn(responseSpec);

                assertDoesNotThrow(() -> productService.subtractProductStockQuantity(createOrderVm()));
        }

        private static OrderVm createOrderVm() {
                OrderItemVm item = new OrderItemVm(
                                1L,
                                101L,
                                "Product A",
                                2,
                                new BigDecimal("29.99"),
                                "note",
                                BigDecimal.ZERO,
                                BigDecimal.ZERO,
                                BigDecimal.ZERO,
                                1001L);

                return new OrderVm(
                                1001L,
                                "customer@example.com",
                                null,
                                null,
                                "note",
                                0,
                                0,
                                1,
                                new BigDecimal("29.99"),
                                BigDecimal.ZERO,
                                null,
                                OrderStatus.PENDING,
                                DeliveryMethod.GRAB_EXPRESS,
                                DeliveryStatus.PREPARING,
                                PaymentStatus.PENDING,
                                Set.of(item),
                                "checkout-1");
        }
}

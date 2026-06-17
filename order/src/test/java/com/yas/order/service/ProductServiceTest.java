package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.order.OrderItemVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.product.ProductCheckoutListVm;
import com.yas.order.viewmodel.product.ProductGetCheckoutListVm;
import com.yas.order.viewmodel.product.ProductQuantityItem;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.math.BigDecimal;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

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
        setUpSecurityContext("test");
        when(serviceUrlConfig.product()).thenReturn(PRODUCT_URL);
    }

    @Test
    void getProductInfomationShouldReturnMapWhenResponseContainsProducts() {
        Set<Long> ids = new LinkedHashSet<>(List.of(1L, 2L));
        List<ProductCheckoutListVm> products = List.of(
            ProductCheckoutListVm.builder()
                .id(1L)
                .name("Product 1")
                .price(10.0)
                .taxClassId(100L)
                .build(),
            ProductCheckoutListVm.builder()
                .id(2L)
                .name("Product 2")
                .price(20.0)
                .taxClassId(200L)
                .build()
        );
        ProductGetCheckoutListVm response = new ProductGetCheckoutListVm(
            products,
            0,
            10,
            products.size(),
            1,
            true
        );

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(response));

        Map<Long, ProductCheckoutListVm> result = productService.getProductInfomation(ids, 0, 10);

        assertThat(result)
            .hasSize(2)
            .containsKeys(1L, 2L);
        assertThat(result.get(1L).getName()).isEqualTo("Product 1");
        assertThat(result.get(2L).getTaxClassId()).isEqualTo(200L);
    }

    @Test
    void getProductInfomationShouldThrowNotFoundWhenResponseIsNull() {
        Set<Long> ids = Set.of(1L);

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(null));

        assertThrows(NotFoundException.class, () -> productService.getProductInfomation(ids, 0, 10));
    }

    @Test
    void getProductInfomationShouldThrowNotFoundWhenProductListIsNull() {
        Set<Long> ids = Set.of(1L);
        ProductGetCheckoutListVm response = new ProductGetCheckoutListVm(
            null,
            0,
            10,
            0,
            0,
            true
        );

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(response));

        assertThrows(NotFoundException.class, () -> productService.getProductInfomation(ids, 0, 10));
    }

    @Test
    void subtractProductStockQuantityShouldCallProductApiWithQuantityItems() {
        Set<OrderItemVm> orderItems = Set.of(
            OrderItemVm.builder()
                .id(1L)
                .productId(10L)
                .productName("Product A")
                .quantity(2)
                .productPrice(new BigDecimal("10.00"))
                .build(),
            OrderItemVm.builder()
                .id(2L)
                .productId(20L)
                .productName("Product B")
                .quantity(3)
                .productPrice(new BigDecimal("20.00"))
                .build()
        );

        OrderVm orderVm = new OrderVm(
            1L,
            "customer@example.com",
            null,
            null,
            "note",
            0.0f,
            0.0f,
            2,
            new BigDecimal("50.00"),
            new BigDecimal("5.00"),
            "CODE",
            null,
            null,
            null,
            null,
            orderItems,
            "checkout-id"
        );

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(restClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Object.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);

        productService.subtractProductStockQuantity(orderVm);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ProductQuantityItem>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(requestBodyUriSpec).body(captor.capture());

        assertThat(captor.getValue()).containsExactlyInAnyOrder(
            new ProductQuantityItem(10L, 2L),
            new ProductQuantityItem(20L, 3L)
        );
    }

    @Test
    void getProductVariationsShouldReturnProductVariations() {
        List<ProductVariationVm> variations = List.of(
            new ProductVariationVm(1L, "Variation A", "SKU-1"),
            new ProductVariationVm(2L, "Variation B", "SKU-2")
        );

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(variations));

        List<ProductVariationVm> result = productService.getProductVariations(99L);

        assertThat(result).containsExactlyElementsOf(variations);
    }
}

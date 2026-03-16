package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import com.yas.order.viewmodel.product.ProductQuantityItem;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

class ProductServiceTest {

    private static final String PRODUCT_URL = "http://api.yas.local/product";

    private RestClient restClient;
    private ServiceUrlConfig serviceUrlConfig;
    private ProductService productService;

    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        restClient = org.mockito.Mockito.mock(RestClient.class);
        serviceUrlConfig = org.mockito.Mockito.mock(ServiceUrlConfig.class);
        productService = new ProductService(restClient, serviceUrlConfig);

        requestHeadersUriSpec = org.mockito.Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        requestBodyUriSpec = org.mockito.Mockito.mock(RestClient.RequestBodyUriSpec.class);
        responseSpec = org.mockito.Mockito.mock(RestClient.ResponseSpec.class);

        when(serviceUrlConfig.product()).thenReturn(PRODUCT_URL);
        setUpSecurityContext("tester");

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        when(restClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Object.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void getProductInfomation_whenResponseContainsProducts_thenReturnMappedProducts() {
        ProductCheckoutListVm first = ProductCheckoutListVm.builder().id(101L).name("A").price(10.0).build();
        ProductCheckoutListVm second = ProductCheckoutListVm.builder().id(202L).name("B").price(20.0).build();
        ProductGetCheckoutListVm body = new ProductGetCheckoutListVm(List.of(first, second), 0, 10, 2, 1, true);

        when(responseSpec.toEntity(any(org.springframework.core.ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(body));

        Map<Long, ProductCheckoutListVm> result = productService.getProductInfomation(Set.of(101L, 202L), 0, 10);

        assertThat(result).hasSize(2);
        assertThat(result).containsKeys(101L, 202L);
        assertThat(result.get(101L).getName()).isEqualTo("A");
    }

    @Test
    void getProductInfomation_whenResponseIsNull_thenThrowNotFoundException() {
        when(responseSpec.toEntity(any(org.springframework.core.ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(null));

        assertThrows(NotFoundException.class, () -> productService.getProductInfomation(Set.of(1L), 0, 10));
    }

    @Test
    void getProductInfomation_whenProductListIsNull_thenThrowNotFoundException() {
        ProductGetCheckoutListVm body = new ProductGetCheckoutListVm(null, 0, 10, 0, 0, true);
        when(responseSpec.toEntity(any(org.springframework.core.ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(body));

        assertThrows(NotFoundException.class, () -> productService.getProductInfomation(Set.of(1L), 0, 10));
    }

    @Test
    void getProductVariations_whenApiReturnsData_thenReturnVariationList() {
        List<ProductVariationVm> variations = List.of(
                new ProductVariationVm(1L, "Var-1", "SKU-1"),
                new ProductVariationVm(2L, "Var-2", "SKU-2")
        );

        when(responseSpec.toEntity(any(org.springframework.core.ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(variations));

        List<ProductVariationVm> result = productService.getProductVariations(123L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void subtractProductStockQuantity_whenValidOrder_thenBuildProductQuantityItemsAndCallApi() {
        OrderVm orderVm = buildOrderVm();

        assertDoesNotThrow(() -> productService.subtractProductStockQuantity(orderVm));

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(requestBodyUriSpec).body(bodyCaptor.capture());

        @SuppressWarnings("unchecked")
        List<ProductQuantityItem> payload = (List<ProductQuantityItem>) bodyCaptor.getValue();

        assertThat(payload).hasSize(2);
        assertThat(payload)
                .extracting(ProductQuantityItem::productId, ProductQuantityItem::quantity)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple(1001L, 2L),
                        org.assertj.core.groups.Tuple.tuple(1002L, 1L)
                );
    }

    @Test
    void handleProductVariationListFallback_whenCalled_thenThrowOriginalError() {
        RuntimeException ex = new RuntimeException("circuit-open");

        Throwable thrown = assertThrows(Throwable.class, () -> productService.handleProductVariationListFallback(ex));

        assertThat(thrown).isSameAs(ex);
    }

    @Test
    void handleProductInfomationFallback_whenCalled_thenThrowOriginalError() {
        RuntimeException ex = new RuntimeException("timeout");

        Throwable thrown = assertThrows(Throwable.class, () -> productService.handleProductInfomationFallback(ex));

        assertThat(thrown).isSameAs(ex);
    }

    private static OrderVm buildOrderVm() {
        Set<OrderItemVm> items = Set.of(
                new OrderItemVm(1L, 1001L, "Product A", 2, new BigDecimal("10.00"), null,
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 500L),
                new OrderItemVm(2L, 1002L, "Product B", 1, new BigDecimal("20.00"), null,
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 500L)
        );

        return new OrderVm(
                500L,
                "buyer@yas.local",
                null,
                null,
                "",
                0f,
                0f,
                2,
                new BigDecimal("40.00"),
                BigDecimal.ZERO,
                null,
                OrderStatus.PENDING,
                DeliveryMethod.GRAB_EXPRESS,
                DeliveryStatus.PREPARING,
                PaymentStatus.PENDING,
                items,
                "checkout-500"
        );
    }
}

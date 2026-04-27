package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@SuppressWarnings({ "rawtypes", "unchecked" })
class ProductServiceTest {

    private static final String PRODUCT_URL = "http://api.yas.local/product";

    private RestClient restClient;
    private ServiceUrlConfig serviceUrlConfig;
    private ProductService productService;
    private RestClient.ResponseSpec responseSpec;

    private void init() {
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        productService = new ProductService(restClient, serviceUrlConfig);
        responseSpec = mock(RestClient.ResponseSpec.class);
        setUpSecurityContext("test");
        when(serviceUrlConfig.product()).thenReturn(PRODUCT_URL);
    }

    @Test
    void testGetProductVariations_whenNormalCase_returnVariations() {
        init();
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        URI url = UriComponentsBuilder.fromUriString(serviceUrlConfig.product())
                .path("/backoffice/product-variations/{productId}")
                .buildAndExpand(10L)
                .toUri();

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.<List<ProductVariationVm>>toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(new ProductVariationVm(10L, "Product A", "SKU-1"))));

        List<ProductVariationVm> result = productService.getProductVariations(10L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(10L);
    }

    @Test
    void testSubtractProductStockQuantity_whenNormalCase_sendQuantityBody() {
        init();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
        when(restClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        OrderVm orderVm = new OrderVm(
                1L,
                "buyer@example.com",
                null,
                null,
                null,
                0.0f,
                0.0f,
                1,
                new BigDecimal("10.00"),
                new BigDecimal("0.00"),
                null,
                null,
                null,
                null,
                null,
                Set.of(new OrderItemVm(1L, 101L, "Product A", 2, new BigDecimal("5.00"), null, null, null, null, null)),
                "checkout-1");

        assertDoesNotThrow(() -> productService.subtractProductStockQuantity(orderVm));

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(requestBodySpec).body(bodyCaptor.capture());
        List<Object> payload = (List<Object>) bodyCaptor.getValue();
        assertThat(payload).hasSize(1);
    }

    @Test
    void testGetProductInfomation_whenNormalCase_returnMap() {
        init();
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        ProductCheckoutListVm product = ProductCheckoutListVm.builder()
                .id(10L)
                .name("Product A")
                .price(12.5)
                .build();
        when(responseSpec.<ProductGetCheckoutListVm>toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(new ProductGetCheckoutListVm(List.of(product), 0, 1, 1, 1, true)));

        Map<Long, ProductCheckoutListVm> result = productService.getProductInfomation(Set.of(10L), 0, 1);

        assertThat(result).hasSize(1);
        assertThat(result.get(10L).getName()).isEqualTo("Product A");
    }

    @Test
    void testGetProductInfomation_whenResponseIsNull_throwNotFoundException() {
        init();
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.<ProductGetCheckoutListVm>toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(null));

        try {
            Map<Long, ProductCheckoutListVm> ignored = productService.getProductInfomation(Set.of(10L), 0, 1);
            assertThat(ignored).isNotNull();
        } catch (NotFoundException exception) {
            assertThat(exception).hasMessage("PRODUCT_NOT_FOUND");
            return;
        }
        throw new AssertionError("Expected NotFoundException");
    }

    @Test
    void testGetProductInfomation_whenProductListIsNull_throwNotFoundException() {
        init();
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.<ProductGetCheckoutListVm>toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(new ProductGetCheckoutListVm(null, 0, 0, 0, 0, true)));

        try {
            Map<Long, ProductCheckoutListVm> ignored = productService.getProductInfomation(Set.of(10L), 0, 1);
            assertThat(ignored).isNotNull();
        } catch (NotFoundException exception) {
            assertThat(exception).hasMessage("PRODUCT_NOT_FOUND");
            return;
        }
        throw new AssertionError("Expected NotFoundException");
    }

}

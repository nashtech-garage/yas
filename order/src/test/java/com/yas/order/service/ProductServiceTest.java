package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setUpSecurityContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.product.ProductCheckoutListVm;
import com.yas.order.viewmodel.product.ProductGetCheckoutListVm;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

class ProductServiceTest {

    private static final String PRODUCT_URL = "http://api.yas.local/product";

    private RestClient restClient;
    private ServiceUrlConfig serviceUrlConfig;
    private ProductService productService;

    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        productService = new ProductService(restClient, serviceUrlConfig);

        requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);

        setUpSecurityContext("test-user");
        when(serviceUrlConfig.product()).thenReturn(PRODUCT_URL);
    }

    @Test
    void getProductVariations_ReturnsResponseBody() {
        List<ProductVariationVm> variations = List.of(new ProductVariationVm(11L, "Red", "SKU-RED"));

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersUriSpec).when(requestHeadersUriSpec).uri(any(URI.class));
        doReturn(requestHeadersUriSpec).when(requestHeadersUriSpec).headers(any());
        doReturn(responseSpec).when(requestHeadersUriSpec).retrieve();
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(variations));

        List<ProductVariationVm> result = productService.getProductVariations(99L);

        assertEquals(1, result.size());
        assertEquals(11L, result.getFirst().id());
    }

    @Test
    void getProductInfomation_ReturnsMappedById() {
        ProductCheckoutListVm product1 = ProductCheckoutListVm.builder().id(1L).name("A").price(10.0).build();
        ProductCheckoutListVm product2 = ProductCheckoutListVm.builder().id(2L).name("B").price(20.0).build();
        ProductGetCheckoutListVm responseVm = new ProductGetCheckoutListVm(List.of(product1, product2), 0, 2, 2, 1, true);

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersUriSpec).when(requestHeadersUriSpec).uri(any(URI.class));
        doReturn(requestHeadersUriSpec).when(requestHeadersUriSpec).headers(any());
        doReturn(responseSpec).when(requestHeadersUriSpec).retrieve();
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(responseVm));

        Map<Long, ProductCheckoutListVm> result = productService.getProductInfomation(Set.of(1L, 2L), 0, 10);

        assertEquals(2, result.size());
        assertEquals("A", result.get(1L).getName());
        assertEquals("B", result.get(2L).getName());
    }

    @Test
    void getProductInfomation_WhenResponseBodyIsNull_ThrowsNotFoundException() {
        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersUriSpec).when(requestHeadersUriSpec).uri(any(URI.class));
        doReturn(requestHeadersUriSpec).when(requestHeadersUriSpec).headers(any());
        doReturn(responseSpec).when(requestHeadersUriSpec).retrieve();
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(null));

        NotFoundException exception =
                assertThrows(NotFoundException.class, () -> productService.getProductInfomation(Set.of(1L), 0, 10));

        assertEquals("PRODUCT_NOT_FOUND", exception.getMessage());
    }

    @Test
    void handleProductVariationListFallback_ThrowsOriginalThrowable() {
        RuntimeException exception = new RuntimeException("variation-error");

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> productService.handleProductVariationListFallback(exception));

        assertSame(exception, thrown);
    }

    @Test
    void handleProductInfomationFallback_ThrowsOriginalThrowable() {
        IllegalStateException exception = new IllegalStateException("product-info-error");

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> productService.handleProductInfomationFallback(exception));

        assertSame(exception, thrown);
    }

}

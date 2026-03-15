package com.yas.cart.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import com.yas.cart.viewmodel.ProductThumbnailVm;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

class ProductServiceTest {

    RestClient restClient;

    ServiceUrlConfig serviceUrlConfig;

    ProductService productService;

    RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        restClient = Mockito.mock(RestClient.class);
        serviceUrlConfig = Mockito.mock(ServiceUrlConfig.class);
        productService = new ProductService(restClient, serviceUrlConfig);
        requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
    }

    @Test
    void getProducts_NormalCase_ReturnProductThumbnailVms() {

        List<Long> ids = List.of(1L, 2L, 3L);
        URI url = UriComponentsBuilder
            .fromUriString("http://api.yas.local/media")
            .path("/storefront/products/list-featured")
            .queryParam("productId", ids)
            .build()
            .toUri();

        when(serviceUrlConfig.product()).thenReturn("http://api.yas.local/media");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(new ParameterizedTypeReference<List<ProductThumbnailVm>>() {
        }))
            .thenReturn(ResponseEntity.ok(getProductThumbnailVms()));

        List<ProductThumbnailVm> result = productService.getProducts(ids);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).id()).isEqualTo(1);
        assertThat(result.get(1).id()).isEqualTo(2);
        assertThat(result.get(2).id()).isEqualTo(3);
    }

    @Test
    void getProductById_whenProductExists_shouldReturnFirstProduct() {
        ProductService spyProductService = Mockito.spy(productService);
        ProductThumbnailVm product = new ProductThumbnailVm(9L, "Product 9", "product-9", "http://example.com/p9.jpg");
        doReturn(List.of(product)).when(spyProductService).getProducts(List.of(9L));

        ProductThumbnailVm result = spyProductService.getProductById(9L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(9L);
    }

    @Test
    void getProductById_whenNoProductReturned_shouldReturnNull() {
        ProductService spyProductService = Mockito.spy(productService);
        doReturn(List.of()).when(spyProductService).getProducts(List.of(99L));

        ProductThumbnailVm result = spyProductService.getProductById(99L);

        assertThat(result).isNull();
    }

    @Test
    void existsById_whenProductExists_shouldReturnTrue() {
        ProductService spyProductService = Mockito.spy(productService);
        ProductThumbnailVm product = new ProductThumbnailVm(7L, "Product 7", "product-7", "http://example.com/p7.jpg");
        doReturn(product).when(spyProductService).getProductById(7L);

        boolean result = spyProductService.existsById(7L);

        assertThat(result).isTrue();
    }

    @Test
    void existsById_whenProductNotExists_shouldReturnFalse() {
        ProductService spyProductService = Mockito.spy(productService);
        doReturn(null).when(spyProductService).getProductById(8L);

        boolean result = spyProductService.existsById(8L);

        assertThat(result).isFalse();
    }

    @Test
    void handleProductThumbnailFallback_whenThrowableProvided_shouldRethrowSameThrowable() {
        RuntimeException rootCause = new RuntimeException("fallback error");

        assertThatThrownBy(() -> productService.handleProductThumbnailFallback(rootCause))
            .isSameAs(rootCause);
    }

    private List<ProductThumbnailVm> getProductThumbnailVms() {

        ProductThumbnailVm product1 = new ProductThumbnailVm(
            1L,
            "Product 1",
            "product-1",
            "http://example.com/product1.jpg"
        );
        ProductThumbnailVm product2 = new ProductThumbnailVm(
            2L,
            "Product 2",
            "product-2",
            "http://example.com/product2.jpg"
        );
        ProductThumbnailVm product3 = new ProductThumbnailVm(
            3L,
            "Product 3",
            "product-3",
            "http://example.com/product3.jpg"
        );

        return List.of(product1, product2, product3);
    }
}
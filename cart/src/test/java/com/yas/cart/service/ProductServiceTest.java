package com.yas.cart.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.yas.cart.viewmodel.ProductThumbnailVm;
import com.yas.commonlibrary.config.ServiceUrlConfig;
import java.net.URI;
import java.util.Collections;
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
    void getProductById_whenProductsExist_shouldReturnFirstProduct() {
        ProductService productServiceSpy = Mockito.spy(productService);
        ProductThumbnailVm product = getProductThumbnailVms().getFirst();

        doReturn(List.of(product)).when(productServiceSpy).getProducts(List.of(product.id()));

        ProductThumbnailVm result = productServiceSpy.getProductById(product.id());

        assertThat(result).isEqualTo(product);
    }

    @Test
    void getProductById_whenProductsAreEmpty_shouldReturnNull() {
        ProductService productServiceSpy = Mockito.spy(productService);

        doReturn(Collections.emptyList()).when(productServiceSpy).getProducts(List.of(1L));

        ProductThumbnailVm result = productServiceSpy.getProductById(1L);

        assertThat(result).isNull();
    }

    @Test
    void existsById_whenProductExists_shouldReturnTrue() {
        ProductService productServiceSpy = Mockito.spy(productService);
        ProductThumbnailVm product = getProductThumbnailVms().getFirst();

        doReturn(List.of(product)).when(productServiceSpy).getProducts(List.of(product.id()));

        boolean result = productServiceSpy.existsById(product.id());

        assertThat(result).isTrue();
    }

    @Test
    void existsById_whenProductDoesNotExist_shouldReturnFalse() {
        ProductService productServiceSpy = Mockito.spy(productService);

        doReturn(Collections.emptyList()).when(productServiceSpy).getProducts(List.of(1L));

        boolean result = productServiceSpy.existsById(1L);

        assertThat(result).isFalse();
    }

    @Test
    void handleProductThumbnailFallback_shouldRethrowThrowable() {
        RuntimeException throwable = new RuntimeException("product service unavailable");

        assertThatThrownBy(() -> productService.handleProductThumbnailFallback(throwable))
            .isSameAs(throwable);
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

package com.yas.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import com.yas.cart.viewmodel.ProductThumbnailVm;
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
                .fromHttpUrl("http://api.yas.local/media")
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
    void getProducts_WhenEmptyList_ReturnEmptyList() {
        // Given
        List<Long> ids = Collections.emptyList();
        URI url = UriComponentsBuilder
                .fromHttpUrl("http://api.yas.local/media")
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
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        // When
        List<ProductThumbnailVm> result = productService.getProducts(ids);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getProductById_WhenProductExists_ReturnProduct() {
        // Given
        Long productId = 1L;
        ProductThumbnailVm expectedProduct = new ProductThumbnailVm(
                1L,
                "Product 1",
                "product-1",
                "http://example.com/product1.jpg");

        URI url = UriComponentsBuilder
                .fromHttpUrl("http://api.yas.local/media")
                .path("/storefront/products/list-featured")
                .queryParam("productId", List.of(productId))
                .build()
                .toUri();

        when(serviceUrlConfig.product()).thenReturn("http://api.yas.local/media");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(new ParameterizedTypeReference<List<ProductThumbnailVm>>() {
        }))
                .thenReturn(ResponseEntity.ok(List.of(expectedProduct)));

        // When
        ProductThumbnailVm result = productService.getProductById(productId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Product 1", result.name());
        assertEquals("product-1", result.slug());
    }

    @Test
    void getProductById_WhenProductNotFound_ReturnNull() {
        // Given
        Long productId = 999L;
        URI url = UriComponentsBuilder
                .fromHttpUrl("http://api.yas.local/media")
                .path("/storefront/products/list-featured")
                .queryParam("productId", List.of(productId))
                .build()
                .toUri();

        when(serviceUrlConfig.product()).thenReturn("http://api.yas.local/media");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(new ParameterizedTypeReference<List<ProductThumbnailVm>>() {
        }))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        // When
        ProductThumbnailVm result = productService.getProductById(productId);

        // Then
        assertNull(result);
    }

    @Test
    void getProductById_WhenProductListIsNull_ReturnNull() {
        // Given
        Long productId = 1L;
        URI url = UriComponentsBuilder
                .fromHttpUrl("http://api.yas.local/media")
                .path("/storefront/products/list-featured")
                .queryParam("productId", List.of(productId))
                .build()
                .toUri();

        when(serviceUrlConfig.product()).thenReturn("http://api.yas.local/media");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(new ParameterizedTypeReference<List<ProductThumbnailVm>>() {
        }))
                .thenReturn(ResponseEntity.ok(null));

        // When
        ProductThumbnailVm result = productService.getProductById(productId);

        // Then
        assertNull(result);
    }

    @Test
    void existsById_WhenProductExists_ReturnTrue() {
        // Given
        Long productId = 1L;
        ProductThumbnailVm expectedProduct = new ProductThumbnailVm(
                1L,
                "Product 1",
                "product-1",
                "http://example.com/product1.jpg");

        URI url = UriComponentsBuilder
                .fromHttpUrl("http://api.yas.local/media")
                .path("/storefront/products/list-featured")
                .queryParam("productId", List.of(productId))
                .build()
                .toUri();

        when(serviceUrlConfig.product()).thenReturn("http://api.yas.local/media");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(new ParameterizedTypeReference<List<ProductThumbnailVm>>() {
        }))
                .thenReturn(ResponseEntity.ok(List.of(expectedProduct)));

        // When
        boolean result = productService.existsById(productId);

        // Then
        assertTrue(result);
    }

    @Test
    void existsById_WhenProductNotFound_ReturnFalse() {
        // Given
        Long productId = 999L;
        URI url = UriComponentsBuilder
                .fromHttpUrl("http://api.yas.local/media")
                .path("/storefront/products/list-featured")
                .queryParam("productId", List.of(productId))
                .build()
                .toUri();

        when(serviceUrlConfig.product()).thenReturn("http://api.yas.local/media");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(new ParameterizedTypeReference<List<ProductThumbnailVm>>() {
        }))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        // When
        boolean result = productService.existsById(productId);

        // Then
        assertFalse(result);
    }

    @Test
    void handleProductThumbnailFallback_ShouldRethrowException() {
        // Given
        RuntimeException exception = new RuntimeException("Service unavailable");

        // When & Then
        assertThrows(Throwable.class, () -> productService.handleProductThumbnailFallback(exception));
    }

    private List<ProductThumbnailVm> getProductThumbnailVms() {

        ProductThumbnailVm product1 = new ProductThumbnailVm(
                1L,
                "Product 1",
                "product-1",
                "http://example.com/product1.jpg");
        ProductThumbnailVm product2 = new ProductThumbnailVm(
                2L,
                "Product 2",
                "product-2",
                "http://example.com/product2.jpg");
        ProductThumbnailVm product3 = new ProductThumbnailVm(
                3L,
                "Product 3",
                "product-3",
                "http://example.com/product3.jpg");

        return List.of(product1, product2, product3);
    }
}

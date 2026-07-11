package com.yas.cart.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import com.yas.cart.viewmodel.ProductThumbnailVm;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

    private static final String PRODUCT_BASE_URL = "http://api.yas.local/media";

    @BeforeEach
    void setUp() {
        restClient = Mockito.mock(RestClient.class);
        serviceUrlConfig = Mockito.mock(ServiceUrlConfig.class);
        productService = new ProductService(restClient, serviceUrlConfig);
        requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
    }

    private void mockRestClientFor(List<Long> ids, List<ProductThumbnailVm> responseBody) {
        URI url = UriComponentsBuilder
            .fromUriString(PRODUCT_BASE_URL)
            .path("/storefront/products/list-featured")
            .queryParam("productId", ids)
            .build()
            .toUri();

        when(serviceUrlConfig.product()).thenReturn(PRODUCT_BASE_URL);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(new ParameterizedTypeReference<List<ProductThumbnailVm>>() {
        })).thenReturn(ResponseEntity.ok(responseBody));
    }

    @Nested
    class GetProductsTest {

        @Test
        void getProducts_whenIdsProvided_shouldReturnProductThumbnailVms() {
            List<Long> ids = List.of(1L, 2L, 3L);
            mockRestClientFor(ids, getProductThumbnailVms());

            List<ProductThumbnailVm> result = productService.getProducts(ids);

            assertThat(result).hasSize(3);
            assertThat(result.get(0).id()).isEqualTo(1L);
            assertThat(result.get(1).id()).isEqualTo(2L);
            assertThat(result.get(2).id()).isEqualTo(3L);
        }
    }

    @Nested
    class GetProductByIdTest {

        @Test
        void getProductById_whenProductExists_shouldReturnProduct() {
            Long productId = 1L;
            ProductThumbnailVm expectedProduct = new ProductThumbnailVm(
                productId, "Product 1", "product-1", "http://example.com/product1.jpg"
            );
            mockRestClientFor(List.of(productId), List.of(expectedProduct));

            ProductThumbnailVm result = productService.getProductById(productId);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(productId);
            assertThat(result.name()).isEqualTo("Product 1");
        }

        @Test
        void getProductById_whenProductListIsEmpty_shouldReturnNull() {
            Long productId = 999L;
            mockRestClientFor(List.of(productId), List.of());

            ProductThumbnailVm result = productService.getProductById(productId);

            assertThat(result).isNull();
        }
    }

    @Nested
    class ExistsByIdTest {

        @Test
        void existsById_whenProductExists_shouldReturnTrue() {
            Long productId = 1L;
            ProductThumbnailVm product = new ProductThumbnailVm(
                productId, "Product 1", "product-1", "http://example.com/product1.jpg"
            );
            mockRestClientFor(List.of(productId), List.of(product));

            boolean result = productService.existsById(productId);

            assertThat(result).isTrue();
        }

        @Test
        void existsById_whenProductDoesNotExist_shouldReturnFalse() {
            Long productId = 999L;
            mockRestClientFor(List.of(productId), List.of());

            boolean result = productService.existsById(productId);

            assertThat(result).isFalse();
        }
    }

    private List<ProductThumbnailVm> getProductThumbnailVms() {
        return List.of(
            new ProductThumbnailVm(1L, "Product 1", "product-1", "http://example.com/product1.jpg"),
            new ProductThumbnailVm(2L, "Product 2", "product-2", "http://example.com/product2.jpg"),
            new ProductThumbnailVm(3L, "Product 3", "product-3", "http://example.com/product3.jpg")
        );
    }
}
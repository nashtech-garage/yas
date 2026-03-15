package com.yas.cart.service;


import static org.assertj.core.api.Assertions.assertThat;
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
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        Long productId = 1L;
        ProductThumbnailVm mockProduct = new ProductThumbnailVm(
            productId, "Product 1", "product-1", "http://example.com/product1.jpg"
        );

        when(serviceUrlConfig.product()).thenReturn("http://api.yas.local/product");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Mockito.any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        
        // Bí mật đã được bật mí: Ép kiểu Mock trả về một List chứa sản phẩm!
        when(responseSpec.toEntity(Mockito.<ParameterizedTypeReference<List<ProductThumbnailVm>>>any()))
            .thenReturn(ResponseEntity.ok(List.of(mockProduct)));

        ProductThumbnailVm result = productService.getProductById(productId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(productId);
    }

    @Test
    void existsById_WhenProductExists_ShouldReturnTrue() {
        Long productId = 1L;
        ProductThumbnailVm mockProduct = new ProductThumbnailVm(
            productId, "Product 1", "product-1", "http://example.com/product1.jpg"
        );

        when(serviceUrlConfig.product()).thenReturn("http://api.yas.local/product");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Mockito.any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        
        // Giả lập trả về một List có chứa sản phẩm (tức là sản phẩm CÓ tồn tại)
        when(responseSpec.toEntity(Mockito.<ParameterizedTypeReference<List<ProductThumbnailVm>>>any()))
            .thenReturn(ResponseEntity.ok(List.of(mockProduct)));

        boolean exists = productService.existsById(productId);

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_WhenProductDoesNotExist_ShouldReturnFalse() {
        Long productId = 99L;

        when(serviceUrlConfig.product()).thenReturn("http://api.yas.local/product");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Mockito.any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        
        // Giả lập trả về một List RỖNG (tức là KHÔNG TÌM THẤY sản phẩm nào)
        when(responseSpec.toEntity(Mockito.<ParameterizedTypeReference<List<ProductThumbnailVm>>>any()))
            .thenReturn(ResponseEntity.ok(List.of()));

        boolean exists = productService.existsById(productId);

        assertThat(exists).isFalse();
    }

    @Test
    void handleProductThumbnailFallback_ShouldThrowException() {
        // Sửa lại: Bắt lỗi thay vì assert kết quả trả về
        Throwable mockException = new RuntimeException("Circuit Breaker Fallback Triggered");
        
        // Vì hàm fallback thực tế ném lỗi ra ngoài, ta dùng assertThrows để kiểm tra
        org.junit.jupiter.api.Assertions.assertThrows(Throwable.class, () -> {
            productService.handleProductThumbnailFallback(mockException);
        });
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
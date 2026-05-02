package com.yas.cart.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.cart.viewmodel.ProductThumbnailVm;
import com.yas.commonlibrary.config.ServiceUrlConfig;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        // Mock chain for restClient.get().uri(url).retrieve().toEntity(...)
    }

    @Test
    void getProducts_returnsList() {
        when(serviceUrlConfig.product()).thenReturn("http://api/product");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.net.URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        
        List<ProductThumbnailVm> expectedProducts = List.of(new ProductThumbnailVm(1L, "p1", "t1"));
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(expectedProducts));

        List<ProductThumbnailVm> result = productService.getProducts(List.of(1L));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.containsAll(expectedProducts));
    }

    @Test
    void existsById_ReturnsTrue() {
        when(serviceUrlConfig.product()).thenReturn("http://api/product");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.net.URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        
        List<ProductThumbnailVm> expectedProducts = List.of(new ProductThumbnailVm(1L, "p1", "t1"));
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(expectedProducts));

        boolean result = productService.existsById(1L);
        assertTrue(result);
    }
    
    @Test
    void existsById_ReturnsFalse() {
        when(serviceUrlConfig.product()).thenReturn("http://api/product");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.net.URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(List.of()));

        boolean result = productService.existsById(1L);
        assertFalse(result);
    }
}

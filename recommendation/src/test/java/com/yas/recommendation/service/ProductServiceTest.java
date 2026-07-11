package com.yas.recommendation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.recommendation.configuration.RecommendationConfig;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

class ProductServiceTest {

    private RestClient restClient;
    private RecommendationConfig config;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        config = mock(RecommendationConfig.class);
        productService = new ProductService(restClient, config);
    }

    @Test
    void getProductDetail_whenCalled_returnProductDetail() {
        long productId = 1L;
        String apiUrl = "http://api.yas.local";
        ProductDetailVm expectedProduct = new ProductDetailVm(
            productId, "Product Name", null, null, null, null, null, "slug",
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        when(config.getApiUrl()).thenReturn(apiUrl);

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(expectedProduct));

        ProductDetailVm actualProduct = productService.getProductDetail(productId);

        assertEquals(expectedProduct, actualProduct);
    }
}

package com.yas.recommendation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.recommendation.configuration.RecommendationConfig;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

class ProductServiceTest {

    @Test
    void getProductDetail_shouldCallConfiguredProductApiAndReturnBody() {
        RestClient restClient = org.mockito.Mockito.mock(RestClient.class);
        RestClient.RequestHeadersUriSpec requestSpec = org.mockito.Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = org.mockito.Mockito.mock(RestClient.ResponseSpec.class);
        RecommendationConfig config = new RecommendationConfig();
        ReflectionTestUtils.setField(config, "apiUrl", "http://product-service");
        ProductDetailVm expected = new ProductDetailVm(
                31L, "Phone", null, null, null, null, null, null, true, true,
                false, true, true, 100D, null, null, null, null, null, null,
                null, null, null, null, null
        );

        when(restClient.get()).thenReturn(requestSpec);
        when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
        when(requestSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(expected));

        ProductDetailVm result = new ProductService(restClient, config).getProductDetail(31L);

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        verify(requestSpec).uri(uriCaptor.capture());
        assertThat(uriCaptor.getValue().toString()).isEqualTo("http://product-service/storefront/products/detail/31");
        assertThat(result).isSameAs(expected);
    }
}

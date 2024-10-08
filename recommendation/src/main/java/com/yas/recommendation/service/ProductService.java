package com.yas.recommendation.service;

import com.yas.recommendation.configuration.RecommendationConfig;
import com.yas.recommendation.dto.ProductDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final RestClient restClient;
    private final RecommendationConfig config;

    public ProductDetailDTO getProductDetail(long productId) {
        final URI url = UriComponentsBuilder
                .fromHttpUrl(config.getApiUrl())
                .path("/storefront/products/detail/" + productId)
                .buildAndExpand()
                .toUri();

        return restClient.get()
                .uri(url)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ProductDetailDTO>() {
                })
                .getBody();
    }
}

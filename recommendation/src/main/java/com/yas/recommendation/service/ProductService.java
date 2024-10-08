package com.yas.recommendation.service;

import com.yas.recommendation.configuration.RecommendationConfig;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Service class responsible for handling product-related operations and interacting with external services.
 */

@Service
@RequiredArgsConstructor
public class ProductService {
    private final RestClient restClient;
    private final RecommendationConfig config;

    /**
     * Retrieves detailed information about a product from an external API using the product's unique identifier.
     *
     * @param productId the unique identifier of the product to fetch details for
     * @return a {@link ProductDetailVm} containing the detailed product information retrieved from the external API
     */
    public ProductDetailVm getProductDetail(long productId) {
        final URI url = UriComponentsBuilder
                .fromHttpUrl(config.getApiUrl())
                .path("/storefront/products/detail/" + productId)
                .buildAndExpand()
                .toUri();

        return restClient.get()
                .uri(url)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ProductDetailVm>() {
                })
                .getBody();
    }
}

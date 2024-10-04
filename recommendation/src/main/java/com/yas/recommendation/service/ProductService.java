package com.yas.recommendation.service;

import com.yas.recommendation.configuration.RecommendationConfig;
import com.yas.recommendation.dto.ProductDetailDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
//        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
//                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(config.getApiUrl())
                .path("/storefront/products/detail/" + productId)
                .buildAndExpand()
                .toUri();

        return restClient.get()
                .uri(url)
//                .headers(h -> h.setBearerAuth(jwt))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ProductDetailDTO>() {
                })
                .getBody();
    }

    public String buildFormattedProduct(ProductDetailDTO productDetail) {
        return StringUtils.EMPTY;
    }
}

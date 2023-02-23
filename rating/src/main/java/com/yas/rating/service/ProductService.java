package com.yas.rating.service;

import com.yas.rating.config.ServiceUrlConfig;
import com.yas.rating.viewmodel.ProductThumbnailVm;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class ProductService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public ProductService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

    public ProductThumbnailVm getProductById(Long productId) {
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.product())
                .path("/storefront/products/featured/{productId}")
                .buildAndExpand(productId)
                .toUri();
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(ProductThumbnailVm.class)
                .block();
    }
}

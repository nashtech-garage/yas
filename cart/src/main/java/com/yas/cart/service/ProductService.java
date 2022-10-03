package com.yas.cart.service;

import java.net.URI;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.yas.cart.config.ServiceUrlConfig;
import com.yas.cart.viewmodel.ProductThumbnailVm;

@Service
public class ProductService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public ProductService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

    public ProductThumbnailVm getProduct(Long id) {
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.product())
                .path("/storefront/products/featured/{productId}")
                .buildAndExpand(id)
                .toUri();
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(ProductThumbnailVm.class)
                .block();
    }
}

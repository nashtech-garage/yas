package com.yas.cart.service;

import java.net.URI;
import java.util.List;

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

    public List<ProductThumbnailVm> getProducts(List<Long> ids) {
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.product())
                .path("/storefront/products/list-featured")
                .queryParam("productId", ids)
                .build()
                .toUri();
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(ProductThumbnailVm.class)
                .collectList()
                .block();
    }
}

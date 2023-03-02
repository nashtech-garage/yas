package com.yas.rating.service;

import com.yas.rating.config.ServiceUrlConfig;
import com.yas.rating.exception.NotFoundException;
import com.yas.rating.utils.Constants;
import com.yas.rating.viewmodel.ErrorVm;
import com.yas.rating.viewmodel.ProductThumbnailVm;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

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
                .onStatus(
                        HttpStatus.NOT_FOUND::equals,
                        response -> response.bodyToMono(String.class).map(NotFoundException::new))
                .bodyToMono(ProductThumbnailVm.class)
                .block();
    }
}

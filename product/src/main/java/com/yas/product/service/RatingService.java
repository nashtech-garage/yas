package com.yas.product.service;

import com.yas.product.config.ServiceUrlConfig;
import com.yas.product.exception.NotFoundException;
import com.yas.product.viewmodel.NoFileMediaVm;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class RatingService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public RatingService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }
    public Double getAverageStar(Long productId) {
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.rating())
                .path("/storefront/ratings/product/{productId}/average-star")
                .buildAndExpand(productId)
                .toUri();
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Double.class)
                .block();
    }

}

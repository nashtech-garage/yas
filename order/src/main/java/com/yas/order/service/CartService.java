package com.yas.order.service;

import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.exception.BadRequestException;
import com.yas.order.exception.NotFoundException;
import com.yas.order.viewmodel.ResponeStatusVm;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@Service
public class CartService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public CartService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

    public ResponeStatusVm addOrderIdIntoCart(Long orderId) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.cart())
                .path("/order-id-additional")
                .buildAndExpand()
                .toUri();
        return webClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(orderId)
                .headers(h -> h.setBearerAuth(jwt))
                .retrieve()
                .onStatus(
                        HttpStatus.BAD_REQUEST::equals,
                        response -> response.bodyToMono(String.class).map(BadRequestException::new))
                .onStatus(
                        HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        response -> response.bodyToMono(String.class).map(BadRequestException::new))
                .onStatus(
                        HttpStatus.NOT_FOUND::equals,
                        response -> response.bodyToMono(String.class).map(NotFoundException::new))
                .bodyToMono(ResponeStatusVm.class)
                .block();
    }
}
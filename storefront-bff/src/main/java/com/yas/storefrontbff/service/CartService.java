package com.yas.storefrontbff.service;

import com.yas.storefrontbff.config.ServiceUrlConfig;
import com.yas.storefrontbff.viewmodel.CartGetDetailVm;
import com.yas.storefrontbff.viewmodel.CartItemVm;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Service
public class CartService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public CartService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

    public Mono<Void> moveGuestCartToUserCart(String userToken, String guestToken) {
        return getGuestCart(guestToken).flatMap(cartGetDetailVm -> addToUserCart(userToken, cartGetDetailVm)).then();
    }

    private Mono<CartGetDetailVm> addToUserCart(String userToken, CartGetDetailVm cartGetDetailVm) {
        List<CartItemVm> cartItemVms = cartGetDetailVm.cartDetails()
                .stream()
                .map(CartItemVm::fromCartDetailVm)
                .toList();
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.services().get("cart"))
                .path("/storefront/carts").build().toUri();

        return webClient.post()
                .uri(url)
                .headers(h -> h.setBearerAuth(userToken))
                .body(BodyInserters.fromValue(cartItemVms))
                .retrieve()
                .bodyToMono(CartGetDetailVm.class);
    }

    private Mono<CartGetDetailVm> getGuestCart(String guestToken) {
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.services().get("cart"))
                .path("/storefront/cart").build().toUri();

        return webClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(guestToken))
                .retrieve()
                .bodyToMono(CartGetDetailVm.class);
    }
}

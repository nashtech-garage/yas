package com.yas.storefrontbff.controller;

import com.yas.storefrontbff.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedHashMap;

@RestController
public class CartController {
    private final CartService cartService;

    private final ReactiveOAuth2AuthorizedClientService authorizedClientService;
    private static final String GUEST_INFO_KEY = "GUEST_INFOMATION";

    public CartController(CartService cartService, ReactiveOAuth2AuthorizedClientService authorizedClientService) {
        this.cartService = cartService;
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/move-cart")
    public Mono<Void> moveGuestCartToUserCart(@AuthenticationPrincipal OAuth2User principal, WebSession session) {
        if (principal != null && session.getAttributes().containsKey(GUEST_INFO_KEY)) {
            HashMap<String, String> guestInfoFromCookie =
                    new LinkedHashMap<>((LinkedHashMap<String, String>) session.getAttributes().get(GUEST_INFO_KEY));
            return authorizedClientService.loadAuthorizedClient("keycloak", principal.getName())
                    .map(OAuth2AuthorizedClient::getAccessToken)
                    .map(OAuth2AccessToken::getTokenValue)
                    .flatMap(userToken -> cartService.moveGuestCartToUserCart(userToken, guestInfoFromCookie.get("accessToken")))
                    .doOnSuccess(s -> session.getAttributes().remove(GUEST_INFO_KEY));
        }
        return Mono.empty();
    }
}

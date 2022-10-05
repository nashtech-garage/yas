package com.yas.storefrontbff.filter;

import com.yas.storefrontbff.config.ServiceUrlConfig;
import com.yas.storefrontbff.viewmodel.GuestUserVm;
import com.yas.storefrontbff.viewmodel.TokenResponseVm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class CartAuthenticationFilter implements WebFilter {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;
    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    public CartAuthenticationFilter(WebClient webClient,
                                    ServiceUrlConfig serviceUrlConfig,
                                    ReactiveClientRegistrationRepository clientRegistrationRepository) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        if (request.getPath().toString().contains("/api/cart/storefront/carts")
                && Objects.equals(request.getMethod(), HttpMethod.POST)) {
            AtomicBoolean isAuthenticated = new AtomicBoolean(false);

            return ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .map(Authentication::getPrincipal)
                    .doOnNext(principal -> {
                        isAuthenticated.set(true);
                    })
                    .doOnError(Throwable::printStackTrace)
                    .then(Mono.defer(() -> {
                        if (!isAuthenticated.get()) {
                            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                            ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("keycloak").block();
                            formData.add("grant_type", "client_credentials");
                            formData.add("client_id", clientRegistration.getClientId());
                            formData.add("client_secret", clientRegistration.getClientSecret());

                            return getToken(formData).map(clientToken -> createGuestUser(clientToken.access_token())
                                    .map(createdUserVm -> {
                                        formData.remove("grant_type");
                                        formData.add("grant_type", "password");
                                        formData.add("username", createdUserVm.email());
                                        formData.add("password", createdUserVm.password());
                                        return getToken(formData).doOnNext(userToken -> {
                                            exchange
                                                    .getRequest()
                                                    .mutate()
                                                    .headers(h -> h.setBearerAuth(userToken.access_token()))
                                                    .build();
                                        }).thenReturn(exchange);
                                    })).flatMap(monox3Void -> monox3Void.flatMap(monox2Void -> monox2Void.flatMap(chain::filter)));
                        }
                        return chain.filter(exchange);
                    }));
        }
        return chain.filter(exchange);
    }

    private Mono<GuestUserVm> createGuestUser(String bearerToken) {
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.services().get("customer"))
                .path("/storefront/customer/guest-user").build().toUri();
        return webClient.post()
                .uri(url)
                .headers(h -> h.setBearerAuth(bearerToken))
                .retrieve()
                .bodyToMono(GuestUserVm.class);
    }

    private Mono<TokenResponseVm> getToken(MultiValueMap<String, String> formData) {
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.services().get("token-identity")).build().toUri();
        return webClient.post()
                .uri(url)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(TokenResponseVm.class);
    }
}

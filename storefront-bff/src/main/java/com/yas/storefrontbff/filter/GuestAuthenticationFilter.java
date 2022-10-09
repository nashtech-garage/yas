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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class GuestAuthenticationFilter implements WebFilter {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;
    private final ReactiveClientRegistrationRepository clientRegistrationRepository;
    private static final String GRANT_TYPE = "grant_type";
    private static final String URL_API_POST_CART = "/api/cart/storefront/carts";
    private static final String GUEST_INFO_KEY = "GUEST_INFOMATION";

    public GuestAuthenticationFilter(WebClient webClient,
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
        if (request.getPath().toString().contains(URL_API_POST_CART)
                && Objects.equals(request.getMethod(), HttpMethod.POST)) {
            AtomicBoolean isAuthenticated = new AtomicBoolean(false);
            AtomicBoolean isAuthenticatedAsGuest = new AtomicBoolean(false);
            HashMap<String, String> guestInfoFromCookie = new LinkedHashMap<>();

            exchange.getSession().subscribe(session -> {
                if (session.getAttributes().containsKey(GUEST_INFO_KEY)) {
                    isAuthenticatedAsGuest.set(true);
                    guestInfoFromCookie.putAll((LinkedHashMap<String, String>) session.getAttributes().get(GUEST_INFO_KEY));
                }
            });

            return ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .map(Authentication::getPrincipal)
                    .doOnNext(principal -> isAuthenticated.set(true))
                    .doOnError(Throwable::printStackTrace)
                    .then(Mono.defer(() -> {
                        if (isAuthenticated.get()) {
                            return chain.filter(exchange);
                        } else if (isAuthenticatedAsGuest.get()) {
                            exchange.getRequest()
                                    .mutate()
                                    .headers(h -> h.setBearerAuth(guestInfoFromCookie.get("accessToken")))
                                    .build();
                            return chain.filter(exchange);
                        } else {
                            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                            formData.add(GRANT_TYPE, "client_credentials");
                            clientRegistrationRepository.findByRegistrationId("keycloak").subscribe(clientRegistration -> {
                                formData.add("client_id", clientRegistration.getClientId());
                                formData.add("client_secret", clientRegistration.getClientSecret());
                            });

                            return getToken(formData).map(clientToken -> createGuestUser(clientToken.access_token())
                                    .map(createdUserVm -> {
                                        formData.remove(GRANT_TYPE);
                                        formData.add(GRANT_TYPE, "password");
                                        formData.add("username", createdUserVm.email());
                                        formData.add("password", createdUserVm.password());

                                        return getToken(formData).map(userToken -> {
                                            HashMap<String, String> guestInfo = new LinkedHashMap<>();
                                            guestInfo.put("accessToken", userToken.access_token());
                                            guestInfo.put("userId", createdUserVm.userId());
                                            guestInfo.put("email", createdUserVm.email());
                                            guestInfo.put("password", createdUserVm.password());

                                            return exchange.getSession().map(session -> {
                                                session.getAttributes().put(GUEST_INFO_KEY, guestInfo);
                                                request.mutate().headers(h -> h.setBearerAuth(userToken.access_token())).build();

                                                return exchange.mutate().request(request).build();
                                            });
                                        });
                                    })).flatMap(monox4Void -> monox4Void.flatMap(monox3Void -> monox3Void.flatMap(monox2Void -> monox2Void.flatMap(chain::filter))));
                        }
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

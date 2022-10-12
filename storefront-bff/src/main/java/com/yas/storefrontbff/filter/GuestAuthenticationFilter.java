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
    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String PASSWORD_KEY = "password";
    private static final String USERNAME_KEY = "username";
    private static final String USER_ID_KEY = "userId";
    private static final String EMAIL_KEY = "email";

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
                                    .headers(h -> h.setBearerAuth(guestInfoFromCookie.get(ACCESS_TOKEN_KEY)))
                                    .build();
                            return chain.filter(exchange);
                        } else {
                            return mutateServerWebExchange(exchange, chain);
                        }
                    }));
        }
        return chain.filter(exchange);
    }

    private Mono<Void> mutateServerWebExchange(ServerWebExchange exchange, WebFilterChain chain) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        HashMap<String, String> guestInfoCookie = new LinkedHashMap<>();

        return createFormDataGetClientToken(formData)
                .flatMap(this::getToken)
                .map(TokenResponseVm::access_token)
                .flatMap(this::createGuestUser)
                .map(createdUserVm -> createFormDataGetGuestToken(formData, createdUserVm, guestInfoCookie))
                .flatMap(this::getToken)
                .map(TokenResponseVm::access_token)
                .map(guestToken -> addGuestTokenToGuestInfoCookie(guestToken, guestInfoCookie))
                .flatMap(guestInfo -> addGuestInfoToCookie(exchange, chain, guestInfo));
    }

    private Mono<MultiValueMap<String, String>> createFormDataGetClientToken(MultiValueMap<String, String> formData) {
        formData.add(GRANT_TYPE, "client_credentials");
        return clientRegistrationRepository.findByRegistrationId("keycloak").map(clientRegistration -> {
            formData.add("client_id", clientRegistration.getClientId());
            formData.add("client_secret", clientRegistration.getClientSecret());
            return formData;
        });
    }

    private MultiValueMap<String, String> createFormDataGetGuestToken(MultiValueMap<String, String> formData,
                                                                      GuestUserVm createdUserVm, HashMap<String, String> guestInfoCookie) {
        guestInfoCookie.put(USER_ID_KEY, createdUserVm.userId());
        guestInfoCookie.put(EMAIL_KEY, createdUserVm.email());
        guestInfoCookie.put(PASSWORD_KEY, createdUserVm.password());

        formData.remove(GRANT_TYPE);
        formData.add(GRANT_TYPE, PASSWORD_KEY);
        formData.add(USERNAME_KEY, createdUserVm.email());
        formData.add(PASSWORD_KEY, createdUserVm.password());

        return formData;
    }

    private HashMap<String, String> addGuestTokenToGuestInfoCookie(String guestToken, HashMap<String, String> guestInfoCookie) {
        guestInfoCookie.put(ACCESS_TOKEN_KEY, guestToken);
        return guestInfoCookie;
    }

    private Mono<Void> addGuestInfoToCookie(ServerWebExchange exchange, WebFilterChain chain,
                                            HashMap<String, String> guestInfo) {
        return exchange.getSession().flatMap(session -> {
            session.getAttributes().put(GUEST_INFO_KEY, guestInfo);
            exchange.getRequest().mutate().headers(h -> h.setBearerAuth(guestInfo.get(ACCESS_TOKEN_KEY))).build();
            return chain.filter(exchange);
        }).then();
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

package com.yas.order.service;

import com.yas.order.config.ServiceUrlConfig;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class TaxService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public TaxService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

    public Double getTaxPercentByAddress(Long taxClassId, Long countryId, Long stateOrProvinceId, String zipCode) {
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.tax()).path("/backoffice/tax-rates/tax-percent")
                .queryParam("taxClassId", taxClassId)
                .queryParam("countryId", countryId)
                .queryParam("stateOrProvinceId", stateOrProvinceId)
                .queryParam("zipCode", zipCode)
                .build().toUri();

        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        return webClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .retrieve()
                .bodyToMono(Double.class)
                .block();
    }
}

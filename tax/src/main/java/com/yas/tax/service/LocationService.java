package com.yas.tax.service;

import com.yas.tax.config.ServiceUrlConfig;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.net.URI;

@Service
public class LocationService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public LocationService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

    public List<StateOrProvinceAndCountryGetNameVm> getStateOrProvinceAndCountryNames(List<Long> stateOrProvinceIds) {
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.location()).path("/backoffice/state-or-provinces/state-country-names").queryParam("stateOrProvinceIds", stateOrProvinceIds).build().toUri();
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        return webClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<StateOrProvinceAndCountryGetNameVm>>() {
                })
                .block();
    }
}

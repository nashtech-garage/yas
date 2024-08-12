package com.yas.order.service;

import com.yas.order.config.ServiceUrlConfig;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class TaxService {
    private final RestClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public Double getTaxPercentByAddress(Long taxClassId, Long countryId, Long stateOrProvinceId, String zipCode) {
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.tax())
            .path("/backoffice/tax-rates/tax-percent")
                .queryParam("taxClassId", taxClassId)
                .queryParam("countryId", countryId)
                .queryParam("stateOrProvinceId", stateOrProvinceId)
                .queryParam("zipCode", zipCode)
                .build().toUri();

        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getTokenValue();
        return webClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .retrieve()
                .body(Double.class);
    }
}

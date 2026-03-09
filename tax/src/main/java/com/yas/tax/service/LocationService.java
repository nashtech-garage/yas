package com.yas.tax.service;

import com.yas.tax.config.ServiceUrlConfig;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class LocationService extends AbstractCircuitBreakFallbackHandler {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleLocationNameListFallback")
    public List<StateOrProvinceAndCountryGetNameVm> getStateOrProvinceAndCountryNames(List<Long> stateOrProvinceIds) {
        final URI url = UriComponentsBuilder.fromUriString(serviceUrlConfig.location())
            .path("/backoffice/state-or-provinces/state-country-names")
            .queryParam("stateOrProvinceIds", stateOrProvinceIds).build().toUri();
        final String jwt =
            ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        return restClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .body(new ParameterizedTypeReference<List<StateOrProvinceAndCountryGetNameVm>>() {
            });
    }

    protected List<StateOrProvinceAndCountryGetNameVm> handleLocationNameListFallback(Throwable throwable)
        throws Throwable {
        return handleTypedFallback(throwable);
    }
}

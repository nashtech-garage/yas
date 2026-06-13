package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.tax.config.ServiceUrlConfig;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private LocationService locationService;

    @BeforeEach
    void setUp() {
        locationService = new LocationService(restClient, serviceUrlConfig);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void getStateOrProvinceAndCountryNames_ShouldReturnList() {
        List<StateOrProvinceAndCountryGetNameVm> mockResponse = List.of(
            new StateOrProvinceAndCountryGetNameVm(1L, "Hanoi", "Vietnam"),
            new StateOrProvinceAndCountryGetNameVm(2L, "New York", "USA")
        );

        when(serviceUrlConfig.location()).thenReturn("http://api.yas.local");
        URI uri = UriComponentsBuilder.fromUriString("http://api.yas.local")
            .path("/backoffice/state-or-provinces/state-country-names")
            .queryParam("stateOrProvinceIds", List.of(1L, 2L))
            .build()
            .toUri();

        setUpSecurityContext("test-token");

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(uri)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body((ParameterizedTypeReference<List<StateOrProvinceAndCountryGetNameVm>>) any(ParameterizedTypeReference.class)))
            .thenReturn(mockResponse);

        List<StateOrProvinceAndCountryGetNameVm> result = locationService.getStateOrProvinceAndCountryNames(List.of(1L, 2L));

        assertThat(result).containsExactlyElementsOf(mockResponse);
    }

    @Test
    void handleLocationNameListFallback_ShouldThrowOriginalException() {
        Throwable throwable = new RuntimeException("Service Down");
        RuntimeException thrown = assertThrows(RuntimeException.class,
            () -> locationService.handleLocationNameListFallback(throwable));
        assertThat(thrown).hasMessage("Service Down");
    }

    private void setUpSecurityContext(String token) {
        Jwt jwt = mock(Jwt.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(jwt.getTokenValue()).thenReturn(token);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
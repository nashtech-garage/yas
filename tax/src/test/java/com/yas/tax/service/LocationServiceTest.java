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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @InjectMocks
    private LocationService locationService;

    @Test
    void getStateOrProvinceAndCountryNames_ShouldReturnList() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn("mocked-token");
        SecurityContextHolder.setContext(securityContext);

        when(serviceUrlConfig.location()).thenReturn("http://localhost:8080");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        List<StateOrProvinceAndCountryGetNameVm> expected = List.of(
            new StateOrProvinceAndCountryGetNameVm(1L, "State", "Country")
        );
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expected);

        List<StateOrProvinceAndCountryGetNameVm> result = locationService.getStateOrProvinceAndCountryNames(List.of(1L));
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().stateOrProvinceName()).isEqualTo("State");
    }

    @Test
    void handleLocationNameListFallback_ShouldThrowException() {
        Throwable throwable = new RuntimeException("Error occurred");
        assertThrows(Throwable.class, () -> locationService.handleLocationNameListFallback(throwable));
    }
}

package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.yas.tax.config.ServiceUrlConfig;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
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
    private ServiceUrlConfig serviceUrlConfig;

    @InjectMocks
    private LocationService locationService;

    @BeforeEach
    void setUp() {
        // Giả lập Security Context chứa JWT Token
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        lenient().when(jwt.getTokenValue()).thenReturn("mock-jwt-token");
        lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @SuppressWarnings("unchecked")
    void getStateOrProvinceAndCountryNames_ShouldReturnList() {
        when(serviceUrlConfig.location()).thenReturn("http://api.location.com");

        // Mock chuỗi gọi RestClient (Deep Mocking thủ công)
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        StateOrProvinceAndCountryGetNameVm mockVm = mock(StateOrProvinceAndCountryGetNameVm.class);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(List.of(mockVm));

        List<StateOrProvinceAndCountryGetNameVm> result = locationService.getStateOrProvinceAndCountryNames(List.of(1L, 2L));

        assertThat(result).hasSize(1);
    }

    @Test
    void handleLocationNameListFallback_ShouldThrowException() {
        Throwable exception = new RuntimeException("Circuit breaker open");
        assertThrows(RuntimeException.class, () -> locationService.handleLocationNameListFallback(exception));
    }
}
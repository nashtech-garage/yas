package com.yas.rating.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.rating.config.ServiceUrlConfig;
import com.yas.rating.viewmodel.CustomerVm;
import java.net.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;

class CustomerServiceTest {

    private RestClient restClient;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        ServiceUrlConfig serviceUrlConfig = new ServiceUrlConfig("http://product-service", "http://customer-service",
                "http://order-service");
        customerService = new CustomerService(restClient, serviceUrlConfig);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCustomer_ShouldReturnCustomerVm() {
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn("token");
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        CustomerVm expectedCustomer = new CustomerVm("user1", "user1@example.com", "John", "Doe");

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.headers(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CustomerVm.class)).thenReturn(expectedCustomer);

        CustomerVm actualCustomer = customerService.getCustomer();

        assertEquals(expectedCustomer, actualCustomer);
    }

    @Test
    void handleFallback_ShouldReturnNull() throws Throwable {
        Object result = customerService.handleFallback(new RuntimeException("boom"));

        assertNull(result);
    }
}

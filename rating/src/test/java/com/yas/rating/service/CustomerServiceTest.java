package com.yas.rating.service;

import static com.yas.rating.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.rating.config.ServiceUrlConfig;
import com.yas.rating.viewmodel.CustomerVm;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

class CustomerServiceTest {

    private RestClient restClient;
    private ServiceUrlConfig serviceUrlConfig;
    private CustomerService customerService;
    private RestClient.ResponseSpec responseSpec;

    private static final String CUSTOMER_URL = "http://api.yas.local/customer";

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        customerService = new CustomerService(restClient, serviceUrlConfig);
        responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCustomer_whenValidJwt_returnsCustomerVm() {
        when(serviceUrlConfig.customer()).thenReturn(CUSTOMER_URL);
        URI uri = UriComponentsBuilder.fromUriString(CUSTOMER_URL)
            .path("/storefront/customer/profile")
            .build()
            .toUri();

        setUpSecurityContext("testUser");

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(uri)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        CustomerVm expectedCustomer = new CustomerVm("testUser", "test@example.com", "Test", "User");
        when(responseSpec.body(CustomerVm.class)).thenReturn(expectedCustomer);

        CustomerVm result = customerService.getCustomer();

        assertThat(result).isEqualTo(expectedCustomer);
    }

    @Test
    void testHandleFallback_returnsNull() throws Throwable {
        CustomerVm result = customerService.handleFallback(new RuntimeException("Test Error"));
        assertThat(result).isNull();
    }
}

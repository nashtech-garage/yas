package com.yas.rating.service;

import static com.yas.rating.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.rating.config.ServiceUrlConfig;
import com.yas.rating.viewmodel.CustomerVm;
import java.net.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCustomer_whenNormalCase_returnCustomerVm() {
        when(serviceUrlConfig.customer()).thenReturn(CUSTOMER_URL);
        URI url = UriComponentsBuilder
            .fromUriString(serviceUrlConfig.customer())
            .path("/storefront/customer/profile")
            .buildAndExpand()
            .toUri();

        setUpSecurityContext("test");
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        CustomerVm customerVm = new CustomerVm("user1", "email", "firstName", "lastName");
        when(responseSpec.body(CustomerVm.class)).thenReturn(customerVm);

        CustomerVm result = customerService.getCustomer();
        assertThat(result.username()).isEqualTo("user1");
    }

    @Test
    void handleFallback_whenCalled_returnNull() throws Throwable {
        CustomerVm result = customerService.handleFallback(mock(Throwable.class));
        assertThat(result).isNull();
    }
}

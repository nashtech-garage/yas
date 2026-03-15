package com.yas.rating.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.rating.config.ServiceUrlConfig;
import com.yas.rating.viewmodel.CustomerVm;
import java.net.URI;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @SuppressWarnings("unchecked")
    void getCustomer_ShouldReturnCustomerVm() {
        // Đã chuyển phần Setup Security Context vào thẳng hàm test này
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        when(jwt.getTokenValue()).thenReturn("mock-jwt-token");
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(serviceUrlConfig.customer()).thenReturn("http://localhost:8080");

        CustomerVm expectedCustomer = mock(CustomerVm.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);

        when(requestHeadersSpec.headers(any(Consumer.class))).thenAnswer(invocation -> {
            Consumer<HttpHeaders> headersConsumer = invocation.getArgument(0);
            HttpHeaders headers = new HttpHeaders();
            headersConsumer.accept(headers); 
            assertEquals("Bearer mock-jwt-token", headers.getFirst(HttpHeaders.AUTHORIZATION));
            return requestHeadersSpec;
        });

        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CustomerVm.class)).thenReturn(expectedCustomer);

        CustomerVm actualCustomer = customerService.getCustomer();

        assertEquals(expectedCustomer, actualCustomer);
    }

    @Test
    void handleFallback_ShouldReturnNull() throws Throwable {
        // Test case này giờ đây sạch sẽ, không còn vướng víu rác setup nữa
        CustomerVm result = customerService.handleFallback(new RuntimeException("Test Exception"));
        assertNull(result);
    }
}
package com.yas.paymentpaypal.service;

import static com.yas.paymentpaypal.utils.SecurityContextUtils.setUpSecurityContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.paymentpaypal.config.ServiceUrlConfig;
import java.net.URI;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

public class OrderServiceTest {
    private RestClient restClient;

    private ServiceUrlConfig serviceUrlConfig;

    private OrderService orderService;

    private RestClient.ResponseSpec responseSpec;

    private static final String ORDER_URL = "http://api.yas.local/order";

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        orderService = new OrderService(restClient, serviceUrlConfig);
        responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        setUpSecurityContext("test");
        when(serviceUrlConfig.order()).thenReturn(ORDER_URL);
    }

    @Test
    void testGetOrderByCheckoutId_ifNormalCase_returnOrderVm() {
        String checkoutId = UUID.randomUUID().toString();

        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.order())
            .path("/storefront/orders/checkout/" + checkoutId)
            .buildAndExpand()
            .toUri();

        RestClient.RequestHeadersUriSpec requestBodyUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);

        orderService.getOrderByCheckoutId(checkoutId);

        verify(restClient, times(1)).get();
    }
}

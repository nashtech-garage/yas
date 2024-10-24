package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.model.enumeration.PaymentMethod;
import com.yas.order.viewmodel.payment.CheckoutPaymentVm;
import java.math.BigDecimal;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

class PaymentServiceTest {

    private RestClient restClient;

    private ServiceUrlConfig serviceUrlConfig;

    private PaymentService paymentService;

    private RestClient.ResponseSpec responseSpec;

    private static final String PAYMENT_URL = "http://api.yas.local/payment";

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        paymentService = new PaymentService(restClient, serviceUrlConfig);
        responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        setUpSecurityContext("test");
        when(serviceUrlConfig.payment()).thenReturn(PAYMENT_URL);
    }

    @Test
    void testCreatePaymentFromEvent_ifNormalCase_returnLong() {

        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.payment())
            .path("/events/payments")
            .buildAndExpand()
            .toUri();

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodyUriSpec);

        CheckoutPaymentVm checkoutPaymentRequestDto = new CheckoutPaymentVm(
            "123",
            PaymentMethod.PAYPAL,
            new BigDecimal(12)
        );

        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(checkoutPaymentRequestDto)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Long.class)).thenReturn(1L);

        Long result = paymentService.createPaymentFromEvent(checkoutPaymentRequestDto);

        assertNotNull(result);
        assertThat(result).isEqualTo(1L);

    }

}
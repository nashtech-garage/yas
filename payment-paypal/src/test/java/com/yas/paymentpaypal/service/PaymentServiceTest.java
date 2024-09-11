package com.yas.paymentpaypal.service;

import static com.yas.paymentpaypal.utils.SecurityContextUtils.setUpSecurityContext;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.paymentpaypal.config.ServiceUrlConfig;
import com.yas.paymentpaypal.viewmodel.CapturedPaymentVm;
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
    void testCapturePayment_ifNormalCase_returnAddressDetailVm() {

        CapturedPaymentVm payment = new CapturedPaymentVm(
            12345L,
            "chk_7890",
            new BigDecimal("250.75"),
            new BigDecimal("5.00"),
            "txn_0011223344",
            "credit_card",
            "completed",
            null
        );

        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.payment())
            .path("/storefront/payments/capture")
            .buildAndExpand()
            .toUri();

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.body(payment)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);

        paymentService.capturePayment(payment);

        verify(restClient, times(1)).post();
    }

}
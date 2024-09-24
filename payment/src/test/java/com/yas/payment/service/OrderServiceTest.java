package com.yas.payment.service;

import static com.yas.payment.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.payment.config.ServiceUrlConfig;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.viewmodel.CapturedPayment;
import com.yas.payment.viewmodel.CheckoutStatusVm;
import com.yas.payment.viewmodel.PaymentOrderStatusVm;
import java.math.BigDecimal;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

class OrderServiceTest {

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
    void testUpdateCheckoutStatus_whenNormalCase_returnLong() {

        CapturedPayment payment = CapturedPayment.builder()
            .orderId(12345L)
            .checkoutId("checkout-1234")
            .amount(new BigDecimal("99.99"))
            .paymentFee(new BigDecimal("2.50"))
            .gatewayTransactionId("txn-67890")
            .paymentMethod(PaymentMethod.COD)
            .paymentStatus(PaymentStatus.COMPLETED)
            .failureMessage(null)
            .build();

        URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.order())
            .path("/storefront/checkouts/status")
            .buildAndExpand()
            .toUri();

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(restClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(CheckoutStatusVm.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Long.class)).thenReturn(1L);

        Long result = orderService.updateCheckoutStatus(payment);

        assertThat(result).isEqualTo(1L);

    }

    @Test
    void testUpdateOrderStatus_whenNormalCase_returnPaymentOrderStatusVm() {

        PaymentOrderStatusVm statusVm = PaymentOrderStatusVm.builder()
            .orderId(123456L)
            .orderStatus("COMPLETED")
            .paymentId(78910L)
            .paymentStatus("SUCCESS")
            .build();

        URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.order())
            .path("/storefront/orders/status")
            .buildAndExpand()
            .toUri();

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(restClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(statusVm)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(PaymentOrderStatusVm.class)).thenReturn(statusVm);

        PaymentOrderStatusVm result = orderService.updateOrderStatus(statusVm);
        assertThat(result.orderId()).isEqualTo(123456L);
        assertThat(result.orderStatus()).isEqualTo("COMPLETED");
        assertThat(result.paymentId()).isEqualTo(78910L);
        assertThat(result.paymentStatus()).isEqualTo("SUCCESS");
    }
}
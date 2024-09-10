package com.yas.paymentpaypal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.Capture;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.MerchantReceivableBreakdown;
import com.paypal.orders.Money;
import com.paypal.orders.Order;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PaymentCollection;
import com.paypal.orders.PurchaseUnit;
import com.yas.paymentpaypal.model.CheckoutIdHelper;
import com.yas.paymentpaypal.viewmodel.CapturedPaymentVm;
import com.yas.paymentpaypal.viewmodel.PaypalRequestPayment;
import com.yas.paymentpaypal.viewmodel.RequestPayment;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaypalServiceTest {

    private PayPalHttpClient payPalHttpClient;

    private PaymentService paymentService;

    private PaypalService paypalService;

    @BeforeEach
    void setUp() {
        payPalHttpClient = mock(PayPalHttpClient.class);
        paymentService = mock(PaymentService.class);
        paypalService = new PaypalService(payPalHttpClient, paymentService);
        CheckoutIdHelper.setCheckoutId("test-checkout-id");
    }

    @Test
    void testCreatePayment_whenSuccess_returnPaypalRequestPayment() throws IOException {

        List<LinkDescription> linkDescriptions = new ArrayList<>();
        LinkDescription linkDescription = new LinkDescription();
        linkDescription.rel("approve");
        linkDescription.href("http://redirect.url");
        linkDescriptions.add(linkDescription);

        Order order = new Order()
            .id("ORDER-123456789")
            .checkoutPaymentIntent("CAPTURE")
            .createTime("2024-09-10T10:00:00Z")
            .updateTime("2024-09-10T10:30:00Z")
            .status("CREATED")
            .expirationTime("2024-09-11T10:00:00Z")
            .links(linkDescriptions);

        HttpResponse mockResponse = mock(HttpResponse.class);
        when(payPalHttpClient.execute(any(OrdersCreateRequest.class))).thenReturn(mockResponse);
        when(mockResponse.result()).thenReturn(order);

        RequestPayment requestPayment = new RequestPayment(BigDecimal.valueOf(2000), "test-checkout-id");
        PaypalRequestPayment result = paypalService.createPayment(requestPayment);

        assertEquals("success", result.status());
        assertEquals("ORDER-123456789", result.paymentId());
        assertEquals("http://redirect.url", result.redirectUrl());
    }

    @Test
    void testCreatePayment_whenIoException_returnPaypalRequestPayment() throws IOException {
        when(payPalHttpClient.execute(any(OrdersCreateRequest.class))).thenThrow(IOException.class);
        RequestPayment requestPayment = new RequestPayment(BigDecimal.valueOf(1000), "test-checkout-id");
        PaypalRequestPayment result = paypalService.createPayment(requestPayment);
        assertTrue(result.status().contains("Error"));
        assertNull(result.paymentId());
        assertNull(result.redirectUrl());
    }

    @Test
    void testCreatePayment_whenLinksIsEmpty_throwNoSuchElementException() throws IOException {

        Order order = new Order()
            .id("ORDER-123456789")
            .checkoutPaymentIntent("CAPTURE")
            .createTime("2024-09-10T10:00:00Z")
            .updateTime("2024-09-10T10:30:00Z")
            .status("CREATED")
            .expirationTime("2024-09-11T10:00:00Z")
            .links(List.of());

        HttpResponse mockResponse = mock(HttpResponse.class);
        when(payPalHttpClient.execute(any(OrdersCreateRequest.class))).thenReturn(mockResponse);
        when(mockResponse.result()).thenReturn(order);

        RequestPayment requestPayment = new RequestPayment(BigDecimal.valueOf(2000), "test-checkout-id");

        assertThrows(NoSuchElementException.class, () ->
            paypalService.createPayment(requestPayment)
        );
    }

    @Test
    void testCapturePayment_whenStatusNotNull_returnCapturedPaymentVm() throws IOException {

        Money money = new Money().value("100");
        MerchantReceivableBreakdown merchantReceivableBreakdown = new MerchantReceivableBreakdown();
        merchantReceivableBreakdown.paypalFee(money);
        Capture capture = new Capture().amount(money);
        capture.sellerReceivableBreakdown(merchantReceivableBreakdown);
        List<Capture> captureList = new ArrayList<>();
        captureList.add(capture);

        PaymentCollection paymentCollection = new PaymentCollection();
        paymentCollection.captures(captureList);
        PurchaseUnit purchaseUnit = new PurchaseUnit();
        purchaseUnit.payments(paymentCollection);

        List<PurchaseUnit> purchaseUnitList = new ArrayList<>();
        purchaseUnitList.add(purchaseUnit);

        Order mockOrder = new Order()
            .id("order-id")
            .status("COMPLETED")
            .purchaseUnits(purchaseUnitList);

        HttpResponse mockResponse = mock(HttpResponse.class);
        when(payPalHttpClient.execute(any(OrdersCaptureRequest.class))).thenReturn(mockResponse);
        when(mockResponse.result()).thenReturn(mockOrder);

        String token = "test-token-1";
        CapturedPaymentVm result = paypalService.capturePayment(token);

        assertNotNull(result);
        assertEquals("order-id", result.gatewayTransactionId());
        assertEquals("COMPLETED", result.paymentStatus());

        verify(paymentService).capturePayment(any());
    }

    @Test
    void testCapturePayment_whenStatusIsNull_returnCapturedPaymentVm() throws IOException {

        Order order = new Order()
            .id("order-id-2");
        HttpResponse mockResponse = mock(HttpResponse.class);
        when(payPalHttpClient.execute(any(OrdersCaptureRequest.class))).thenReturn(mockResponse);
        when(mockResponse.result()).thenReturn(order);
        CapturedPaymentVm result = paypalService.capturePayment("test-token-2");
        assertEquals("Something Wrong!", result.failureMessage());
    }

    @Test
    void testCapturePayment_whenIoException_returnCapturedPaymentVm() throws IOException {

        IOException ioException = new IOException("error message");
        when(payPalHttpClient.execute(any(OrdersCaptureRequest.class))).thenThrow(ioException);

        CapturedPaymentVm result = paypalService.capturePayment("test-token-2");
        assertEquals("error message", result.failureMessage());
    }

}
package com.yas.paymentpaypal.service;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.yas.paymentpaypal.utils.Constants;
import com.yas.paymentpaypal.viewmodel.PaypalRequestPayment;
import com.yas.paymentpaypal.viewmodel.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class PaypalService {
    @Autowired
    private PayPalHttpClient payPalHttpClient;

    public PaypalRequestPayment createPayment(BigDecimal fee) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown().currencyCode("USD").value(fee.toString());
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().amountWithBreakdown(amountWithBreakdown);
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));
        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl("http://localhost:8093/payment-paypal/capture")
                .cancelUrl("http://localhost:8093/payment-paypal/cancel")
                .brandName(Constants.YAS.BRAND_NAME)
                .landingPage("BILLING")
                .userAction("PAY_NOW")
                .shippingPreference("NO_SHIPPING");

        orderRequest.applicationContext(applicationContext);
        OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);

        try {
            HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
            Order order = orderHttpResponse.result();
            String redirectUrl = order.links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .href();

            return new PaypalRequestPayment("success", order.id(), redirectUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new PaypalRequestPayment("Error" + e.getMessage(), null ,null);
        }
    }


    public PaymentResponse completePayment(String token) {
        OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(token);
        try {
            HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
            if (httpResponse.result().status() != null) {
                return new PaymentResponse("success", token);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new PaymentResponse("error",null);
    }
}
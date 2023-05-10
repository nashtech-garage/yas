package com.yas.payment.service;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.yas.payment.utils.Constants;
//import com.yas.payment.viewmodel.OrderItemPostVm;
import com.yas.payment.viewmodel.paypal.CompletedOrder;
import com.yas.payment.viewmodel.paypal.PaypalOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class PayPalService {
    @Autowired
    private PayPalHttpClient payPalHttpClient;

    public PaypalOrder createPayment(BigDecimal fee) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown().currencyCode("USD").value(fee.toString());
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().amountWithBreakdown(amountWithBreakdown);
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));
        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl("http://localhost:8081/payment/paypal/capture")
                .cancelUrl("http://localhost:8081/payment/paypal/cancel")
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

            return new PaypalOrder("success", order.id(), redirectUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new PaypalOrder("Error" + e.getMessage(), null ,null);
        }
    }


    public CompletedOrder completePayment(String token) {
        OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(token);
        try {
            HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
            if (httpResponse.result().status() != null) {
                return new CompletedOrder("success", token);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new CompletedOrder("error",null);
    }
}
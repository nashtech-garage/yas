package com.yas.order.service;


import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.yas.order.viewmodel.CompletedOrder;
import com.yas.order.viewmodel.PaymentOrder;
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

    public PaymentOrder createPayment(BigDecimal fee) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");
        AmountWithBreakdown amountBreakdown = new AmountWithBreakdown().currencyCode("USD").value(fee.toString());
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().amountWithBreakdown(amountBreakdown);
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));
        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl("https://localhost:4200/capture")
                .cancelUrl("https://localhost:4200/cancel")
                .brandName("Your brand name")
                .landingPage("BILLING")
                .userAction("CONTINUE")
                .shippingPreference("NO_SHIPPING");

        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
                .amountWithBreakdown(new AmountWithBreakdown()
                        .currencyCode("USD")
                        .value("10.00"))
                .description("Your purchase description");
        List<Item> items = new ArrayList<>();
        // Add items to the purchase unit
        Item item1 = new Item();
        item1.name("Item 1").description("Item 1 description").quantity("1").unitAmount(new Money().currencyCode("USD").value("5.00"));
        Item item2 = new Item();
        item2.name("Item 2").description("Item 2 description").quantity("2").unitAmount(new Money().currencyCode("USD").value("2.50"));
        items.add(item1);
        items.add(item2);
        purchaseUnit.items(items);

        purchaseUnits.add(purchaseUnit);

        orderRequest.applicationContext(applicationContext).purchaseUnits(purchaseUnits);
        OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);

        try {
            HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
            Order order = orderHttpResponse.result();

            String redirectUrl = order.links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .href();

            return new PaymentOrder("success", order.id(), redirectUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new PaymentOrder("Error");
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
        return new CompletedOrder("error");
    }
}
package com.yas.paymentpaypal.service;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.yas.paymentpaypal.model.CheckoutIdHelper;
import com.yas.paymentpaypal.utils.Constants;
import com.yas.paymentpaypal.viewmodel.CapturedPaymentVm;
import com.yas.paymentpaypal.viewmodel.PaypalRequestPayment;
import com.yas.paymentpaypal.viewmodel.RequestPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaypalService {
    private final PayPalHttpClient payPalHttpClient;
    private final PaymentService paymentService;

    private static final String returnUrl = "http://api.yas.local/payment-paypal/capture";
    private static final String cancelUrl = "http://api.yas.local/payment-paypal/cancel";

    public PaypalRequestPayment createPayment(RequestPayment requestPayment) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown().currencyCode("USD").value(requestPayment.totalPrice().toString());
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().amountWithBreakdown(amountWithBreakdown);
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));
        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
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

            CheckoutIdHelper.setCheckoutId(requestPayment.checkoutId());
            return new PaypalRequestPayment("success", order.id(), redirectUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new PaypalRequestPayment("Error" + e.getMessage(), null, null);
        }
    }


    public CapturedPaymentVm capturePayment(String token) {
        OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(token);
        try {
            HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
            if (httpResponse.result().status() != null) {
                Order order = httpResponse.result();
                Capture capture = order.purchaseUnits().get(0).payments().captures().get(0);

                String paypalFee = capture.sellerReceivableBreakdown().paypalFee().value();
                BigDecimal paymentFee = new BigDecimal(paypalFee);
                BigDecimal amount = new BigDecimal(capture.amount().value());

                CapturedPaymentVm capturedPayment = CapturedPaymentVm.builder()
                        .paymentFee(paymentFee)
                        .gatewayTransactionId(order.id())
                        .amount(amount)
                        .paymentStatus(order.status())
                        .paymentMethod("PAYPAL")
                        .checkoutId(CheckoutIdHelper.checkoutId)
                        .build();
                paymentService.capturePaymentInfoToPaymentService(capturedPayment);
                return capturedPayment;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return CapturedPaymentVm.builder().failureMessage(e.getMessage()).build();
        }
        return CapturedPaymentVm.builder().failureMessage("Something Wrong!").build();
    }
}
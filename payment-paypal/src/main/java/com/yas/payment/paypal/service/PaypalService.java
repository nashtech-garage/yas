package com.yas.payment.paypal.service;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Capture;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;
import com.yas.payment.paypal.model.CheckoutIdHelper;
import com.yas.payment.paypal.utils.Constants;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentResponse;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaypalService {
    private final PayPalHttpClientInitializer payPalHttpClientInitializer;
    private final BigDecimal maxPay = BigDecimal.valueOf(1000);
    @Value("${yas.public.url}/success")
    private String returnUrl;
    @Value("${yas.public.url}/cancel")
    private String cancelUrl;

    public PaypalCreatePaymentResponse createPayment(PaypalCreatePaymentRequest createPaymentRequest) {
        PayPalHttpClient payPalHttpClient
            = payPalHttpClientInitializer.createPaypalClient(createPaymentRequest.paymentSettings());
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        // Workaround to not exceed limit amount of a transaction
        BigDecimal totalPrice = createPaymentRequest.totalPrice();
        if (totalPrice.compareTo(maxPay) > 0) {
            totalPrice = maxPay;
        }

        AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown().currencyCode("USD")
            .value(totalPrice.toString());
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().amountWithBreakdown(amountWithBreakdown);
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));
        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .brandName(Constants.Yas.BRAND_NAME)
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

            CheckoutIdHelper.setCheckoutId(createPaymentRequest.checkoutId());
            return new PaypalCreatePaymentResponse("success", order.id(), redirectUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new PaypalCreatePaymentResponse("Error" + e.getMessage(),null, null);
        }
    }

    public PaypalCapturePaymentResponse capturePayment(PaypalCapturePaymentRequest capturePaymentRequest) {
        PayPalHttpClient payPalHttpClient
            = payPalHttpClientInitializer.createPaypalClient(capturePaymentRequest.paymentSettings());
        OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(capturePaymentRequest.token());
        try {
            HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
            if (httpResponse.result().status() != null) {
                Order order = httpResponse.result();
                Capture capture = order.purchaseUnits().get(0).payments().captures().get(0);

                String paypalFee = capture.sellerReceivableBreakdown().paypalFee().value();
                BigDecimal paymentFee = new BigDecimal(paypalFee);
                BigDecimal amount = new BigDecimal(capture.amount().value());

                return PaypalCapturePaymentResponse.builder()
                        .paymentFee(paymentFee)
                        .gatewayTransactionId(order.id())
                        .amount(amount)
                        .paymentStatus(order.status())
                        .paymentMethod("PAYPAL")
                        .checkoutId(CheckoutIdHelper.getCheckoutId())
                        .build();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return PaypalCapturePaymentResponse.builder().failureMessage(e.getMessage()).build();
        }
        return PaypalCapturePaymentResponse.builder().failureMessage("Something Wrong!").build();
    }
}

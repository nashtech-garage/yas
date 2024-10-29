package com.yas.paymentpaypal.service;

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
import com.yas.paymentpaypal.model.CheckoutIdHelper;
import com.yas.paymentpaypal.utils.Constants;
import com.yas.paymentpaypal.viewmodel.CapturedPaymentVm;
import com.yas.paymentpaypal.viewmodel.PaymentPaypalRequest;
import com.yas.paymentpaypal.viewmodel.PaymentPaypalResponse;
import com.yas.paymentpaypal.viewmodel.PaypalRequestPayment;
import com.yas.paymentpaypal.viewmodel.RequestPayment;
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
    private final PayPalHttpClient payPalHttpClient;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final BigDecimal maxPay = BigDecimal.valueOf(1000);
    @Value("${yas.public.url}/capture")
    private String returnUrl;
    @Value("${yas.public.url}/cancel")
    private String cancelUrl;

    public PaypalRequestPayment createPayment(RequestPayment requestPayment) {

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        // Workaround to not exceed limit amount of a transaction
        BigDecimal totalPrice = adjustTotalPrice(requestPayment.totalPrice());

        AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown().currencyCode("USD")
            .value(totalPrice.toString());
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().amountWithBreakdown(amountWithBreakdown);
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));

        ApplicationContext applicationContext = buildApplicationContext();

        orderRequest.applicationContext(applicationContext);
        OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);

        try {
            HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
            String redirectUrl = getRedirectUrl(orderHttpResponse);

            CheckoutIdHelper.setCheckoutId(requestPayment.checkoutId());
            return new PaypalRequestPayment("success", orderHttpResponse.result().id(), redirectUrl);
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

                var orderVm = orderService.getOrderByCheckoutId(CheckoutIdHelper.getCheckoutId());

                CapturedPaymentVm capturedPayment = CapturedPaymentVm.builder()
                    .orderId(orderVm.id())
                    .paymentFee(paymentFee)
                    .gatewayTransactionId(order.id())
                    .amount(amount)
                    .paymentStatus(order.status())
                    .paymentMethod("PAYPAL")
                    .checkoutId(CheckoutIdHelper.getCheckoutId())
                    .build();

                paymentService.capturePayment(capturedPayment);
                return capturedPayment;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return CapturedPaymentVm.builder().failureMessage(e.getMessage()).build();
        }
        return CapturedPaymentVm.builder().failureMessage("Something Wrong!").build();
    }

    public PaymentPaypalResponse createOrderOnPaypal(PaymentPaypalRequest paymentPaypalRequest) {

        try {
            OrderRequest orderRequest = buildOrderRequest(paymentPaypalRequest);
            OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);

            HttpResponse<Order> orderResponse = executeOrderRequest(ordersCreateRequest);
            String redirectUrl = getRedirectUrl(orderResponse);

            CheckoutIdHelper.setCheckoutId(paymentPaypalRequest.checkoutId());
            return new PaymentPaypalResponse("success", orderResponse.result().id(), redirectUrl);

        } catch (IOException e) {
            log.error(e.getMessage());
            return new PaymentPaypalResponse("Error: " + e.getMessage(), null, null);
        }
    }

    private OrderRequest buildOrderRequest(PaymentPaypalRequest paymentPaypalRequest) {

        BigDecimal totalPrice = adjustTotalPrice(paymentPaypalRequest.totalPrice());
        AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown()
                .currencyCode("USD")
                .value(totalPrice.toString());

        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
                .amountWithBreakdown(amountWithBreakdown);

        ApplicationContext applicationContext = buildApplicationContext();

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));
        orderRequest.applicationContext(applicationContext);

        return orderRequest;
    }

    private BigDecimal adjustTotalPrice(BigDecimal totalPrice) {
        return (totalPrice.compareTo(maxPay) > 0) ? maxPay : totalPrice;
    }

    private ApplicationContext buildApplicationContext() {
        return new ApplicationContext()
                .cancelUrl(cancelUrl)
                .returnUrl(returnUrl)
                .brandName(Constants.Yas.BRAND_NAME)
                .userAction("PAY_NOW")
                .landingPage("BILLING")
                .shippingPreference("NO_SHIPPING");
    }

    private HttpResponse<Order> executeOrderRequest(OrdersCreateRequest ordersCreateRequest) throws IOException {
        return payPalHttpClient.execute(ordersCreateRequest);
    }

    private String getRedirectUrl(HttpResponse<Order> orderResponse) {
        return orderResponse.result().links().stream()
                .filter(link -> "approve".equals(link.rel()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .href();
    }
}

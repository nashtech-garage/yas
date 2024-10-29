package com.yas.payment.service.provider.handler;

import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.CapturePaymentRequest;
import com.yas.payment.viewmodel.CapturePaymentResponse;
import com.yas.payment.viewmodel.InitPaymentRequest;
import com.yas.payment.viewmodel.InitPaymentResponse;
import com.yas.payment.paypal.service.PaypalService;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentResponse;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaypalHandler extends AbstractPaymentHandler implements PaymentHandler {

    private final PaypalService paypalService;

    PaypalHandler(PaymentProviderService paymentProviderService, PaypalService paypalService) {
        super(paymentProviderService);
        this.paypalService = paypalService;
    }

    @Override
    public String getProviderId() {
        return PaymentMethod.PAYPAL.name();
    }

    @Override
    public InitPaymentResponse initPayment(InitPaymentRequest initPaymentRequest) {
        PaypalCreatePaymentRequest requestPayment = new PaypalCreatePaymentRequest(
                initPaymentRequest.totalPrice(),
                initPaymentRequest.checkoutId(),
                initPaymentRequest.paymentMethod(),
                getPaymentSettings(initPaymentRequest.paymentMethod()));
        PaypalCreatePaymentResponse paypalCreatePaymentResponse = paypalService.createPayment(requestPayment);
        return new InitPaymentResponse(paypalCreatePaymentResponse.status(), paypalCreatePaymentResponse.paymentId(), paypalCreatePaymentResponse.redirectUrl());
    }

    @Override
    public CapturePaymentResponse capturePayment(CapturePaymentRequest capturePaymentRequest) {
        PaypalCapturePaymentRequest paypalCapturePaymentRequest = new PaypalCapturePaymentRequest(
                capturePaymentRequest.token(),
                getPaymentSettings(capturePaymentRequest.paymentMethod())
        );

        PaypalCapturePaymentResponse paypalCapturePaymentResponse = paypalService.capturePayment(paypalCapturePaymentRequest);
        return new CapturePaymentResponse(
                paypalCapturePaymentResponse.orderId(),
                paypalCapturePaymentResponse.checkoutId(),
                paypalCapturePaymentResponse.amount(),
                paypalCapturePaymentResponse.paymentFee(),
                paypalCapturePaymentResponse.gatewayTransactionId(),
                PaymentMethod.valueOf(paypalCapturePaymentResponse.paymentMethod()),
                PaymentStatus.valueOf(paypalCapturePaymentResponse.paymentStatus()),
                paypalCapturePaymentResponse.failureMessage());
    }
}

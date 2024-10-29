package com.yas.payment.service.provider.handler;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.paypal.service.PaypalService;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentResponse;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentResponse;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
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
    public InitiatedPayment initPayment(InitPaymentRequestVm initPaymentRequestVm) {
        PaypalCreatePaymentRequest requestPayment = PaypalCreatePaymentRequest.builder()
                .totalPrice(initPaymentRequestVm.totalPrice())
                .checkoutId(initPaymentRequestVm.checkoutId())
                .paymentMethod(initPaymentRequestVm.paymentMethod())
                .paymentSettings(initPaymentRequestVm.paymentMethod())
                .build();
        PaypalCreatePaymentResponse paypalCreatePaymentResponse = paypalService.createPayment(requestPayment);
        return InitiatedPayment.builder()
                .status(paypalCreatePaymentResponse.status())
                .paymentId(paypalCreatePaymentResponse.paymentId())
                .redirectUrl(paypalCreatePaymentResponse.redirectUrl())
                .build();
    }

    @Override
    public CapturedPayment capturePayment(CapturePaymentRequestVm capturePaymentRequestVM) {
        PaypalCapturePaymentRequest paypalCapturePaymentRequest = PaypalCapturePaymentRequest.builder()
                .token(capturePaymentRequestVM.token())
                .paymentSettings(capturePaymentRequestVM.paymentMethod())
                .build();
        PaypalCapturePaymentResponse paypalCapturePaymentResponse = paypalService.capturePayment(paypalCapturePaymentRequest);
        return CapturedPayment.builder()
                .checkoutId(paypalCapturePaymentResponse.checkoutId())
                .amount(paypalCapturePaymentResponse.amount())
                .paymentFee(paypalCapturePaymentResponse.paymentFee())
                .gatewayTransactionId(paypalCapturePaymentResponse.gatewayTransactionId())
                .paymentMethod(PaymentMethod.valueOf(paypalCapturePaymentResponse.paymentMethod()))
                .paymentStatus(PaymentStatus.valueOf(paypalCapturePaymentResponse.paymentStatus()))
                .failureMessage(paypalCapturePaymentResponse.failureMessage())
                .build();
    }
}

package com.yas.payment.service.provider.handler;

import com.yas.payment.viewmodel.CapturePaymentRequest;
import com.yas.payment.viewmodel.CapturePaymentResponse;
import com.yas.payment.viewmodel.CreatePaymentRequest;
import com.yas.payment.viewmodel.CreatePaymentResponse;

public interface PaymentHandler {
    String getProviderId();
    CreatePaymentResponse createPayment(CreatePaymentRequest createPaymentRequest);
    CapturePaymentResponse capturePayment(CapturePaymentRequest capturePaymentRequest);
}

package com.yas.payment.service.provider.handler;

import com.yas.payment.viewmodel.CapturePaymentRequest;
import com.yas.payment.viewmodel.CapturePaymentResponse;
import com.yas.payment.viewmodel.InitPaymentRequest;
import com.yas.payment.viewmodel.InitPaymentResponse;

public interface PaymentHandler {
    String getProviderId();
    InitPaymentResponse initPayment(InitPaymentRequest initPaymentRequest);
    CapturePaymentResponse capturePayment(CapturePaymentRequest capturePaymentRequest);
}

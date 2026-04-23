package com.yas.payment.service.provider.handler;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;

public interface PaymentHandler {
    String getProviderId();

    InitiatedPayment initPayment(InitPaymentRequestVm initPaymentRequestVm);

    CapturedPayment capturePayment(CapturePaymentRequestVm capturePaymentRequestVm);
}

package com.yas.payment.paypal.viewmodel;

import lombok.Builder;

@Builder
public record PaypalCapturePaymentRequest(String token, String paymentSettings) {
}

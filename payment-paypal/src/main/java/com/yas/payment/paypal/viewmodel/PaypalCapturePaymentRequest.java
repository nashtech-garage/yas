package com.yas.payment.paypal.viewmodel;

public record PaypalCapturePaymentRequest(String token, String paymentSettings) {
}

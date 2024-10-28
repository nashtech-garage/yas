package com.yas.payment.viewmodel;

public record CapturePaymentRequest(String paymentMethod, String token) {
}

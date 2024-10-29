package com.yas.payment.viewmodel;

public record InitPaymentResponse(String status, String paymentId, String redirectUrl) {
}

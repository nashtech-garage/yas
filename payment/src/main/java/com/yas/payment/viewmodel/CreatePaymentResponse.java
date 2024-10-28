package com.yas.payment.viewmodel;

public record CreatePaymentResponse(String status, String paymentId, String redirectUrl) {
}

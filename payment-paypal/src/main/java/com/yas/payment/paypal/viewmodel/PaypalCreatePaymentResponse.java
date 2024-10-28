package com.yas.payment.paypal.viewmodel;

public record PaypalCreatePaymentResponse(String status, String paymentId, String redirectUrl) {
}
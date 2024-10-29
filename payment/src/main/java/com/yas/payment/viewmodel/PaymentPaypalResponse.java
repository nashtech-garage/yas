package com.yas.payment.viewmodel;

public record PaymentPaypalResponse(String status, String orderId, String redirectUrl) {
}
package com.yas.paymentpaypal.viewmodel;

public record PaymentPaypalResponse(String status, String orderId, String redirectUrl) {
}
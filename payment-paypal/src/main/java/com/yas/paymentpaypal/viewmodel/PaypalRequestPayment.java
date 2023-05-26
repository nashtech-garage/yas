package com.yas.paymentpaypal.viewmodel;

public record PaypalRequestPayment(String status, String paymentId, String redirectUrl) {
}
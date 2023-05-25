package com.yas.paymentpaypal.viewmodel;

public record PaypalRequestPayment(String status, String payId, String redirectUrl) {
}
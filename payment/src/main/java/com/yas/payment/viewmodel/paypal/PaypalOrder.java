package com.yas.payment.viewmodel.paypal;

public record PaypalOrder(String status, String payId, String redirectUrl) {
}
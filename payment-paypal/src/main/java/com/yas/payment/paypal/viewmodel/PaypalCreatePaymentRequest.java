package com.yas.payment.paypal.viewmodel;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaypalCreatePaymentRequest(BigDecimal totalPrice, String checkoutId, String paymentMethod, String paymentSettings) {
}
package com.yas.payment.paypal.viewmodel;

import java.math.BigDecimal;

public record PaypalCreatePaymentRequest(BigDecimal totalPrice, String checkoutId, String paymentMethod, String paymentSettings) {
}
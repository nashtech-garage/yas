package com.yas.payment.viewmodel;

import java.math.BigDecimal;

public record CreatePaymentRequest(String paymentMethod, BigDecimal totalPrice, String checkoutId) {
}

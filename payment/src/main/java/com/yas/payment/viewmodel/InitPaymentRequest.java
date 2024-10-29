package com.yas.payment.viewmodel;

import java.math.BigDecimal;

public record InitPaymentRequest(String paymentMethod, BigDecimal totalPrice, String checkoutId) {
}

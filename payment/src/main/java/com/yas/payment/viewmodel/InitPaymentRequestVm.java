package com.yas.payment.viewmodel;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record InitPaymentRequestVm(String paymentMethod, BigDecimal totalPrice, String checkoutId) {
}

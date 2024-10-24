package com.yas.payment.paypal.viewmodel;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record PaypalCreatePaymentRequest(
    BigDecimal totalPrice, String checkoutId, String paymentMethod, String paymentSettings
) {
}
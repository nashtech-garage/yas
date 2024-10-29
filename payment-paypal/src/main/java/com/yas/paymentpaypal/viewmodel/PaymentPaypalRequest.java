package com.yas.paymentpaypal.viewmodel;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record PaymentPaypalRequest(BigDecimal totalPrice, String checkoutId) {
}
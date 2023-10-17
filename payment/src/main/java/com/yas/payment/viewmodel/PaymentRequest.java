package com.yas.payment.viewmodel;

import com.yas.payment.model.enumeration.EPaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentRequest(BigDecimal totalPrice, EPaymentMethod paymentMethod) {
}
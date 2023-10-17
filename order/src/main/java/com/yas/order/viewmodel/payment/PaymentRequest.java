package com.yas.order.viewmodel.payment;

import com.yas.order.model.enumeration.EPaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentRequest(BigDecimal totalPrice, EPaymentMethod paymentMethod) {
}
package com.yas.payment.model;

import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Setter
@Getter
public class CapturedPayment {
    Long orderId;
    String checkoutId;
    BigDecimal amount;
    BigDecimal paymentFee;
    String gatewayTransactionId;
    PaymentMethod paymentMethod;
    PaymentStatus paymentStatus;
    String failureMessage;
}

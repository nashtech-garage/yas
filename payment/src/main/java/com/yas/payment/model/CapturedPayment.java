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
    private Long orderId;
    private String checkoutId;
    private BigDecimal amount;
    private BigDecimal paymentFee;
    private String gatewayTransactionId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String failureMessage;
}

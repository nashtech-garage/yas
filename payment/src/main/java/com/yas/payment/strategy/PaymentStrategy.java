package com.yas.payment.strategy;

import java.math.BigDecimal;

public interface PaymentStrategy {
    void purchase(BigDecimal totalPrice);
    void refund();
}

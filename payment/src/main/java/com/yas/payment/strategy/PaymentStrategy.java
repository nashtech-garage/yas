package com.yas.payment.strategy;

import java.math.BigDecimal;

public sealed interface PaymentStrategy permits CoDPayment, PaypalPayment, CreditCardPayment{
    void purchase(BigDecimal totalPrice);
    void refund();
}

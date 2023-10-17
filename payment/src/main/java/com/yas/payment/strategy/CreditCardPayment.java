package com.yas.payment.strategy;

import com.yas.payment.viewmodel.PaymentRequest;

import java.math.BigDecimal;

public class CreditCardPayment implements PaymentStrategy{
    @Override
    public void purchase(BigDecimal totalPrice) {

    }

    @Override
    public void refund() {

    }
}

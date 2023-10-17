package com.yas.payment.strategy;

import com.yas.payment.viewmodel.PaymentRequest;

import java.math.BigDecimal;

public class PaymentManager {
    private PaymentStrategy paymentStrategy;

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void purchase(BigDecimal totalPrice){
        this.paymentStrategy.purchase(totalPrice);
    }

    public void refund(){
        this.paymentStrategy.refund();;
    }
}

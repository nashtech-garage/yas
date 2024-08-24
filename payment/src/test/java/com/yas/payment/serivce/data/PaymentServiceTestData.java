package com.yas.payment.serivce.data;

import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.viewmodel.CapturedPayment;
import com.yas.payment.viewmodel.PaymentOrderStatusVm;

import java.math.BigDecimal;

public class PaymentServiceTestData {
    public static final long ORDER_ID = 1L;
    public static final PaymentStatus PAYMENT_STATUS = PaymentStatus.COMPLETED;
    private PaymentServiceTestData() {}

    public static CapturedPayment getCapturedPayment() {
        return new CapturedPayment (
                ORDER_ID, "11111",
                BigDecimal.valueOf(100000.00), BigDecimal.valueOf(10.00),
                "22222", PaymentMethod.BANKING,
                PaymentStatus.PENDING, "message"
        );
    }

    public static PaymentOrderStatusVm getPaymentOrderStatusVm() {
        return new PaymentOrderStatusVm (
                ORDER_ID, "testOrderStatus",
                100L, PAYMENT_STATUS.name()
        );
    }

    public static Payment getPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaymentStatus(PAYMENT_STATUS);
        return payment;
    }
}

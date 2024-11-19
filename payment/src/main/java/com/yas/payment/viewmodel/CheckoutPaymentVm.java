package com.yas.payment.viewmodel;

import com.yas.payment.model.enumeration.PaymentMethod;
import java.math.BigDecimal;

public record CheckoutPaymentVm(String checkoutId, PaymentMethod paymentMethod, BigDecimal totalAmount) {

}
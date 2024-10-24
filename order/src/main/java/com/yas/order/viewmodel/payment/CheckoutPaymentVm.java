package com.yas.order.viewmodel.payment;

import com.yas.order.model.enumeration.PaymentMethod;
import java.math.BigDecimal;

public record CheckoutPaymentVm(String checkoutId, PaymentMethod paymentMethod, BigDecimal totalAmount) {

}
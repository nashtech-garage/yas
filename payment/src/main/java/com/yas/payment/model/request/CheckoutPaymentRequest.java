package com.yas.payment.model.request;

import com.yas.payment.model.enumeration.PaymentMethod;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CheckoutPaymentRequest {

    private String checkoutId;

    private PaymentMethod paymentMethod;

    private BigDecimal totalAmount;
}

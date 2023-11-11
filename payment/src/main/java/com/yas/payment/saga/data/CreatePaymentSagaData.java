package com.yas.payment.saga.data;

import com.yas.payment.model.enumeration.EPaymentMethod;
import com.yas.payment.viewmodel.CapturedPayment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreatePaymentSagaData {
    private BigDecimal totalPrice;
    private EPaymentMethod paymentMethod;
    private CapturedPayment capturedPayment;
}

package com.yas.payment.saga.data;

import com.yas.payment.viewmodel.CapturedPayment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CompletePaymentSagaData {
    private CapturedPayment capturedPayment;
    private PaymentData payment;
    private Long orderId;
}

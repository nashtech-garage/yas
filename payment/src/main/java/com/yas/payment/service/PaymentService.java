package com.yas.payment.service;

import com.yas.payment.exception.NotFoundException;
import com.yas.payment.model.Payment;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.repository.PaymentProviderRepository;
import com.yas.payment.repository.PaymentRepository;
import com.yas.payment.utils.Constants;
import com.yas.payment.viewmodel.CompletedPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public Payment paymentCompleted(CompletedPayment completedPayment) {
        Payment payment =Payment.builder()
                .checkoutId(completedPayment.checkoutId())
                .orderId(completedPayment.orderId())
                .paymentStatus(completedPayment.paymentStatus())
                .paymentFee(completedPayment.paymentFee())
                .paymentMethod(completedPayment.paymentMethod())
                .amount(completedPayment.amount())
                .failureMessage(completedPayment.failureMessage())
                .gatewayTransactionId(completedPayment.gatewayTransactionId())
                .build();
        return paymentRepository.save(payment);
    }
}

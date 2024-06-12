package com.yas.payment.service;

import com.yas.payment.model.Payment;
import com.yas.payment.repository.PaymentRepository;
import com.yas.payment.viewmodel.CapturedPayment;
import com.yas.payment.viewmodel.PaymentOrderStatusVm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;


    public PaymentOrderStatusVm capturePayment(CapturedPayment capturedPayment) {
        Payment payment = createPayment(capturedPayment);
        Long orderId = orderService.updateCheckoutStatus(capturedPayment);
        PaymentOrderStatusVm orderPaymentStatusVm =
                PaymentOrderStatusVm.builder()
                        .paymentId(payment.getId())
                        .orderId(orderId)
                        .paymentStatus(payment.getPaymentStatus().name())
                        .build();
        return orderService.updateOrderStatus(orderPaymentStatusVm);
    }

    public Payment createPayment(CapturedPayment completedPayment) {
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

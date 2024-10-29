package com.yas.payment.service;

import com.yas.payment.model.Payment;
import com.yas.payment.repository.PaymentRepository;
import com.yas.payment.service.provider.handler.PaymentHandler;
import com.yas.payment.viewmodel.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final Map<String, PaymentHandler> providers = new HashMap<>();

    @Autowired
    private List<PaymentHandler> paymentHandlers;

    @PostConstruct
    public void initializeProviders() {
        for (PaymentHandler handler : paymentHandlers) {
            providers.put(handler.getProviderId().toLowerCase(), handler);
        }
    }

    private PaymentHandler getPaymentHandler(String providerName) {
        PaymentHandler handler = providers.get(providerName.toLowerCase());
        if (handler == null) {
            throw new IllegalArgumentException("No payment handler found for provider: " + providerName);
        }
        return handler;
    }

    public InitPaymentResponse initPayment(InitPaymentRequest initPaymentRequest) {
        PaymentHandler paymentHandler = getPaymentHandler(initPaymentRequest.paymentMethod());
        return paymentHandler.initPayment(initPaymentRequest);
    }

    public CapturePaymentResponse capturePayment(CapturePaymentRequest capturePaymentRequest) {
        PaymentHandler paymentHandler = getPaymentHandler(capturePaymentRequest.paymentMethod());
        CapturePaymentResponse capturePaymentResponse = paymentHandler.capturePayment(capturePaymentRequest);
        Long orderId = orderService.updateCheckoutStatus(capturePaymentResponse);
        Payment payment = createPayment(capturePaymentResponse, orderId);
        PaymentOrderStatusVm orderPaymentStatusVm =
                PaymentOrderStatusVm.builder()
                        .paymentId(payment.getId())
                        .orderId(orderId)
                        .paymentStatus(payment.getPaymentStatus().name())
                        .build();
        orderService.updateOrderStatus(orderPaymentStatusVm);
        return capturePaymentResponse;
    }

    private Payment createPayment(CapturePaymentResponse completedPayment, Long orderId) {
        Payment payment = Payment.builder()
                .checkoutId(completedPayment.checkoutId())
                .orderId(orderId)
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

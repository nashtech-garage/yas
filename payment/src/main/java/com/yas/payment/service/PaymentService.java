package com.yas.payment.service;

import com.yas.payment.model.Payment;
import com.yas.payment.repository.PaymentRepository;
import com.yas.payment.strategy.CoDPayment;
import com.yas.payment.strategy.CreditCardPayment;
import com.yas.payment.strategy.PaymentManager;
import com.yas.payment.strategy.PaypalPayment;
import com.yas.payment.viewmodel.CapturedPayment;
import com.yas.payment.viewmodel.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentManager paymentManager;

    public void  createPayment(PaymentRequest paymentRequest) {
        switch (paymentRequest.paymentMethod()) {
            case COD:
                paymentManager.setPaymentStrategy(new CoDPayment());
                log.info("Payment by COD");
                break;
            case CREDIT_CARD:
                paymentManager.setPaymentStrategy(new CreditCardPayment());
                log.info("Payment by CREDIT_CARD");
                break;
            case PAYPAL:
                paymentManager.setPaymentStrategy(new PaypalPayment());
                log.info("Payment by PAYPAL");
                break;
            default:
                log.warn("Payment method doest exist");
                break;
        }

        paymentManager.purchase(paymentRequest.totalPrice());
    }

    public Payment capturePayment(CapturedPayment completedPayment) {
        Payment payment = Payment.builder()
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

package com.yas.payment.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.repository.PaymentRepository;
import com.yas.payment.utils.Constants;
import com.yas.payment.viewmodel.CapturedPayment;
import com.yas.payment.viewmodel.CheckoutPaymentVm;
import com.yas.payment.viewmodel.PaymentOrderStatusVm;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {


    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

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
        Payment payment = Payment.builder()
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

    public Long createPaymentFromEvent(CheckoutPaymentVm checkoutPaymentVm) {

        Payment payment = Payment.builder()
            .checkoutId(checkoutPaymentVm.checkoutId())
            .paymentStatus(
                PaymentMethod.COD.equals(checkoutPaymentVm.paymentMethod())
                    ? PaymentStatus.NEW : PaymentStatus.PROCESSING
            )
            .paymentMethod(checkoutPaymentVm.paymentMethod())
            .amount(checkoutPaymentVm.totalAmount())
            .build();

        Payment createdPayment = paymentRepository.save(payment);

        LOGGER.info("Payment is created successfully with ID: {}", createdPayment.getId());

        return createdPayment.getId();
    }

    public Payment findPaymentById(Long id) {

        return paymentRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(Constants.ErrorCode.PAYMENT_NOT_FOUND, id));
    }

    public void updatePayment(Payment payment) {

        if (Objects.isNull(payment.getId())) {
            throw new BadRequestException(Constants.Message.PAYMENT_ID_REQUIRED);
        }
        paymentRepository.save(payment);
    }

}

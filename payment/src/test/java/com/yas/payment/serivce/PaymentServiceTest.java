package com.yas.payment.serivce;

import com.yas.payment.repository.PaymentRepository;
import com.yas.payment.service.OrderService;
import com.yas.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.yas.payment.serivce.data.PaymentServiceTestData.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    private PaymentService paymentService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository, orderService);
    }
    @Test
    void capturePayment_shouldReturn_PaymentOrderStatusVm() {
        when(orderService.updateCheckoutStatus(any())).thenReturn(ORDER_ID);
        when(orderService.updateOrderStatus(any())).thenReturn(getPaymentOrderStatusVm());
        when(paymentRepository.save(any())).thenReturn(getPayment());
        assertEquals(paymentService.capturePayment(getCapturedPayment()).paymentStatus(), PAYMENT_STATUS.name());
    }
}

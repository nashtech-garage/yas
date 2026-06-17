package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.model.Order;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceStatusTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(1L)
                .orderStatus(OrderStatus.PENDING)
                .build();
    }

    @Test
    void rejectOrder_whenOrderNotFound_thenThrowNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.rejectOrder(1L, "reason"));
    }

    @Test
    void rejectOrder_whenOrderFound_thenUpdateStatusToReject() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        
        orderService.rejectOrder(1L, "reason");
        
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
        assertThat(order.getRejectReason()).isEqualTo("reason");
        verify(orderRepository).save(order);
    }

    @Test
    void acceptOrder_whenOrderNotFound_thenThrowNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.acceptOrder(1L));
    }

    @Test
    void acceptOrder_whenOrderFound_thenUpdateStatusToAccepted() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        
        orderService.acceptOrder(1L);
        
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderPaymentStatus_whenOrderNotFound_thenThrowNotFoundException() {
        PaymentOrderStatusVm vm = PaymentOrderStatusVm.builder().orderId(1L).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.updateOrderPaymentStatus(vm));
    }

    @Test
    void updateOrderPaymentStatus_whenCompleted_thenUpdateToPaid() {
        PaymentOrderStatusVm vm = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentId(1L)
                .paymentStatus(PaymentStatus.COMPLETED.name())
                .build();
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(vm);

        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(result.orderId()).isEqualTo(1L);
    }

    @Test
    void updateOrderPaymentStatus_whenNotCompleted_thenKeepPending() {
        PaymentOrderStatusVm vm = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentId(1L)
                .paymentStatus(PaymentStatus.PENDING.name())
                .build();
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.updateOrderPaymentStatus(vm);

        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
    }
}

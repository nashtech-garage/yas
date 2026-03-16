package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PromotionService promotionService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getLatestOrders_whenCountIsNotPositive_thenReturnEmptyList() {
        List<?> result = orderService.getLatestOrders(0);

        assertThat(result).isEmpty();
        verify(orderRepository, never()).getLatestOrders(any());
    }

    @Test
    void getLatestOrders_whenRepositoryReturnsEmpty_thenReturnEmptyList() {
        when(orderRepository.getLatestOrders(any())).thenReturn(List.of());

        var result = orderService.getLatestOrders(5);

        assertThat(result).isEmpty();
    }

    @Test
    void getLatestOrders_whenRepositoryReturnsData_thenMapToOrderBriefVm() {
        Order order = buildOrder(11L);
        when(orderRepository.getLatestOrders(any())).thenReturn(List.of(order));

        var result = orderService.getLatestOrders(1);

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting("id", "email")
                .containsExactly(11L, "order-11@yas.local");
    }

    @Test
    void findOrderByCheckoutId_whenOrderExists_thenReturnOrder() {
        Order order = buildOrder(12L);
        order.setCheckoutId("checkout-12");
        when(orderRepository.findByCheckoutId("checkout-12")).thenReturn(Optional.of(order));

        Order result = orderService.findOrderByCheckoutId("checkout-12");

        assertThat(result).isEqualTo(order);
    }

    @Test
    void findOrderByCheckoutId_whenOrderMissing_thenThrowNotFoundException() {
        when(orderRepository.findByCheckoutId("missing-checkout")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.findOrderByCheckoutId("missing-checkout"));
    }

    @Test
    void updateOrderPaymentStatus_whenPaymentCompleted_thenSetOrderStatusPaid() {
        Order order = buildOrder(20L);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        when(orderRepository.findById(20L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                .orderId(20L)
                .paymentId(2000L)
                .paymentStatus(PaymentStatus.COMPLETED.name())
                .build();

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

        assertThat(order.getPaymentId()).isEqualTo(2000L);
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(result.orderStatus()).isEqualTo(OrderStatus.PAID.getName());
    }

    @Test
    void updateOrderPaymentStatus_whenPaymentNotCompleted_thenKeepOrderStatus() {
        Order order = buildOrder(21L);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        when(orderRepository.findById(21L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                .orderId(21L)
                .paymentId(2100L)
                .paymentStatus(PaymentStatus.PENDING.name())
                .build();

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(result.orderStatus()).isEqualTo(OrderStatus.PENDING.getName());
    }

    @Test
    void rejectOrder_whenOrderExists_thenSetRejectStatusAndReason() {
        Order order = buildOrder(30L);
        when(orderRepository.findById(30L)).thenReturn(Optional.of(order));

        orderService.rejectOrder(30L, "Out of stock");

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
        assertThat(order.getRejectReason()).isEqualTo("Out of stock");
        verify(orderRepository).save(order);
    }

    @Test
    void acceptOrder_whenOrderExists_thenSetAcceptedStatus() {
        Order order = buildOrder(31L);
        when(orderRepository.findById(31L)).thenReturn(Optional.of(order));

        orderService.acceptOrder(31L);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
        verify(orderRepository).save(order);
    }

    private static Order buildOrder(Long id) {
        OrderAddress billing = new OrderAddress();
        billing.setId(id + 100);
        billing.setPhone("0900000000");

        Order order = new Order();
        order.setId(id);
        order.setEmail("order-" + id + "@yas.local");
        order.setBillingAddressId(billing);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setCreatedOn(ZonedDateTime.now());
        return order;
    }
}

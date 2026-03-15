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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

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

    private Order order;

    @BeforeEach
    void setUp() {
        order = buildOrder(10L, OrderStatus.ACCEPTED, PaymentStatus.PENDING);
    }

    @Test
    void updateOrderPaymentStatus_whenCompleted_shouldSetPaidStatusAndReturnResponse() {
        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                .orderId(order.getId())
                .paymentId(999L)
                .paymentStatus(PaymentStatus.COMPLETED.name())
                .build();

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

        assertThat(order.getPaymentId()).isEqualTo(999L);
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(result.orderId()).isEqualTo(order.getId());
        assertThat(result.orderStatus()).isEqualTo(OrderStatus.PAID.getName());
        assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED.name());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderPaymentStatus_whenNotCompleted_shouldNotChangeOrderStatus() {
        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                .orderId(order.getId())
                .paymentId(222L)
                .paymentStatus(PaymentStatus.PENDING.name())
                .build();

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
        assertThat(result.orderStatus()).isEqualTo(OrderStatus.ACCEPTED.getName());
        verify(orderRepository).save(order);
    }

    @Test
    void rejectOrder_whenOrderExists_shouldSetRejectStatusAndReason() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        orderService.rejectOrder(order.getId(), "invalid payment");

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertThat(captor.getValue().getOrderStatus()).isEqualTo(OrderStatus.REJECT);
        assertThat(captor.getValue().getRejectReason()).isEqualTo("invalid payment");
    }

    @Test
    void acceptOrder_whenOrderExists_shouldSetAcceptedStatus() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        orderService.acceptOrder(order.getId());

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertThat(captor.getValue().getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void getLatestOrders_whenCountIsZero_shouldReturnEmptyAndSkipRepository() {
        var result = orderService.getLatestOrders(0);

        assertThat(result).isEmpty();
        verify(orderRepository, never()).getLatestOrders(any(Pageable.class));
    }

    @Test
    void getLatestOrders_whenRepositoryReturnsData_shouldMapToBriefVm() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of(order));

        var result = orderService.getLatestOrders(2);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(order.getId());
        assertThat(result.getFirst().email()).isEqualTo(order.getEmail());
        assertThat(result.getFirst().orderStatus()).isEqualTo(order.getOrderStatus());
    }

    @Test
    void findOrderByCheckoutId_whenMissing_shouldThrowNotFoundException() {
        when(orderRepository.findByCheckoutId("missing-checkout")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.findOrderByCheckoutId("missing-checkout"));
    }

    private Order buildOrder(Long id, OrderStatus orderStatus, PaymentStatus paymentStatus) {
        OrderAddress billingAddress = OrderAddress.builder()
                .id(1L)
                .contactName("john")
                .phone("0123456789")
                .addressLine1("street")
                .city("city")
                .zipCode("700000")
                .districtId(1L)
                .districtName("district")
                .stateOrProvinceId(1L)
                .stateOrProvinceName("state")
                .countryId(84L)
                .countryName("VN")
                .build();

        return Order.builder()
                .id(id)
                .email("user@example.com")
                .billingAddressId(billingAddress)
                .orderStatus(orderStatus)
                .paymentStatus(paymentStatus)
                .build();
    }
}

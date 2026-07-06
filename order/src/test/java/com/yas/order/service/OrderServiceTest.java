package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.model.csv.OrderItemCsv;
import com.yas.order.model.request.OrderRequest;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderBriefVm;
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;

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

    private Order orderEntity;
    private OrderAddress billing;
    private OrderAddress shipping;

    @BeforeEach
    void setUp() {
        billing = OrderAddress.builder().id(1L).phone("1").contactName("b").build();
        shipping = OrderAddress.builder().id(2L).phone("2").contactName("s").build();
        orderEntity = Order.builder()
            .id(10L)
            .email("u@test.com")
            .billingAddressId(billing)
            .shippingAddressId(shipping)
            .totalPrice(BigDecimal.TEN)
            .orderStatus(OrderStatus.PENDING)
            .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
            .deliveryStatus(DeliveryStatus.PREPARING)
            .paymentStatus(PaymentStatus.PENDING)
            .checkoutId("chk-1")
            .build();
    }

    @Test
    void getOrderWithItemsById_returnsVm() {
        OrderItem item = OrderItem.builder().id(1L).productId(99L).orderId(10L).build();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(orderEntity));
        when(orderItemRepository.findAllByOrderId(10L)).thenReturn(List.of(item));

        OrderVm vm = orderService.getOrderWithItemsById(10L);

        assertEquals(10L, vm.id());
        assertEquals(1, vm.orderItemVms().size());
    }

    @Test
    void getOrderWithItemsById_throwsWhenMissing() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(1L));
    }

    @Test
    void getLatestOrders_returnsEmptyWhenCountNotPositive() {
        assertTrue(orderService.getLatestOrders(0).isEmpty());
        assertTrue(orderService.getLatestOrders(-1).isEmpty());
    }

    @Test
    void getLatestOrders_returnsEmptyWhenNoOrders() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(Collections.emptyList());
        assertTrue(orderService.getLatestOrders(5).isEmpty());
    }

    @Test
    void getLatestOrders_mapsOrders() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of(orderEntity));

        List<OrderBriefVm> list = orderService.getLatestOrders(3);

        assertEquals(1, list.size());
        assertEquals("u@test.com", list.getFirst().email());
    }

    @Test
    void findOrderByCheckoutId_returnsOrder() {
        when(orderRepository.findByCheckoutId("chk")).thenReturn(Optional.of(orderEntity));
        assertEquals(10L, orderService.findOrderByCheckoutId("chk").getId());
    }

    @Test
    void findOrderByCheckoutId_throwsWhenMissing() {
        when(orderRepository.findByCheckoutId("x")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.findOrderByCheckoutId("x"));
    }

    @Test
    void findOrderVmByCheckoutId_returnsGetVm() {
        OrderItem item = OrderItem.builder().id(3L).orderId(10L).productId(1L).build();
        when(orderRepository.findByCheckoutId("chk")).thenReturn(Optional.of(orderEntity));
        when(orderItemRepository.findAllByOrderId(10L)).thenReturn(List.of(item));

        OrderGetVm vm = orderService.findOrderVmByCheckoutId("chk");

        assertEquals(10L, vm.id());
    }

    @Test
    void updateOrderPaymentStatus_setsPaidWhenCompleted() {
        PaymentOrderStatusVm req = PaymentOrderStatusVm.builder()
            .orderId(10L)
            .paymentId(500L)
            .paymentStatus(PaymentStatus.COMPLETED.name())
            .orderStatus("ignored")
            .build();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentOrderStatusVm out = orderService.updateOrderPaymentStatus(req);

        assertEquals(10L, out.orderId());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrderPaymentStatus_throwsWhenOrderMissing() {
        PaymentOrderStatusVm req = PaymentOrderStatusVm.builder()
            .orderId(99L)
            .paymentId(1L)
            .paymentStatus(PaymentStatus.PENDING.name())
            .build();
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.updateOrderPaymentStatus(req));
    }

    @Test
    void rejectOrder_updatesStatus() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        orderService.rejectOrder(10L, "bad");

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void acceptOrder_updatesStatus() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        orderService.acceptOrder(10L);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getAllOrder_returnsEmptyVmWhenPageEmpty() {
        ZonedDateTime from = ZonedDateTime.now().minusDays(1);
        ZonedDateTime to = ZonedDateTime.now();
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(Page.empty());

        OrderListVm list = orderService.getAllOrder(
            Pair.of(from, to),
            "p",
            List.of(OrderStatus.PENDING),
            Pair.of("US", "123"),
            "e@x.com",
            Pair.of(0, 10)
        );

        assertEquals(0, list.totalElements());
    }

    @Test
    void getAllOrder_returnsContent() {
        ZonedDateTime from = ZonedDateTime.now().minusDays(1);
        ZonedDateTime to = ZonedDateTime.now();
        Page<Order> page = new PageImpl<>(List.of(orderEntity));
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        OrderListVm list = orderService.getAllOrder(
            Pair.of(from, to),
            "",
            Collections.emptyList(),
            Pair.of("", ""),
            "",
            Pair.of(0, 10)
        );

        assertEquals(1, list.orderList().size());
        assertEquals(1, list.totalElements());
    }

    @Test
    void exportCsv_returnsBytesWhenNoOrders() throws IOException {
        ZonedDateTime from = ZonedDateTime.now().minusDays(1);
        ZonedDateTime to = ZonedDateTime.now();
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(Page.empty());

        OrderRequest req = OrderRequest.builder()
            .createdFrom(from)
            .createdTo(to)
            .productName("")
            .orderStatus(Collections.emptyList())
            .billingCountry("")
            .billingPhoneNumber("")
            .email("")
            .pageNo(0)
            .pageSize(10)
            .build();

        byte[] csv = orderService.exportCsv(req);

        assertNotNull(csv);
    }

    @Test
    void exportCsv_mapsRowsWhenOrdersPresent() throws IOException {
        ZonedDateTime from = ZonedDateTime.now().minusDays(1);
        ZonedDateTime to = ZonedDateTime.now();
        Page<Order> page = new PageImpl<>(List.of(orderEntity));
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        OrderItemCsv csvRow = OrderItemCsv.builder().email("u@test.com").build();
        when(orderMapper.toCsv(any(OrderBriefVm.class))).thenReturn(csvRow);

        OrderRequest req = OrderRequest.builder()
            .createdFrom(from)
            .createdTo(to)
            .productName("")
            .orderStatus(Collections.emptyList())
            .billingCountry("")
            .billingPhoneNumber("")
            .email("")
            .pageNo(0)
            .pageSize(10)
            .build();

        byte[] csv = orderService.exportCsv(req);

        assertNotNull(csv);
        verify(orderMapper).toCsv(any(OrderBriefVm.class));
    }
}

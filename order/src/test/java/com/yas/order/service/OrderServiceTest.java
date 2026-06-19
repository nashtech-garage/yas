package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.PaymentMethod;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private ProductService productService;
    private CartService cartService;
    private OrderMapper orderMapper;
    private PromotionService promotionService;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);
        productService = mock(ProductService.class);
        cartService = mock(CartService.class);
        orderMapper = mock(OrderMapper.class);
        promotionService = mock(PromotionService.class);
        orderService = new OrderService(
                orderRepository,
                orderItemRepository,
                productService,
                cartService,
                orderMapper,
                promotionService
        );
    }

    @Test
    void createOrder_Success() {
        OrderAddressPostVm addressPostVm = OrderAddressPostVm.builder()
                .phone("123456789")
                .contactName("Contact Name")
                .addressLine1("Address Line 1")
                .city("City")
                .zipCode("12345")
                .districtId(1L)
                .districtName("District")
                .stateOrProvinceId(1L)
                .stateOrProvinceName("State")
                .countryId(1L)
                .countryName("Vietnam")
                .build();

        OrderPostVm orderPostVm = OrderPostVm.builder()
                .checkoutId("CHK123")
                .email("test@example.com")
                .note("Order Note")
                .tax(0.0f)
                .discount(0.0f)
                .numberItem(1)
                .totalPrice(BigDecimal.valueOf(100.0))
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(Collections.emptyList())
                .billingAddressPostVm(addressPostVm)
                .shippingAddressPostVm(addressPostVm)
                .build();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            return order;
        });
        
        // Mocking for acceptOrder which is called inside createOrder
        Order mockOrder = Order.builder().id(1L).build();
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));

        OrderVm result = orderService.createOrder(orderPostVm);

        assertNotNull(result);
        assertEquals("test@example.com", result.email());
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(anyList());
    }

    @Test
    void getOrderWithItemsById_Success() {
        Order order = Order.builder().id(1L).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(Collections.emptyList());

        OrderVm result = orderService.getOrderWithItemsById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getOrderWithItemsById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(1L));
    }

    @Test
    void updateOrderPaymentStatus_Success() {
        Order order = Order.builder()
                .id(1L)
                .orderStatus(OrderStatus.PENDING)
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentOrderStatusVm paymentVm = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentId(123L)
                .paymentStatus("COMPLETED")
                .build();

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(paymentVm);

        assertNotNull(result);
        assertEquals("PAID", result.orderStatus());
        assertEquals(123L, result.paymentId());
        
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(PaymentStatus.COMPLETED, orderCaptor.getValue().getPaymentStatus());
        assertEquals(OrderStatus.PAID, orderCaptor.getValue().getOrderStatus());
    }

    @Test
    void acceptOrder_Success() {
        Order order = Order.builder().id(1L).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.acceptOrder(1L);

        verify(orderRepository).save(any(Order.class));
        assertEquals(OrderStatus.ACCEPTED, order.getOrderStatus());
    }

    @Test
    void rejectOrder_Success() {
        Order order = Order.builder().id(1L).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.rejectOrder(1L, "Reason");

        verify(orderRepository).save(any(Order.class));
        assertEquals(OrderStatus.REJECT, order.getOrderStatus());
        assertEquals("Reason", order.getRejectReason());
    }
}

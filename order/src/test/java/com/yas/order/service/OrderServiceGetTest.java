package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderBriefVm;
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderVm;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;

@ExtendWith(MockitoExtension.class)
class OrderServiceGetTest {

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
    private OrderItem orderItem;
    private com.yas.order.model.OrderAddress address;
    private MockedStatic<AuthenticationUtils> authenticationUtilsMock;

    @BeforeEach
    void setUp() {
        address = com.yas.order.model.OrderAddress.builder()
                .id(1L)
                .phone("123")
                .contactName("Contact")
                .addressLine1("Line 1")
                .build();
                
        order = Order.builder()
                .id(1L)
                .email("test@example.com")
                .orderStatus(OrderStatus.PENDING)
                .checkoutId("checkout-1")
                .shippingAddressId(address)
                .billingAddressId(address)
                .build();
        
        orderItem = OrderItem.builder()
                .id(1L)
                .orderId(1L)
                .productId(10L)
                .quantity(2)
                .build();
                
        authenticationUtilsMock = Mockito.mockStatic(AuthenticationUtils.class);
    }

    @AfterEach
    void tearDown() {
        authenticationUtilsMock.close();
    }

    @Test
    void getOrderWithItemsById_whenOrderNotFound_thenThrowNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(1L));
    }

    @Test
    void getOrderWithItemsById_whenOrderExists_thenReturnOrderVm() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(orderItem));

        OrderVm result = orderService.getOrderWithItemsById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.orderItemVms()).hasSize(1);
    }

    @Test
    void findOrderByCheckoutId_whenNotFound_thenThrowNotFoundException() {
        when(orderRepository.findByCheckoutId("checkout-1")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.findOrderByCheckoutId("checkout-1"));
    }

    @Test
    void findOrderByCheckoutId_whenFound_thenReturnOrder() {
        when(orderRepository.findByCheckoutId("checkout-1")).thenReturn(Optional.of(order));
        Order result = orderService.findOrderByCheckoutId("checkout-1");
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findOrderVmByCheckoutId_whenFound_thenReturnOrderGetVm() {
        when(orderRepository.findByCheckoutId("checkout-1")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(orderItem));

        OrderGetVm result = orderService.findOrderVmByCheckoutId("checkout-1");
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getLatestOrders_whenCountIsZero_thenReturnEmpty() {
        List<OrderBriefVm> result = orderService.getLatestOrders(0);
        assertThat(result).isEmpty();
    }

    @Test
    void getLatestOrders_whenNoOrders_thenReturnEmpty() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of());
        List<OrderBriefVm> result = orderService.getLatestOrders(5);
        assertThat(result).isEmpty();
    }

    @Test
    void getLatestOrders_whenHasOrders_thenReturnList() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of(order));
        List<OrderBriefVm> result = orderService.getLatestOrders(5);
        assertThat(result).hasSize(1);
    }

    @Test
    void getMyOrders_whenCalled_thenReturnList() {
        authenticationUtilsMock.when(AuthenticationUtils::extractUserId).thenReturn("user-1");
        when(orderRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(order));

        List<OrderGetVm> result = orderService.getMyOrders("product", OrderStatus.PENDING);
        assertThat(result).hasSize(1);
    }

    @Test
    void getAllOrder_whenEmpty_thenReturnEmptyOrderListVm() {
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());
        
        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now(), ZonedDateTime.now()), "product", List.of(), Pair.of("VN", "123"), "email", Pair.of(0, 10));
        
        assertThat(result.orderList()).isNull();
        assertThat(result.totalElements()).isEqualTo(0);
    }

    @Test
    void getAllOrder_whenHasData_thenReturnOrderListVm() {
        Page<Order> page = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        
        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now(), ZonedDateTime.now()), "product", List.of(), Pair.of("VN", "123"), "email", Pair.of(0, 10));
        
        assertThat(result.orderList()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }
}

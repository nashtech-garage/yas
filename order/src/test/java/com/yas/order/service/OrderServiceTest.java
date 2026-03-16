package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setSubjectUpSecurityContext;
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
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentMethod;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.promotion.PromotionUsageVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    void updateOrderPaymentStatus_whenOrderNotFound_thenThrowNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                .orderId(99L)
                .paymentId(9900L)
                .paymentStatus(PaymentStatus.PENDING.name())
                .build();

        assertThrows(NotFoundException.class, () -> orderService.updateOrderPaymentStatus(request));
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

    @Test
    void rejectOrder_whenOrderMissing_thenThrowNotFoundException() {
        when(orderRepository.findById(33L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.rejectOrder(33L, "not-found"));
    }

    @Test
    void acceptOrder_whenOrderMissing_thenThrowNotFoundException() {
        when(orderRepository.findById(34L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.acceptOrder(34L));
    }

    @Test
    void createOrder_whenValidInput_thenCreateAndCallDependencies() {
        OrderPostVm orderPostVm = buildOrderPostVm();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(500L);
            return saved;
        });
        when(orderItemRepository.saveAll(any())).thenAnswer(invocation -> {
            Iterable<OrderItem> items = invocation.getArgument(0);
            List<OrderItem> savedItems = new ArrayList<>();
            items.forEach(savedItems::add);
            return savedItems;
        });
        when(orderRepository.findById(500L)).thenAnswer(invocation ->
            Optional.of(buildOrder(500L))
        );

        var result = orderService.createOrder(orderPostVm);

        assertThat(result.id()).isEqualTo(500L);
        assertThat(result.email()).isEqualTo(orderPostVm.email());
        assertThat(result.orderItemVms()).hasSize(2);

        verify(productService).subtractProductStockQuantity(any());
        verify(cartService).deleteCartItems(any());
        verify(orderRepository).findById(500L);

        ArgumentCaptor<List<PromotionUsageVm>> promotionCaptor = ArgumentCaptor.forClass(List.class);
        verify(promotionService).updateUsagePromotion(promotionCaptor.capture());
        assertThat(promotionCaptor.getValue()).hasSize(2);
    }

    @Test
    void getOrderWithItemsById_whenFound_thenReturnOrderVm() {
        Order order = buildOrder(61L);
        List<OrderItem> orderItems = List.of(
                buildOrderItem(1001L, 61L),
                buildOrderItem(1002L, 61L)
        );

        when(orderRepository.findById(61L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(61L)).thenReturn(orderItems);

        var result = orderService.getOrderWithItemsById(61L);

        assertThat(result.id()).isEqualTo(61L);
        assertThat(result.orderItemVms()).hasSize(2);
    }

    @Test
    void getOrderWithItemsById_whenMissing_thenThrowNotFoundException() {
        when(orderRepository.findById(62L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(62L));
    }

    @Test
    void findOrderVmByCheckoutId_whenFound_thenReturnOrderWithItems() {
        Order order = buildOrder(70L);
        order.setCheckoutId("checkout-70");
        when(orderRepository.findByCheckoutId("checkout-70")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(70L)).thenReturn(List.of(buildOrderItem(2001L, 70L)));

        var result = orderService.findOrderVmByCheckoutId("checkout-70");

        assertThat(result.id()).isEqualTo(70L);
        assertThat(result.orderItems()).hasSize(1);
    }

    @Test
    void getAllOrder_whenNoData_thenReturnEmptyOrderListVm() {
        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(Page.empty());

        var result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
                "",
                Collections.emptyList(),
                Pair.of("", ""),
                "user@yas.local",
                Pair.of(0, 10)
        );

        assertThat(result.orderList()).isNull();
        assertThat(result.totalElements()).isEqualTo(0);
    }

    @Test
    void getAllOrder_whenHasData_thenReturnMappedResult() {
        Order order = buildOrder(80L);
        Page<Order> orderPage = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(orderPage);

        var result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
                "",
                Collections.singletonList(OrderStatus.PENDING),
                Pair.of("VN", "0900000000"),
                "user@yas.local",
                Pair.of(0, 10)
        );

        assertThat(result.orderList()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void getMyOrders_whenHasData_thenReturnMyOrders() {
        setSubjectUpSecurityContext("user-1");
        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(buildOrder(90L)));

        var result = orderService.getMyOrders("", OrderStatus.PENDING);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(90L);
    }

    private static Order buildOrder(Long id) {
        OrderAddress billing = new OrderAddress();
        billing.setId(id + 100);
        billing.setPhone("0900000000");

        OrderAddress shipping = new OrderAddress();
        shipping.setId(id + 200);
        shipping.setPhone("0911111111");

        Order order = new Order();
        order.setId(id);
        order.setEmail("order-" + id + "@yas.local");
        order.setShippingAddressId(shipping);
        order.setBillingAddressId(billing);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setDeliveryStatus(DeliveryStatus.PREPARING);
        order.setDeliveryMethod(DeliveryMethod.GRAB_EXPRESS);
        order.setTotalPrice(new BigDecimal("100.00"));
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setCreatedOn(ZonedDateTime.now());
        return order;
    }

    private static OrderPostVm buildOrderPostVm() {
        OrderAddressPostVm shippingAddress = OrderAddressPostVm.builder()
                .contactName("Ship User")
                .phone("0909000001")
                .addressLine1("Line 1")
                .addressLine2("Line 2")
                .city("HCM")
                .zipCode("700000")
                .districtId(1L)
                .districtName("District 1")
                .stateOrProvinceId(79L)
                .stateOrProvinceName("Ho Chi Minh")
                .countryId(84L)
                .countryName("VN")
                .build();

        OrderAddressPostVm billingAddress = OrderAddressPostVm.builder()
                .contactName("Bill User")
                .phone("0909000002")
                .addressLine1("Line A")
                .addressLine2("Line B")
                .city("HCM")
                .zipCode("700000")
                .districtId(2L)
                .districtName("District 2")
                .stateOrProvinceId(79L)
                .stateOrProvinceName("Ho Chi Minh")
                .countryId(84L)
                .countryName("VN")
                .build();

        return OrderPostVm.builder()
                .checkoutId("checkout-500")
                .email("buyer@yas.local")
                .shippingAddressPostVm(shippingAddress)
                .billingAddressPostVm(billingAddress)
                .note("test-order")
                .tax(2.0f)
                .discount(1.0f)
                .numberItem(2)
                .totalPrice(new BigDecimal("99.00"))
                .deliveryFee(new BigDecimal("5.00"))
                .couponCode("SALE")
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of(
                        OrderItemPostVm.builder()
                                .productId(1001L)
                                .productName("Product A")
                                .quantity(1)
                                .productPrice(new BigDecimal("30.00"))
                                .build(),
                        OrderItemPostVm.builder()
                                .productId(1002L)
                                .productName("Product B")
                                .quantity(1)
                                .productPrice(new BigDecimal("69.00"))
                                .build()
                ))
                .build();
    }

    private static OrderItem buildOrderItem(Long productId, Long orderId) {
        return OrderItem.builder()
                .id(productId + 10000)
                .orderId(orderId)
                .productId(productId)
                .productName("Product-" + productId)
                .quantity(1)
                .productPrice(new BigDecimal("10.00"))
                .build();
    }
}

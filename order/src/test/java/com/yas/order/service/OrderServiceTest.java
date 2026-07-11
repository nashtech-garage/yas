package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.csv.OrderItemCsv;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentMethod;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.model.request.OrderRequest;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderBriefVm;
import com.yas.order.viewmodel.order.OrderExistsByProductAndUserGetVm;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import com.yas.order.viewmodel.promotion.PromotionUsageVm;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    private Order order;

    @BeforeEach
    void setUp() {
        order = buildOrder(1L, "checkout-1", OrderStatus.PENDING, PaymentStatus.PENDING);
    }

    @Test
    void createOrder_whenValidPayload_shouldPersistAndTriggerDependentActions() {
        OrderPostVm postVm = buildOrderPostVm();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderVm result = orderService.createOrder(postVm);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.orderItemVms()).hasSize(2);
        verify(productService).subtractProductStockQuantity(any(OrderVm.class));
        verify(cartService).deleteCartItems(any(OrderVm.class));
        verify(promotionService).updateUsagePromotion(any(List.class));
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void getOrderWithItemsById_whenFound_shouldReturnVmWithItems() {
        List<OrderItem> orderItems = List.of(
                buildOrderItem(100L, 1L, "item-a", 2),
                buildOrderItem(101L, 1L, "item-b", 1)
        );
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(orderItems);

        OrderVm result = orderService.getOrderWithItemsById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.orderItemVms()).hasSize(2);
    }

    @Test
    void getOrderWithItemsById_whenNotFound_shouldThrowNotFoundException() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderWithItemsById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllOrder_whenEmptyPage_shouldReturnEmptyResult() {
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
                "phone",
                List.of(),
                Pair.of("VN", "0123"),
                "customer@example.com",
                Pair.of(0, 10)
        );

        assertThat(result.orderList()).isNull();
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();
    }

    @Test
    void getAllOrder_whenPageHasData_shouldMapToOrderBriefVm() {
        Page<Order> orderPage = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(orderPage);

        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
                "phone",
                List.of(OrderStatus.PENDING),
                Pair.of("VN", "0123"),
                "customer@example.com",
                Pair.of(0, 10)
        );

        assertThat(result.orderList()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    void getLatestOrders_whenCountLessThanOrEqualZero_shouldReturnEmptyList() {
        List<OrderBriefVm> result = orderService.getLatestOrders(0);

        assertThat(result).isEmpty();
    }

    @Test
    void getLatestOrders_whenRepositoryReturnsEmpty_shouldReturnEmptyList() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of());

        List<OrderBriefVm> result = orderService.getLatestOrders(5);

        assertThat(result).isEmpty();
    }

    @Test
    void getLatestOrders_whenRepositoryReturnsOrders_shouldReturnMappedItems() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of(order));

        List<OrderBriefVm> result = orderService.getLatestOrders(5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void findOrderByCheckoutId_whenFound_shouldReturnOrder() {
        when(orderRepository.findByCheckoutId("checkout-1")).thenReturn(Optional.of(order));

        Order result = orderService.findOrderByCheckoutId("checkout-1");

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findOrderByCheckoutId_whenNotFound_shouldThrowNotFoundException() {
        when(orderRepository.findByCheckoutId("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findOrderByCheckoutId("missing"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findOrderVmByCheckoutId_whenFound_shouldReturnOrderVm() {
        List<OrderItem> orderItems = List.of(buildOrderItem(100L, 1L, "item-a", 2));
        when(orderRepository.findByCheckoutId("checkout-1")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(orderItems);

        var result = orderService.findOrderVmByCheckoutId("checkout-1");

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.orderItems()).hasSize(1);
    }

    @Test
    void updateOrderPaymentStatus_whenCompleted_shouldMarkOrderAsPaid() {
        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentId(100L)
                .paymentStatus(PaymentStatus.COMPLETED.name())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(result.orderId()).isEqualTo(1L);
        assertThat(result.paymentId()).isEqualTo(100L);
    }

    @Test
    void updateOrderPaymentStatus_whenNonCompleted_shouldNotChangeOrderStatusToPaid() {
        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentId(101L)
                .paymentStatus(PaymentStatus.PENDING.name())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.updateOrderPaymentStatus(request);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void rejectOrder_whenFound_shouldSetRejectStatusAndReason() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.rejectOrder(1L, "out-of-stock");

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
        assertThat(order.getRejectReason()).isEqualTo("out-of-stock");
        verify(orderRepository).save(order);
    }

    @Test
    void acceptOrder_whenFound_shouldSetAcceptedStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.acceptOrder(1L);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
        verify(orderRepository).save(order);
    }

    @Test
    void exportCsv_whenOrderListIsNull_shouldReturnCsvBytes() throws IOException {
        OrderService serviceSpy = org.mockito.Mockito.spy(orderService);
        OrderRequest request = buildOrderRequest();
        doReturn(new OrderListVm(null, 0, 0)).when(serviceSpy)
            .getAllOrder(any(), any(), any(), any(), any(), any());

        byte[] result = serviceSpy.exportCsv(request);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void exportCsv_whenOrderListHasData_shouldUseOrderMapper() throws IOException {
        OrderService serviceSpy = org.mockito.Mockito.spy(orderService);
        OrderRequest request = buildOrderRequest();
        OrderBriefVm briefVm = OrderBriefVm.builder()
                .id(1L)
                .email("customer@example.com")
                .billingAddressVm(OrderAddressVm.builder().phone("0123").build())
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .totalPrice(BigDecimal.TEN)
                .createdOn(ZonedDateTime.now())
                .build();
        doReturn(new OrderListVm(List.of(briefVm), 1, 1)).when(serviceSpy)
            .getAllOrder(any(), any(), any(), any(), any(), any());
        when(orderMapper.toCsv(briefVm)).thenReturn(OrderItemCsv.builder().email("customer@example.com").build());

        byte[] result = serviceSpy.exportCsv(request);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
        verify(orderMapper).toCsv(briefVm);
    }

    @Test
    void createOrder_shouldCreatePromotionUsageEntriesForOrderItems() {
        OrderPostVm postVm = buildOrderPostVm();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.createOrder(postVm);

        ArgumentCaptor<List<PromotionUsageVm>> captor = ArgumentCaptor.forClass(List.class);
        verify(promotionService).updateUsagePromotion(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

    private OrderPostVm buildOrderPostVm() {
        OrderAddressPostVm address = OrderAddressPostVm.builder()
                .contactName("John")
                .phone("0123456789")
                .addressLine1("123 Main St")
                .addressLine2("Apt 10")
                .city("HCM")
                .zipCode("70000")
                .districtId(1L)
                .districtName("D1")
                .stateOrProvinceId(79L)
                .stateOrProvinceName("HCM")
                .countryId(84L)
                .countryName("Vietnam")
                .build();

        List<OrderItemPostVm> items = List.of(
                OrderItemPostVm.builder()
                        .productId(100L)
                        .productName("Product A")
                        .quantity(2)
                        .productPrice(BigDecimal.valueOf(99_000))
                        .note("note a")
                        .build(),
                OrderItemPostVm.builder()
                        .productId(101L)
                        .productName("Product B")
                        .quantity(1)
                        .productPrice(BigDecimal.valueOf(55_000))
                        .note("note b")
                        .build()
        );

        return OrderPostVm.builder()
                .checkoutId("checkout-1")
                .email("customer@example.com")
                .shippingAddressPostVm(address)
                .billingAddressPostVm(address)
                .note("deliver in office hours")
                .tax(1.2f)
                .discount(0.3f)
                .numberItem(3)
                .totalPrice(BigDecimal.valueOf(253_000))
                .deliveryFee(BigDecimal.valueOf(15_000))
                .couponCode("SPRING")
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(items)
                .build();
    }

    private Order buildOrder(Long id, String checkoutId, OrderStatus orderStatus, PaymentStatus paymentStatus) {
        OrderAddress shippingAddress = OrderAddress.builder()
                .id(11L)
                .phone("0987654321")
                .contactName("Ship")
                .addressLine1("Ship Street")
                .city("HCM")
                .zipCode("70000")
                .districtId(1L)
                .districtName("D1")
                .stateOrProvinceId(79L)
                .stateOrProvinceName("HCM")
                .countryId(84L)
                .countryName("Vietnam")
                .build();

        OrderAddress billingAddress = OrderAddress.builder()
                .id(12L)
                .phone("0123123123")
                .contactName("Bill")
                .addressLine1("Bill Street")
                .city("HCM")
                .zipCode("70000")
                .districtId(1L)
                .districtName("D1")
                .stateOrProvinceId(79L)
                .stateOrProvinceName("HCM")
                .countryId(84L)
                .countryName("Vietnam")
                .build();

        return Order.builder()
                .id(id)
                .email("customer@example.com")
                .checkoutId(checkoutId)
                .note("note")
                .tax(1.1f)
                .discount(0.4f)
                .numberItem(2)
                .totalPrice(BigDecimal.valueOf(100_000))
                .deliveryFee(BigDecimal.valueOf(10_000))
                .couponCode("CODE")
                .orderStatus(orderStatus)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(paymentStatus)
                .shippingAddressId(shippingAddress)
                .billingAddressId(billingAddress)
                .build();
    }

    private OrderItem buildOrderItem(Long productId, Long orderId, String name, int quantity) {
        return OrderItem.builder()
                .productId(productId)
                .orderId(orderId)
                .productName(name)
                .quantity(quantity)
                .productPrice(BigDecimal.valueOf(10_000))
                .note("note")
                .build();
    }

    private OrderRequest buildOrderRequest() {
        return OrderRequest.builder()
                .createdFrom(ZonedDateTime.now().minusDays(1))
                .createdTo(ZonedDateTime.now())
                .warehouse("main")
                .productName("Product")
                .orderStatus(List.of(OrderStatus.PENDING))
                .billingPhoneNumber("0123")
                .email("customer@example.com")
                .billingCountry("VN")
                .pageNo(0)
                .pageSize(10)
                .build();
    }
}

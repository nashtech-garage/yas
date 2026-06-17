package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.utils.AuthenticationUtils;
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
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.product.ProductVariationVm;
import com.yas.order.viewmodel.promotion.PromotionUsageVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;

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
    void createOrderShouldPersistAndTriggerDependencies() {
        OrderPostVm orderPostVm = buildOrderPostVm();
        AtomicReference<Order> savedOrderRef = new AtomicReference<>();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(1L);
            savedOrderRef.set(saved);
            return saved;
        });
        when(orderRepository.findById(1L)).thenAnswer(invocation -> Optional.ofNullable(savedOrderRef.get()));
        when(orderItemRepository.saveAll(any(Set.class))).thenAnswer(invocation -> {
            Set<OrderItem> items = invocation.getArgument(0);
            return new ArrayList<>(items);
        });

        OrderVm result = orderService.createOrder(orderPostVm);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(savedOrderRef.get().getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);

        verify(productService).subtractProductStockQuantity(any(OrderVm.class));
        verify(cartService).deleteCartItems(any(OrderVm.class));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<PromotionUsageVm>> promotionCaptor = ArgumentCaptor.forClass((Class) List.class);
        verify(promotionService).updateUsagePromotion(promotionCaptor.capture());
        assertThat(promotionCaptor.getValue()).hasSize(orderPostVm.orderItemPostVms().size());
    }

    @Test
    void getOrderWithItemsByIdShouldReturnOrderVm() {
        Order order = buildOrder(1L);
        List<OrderItem> items = List.of(buildOrderItem(order.getId(), 10L));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(items);

        OrderVm result = orderService.getOrderWithItemsById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.orderItemVms()).hasSize(1);
    }

    @Test
    void getOrderWithItemsByIdShouldThrowNotFoundWhenMissing() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(1L));
    }

    @Test
    void getAllOrderShouldReturnEmptyWhenPageIsEmpty() {
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of()));

        OrderListVm result = orderService.getAllOrder(
            Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
            "",
            List.of(),
            Pair.of("", ""),
            "",
            Pair.of(0, 10)
        );

        assertThat(result.orderList()).isNull();
        assertThat(result.totalElements()).isEqualTo(0);
        assertThat(result.totalPages()).isEqualTo(0);
    }

    @Test
    void getAllOrderShouldReturnOrderListWhenPageHasContent() {
        Order order = buildOrder(1L);
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(order)));

        OrderListVm result = orderService.getAllOrder(
            Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
            "product",
            List.of(OrderStatus.PENDING),
            Pair.of("", ""),
            "mail@example.com",
            Pair.of(0, 10)
        );

        assertThat(result.orderList()).hasSize(1);
    }

    @Test
    void getLatestOrdersShouldReturnEmptyWhenCountNonPositive() {
        List<OrderBriefVm> result = orderService.getLatestOrders(0);

        assertThat(result).isEmpty();
    }

    @Test
    void getLatestOrdersShouldReturnEmptyWhenRepositoryEmpty() {
        when(orderRepository.getLatestOrders(any(PageRequest.class))).thenReturn(List.of());

        List<OrderBriefVm> result = orderService.getLatestOrders(5);

        assertThat(result).isEmpty();
    }

    @Test
    void getLatestOrdersShouldReturnOrdersWhenPresent() {
        when(orderRepository.getLatestOrders(any(PageRequest.class)))
            .thenReturn(List.of(buildOrder(1L)));

        List<OrderBriefVm> result = orderService.getLatestOrders(5);

        assertThat(result).hasSize(1);
    }

    @Test
    void isOrderCompletedWithUserIdAndProductIdShouldReturnTrueWhenOrderExists() {
        List<ProductVariationVm> variations = List.of(
            new ProductVariationVm(10L, "Var A", "SKU-A"),
            new ProductVariationVm(20L, "Var B", "SKU-B")
        );

        try (MockedStatic<AuthenticationUtils> mocked = Mockito.mockStatic(AuthenticationUtils.class)) {
            mocked.when(AuthenticationUtils::extractUserId).thenReturn("user-1");
            when(productService.getProductVariations(1L)).thenReturn(variations);
            when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.of(buildOrder(1L)));

            OrderExistsByProductAndUserGetVm result = orderService.isOrderCompletedWithUserIdAndProductId(1L);

            assertThat(result.isPresent()).isTrue();
        }
    }

    @Test
    void isOrderCompletedWithUserIdAndProductIdShouldReturnFalseWhenOrderMissing() {
        try (MockedStatic<AuthenticationUtils> mocked = Mockito.mockStatic(AuthenticationUtils.class)) {
            mocked.when(AuthenticationUtils::extractUserId).thenReturn("user-1");
            when(productService.getProductVariations(1L)).thenReturn(List.of());
            when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

            OrderExistsByProductAndUserGetVm result = orderService.isOrderCompletedWithUserIdAndProductId(1L);

            assertThat(result.isPresent()).isFalse();
        }
    }

    @Test
    void getMyOrdersShouldReturnOrdersForCurrentUser() {
        try (MockedStatic<AuthenticationUtils> mocked = Mockito.mockStatic(AuthenticationUtils.class)) {
            mocked.when(AuthenticationUtils::extractUserId).thenReturn("user-1");
            when(orderRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(buildOrder(1L), buildOrder(2L)));

            List<OrderGetVm> result = orderService.getMyOrders("product", OrderStatus.PENDING);

            assertThat(result).hasSize(2);
        }
    }

    @Test
    void findOrderVmByCheckoutIdShouldReturnOrderGetVm() {
        Order order = buildOrder(1L);
        when(orderRepository.findByCheckoutId("checkout-1")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L))
            .thenReturn(List.of(buildOrderItem(1L, 20L)));

        OrderGetVm result = orderService.findOrderVmByCheckoutId("checkout-1");

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.orderItems()).hasSize(1);
    }

    @Test
    void findOrderByCheckoutIdShouldThrowNotFoundWhenMissing() {
        when(orderRepository.findByCheckoutId("checkout-1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.findOrderByCheckoutId("checkout-1"));
    }

    @Test
    void updateOrderPaymentStatusShouldSetPaidWhenCompleted() {
        Order order = buildOrder(1L);
        order.setOrderStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        PaymentOrderStatusVm request = new PaymentOrderStatusVm(
            1L,
            OrderStatus.ACCEPTED.getName(),
            100L,
            PaymentStatus.COMPLETED.name()
        );

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

        assertThat(result.orderStatus()).isEqualTo(OrderStatus.PAID.getName());
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void updateOrderPaymentStatusShouldKeepOrderStatusWhenNotCompleted() {
        Order order = buildOrder(1L);
        order.setOrderStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        PaymentOrderStatusVm request = new PaymentOrderStatusVm(
            1L,
            OrderStatus.ACCEPTED.getName(),
            100L,
            PaymentStatus.PENDING.name()
        );

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

        assertThat(result.orderStatus()).isEqualTo(OrderStatus.PENDING.getName());
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void rejectOrderShouldUpdateStatusAndReason() {
        Order order = buildOrder(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.rejectOrder(1L, "reject reason");

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
        assertThat(order.getRejectReason()).isEqualTo("reject reason");
        verify(orderRepository).save(order);
    }

    @Test
    void acceptOrderShouldUpdateStatus() {
        Order order = buildOrder(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.acceptOrder(1L);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
        verify(orderRepository).save(order);
    }

    @Test
    void exportCsvShouldReturnBytesForEmptyResult() throws Exception {
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of()));

        OrderRequest orderRequest = OrderRequest.builder()
            .createdFrom(ZonedDateTime.now().minusDays(1))
            .createdTo(ZonedDateTime.now())
            .productName("product")
            .orderStatus(List.of(OrderStatus.PENDING))
            .billingCountry("")
            .billingPhoneNumber("")
            .email("mail@example.com")
            .pageNo(0)
            .pageSize(10)
            .build();

        byte[] result = orderService.exportCsv(orderRequest);

        assertThat(result).isNotNull();
    }

    @Test
    void exportCsvShouldReturnBytesForOrders() throws Exception {
        Order order = buildOrder(1L);
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(order)));
        when(orderMapper.toCsv(any(OrderBriefVm.class))).thenReturn(OrderItemCsv.builder().build());

        OrderRequest orderRequest = OrderRequest.builder()
            .createdFrom(ZonedDateTime.now().minusDays(1))
            .createdTo(ZonedDateTime.now())
            .productName("product")
            .orderStatus(List.of(OrderStatus.PENDING))
            .billingCountry("")
            .billingPhoneNumber("")
            .email("mail@example.com")
            .pageNo(0)
            .pageSize(10)
            .build();

        byte[] result = orderService.exportCsv(orderRequest);

        assertThat(result).isNotNull();
        verify(orderMapper, times(1)).toCsv(any(OrderBriefVm.class));
    }

    private OrderPostVm buildOrderPostVm() {
        OrderAddressPostVm address = OrderAddressPostVm.builder()
            .contactName("contact")
            .phone("123")
            .addressLine1("line1")
            .addressLine2("line2")
            .city("city")
            .zipCode("zip")
            .districtId(1L)
            .districtName("district")
            .stateOrProvinceId(2L)
            .stateOrProvinceName("state")
            .countryId(3L)
            .countryName("country")
            .build();

        List<OrderItemPostVm> items = List.of(
            OrderItemPostVm.builder()
                .productId(10L)
                .productName("Product A")
                .quantity(2)
                .productPrice(new BigDecimal("10.00"))
                .note("note")
                .build(),
            OrderItemPostVm.builder()
                .productId(20L)
                .productName("Product B")
                .quantity(1)
                .productPrice(new BigDecimal("20.00"))
                .note("note")
                .build()
        );

        return OrderPostVm.builder()
            .checkoutId("checkout-1")
            .email("mail@example.com")
            .shippingAddressPostVm(address)
            .billingAddressPostVm(address)
            .note("note")
            .tax(1.0f)
            .discount(2.0f)
            .numberItem(3)
            .totalPrice(new BigDecimal("30.00"))
            .deliveryFee(new BigDecimal("5.00"))
            .couponCode("CODE")
            .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
            .paymentMethod(PaymentMethod.COD)
            .paymentStatus(PaymentStatus.PENDING)
            .orderItemPostVms(items)
            .build();
    }

    private Order buildOrder(Long id) {
        OrderAddress address = OrderAddress.builder()
            .id(1L)
            .contactName("contact")
            .phone("123")
            .addressLine1("line1")
            .addressLine2("line2")
            .city("city")
            .zipCode("zip")
            .districtId(1L)
            .districtName("district")
            .stateOrProvinceId(2L)
            .stateOrProvinceName("state")
            .countryId(3L)
            .countryName("country")
            .build();

        return Order.builder()
            .id(id)
            .email("mail@example.com")
            .note("note")
            .tax(1.0f)
            .discount(2.0f)
            .numberItem(3)
            .totalPrice(new BigDecimal("30.00"))
            .couponCode("CODE")
            .orderStatus(OrderStatus.PENDING)
            .deliveryFee(new BigDecimal("5.00"))
            .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
            .deliveryStatus(DeliveryStatus.PREPARING)
            .paymentStatus(PaymentStatus.PENDING)
            .shippingAddressId(address)
            .billingAddressId(address)
            .checkoutId("checkout-1")
            .build();
    }

    private OrderItem buildOrderItem(Long orderId, Long productId) {
        return OrderItem.builder()
            .id(1L)
            .orderId(orderId)
            .productId(productId)
            .productName("Product A")
            .quantity(1)
            .productPrice(new BigDecimal("10.00"))
            .discountAmount(BigDecimal.ZERO)
            .taxAmount(BigDecimal.ZERO)
            .taxPercent(BigDecimal.ZERO)
            .build();
    }
}

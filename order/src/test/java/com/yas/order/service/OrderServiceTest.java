package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setSubjectUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
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
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
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

    @Test
    void createOrder_shouldCreateOrderAndCallDownstreamServices() {
        OrderPostVm postVm = createOrderPostVm();
        AtomicReference<Order> savedOrderRef = new AtomicReference<>();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            if (order.getId() == null) {
                order.setId(100L);
            }
            savedOrderRef.set(order);
            return order;
        });
        when(orderRepository.findById(100L)).thenAnswer(invocation -> Optional.of(savedOrderRef.get()));
        when(orderItemRepository.saveAll(any())).thenReturn(List.of(
                createOrderItem(1L, 100L),
                createOrderItem(2L, 100L)));

        OrderVm result = orderService.createOrder(postVm);

        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.orderItemVms()).hasSize(2);
        assertThat(result.orderStatus()).isEqualTo(OrderStatus.PENDING);
        verify(productService).subtractProductStockQuantity(any(OrderVm.class));
        verify(cartService).deleteCartItems(any(OrderVm.class));
        verify(promotionService).updateUsagePromotion(any());
    }

    @Test
    void getOrderWithItemsById_whenOrderExists_shouldReturnVm() {
        Order order = createOrder(10L);
        List<OrderItem> items = List.of(createOrderItem(1L, 10L));
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(10L)).thenReturn(items);

        OrderVm result = orderService.getOrderWithItemsById(10L);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.orderItemVms()).hasSize(1);
    }

    @Test
    void getOrderWithItemsById_whenOrderNotFound_shouldThrow() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(999L));
    }

    @Test
    void getAllOrder_whenEmptyPage_shouldReturnEmptySummary() {
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());

        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
                null,
                List.of(),
                Pair.of("", ""),
                "",
                Pair.of(0, 10));

        assertThat(result.orderList()).isNull();
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();
    }

    @Test
    void getAllOrder_whenPageHasData_shouldMapToBriefVm() {
        Order order = createOrder(11L);
        Page<Order> page = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
                "shirt",
                List.of(OrderStatus.PENDING),
                Pair.of("vn", "0123"),
                "mail@example.com",
                Pair.of(0, 10));

        assertThat(result.orderList()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void getLatestOrders_whenInvalidCount_shouldReturnEmpty() {
        assertThat(orderService.getLatestOrders(0)).isEmpty();
    }

    @Test
    void getLatestOrders_whenRepositoryReturnsEmpty_shouldReturnEmpty() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of());

        assertThat(orderService.getLatestOrders(5)).isEmpty();
    }

    @Test
    void getLatestOrders_whenRepositoryReturnsData_shouldMap() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of(createOrder(3L)));

        List<OrderBriefVm> result = orderService.getLatestOrders(3);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(3L);
    }

    @Test
    void isOrderCompletedWithUserIdAndProductId_whenNoVariation_shouldUseOriginalProductId() {
        setSubjectUpSecurityContext("user-1");
        when(productService.getProductVariations(5L)).thenReturn(List.of());
        when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.of(createOrder(1L)));

        OrderExistsByProductAndUserGetVm result = orderService.isOrderCompletedWithUserIdAndProductId(5L);

        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void isOrderCompletedWithUserIdAndProductId_whenHasVariations_shouldCheckVariationIds() {
        setSubjectUpSecurityContext("user-1");
        when(productService.getProductVariations(5L)).thenReturn(List.of(
                new ProductVariationVm(8L, "v1", "sku1"),
                new ProductVariationVm(9L, "v2", "sku2")));
        when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        OrderExistsByProductAndUserGetVm result = orderService.isOrderCompletedWithUserIdAndProductId(5L);

        assertThat(result.isPresent()).isFalse();
    }

    @Test
    void getMyOrders_shouldReturnMappedOrders() {
        setSubjectUpSecurityContext("user-1");
        when(orderRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(createOrder(33L)));

        List<OrderGetVm> result = orderService.getMyOrders("shoe", OrderStatus.PAID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(33L);
    }

    @Test
    void findOrderVmByCheckoutId_shouldReturnOrderVm() {
        Order order = createOrder(50L);
        when(orderRepository.findByCheckoutId("checkout-1")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(50L)).thenReturn(List.of(createOrderItem(10L, 50L)));

        OrderGetVm result = orderService.findOrderVmByCheckoutId("checkout-1");

        assertThat(result.id()).isEqualTo(50L);
        assertThat(result.orderItems()).hasSize(1);
    }

    @Test
    void findOrderByCheckoutId_whenNotFound_shouldThrow() {
        when(orderRepository.findByCheckoutId("not-found")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.findOrderByCheckoutId("not-found"));
    }

    @Test
    void updateOrderPaymentStatus_whenCompleted_shouldSetPaidStatus() {
        Order order = createOrder(70L);
        order.setOrderStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findById(70L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(PaymentOrderStatusVm.builder()
                .orderId(70L)
                .paymentId(998L)
                .paymentStatus(PaymentStatus.COMPLETED.name())
                .build());

        assertThat(result.orderStatus()).isEqualTo(OrderStatus.PAID.getName());
        assertThat(order.getPaymentId()).isEqualTo(998L);
    }

    @Test
    void updateOrderPaymentStatus_whenNotCompleted_shouldKeepCurrentOrderStatus() {
        Order order = createOrder(71L);
        order.setOrderStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findById(71L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(PaymentOrderStatusVm.builder()
                .orderId(71L)
                .paymentId(999L)
                .paymentStatus(PaymentStatus.PENDING.name())
                .build());

        assertThat(result.orderStatus()).isEqualTo(OrderStatus.ACCEPTED.getName());
    }

    @Test
    void rejectAndAcceptOrder_whenOrderExists_shouldUpdateStatus() {
        Order order = createOrder(72L);
        when(orderRepository.findById(72L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.rejectOrder(72L, "out-of-stock");
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
        assertThat(order.getRejectReason()).isEqualTo("out-of-stock");

        orderService.acceptOrder(72L);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void rejectAndAcceptOrder_whenOrderMissing_shouldThrow() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.rejectOrder(73L, "reason"));
        assertThrows(NotFoundException.class, () -> orderService.acceptOrder(73L));
    }

    @Test
    void exportCsv_whenNoOrders_shouldReturnEmptyCsv() throws IOException {
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());

        byte[] csv = orderService.exportCsv(createOrderRequest());

        assertNotNull(csv);
        verify(orderMapper, never()).toCsv(any());
    }

    @Test
    void exportCsv_whenHasOrders_shouldMapAndExportCsv() throws IOException {
        Order order = createOrder(81L);
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(order)));
        when(orderMapper.toCsv(any(OrderBriefVm.class))).thenReturn(OrderItemCsv.builder()
                .id(81L)
                .email("alice@example.com")
                .phone("0123456789")
                .build());

        byte[] csv = orderService.exportCsv(createOrderRequest());

        assertNotNull(csv);
        ArgumentCaptor<OrderBriefVm> orderCaptor = ArgumentCaptor.forClass(OrderBriefVm.class);
        verify(orderMapper).toCsv(orderCaptor.capture());
        assertEquals(81L, orderCaptor.getValue().id());
        assertThat(csv.length).isGreaterThanOrEqualTo(0);
    }

    private static OrderPostVm createOrderPostVm() {
        return OrderPostVm.builder()
                .checkoutId("checkout-100")
                .email("alice@example.com")
                .shippingAddressPostVm(createAddressPostVm("Ship User", "0909"))
                .billingAddressPostVm(createAddressPostVm("Bill User", "0808"))
                .note("note")
                .tax(1.5f)
                .discount(0.5f)
                .numberItem(2)
                .totalPrice(new BigDecimal("99.99"))
                .deliveryFee(new BigDecimal("5.00"))
                .couponCode("SALE10")
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of(
                        new OrderItemPostVm(
                                1001L,
                                "Product 1",
                                1,
                                new BigDecimal("49.99"),
                                "n1",
                                BigDecimal.ZERO,
                                BigDecimal.ZERO,
                                BigDecimal.ZERO),
                        new OrderItemPostVm(
                                1002L,
                                "Product 2",
                                1,
                                new BigDecimal("50.00"),
                                "n2",
                                BigDecimal.ZERO,
                                BigDecimal.ZERO,
                                BigDecimal.ZERO)))
                .build();
    }

    private static OrderAddressPostVm createAddressPostVm(String name, String phone) {
        return OrderAddressPostVm.builder()
                .contactName(name)
                .phone(phone)
                .addressLine1("line1")
                .addressLine2("line2")
                .city("city")
                .zipCode("10000")
                .districtId(1L)
                .districtName("district")
                .stateOrProvinceId(2L)
                .stateOrProvinceName("state")
                .countryId(3L)
                .countryName("country")
                .build();
    }

    private static Order createOrder(Long id) {
        Order order = Order.builder()
                .id(id)
                .email("alice@example.com")
                .shippingAddressId(createAddress(1L))
                .billingAddressId(createAddress(2L))
                .note("note")
                .tax(1.1f)
                .discount(0.2f)
                .numberItem(1)
                .totalPrice(new BigDecimal("10.00"))
                .deliveryFee(new BigDecimal("1.00"))
                .couponCode("code")
                .orderStatus(OrderStatus.PENDING)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(PaymentStatus.PENDING)
                .checkoutId("checkout-1")
                .build();
        order.setCreatedOn(ZonedDateTime.now());
        return order;
    }

    private static OrderItem createOrderItem(Long id, Long orderId) {
        return OrderItem.builder()
                .id(id)
                .orderId(orderId)
                .productId(501L)
                .productName("name")
                .quantity(2)
                .productPrice(new BigDecimal("20.00"))
                .discountAmount(new BigDecimal("1.00"))
                .taxAmount(new BigDecimal("2.00"))
                .taxPercent(new BigDecimal("10.00"))
                .note("n")
                .build();
    }

    private static OrderAddress createAddress(Long id) {
        return OrderAddress.builder()
                .id(id)
                .contactName("name")
                .phone("0909")
                .addressLine1("a1")
                .addressLine2("a2")
                .city("city")
                .zipCode("70000")
                .districtId(1L)
                .districtName("district")
                .stateOrProvinceId(2L)
                .stateOrProvinceName("state")
                .countryId(3L)
                .countryName("country")
                .build();
    }

    private static OrderRequest createOrderRequest() {
        OrderRequest request = new OrderRequest();
        request.setCreatedFrom(ZonedDateTime.now().minusDays(1));
        request.setCreatedTo(ZonedDateTime.now());
        request.setPageNo(0);
        request.setPageSize(10);
        request.setOrderStatus(List.of(OrderStatus.PENDING));
        request.setBillingCountry("");
        request.setBillingPhoneNumber("");
        request.setEmail("");
        request.setProductName("");
        return request;
    }
}

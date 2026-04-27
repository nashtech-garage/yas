package com.yas.order.service;

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
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.math.BigDecimal;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import static com.yas.order.utils.SecurityContextUtils.setSubjectUpSecurityContext;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private PromotionService promotionService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testGetLatestOrders_whenCountIsZeroOrNegative_returnEmptyList() {
        assertThat(orderService.getLatestOrders(0)).isEmpty();
        assertThat(orderService.getLatestOrders(-1)).isEmpty();
    }

    @Test
    void testGetOrderWithItemsById_whenOrderExists_returnMappedOrderVm() {
        Order order = buildOrder(60L);
        order.setDeliveryMethod(DeliveryMethod.GRAB_EXPRESS);
        order.setDeliveryStatus(DeliveryStatus.PREPARING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        when(orderRepository.findById(60L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(60L)).thenReturn(List.of(buildOrderItem(600L, 60L, "Product X")));

        OrderVm result = orderService.getOrderWithItemsById(60L);

        assertThat(result.id()).isEqualTo(60L);
        assertThat(result.email()).isEqualTo("buyer@example.com");
        assertThat(result.orderItemVms()).hasSize(1);
    }

    @Test
    void testGetOrderWithItemsById_whenOrderNotFound_throwNotFoundException() {
        when(orderRepository.findById(60L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderWithItemsById(60L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Order 60 is not found");
    }

    @Test
    void testFindOrderByCheckoutId_whenOrderExists_returnOrder() {
        Order order = buildOrder(70L);
        order.setCheckoutId("checkout-70");

        when(orderRepository.findByCheckoutId("checkout-70")).thenReturn(Optional.of(order));

        Order result = orderService.findOrderByCheckoutId("checkout-70");

        assertThat(result.getId()).isEqualTo(70L);
        assertThat(result.getCheckoutId()).isEqualTo("checkout-70");
    }

    @Test
    void testFindOrderByCheckoutId_whenOrderNotFound_throwNotFoundException() {
        when(orderRepository.findByCheckoutId("missing-checkout")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findOrderByCheckoutId("missing-checkout"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Order of checkoutId missing-checkout is not found");
    }

    @Test
    void testGetAllOrder_whenNoResults_returnEmptyOrderListVm() {
        when(orderRepository.findAll(anyOrderSpecification(), any(Pageable.class))).thenReturn(Page.empty());

        var result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.parse("2026-04-20T00:00:00Z"), ZonedDateTime.parse("2026-04-27T00:00:00Z")),
                "product",
                List.of(),
                Pair.of("Vietnam", "0900000001"),
                "buyer@example.com",
                Pair.of(0, 10));

        assertThat(result.orderList()).isNull();
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();
    }

    @Test
    void testGetAllOrder_whenResultsExist_returnMappedOrderListVm() {
        Order order = buildOrder(80L);
        order.setDeliveryMethod(DeliveryMethod.GRAB_EXPRESS);
        order.setDeliveryStatus(DeliveryStatus.DELIVERED);
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        order.setCreatedOn(ZonedDateTime.parse("2026-04-27T10:15:30Z"));

        when(orderRepository.findAll(anyOrderSpecification(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(order)));

        var result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.parse("2026-04-20T00:00:00Z"), ZonedDateTime.parse("2026-04-27T00:00:00Z")),
                "product",
                List.of(OrderStatus.COMPLETED),
                Pair.of("Vietnam", "0900000001"),
                "buyer@example.com",
                Pair.of(0, 10));

        assertThat(result.orderList()).hasSize(1);
        assertThat(result.orderList().getFirst().id()).isEqualTo(80L);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    void testAcceptOrder_whenOrderExists_updateStatusToAccepted() {
        Order order = buildOrder(90L);
        order.setOrderStatus(OrderStatus.PENDING);
        when(orderRepository.findById(90L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.acceptOrder(90L);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
        verify(orderRepository).save(order);
    }

    @Test
    void testRejectOrder_whenOrderExists_updateStatusAndReason() {
        Order order = buildOrder(91L);
        when(orderRepository.findById(91L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.rejectOrder(91L, "out of stock");

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
        assertThat(order.getRejectReason()).isEqualTo("out of stock");
        verify(orderRepository).save(order);
    }

    @Test
    void testExportCsv_whenNoOrders_returnCsvBytes() throws IOException {
        when(orderRepository.findAll(anyOrderSpecification(), any(Pageable.class))).thenReturn(Page.empty());

        OrderRequest request = OrderRequest.builder()
                .createdFrom(ZonedDateTime.parse("2026-04-20T00:00:00Z"))
                .createdTo(ZonedDateTime.parse("2026-04-27T00:00:00Z"))
                .productName("product")
                .orderStatus(List.of())
                .billingCountry("Vietnam")
                .billingPhoneNumber("0900000001")
                .email("buyer@example.com")
                .pageNo(0)
                .pageSize(10)
                .build();

        byte[] result = orderService.exportCsv(request);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void testExportCsv_whenOrdersExist_returnCsvBytes() throws IOException {
        Order order = buildOrder(100L);
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setDeliveryMethod(DeliveryMethod.GRAB_EXPRESS);
        order.setDeliveryStatus(DeliveryStatus.DELIVERED);
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        order.setCreatedOn(ZonedDateTime.parse("2026-04-27T10:15:30Z"));

        when(orderRepository.findAll(anyOrderSpecification(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(order)));
        when(orderMapper.toCsv(any())).thenReturn(OrderItemCsv.builder().build());

        OrderRequest request = OrderRequest.builder()
                .createdFrom(ZonedDateTime.parse("2026-04-20T00:00:00Z"))
                .createdTo(ZonedDateTime.parse("2026-04-27T00:00:00Z"))
                .productName("product")
                .orderStatus(List.of(OrderStatus.COMPLETED))
                .billingCountry("Vietnam")
                .billingPhoneNumber("0900000001")
                .email("buyer@example.com")
                .pageNo(0)
                .pageSize(10)
                .build();

        byte[] result = orderService.exportCsv(request);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
        verify(orderMapper).toCsv(any());
    }

    @Test
    void testGetLatestOrders_whenRepositoryReturnsOrders_returnMappedBriefVms() {
        Order order = buildOrder(1L);
        order.setBillingAddressId(buildAddress());
        order.setCreatedOn(ZonedDateTime.parse("2026-04-27T10:15:30Z"));

        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of(order));

        List<OrderBriefVm> result = orderService.getLatestOrders(1);

        assertThat(result).hasSize(1);
        OrderBriefVm actual = result.getFirst();
        assertThat(actual.id()).isEqualTo(1L);
        assertThat(actual.email()).isEqualTo("buyer@example.com");
        assertThat(actual.billingAddressVm().phone()).isEqualTo("0900000001");
        assertThat(actual.totalPrice()).isEqualByComparingTo("120.00");
        verify(orderRepository).getLatestOrders(any(Pageable.class));
    }

    @Test
    void testCreateOrder_whenRequestIsValid_persistOrderAndInvokeDependencies() {
        OrderAddressPostVm address = buildAddressPostVm();
        OrderItemPostVm item = OrderItemPostVm.builder()
                .productId(101L)
                .productName("Product A")
                .quantity(1)
                .productPrice(new BigDecimal("50.00"))
                .note("note")
                .discountAmount(BigDecimal.ONE)
                .taxAmount(BigDecimal.ONE)
                .taxPercent(new BigDecimal("10.0"))
                .build();
        OrderPostVm request = OrderPostVm.builder()
                .checkoutId("checkout-10")
                .email("buyer@example.com")
                .shippingAddressPostVm(address)
                .billingAddressPostVm(address)
                .note("order note")
                .tax(2.0f)
                .discount(1.0f)
                .numberItem(1)
                .totalPrice(new BigDecimal("120.00"))
                .deliveryFee(new BigDecimal("10.00"))
                .couponCode("PROMO10")
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of(item))
                .build();

        AtomicReference<Order> savedOrder = new AtomicReference<>();
        doAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(50L);
            savedOrder.set(order);
            return order;
        }).when(orderRepository).save(any(Order.class));
        when(orderRepository.findById(50L)).thenAnswer(invocation -> Optional.of(savedOrder.get()));
        when(orderItemRepository.saveAll(any())).thenAnswer(invocation -> {
            Set<OrderItem> orderItems = invocation.getArgument(0);
            return new ArrayList<>(orderItems);
        });

        OrderVm result = orderService.createOrder(request);

        assertThat(result.id()).isEqualTo(50L);
        assertThat(result.email()).isEqualTo("buyer@example.com");
        assertThat(result.orderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(savedOrder.get().getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
        verify(productService).subtractProductStockQuantity(result);
        verify(cartService).deleteCartItems(result);
        verify(promotionService).updateUsagePromotion(any());
    }

    @Test
    void testFindOrderVmByCheckoutId_whenOrderExists_returnOrderVmWithItems() {
        Order order = buildOrder(10L);
        order.setCheckoutId("checkout-1");
        order.setOrderStatus(OrderStatus.ACCEPTED);
        order.setDeliveryStatus(DeliveryStatus.PREPARING);
        order.setDeliveryMethod(DeliveryMethod.GRAB_EXPRESS);
        order.setPaymentStatus(PaymentStatus.PENDING);

        OrderItem firstItem = buildOrderItem(100L, 10L, "Product A");
        OrderItem secondItem = buildOrderItem(101L, 10L, "Product B");

        when(orderRepository.findByCheckoutId("checkout-1")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(10L)).thenReturn(List.of(firstItem, secondItem));

        OrderGetVm result = orderService.findOrderVmByCheckoutId("checkout-1");

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.orderStatus()).isEqualTo(OrderStatus.ACCEPTED);
        assertThat(result.orderItems()).hasSize(2);
        assertThat(result.orderItems())
                .extracting(item -> item.productName())
                .containsExactlyInAnyOrder("Product A", "Product B");
        verify(orderRepository).findByCheckoutId("checkout-1");
        verify(orderItemRepository).findAllByOrderId(10L);
    }

    @Test
    void testGetMyOrders_whenUserHasOrders_returnMappedList() {
        setSubjectUpSecurityContext("user-1");

        Order order = buildOrder(20L);
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setDeliveryStatus(DeliveryStatus.DELIVERED);
        order.setDeliveryMethod(DeliveryMethod.GRAB_EXPRESS);
        order.setCreatedOn(ZonedDateTime.parse("2026-04-27T10:15:30Z"));

        when(orderRepository.findAll(anyOrderSpecification(), anySort())).thenReturn(List.of(order));

        List<OrderGetVm> result = orderService.getMyOrders("phone", OrderStatus.COMPLETED);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(20L);
        assertThat(result.getFirst().orderStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(result.getFirst().orderItems()).isEmpty();
        verify(orderRepository).findAll(anyOrderSpecification(), anySort());
    }

    @Test
    void testIsOrderCompletedWithUserIdAndProductId_whenProductVariationsExist_returnTrue() {
        setSubjectUpSecurityContext("user-1");

        when(productService.getProductVariations(1L))
                .thenReturn(List.of(
                        new ProductVariationVm(11L, "Variant 1", "SKU-1"),
                        new ProductVariationVm(12L, "Variant 2", "SKU-2")));
        when(orderRepository.findOne(anyOrderSpecification())).thenReturn(Optional.of(buildOrder(30L)));

        boolean result = orderService.isOrderCompletedWithUserIdAndProductId(1L).isPresent();

        assertTrue(result);
        verify(productService).getProductVariations(1L);
        verify(orderRepository).findOne(anyOrderSpecification());
    }

    @Test
    void testIsOrderCompletedWithUserIdAndProductId_whenNoProductVariationsAndNoOrder_returnFalse() {
        setSubjectUpSecurityContext("user-1");

        when(productService.getProductVariations(1L)).thenReturn(List.of());
        when(orderRepository.findOne(anyOrderSpecification())).thenReturn(Optional.empty());

        boolean result = orderService.isOrderCompletedWithUserIdAndProductId(1L).isPresent();

        assertFalse(result);
        verify(productService).getProductVariations(1L);
        verify(orderRepository).findOne(anyOrderSpecification());
    }

    @Test
    void testUpdateOrderPaymentStatus_whenPaymentCompleted_setOrderStatusPaid() {
        Order order = buildOrder(40L);
        order.setOrderStatus(OrderStatus.ACCEPTED);
        order.setPaymentStatus(PaymentStatus.PENDING);

        PaymentOrderStatusVm request = new PaymentOrderStatusVm(
                40L,
                OrderStatus.ACCEPTED.getName(),
                2001L,
                PaymentStatus.COMPLETED.name());

        when(orderRepository.findById(40L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

        assertThat(result.orderStatus()).isEqualTo(OrderStatus.PAID.getName());
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(order.getPaymentId()).isEqualTo(2001L);
        verify(orderRepository).save(order);
    }

    @Test
    void testUpdateOrderPaymentStatus_whenOrderNotFound_throwNotFoundException() {
        PaymentOrderStatusVm request = new PaymentOrderStatusVm(
                404L,
                OrderStatus.ACCEPTED.getName(),
                2001L,
                PaymentStatus.PENDING.name());

        when(orderRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrderPaymentStatus(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Order 404 is not found");
    }

    private Order buildOrder(Long id) {
        return Order.builder()
                .id(id)
                .email("buyer@example.com")
                .totalPrice(new BigDecimal("120.00"))
                .deliveryFee(new BigDecimal("10.00"))
                .discount(5.0f)
                .tax(2.0f)
                .numberItem(2)
                .billingAddressId(buildAddress())
                .shippingAddressId(buildAddress())
                .build();
    }

    private OrderAddress buildAddress() {
        return OrderAddress.builder()
                .id(1L)
                .phone("0900000001")
                .contactName("Receiver")
                .addressLine1("1 Main St")
                .addressLine2("District 1")
                .city("Ho Chi Minh City")
                .zipCode("700000")
                .districtId(11L)
                .districtName("District 1")
                .stateOrProvinceId(22L)
                .stateOrProvinceName("HCMC")
                .countryId(33L)
                .countryName("Vietnam")
                .build();
    }

    private OrderAddressPostVm buildAddressPostVm() {
        return OrderAddressPostVm.builder()
                .contactName("Receiver")
                .phone("0900000001")
                .addressLine1("1 Main St")
                .addressLine2("District 1")
                .city("Ho Chi Minh City")
                .zipCode("700000")
                .districtId(11L)
                .districtName("District 1")
                .stateOrProvinceId(22L)
                .stateOrProvinceName("HCMC")
                .countryId(33L)
                .countryName("Vietnam")
                .build();
    }

    private OrderItem buildOrderItem(Long id, Long orderId, String productName) {
        return OrderItem.builder()
                .id(id)
                .orderId(orderId)
                .productId(id + 1000)
                .productName(productName)
                .quantity(1)
                .productPrice(new BigDecimal("50.00"))
                .discountAmount(BigDecimal.ONE)
                .taxAmount(BigDecimal.ONE)
                .build();
    }

    private Specification<Order> anyOrderSpecification() {
        return any();
    }

    private Sort anySort() {
        return any();
    }
}
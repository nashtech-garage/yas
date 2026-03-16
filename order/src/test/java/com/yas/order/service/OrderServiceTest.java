package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setSubjectUpSecurityContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
                promotionService);
    }

    @Test
    void createOrder_CreatesAndTriggersDependentActions() {
        OrderPostVm orderPostVm = buildOrderPostVm("checkout-100");

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(100L);
            return order;
        });

        when(orderItemRepository.saveAll(any())).thenAnswer(invocation -> {
            Set<OrderItem> orderItems = invocation.getArgument(0);
            return new ArrayList<>(orderItems);
        });

        Order acceptedOrder = buildOrder(100L, "checkout-100");
        acceptedOrder.setOrderStatus(OrderStatus.PENDING);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(acceptedOrder));

        OrderVm result = orderService.createOrder(orderPostVm);

        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals("checkout-100", result.checkoutId());
        assertEquals(2, result.orderItemVms().size());

        verify(productService).subtractProductStockQuantity(any(OrderVm.class));
        verify(cartService).deleteCartItems(any(OrderVm.class));
        verify(promotionService).updateUsagePromotion(any(List.class));
        verify(orderRepository).findById(100L);
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void getOrderWithItemsById_WhenOrderExists_ReturnsOrderVm() {
        Order order = buildOrder(11L, "checkout-11");
        List<OrderItem> orderItems = List.of(buildOrderItem(11L, 1001L));

        when(orderRepository.findById(11L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(11L)).thenReturn(orderItems);

        OrderVm result = orderService.getOrderWithItemsById(11L);

        assertEquals(11L, result.id());
        assertEquals("checkout-11", result.checkoutId());
        assertEquals(1, result.orderItemVms().size());
    }

    @Test
    void getOrderWithItemsById_WhenOrderNotFound_ThrowsNotFoundException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(999L));
    }

    @Test
    void getAllOrder_WhenPageIsEmpty_ReturnsEmptyOrderListVm() {
        when(orderRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(Page.empty());

        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
                "product",
                List.of(),
                Pair.of("VN", "0123"),
                "buyer@yas.local",
                Pair.of(0, 10));

        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
        assertEquals(null, result.orderList());
    }

    @Test
    void getAllOrder_WhenPageHasData_ReturnsBriefItems() {
        Order order = buildOrder(21L, "checkout-21");
        Page<Order> orderPage = new PageImpl<>(List.of(order), PageRequest.of(0, 5), 1);
        when(orderRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(orderPage);

        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
                "product",
                List.of(OrderStatus.PENDING),
                Pair.of("VN", "0123"),
                "buyer@yas.local",
                Pair.of(0, 5));

        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        assertEquals(1, result.orderList().size());
    }

    @Test
    void getLatestOrders_WhenCountNotPositive_ReturnsEmptyList() {
        List<OrderBriefVm> result = orderService.getLatestOrders(0);

        assertTrue(result.isEmpty());
        verify(orderRepository, never()).getLatestOrders(any(PageRequest.class));
    }

    @Test
    void getLatestOrders_WhenRepositoryReturnsEmpty_ReturnsEmptyList() {
        when(orderRepository.getLatestOrders(any(PageRequest.class))).thenReturn(List.of());

        List<OrderBriefVm> result = orderService.getLatestOrders(3);

        assertTrue(result.isEmpty());
    }

    @Test
    void getLatestOrders_WhenDataExists_ReturnsMappedList() {
        when(orderRepository.getLatestOrders(any(PageRequest.class))).thenReturn(List.of(buildOrder(22L, "checkout-22")));

        List<OrderBriefVm> result = orderService.getLatestOrders(1);

        assertEquals(1, result.size());
        assertEquals(22L, result.getFirst().id());
    }

    @Test
    void isOrderCompletedWithUserIdAndProductId_WhenNoVariations_UsesOriginalProductId() {
        setSubjectUpSecurityContext("user-1");
        when(productService.getProductVariations(5L)).thenReturn(List.of());
        when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.of(buildOrder(33L, "checkout-33")));

        OrderExistsByProductAndUserGetVm result = orderService.isOrderCompletedWithUserIdAndProductId(5L);

        assertTrue(result.isPresent());
    }

    @Test
    void isOrderCompletedWithUserIdAndProductId_WhenHasVariations_UsesVariationIds() {
        setSubjectUpSecurityContext("user-2");
        when(productService.getProductVariations(8L)).thenReturn(List.of(
                new ProductVariationVm(100L, "Var-1", "SKU-1"),
                new ProductVariationVm(101L, "Var-2", "SKU-2")));
        when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        OrderExistsByProductAndUserGetVm result = orderService.isOrderCompletedWithUserIdAndProductId(8L);

        assertFalse(result.isPresent());
    }

    @Test
    void getMyOrders_ReturnsMappedOrderGetVmList() {
        setSubjectUpSecurityContext("user-3");
        when(orderRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(buildOrder(44L, "checkout-44")));

        List<OrderGetVm> result = orderService.getMyOrders("product", OrderStatus.PENDING);

        assertEquals(1, result.size());
        assertEquals(44L, result.getFirst().id());
    }

    @Test
    void findOrderVmByCheckoutId_ReturnsMappedVm() {
        Order order = buildOrder(55L, "checkout-55");
        when(orderRepository.findByCheckoutId("checkout-55")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(55L)).thenReturn(List.of(buildOrderItem(55L, 5001L)));

        OrderGetVm result = orderService.findOrderVmByCheckoutId("checkout-55");

        assertEquals(55L, result.id());
        assertEquals(1, result.orderItems().size());
    }

    @Test
    void findOrderByCheckoutId_WhenNotFound_ThrowsNotFoundException() {
        when(orderRepository.findByCheckoutId("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.findOrderByCheckoutId("missing"));
    }

    @Test
    void updateOrderPaymentStatus_WhenCompleted_SetsOrderStatusPaid() {
        Order order = buildOrder(66L, "checkout-66");
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        when(orderRepository.findById(66L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentOrderStatusVm requestVm = PaymentOrderStatusVm.builder()
                .orderId(66L)
                .paymentId(888L)
                .paymentStatus(PaymentStatus.COMPLETED.name())
                .build();

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(requestVm);

        assertEquals(66L, result.orderId());
        assertEquals(OrderStatus.PAID.getName(), result.orderStatus());
        assertEquals(PaymentStatus.COMPLETED.name(), result.paymentStatus());
    }

    @Test
    void updateOrderPaymentStatus_WhenNotCompleted_DoesNotForcePaidStatus() {
        Order order = buildOrder(67L, "checkout-67");
        order.setOrderStatus(OrderStatus.PENDING);

        when(orderRepository.findById(67L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentOrderStatusVm requestVm = PaymentOrderStatusVm.builder()
                .orderId(67L)
                .paymentId(889L)
                .paymentStatus(PaymentStatus.PENDING.name())
                .build();

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(requestVm);

        assertEquals(OrderStatus.PENDING.getName(), result.orderStatus());
        assertEquals(PaymentStatus.PENDING.name(), result.paymentStatus());
    }

    @Test
    void rejectOrder_UpdatesStatusAndReason() {
        Order order = buildOrder(68L, "checkout-68");
        when(orderRepository.findById(68L)).thenReturn(Optional.of(order));

        orderService.rejectOrder(68L, "invalid payment");

        assertEquals(OrderStatus.REJECT, order.getOrderStatus());
        assertEquals("invalid payment", order.getRejectReason());
        verify(orderRepository).save(order);
    }

    @Test
    void acceptOrder_UpdatesStatus() {
        Order order = buildOrder(69L, "checkout-69");
        when(orderRepository.findById(69L)).thenReturn(Optional.of(order));

        orderService.acceptOrder(69L);

        assertEquals(OrderStatus.ACCEPTED, order.getOrderStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void exportCsv_WhenNoOrderData_ReturnsCsvBytes() throws IOException {
        when(orderRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(Page.empty());

        OrderRequest request = buildOrderRequest();

        byte[] csv = orderService.exportCsv(request);

        assertNotNull(csv);
    }

    @Test
    void exportCsv_WhenOrderDataExists_UsesMapperAndReturnsCsvBytes() throws IOException {
        Order order = buildOrder(77L, "checkout-77");
        Page<Order> page = new PageImpl<>(List.of(order), PageRequest.of(0, 5), 1);
        when(orderRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        when(orderMapper.toCsv(any(OrderBriefVm.class))).thenReturn(OrderItemCsv.builder()
                .id(77L)
                .email("buyer@yas.local")
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .build());

        OrderRequest request = buildOrderRequest();

        byte[] csv = orderService.exportCsv(request);

        assertNotNull(csv);
        verify(orderMapper).toCsv(any(OrderBriefVm.class));
    }

    private static OrderPostVm buildOrderPostVm(String checkoutId) {
        return OrderPostVm.builder()
                .checkoutId(checkoutId)
                .email("buyer@yas.local")
                .shippingAddressPostVm(buildAddressPostVm("Ship"))
                .billingAddressPostVm(buildAddressPostVm("Bill"))
                .note("note")
                .tax(1.2f)
                .discount(0.5f)
                .numberItem(2)
                .totalPrice(BigDecimal.valueOf(100.5))
                .deliveryFee(BigDecimal.valueOf(5.0))
                .couponCode("PROMO10")
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of(
                        OrderItemPostVm.builder()
                                .productId(1001L)
                                .productName("Product A")
                                .quantity(1)
                                .productPrice(BigDecimal.valueOf(50.0))
                                .note("note")
                                .build(),
                        OrderItemPostVm.builder()
                                .productId(1002L)
                                .productName("Product B")
                                .quantity(1)
                                .productPrice(BigDecimal.valueOf(50.5))
                                .note("note")
                                .build()))
                .build();
    }

    private static OrderAddressPostVm buildAddressPostVm(String prefix) {
        return OrderAddressPostVm.builder()
                .contactName(prefix + " Contact")
                .phone("0123456789")
                .addressLine1(prefix + " Addr1")
                .addressLine2(prefix + " Addr2")
                .city("HCM")
                .zipCode("700000")
                .districtId(1L)
                .districtName("District")
                .stateOrProvinceId(2L)
                .stateOrProvinceName("State")
                .countryId(84L)
                .countryName("VN")
                .build();
    }

    private static Order buildOrder(Long id, String checkoutId) {
        OrderAddress billing = OrderAddress.builder()
                .id(20L)
                .contactName("Bill Contact")
                .phone("0999")
                .addressLine1("Addr1")
                .city("HCM")
                .zipCode("700000")
                .districtId(1L)
                .districtName("District")
                .stateOrProvinceId(2L)
                .stateOrProvinceName("State")
                .countryId(84L)
                .countryName("VN")
                .build();

        OrderAddress shipping = OrderAddress.builder()
                .id(21L)
                .contactName("Ship Contact")
                .phone("0888")
                .addressLine1("Addr1")
                .city("HCM")
                .zipCode("700000")
                .districtId(1L)
                .districtName("District")
                .stateOrProvinceId(2L)
                .stateOrProvinceName("State")
                .countryId(84L)
                .countryName("VN")
                .build();

        Order order = Order.builder()
                .id(id)
                .email("buyer@yas.local")
                .shippingAddressId(shipping)
                .billingAddressId(billing)
                .note("note")
                .tax(1.0f)
                .discount(0.0f)
                .numberItem(2)
                .couponCode("PROMO")
                .totalPrice(BigDecimal.valueOf(120.0))
                .deliveryFee(BigDecimal.valueOf(5.0))
                .orderStatus(OrderStatus.PENDING)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(PaymentStatus.PENDING)
                .checkoutId(checkoutId)
                .build();
        order.setCreatedOn(ZonedDateTime.now());
        return order;
    }

    private static OrderItem buildOrderItem(Long orderId, Long productId) {
        return OrderItem.builder()
                .id(1L)
                .orderId(orderId)
                .productId(productId)
                .productName("Product")
                .quantity(1)
                .productPrice(BigDecimal.valueOf(10.0))
                .note("note")
                .discountAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .taxPercent(BigDecimal.ZERO)
                .build();
    }

    private static OrderRequest buildOrderRequest() {
        return OrderRequest.builder()
                .createdFrom(ZonedDateTime.now().minusDays(1))
                .createdTo(ZonedDateTime.now())
                .productName("Product")
                .orderStatus(List.of(OrderStatus.PENDING))
                .billingPhoneNumber("0123")
                .email("buyer@yas.local")
                .billingCountry("VN")
                .pageNo(0)
                .pageSize(5)
                .build();
    }

    private static void assertTrue(boolean value) {
        assertEquals(true, value);
    }
}

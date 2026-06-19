package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setSubjectUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.csv.CsvExporter;
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
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    private OrderPostVm orderPostVm;
    private OrderAddress shippingAddress;
    private OrderAddress billingAddress;
    private List<OrderItem> orderItems;

    @BeforeEach
    void setUp() {
        orderPostVm = buildOrderPostVm();
        shippingAddress = toAddress(orderPostVm.shippingAddressPostVm(), 11L);
        billingAddress = toAddress(orderPostVm.billingAddressPostVm(), 22L);
        orderItems = List.of(
                OrderItem.builder()
                        .id(1L)
                        .orderId(100L)
                        .productId(10L)
                        .productName("Keyboard")
                        .quantity(2)
                        .productPrice(new BigDecimal("50.00"))
                        .note("blue switch")
                        .build(),
                OrderItem.builder()
                        .id(2L)
                        .orderId(100L)
                        .productId(20L)
                        .productName("Mouse")
                        .quantity(1)
                        .productPrice(new BigDecimal("30.00"))
                        .note("wireless")
                        .build());
    }

    @Test
    void testCreateOrder_whenValidRequest_thenCreateOrderAndTriggerSideEffects() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            if (order.getId() == null) {
                order.setId(100L);
            }
            return order;
        });
        when(orderRepository.findById(100L)).thenReturn(Optional.of(buildOrder(100L)));
        when(orderItemRepository.saveAll(anyCollection())).thenReturn(orderItems);

        OrderVm result = orderService.createOrder(orderPostVm);

        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.email()).isEqualTo(orderPostVm.email());
        assertThat(result.orderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.orderItemVms()).hasSize(2);

        verify(productService).subtractProductStockQuantity(result);
        verify(cartService).deleteCartItems(result);
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(promotionService).updateUsagePromotion(org.mockito.ArgumentMatchers.argThat(usages -> usages.size() == 2
                && usages.stream().allMatch(usage -> usage.orderId().equals(100L))
                && usages.stream().allMatch(usage -> "SALE10".equals(usage.promotionCode()))));
    }

    @Test
    void testGetOrderWithItemsById_whenOrderExists_thenReturnOrderVm() {
        Order order = buildOrder(100L);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(100L)).thenReturn(orderItems);

        OrderVm result = orderService.getOrderWithItemsById(100L);

        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.orderItemVms()).hasSize(2);
        assertThat(result.billingAddressVm().phone()).isEqualTo(billingAddress.getPhone());
    }

    @Test
    void testGetOrderWithItemsById_whenOrderMissing_thenThrowNotFoundException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(999L));
    }

    @Test
    void testGetAllOrder_whenPageEmpty_thenReturnEmptySummary() {
        when(orderRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Order>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now().minusDays(2), ZonedDateTime.now()),
                "Keyboard",
                List.of(),
                Pair.of("VN", "0909"),
                "alice@example.com",
                Pair.of(1, 5));

        assertThat(result.orderList()).isNull();
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();
    }

    @Test
    void testGetAllOrder_whenPageHasData_thenReturnMappedOrders() {
        Order order = buildOrder(100L);
        when(orderRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Order>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(order)));

        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now().minusDays(2), ZonedDateTime.now()),
                "Keyboard",
                List.of(OrderStatus.ACCEPTED),
                Pair.of("VN", "0909"),
                "alice@example.com",
                Pair.of(0, 10));

        assertThat(result.orderList()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.orderList().getFirst().id()).isEqualTo(100L);
        assertThat(result.orderList().getFirst().orderStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void testGetLatestOrders_whenCountIsNotPositive_thenReturnEmptyList() {
        assertThat(orderService.getLatestOrders(0)).isEmpty();
        verify(orderRepository, never()).getLatestOrders(any(Pageable.class));
    }

    @Test
    void testGetLatestOrders_whenRepositoryReturnsEmpty_thenReturnEmptyList() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of());

        assertThat(orderService.getLatestOrders(5)).isEmpty();
    }

    @Test
    void testGetLatestOrders_whenRepositoryReturnsData_thenReturnMappedOrders() {
        Order order = buildOrder(100L);
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of(order));

        List<OrderBriefVm> result = orderService.getLatestOrders(3);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(100L);
        assertThat(result.getFirst().paymentStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void testIsOrderCompletedWithUserIdAndProductId_whenNoVariation_thenUseOriginalProductId() {
        setSubjectUpSecurityContext("user-1");
        when(productService.getProductVariations(10L)).thenReturn(List.of());
        when(orderRepository.findOne(org.mockito.ArgumentMatchers.<Specification<Order>>any()))
                .thenReturn(Optional.empty());

        OrderExistsByProductAndUserGetVm result = orderService.isOrderCompletedWithUserIdAndProductId(10L);

        assertThat(result.isPresent()).isFalse();
    }

    @Test
    void testIsOrderCompletedWithUserIdAndProductId_whenVariationsExist_thenReturnTrue() {
        setSubjectUpSecurityContext("user-1");
        when(productService.getProductVariations(10L))
                .thenReturn(List.of(new ProductVariationVm(11L, "M", "SKU-M"),
                        new ProductVariationVm(12L, "L", "SKU-L")));
        when(orderRepository.findOne(org.mockito.ArgumentMatchers.<Specification<Order>>any()))
                .thenReturn(Optional.of(buildOrder(100L)));

        OrderExistsByProductAndUserGetVm result = orderService.isOrderCompletedWithUserIdAndProductId(10L);

        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void testGetMyOrders_whenOrdersExist_thenReturnMappedResults() {
        setSubjectUpSecurityContext("user-1");
        Order firstOrder = buildOrder(100L);
        Order secondOrder = buildOrder(101L);
        when(orderRepository.findAll(
                org.mockito.ArgumentMatchers.<Specification<Order>>any(),
                any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(firstOrder, secondOrder));

        List<OrderGetVm> result = orderService.getMyOrders("Mouse", OrderStatus.ACCEPTED);

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().orderItems()).isEmpty();
        assertThat(result.get(1).id()).isEqualTo(101L);
    }

    @Test
    void testFindOrderVmByCheckoutId_whenOrderExists_thenReturnOrderWithItems() {
        Order order = buildOrder(100L);
        when(orderRepository.findByCheckoutId("checkout-1")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(100L)).thenReturn(orderItems);

        OrderGetVm result = orderService.findOrderVmByCheckoutId("checkout-1");

        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.orderItems()).hasSize(2);
    }

    @Test
    void testFindOrderByCheckoutId_whenMissing_thenThrowNotFoundException() {
        when(orderRepository.findByCheckoutId("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.findOrderByCheckoutId("missing"));
    }

    @Test
    void testUpdateOrderPaymentStatus_whenCompleted_thenSetOrderPaid() {
        Order order = buildOrder(100L);
        order.setOrderStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(
                PaymentOrderStatusVm.builder()
                        .orderId(100L)
                        .paymentId(500L)
                        .paymentStatus(PaymentStatus.COMPLETED.name())
                        .orderStatus(OrderStatus.PAID.getName())
                        .build());

        assertThat(order.getPaymentId()).isEqualTo(500L);
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(result.orderStatus()).isEqualTo(OrderStatus.PAID.getName());
    }

    @Test
    void testUpdateOrderPaymentStatus_whenStatusNotCompleted_thenKeepCurrentOrderStatus() {
        Order order = buildOrder(100L);
        order.setOrderStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.updateOrderPaymentStatus(
                PaymentOrderStatusVm.builder()
                        .orderId(100L)
                        .paymentId(500L)
                        .paymentStatus(PaymentStatus.PENDING.name())
                        .orderStatus(OrderStatus.ACCEPTED.getName())
                        .build());

        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void testUpdateOrderPaymentStatus_whenOrderMissing_thenThrowNotFoundException() {
        when(orderRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.updateOrderPaymentStatus(
                PaymentOrderStatusVm.builder()
                        .orderId(100L)
                        .paymentId(500L)
                        .paymentStatus(PaymentStatus.PENDING.name())
                        .orderStatus(OrderStatus.ACCEPTED.getName())
                        .build()));
    }

    @Test
    void testRejectOrder_whenOrderExists_thenPersistRejectedState() {
        Order order = buildOrder(100L);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        orderService.rejectOrder(100L, "Out of stock");

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
        assertThat(order.getRejectReason()).isEqualTo("Out of stock");
        verify(orderRepository).save(order);
    }

    @Test
    void testRejectOrder_whenOrderMissing_thenThrowNotFoundException() {
        when(orderRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.rejectOrder(100L, "Out of stock"));
    }

    @Test
    void testAcceptOrder_whenOrderExists_thenPersistAcceptedState() {
        Order order = buildOrder(100L);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        orderService.acceptOrder(100L);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
        verify(orderRepository).save(order);
    }

    @Test
    void testAcceptOrder_whenOrderMissing_thenThrowNotFoundException() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.acceptOrder(100L));
    }

    @Test
    void testExportCsv_whenNoOrderData_thenReturnHeaderOnlyCsv() throws IOException {
        OrderService spyOrderService = org.mockito.Mockito.spy(orderService);
        OrderRequest orderRequest = buildOrderRequest();
        doReturn(new OrderListVm(null, 0, 0)).when(spyOrderService)
                .getAllOrder(any(), anyString(), anyList(), any(), anyString(), any());

        byte[] result = spyOrderService.exportCsv(orderRequest);

        assertThat(result).isEqualTo(CsvExporter.exportToCsv(List.of(), OrderItemCsv.class));
        verify(orderMapper, never()).toCsv(any());
    }

    @Test
    void testExportCsv_whenOrdersExist_thenReturnCsvContent() throws IOException {
        OrderService spyOrderService = org.mockito.Mockito.spy(orderService);
        OrderRequest orderRequest = buildOrderRequest();
        OrderBriefVm orderBriefVm = OrderBriefVm.builder()
                .id(100L)
                .email("alice@example.com")
                .billingAddressVm(OrderAddressVm.fromModel(billingAddress))
                .totalPrice(new BigDecimal("130.00"))
                .orderStatus(OrderStatus.ACCEPTED)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(PaymentStatus.PENDING)
                .createdOn(ZonedDateTime.parse("2026-03-11T10:15:30Z"))
                .build();
        OrderItemCsv csvRow = OrderItemCsv.builder()
                .id(100L)
                .email("alice@example.com")
                .phone("0909000111")
                .totalPrice(new BigDecimal("130.00"))
                .orderStatus(OrderStatus.ACCEPTED)
                .paymentStatus(PaymentStatus.PENDING)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .createdOn(ZonedDateTime.parse("2026-03-11T10:15:30Z"))
                .build();
        doReturn(new OrderListVm(List.of(orderBriefVm), 1, 1)).when(spyOrderService)
                .getAllOrder(any(), anyString(), anyList(), any(), anyString(), any());
        when(orderMapper.toCsv(orderBriefVm)).thenReturn(csvRow);

        byte[] result = spyOrderService.exportCsv(orderRequest);

        assertThat(result).isEqualTo(CsvExporter.exportToCsv(List.of(csvRow), OrderItemCsv.class));
        verify(orderMapper).toCsv(orderBriefVm);
    }

    private OrderPostVm buildOrderPostVm() {
        OrderAddressPostVm shippingAddressPostVm = OrderAddressPostVm.builder()
                .contactName("Alice")
                .phone("0909000111")
                .addressLine1("12 Nguyen Trai")
                .addressLine2("District 1")
                .city("Ho Chi Minh")
                .zipCode("700000")
                .districtId(1L)
                .districtName("District 1")
                .stateOrProvinceId(79L)
                .stateOrProvinceName("Ho Chi Minh")
                .countryId(84L)
                .countryName("Vietnam")
                .build();

        OrderAddressPostVm billingAddressPostVm = OrderAddressPostVm.builder()
                .contactName("Bob")
                .phone("0909000222")
                .addressLine1("34 Le Loi")
                .addressLine2("District 3")
                .city("Ho Chi Minh")
                .zipCode("700000")
                .districtId(3L)
                .districtName("District 3")
                .stateOrProvinceId(79L)
                .stateOrProvinceName("Ho Chi Minh")
                .countryId(84L)
                .countryName("Vietnam")
                .build();

        return OrderPostVm.builder()
                .checkoutId("checkout-1")
                .email("alice@example.com")
                .shippingAddressPostVm(shippingAddressPostVm)
                .billingAddressPostVm(billingAddressPostVm)
                .note("Call before delivery")
                .tax(3.5f)
                .discount(10.0f)
                .numberItem(3)
                .totalPrice(new BigDecimal("130.00"))
                .deliveryFee(new BigDecimal("5.00"))
                .couponCode("SALE10")
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of(
                        OrderItemPostVm.builder()
                                .productId(10L)
                                .productName("Keyboard")
                                .quantity(2)
                                .productPrice(new BigDecimal("50.00"))
                                .note("blue switch")
                                .build(),
                        OrderItemPostVm.builder()
                                .productId(20L)
                                .productName("Mouse")
                                .quantity(1)
                                .productPrice(new BigDecimal("30.00"))
                                .note("wireless")
                                .build()))
                .build();
    }

    private Order buildOrder(Long id) {
        Order order = Order.builder()
                .id(id)
                .email(orderPostVm.email())
                .shippingAddressId(shippingAddress)
                .billingAddressId(billingAddress)
                .note(orderPostVm.note())
                .tax(orderPostVm.tax())
                .discount(orderPostVm.discount())
                .numberItem(orderPostVm.numberItem())
                .totalPrice(orderPostVm.totalPrice())
                .deliveryFee(orderPostVm.deliveryFee())
                .couponCode(orderPostVm.couponCode())
                .orderStatus(OrderStatus.ACCEPTED)
                .deliveryMethod(orderPostVm.deliveryMethod())
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(orderPostVm.paymentStatus())
                .checkoutId(orderPostVm.checkoutId())
                .build();
        order.setCreatedOn(ZonedDateTime.parse("2026-03-11T10:15:30Z"));
        return order;
    }

    private OrderAddress toAddress(OrderAddressPostVm addressPostVm, Long id) {
        return OrderAddress.builder()
                .id(id)
                .contactName(addressPostVm.contactName())
                .phone(addressPostVm.phone())
                .addressLine1(addressPostVm.addressLine1())
                .addressLine2(addressPostVm.addressLine2())
                .city(addressPostVm.city())
                .zipCode(addressPostVm.zipCode())
                .districtId(addressPostVm.districtId())
                .districtName(addressPostVm.districtName())
                .stateOrProvinceId(addressPostVm.stateOrProvinceId())
                .stateOrProvinceName(addressPostVm.stateOrProvinceName())
                .countryId(addressPostVm.countryId())
                .countryName(addressPostVm.countryName())
                .build();
    }

    private OrderRequest buildOrderRequest() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCreatedFrom(ZonedDateTime.parse("2026-03-01T00:00:00Z"));
        orderRequest.setCreatedTo(ZonedDateTime.parse("2026-03-31T23:59:59Z"));
        orderRequest.setProductName("Keyboard");
        orderRequest.setOrderStatus(List.of(OrderStatus.ACCEPTED));
        orderRequest.setBillingCountry("Vietnam");
        orderRequest.setBillingPhoneNumber("0909");
        orderRequest.setEmail("alice@example.com");
        orderRequest.setPageNo(0);
        orderRequest.setPageSize(10);
        return orderRequest;
    }
}

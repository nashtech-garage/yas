package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setSubjectUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import com.yas.order.viewmodel.promotion.PromotionUsageVm;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

    private OrderAddress shippingAddress;
    private OrderAddress billingAddress;

    @BeforeEach
    void setUp() {
        shippingAddress = OrderAddress.builder()
                .id(1L)
                .contactName("John Doe")
                .phone("+123456789")
                .addressLine1("123 Main St")
                .addressLine2("Apt 4B")
                .city("Springfield")
                .zipCode("62701")
                .districtId(101L)
                .districtName("Downtown")
                .stateOrProvinceId(201L)
                .stateOrProvinceName("Illinois")
                .countryId(301L)
                .countryName("USA")
                .build();

        billingAddress = OrderAddress.builder()
                .id(2L)
                .contactName("Jane Smith")
                .phone("+1987654321")
                .addressLine1("789 Elm Street")
                .addressLine2("Suite 5A")
                .city("Greenville")
                .zipCode("29601")
                .districtId(102L)
                .districtName("North District")
                .stateOrProvinceId(202L)
                .stateOrProvinceName("South Carolina")
                .countryId(302L)
                .countryName("United States")
                .build();
    }

    @Nested
    class CreateOrderTest {

        @Test
        void createOrder_whenEmptyItemsList_shouldCreateOrderWithNoItems() {
            OrderPostVm orderPostVm = buildOrderPostVmWithNoItems();
            Order savedOrder = buildOrder(1L);

            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order o = invocation.getArgument(0);
                if (o.getId() == null) o.setId(1L);
                return savedOrder;
            });
            when(orderItemRepository.saveAll(any())).thenReturn(List.of());
            when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

            OrderVm result = orderService.createOrder(orderPostVm);

            assertNotNull(result);
            assertNotNull(result.orderItemVms());
            assertTrue(result.orderItemVms().isEmpty());
            verify(promotionService).updateUsagePromotion(List.of());
        }

        @Test
        void createOrder_whenNoCouponCode_shouldCreateOrderAndBuildPromotionUsageWithNullCode() {
            OrderPostVm orderPostVm = buildOrderPostVmNoCoupon();
            Order savedOrder = buildOrderNoCoupon(1L);

            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order o = invocation.getArgument(0);
                if (o.getId() == null) o.setId(1L);
                return savedOrder;
            });
            when(orderItemRepository.saveAll(any())).thenAnswer(invocation -> {
                Set<OrderItem> items = invocation.getArgument(0);
                return items.stream().toList();
            });
            when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<PromotionUsageVm>> promotionCaptor =
                    ArgumentCaptor.forClass((Class<List<PromotionUsageVm>>) (Class<?>) List.class);

            orderService.createOrder(orderPostVm);

            verify(promotionService).updateUsagePromotion(promotionCaptor.capture());
            assertThat(promotionCaptor.getValue()).allMatch(vm -> vm.promotionCode() == null);
        }

        @Test
        void createOrder_whenValidRequest_shouldCreateOrderAndItems() {
            OrderPostVm orderPostVm = buildOrderPostVm();
            Order savedOrder = buildOrder(1L);

            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order o = invocation.getArgument(0);
                if (o.getId() == null) o.setId(1L);
                return savedOrder;
            });
            when(orderItemRepository.saveAll(any())).thenAnswer(invocation -> {
                Set<OrderItem> items = invocation.getArgument(0);
                items.forEach(item -> item.setId(1L));
                return items.stream().toList();
            });
            when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

            OrderVm result = orderService.createOrder(orderPostVm);

            assertNotNull(result);
            verify(orderRepository, times(2)).save(any(Order.class));
            verify(orderItemRepository).saveAll(any());
            verify(productService).subtractProductStockQuantity(any(OrderVm.class));
            verify(cartService).deleteCartItems(any(OrderVm.class));
            verify(promotionService).updateUsagePromotion(anyList());
        }

        @Test
        void createOrder_shouldSetCorrectOrderStatus() {
            OrderPostVm orderPostVm = buildOrderPostVm();
            Order savedOrder = buildOrder(1L);

            ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
            when(orderRepository.save(orderCaptor.capture())).thenAnswer(invocation -> {
                Order o = invocation.getArgument(0);
                if (o.getId() == null) o.setId(1L);
                return savedOrder;
            });
            when(orderItemRepository.saveAll(any())).thenReturn(List.of());
            when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

            orderService.createOrder(orderPostVm);

            Order capturedOrder = orderCaptor.getAllValues().get(0);
            assertEquals(OrderStatus.PENDING, capturedOrder.getOrderStatus());
            assertEquals(DeliveryStatus.PREPARING, capturedOrder.getDeliveryStatus());
        }

        @Test
        void createOrder_shouldBuildPromotionUsageForEachItem() {
            OrderPostVm orderPostVm = buildOrderPostVm();
            Order savedOrder = buildOrder(1L);
            savedOrder.setCouponCode("COUPON2024");

            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order o = invocation.getArgument(0);
                if (o.getId() == null) o.setId(1L);
                return savedOrder;
            });
            when(orderItemRepository.saveAll(any())).thenAnswer(invocation -> {
                Set<OrderItem> items = invocation.getArgument(0);
                return items.stream().toList();
            });
            when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<PromotionUsageVm>> promotionCaptor = ArgumentCaptor.forClass((Class<List<PromotionUsageVm>>) (Class<?>) List.class);

            orderService.createOrder(orderPostVm);

            verify(promotionService).updateUsagePromotion(promotionCaptor.capture());
            assertThat(promotionCaptor.getValue()).hasSize(orderPostVm.orderItemPostVms().size());
        }
    }

    @Nested
    class GetOrderWithItemsByIdTest {

        @Test
        void getOrderWithItemsById_whenOrderExists_shouldReturnOrderVm() {
            Order order = buildOrder(1L);
            List<OrderItem> items = List.of(buildOrderItem(1L, 1L));

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderItemRepository.findAllByOrderId(1L)).thenReturn(items);

            OrderVm result = orderService.getOrderWithItemsById(1L);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertNotNull(result.orderItemVms());
        }

        @Test
        void getOrderWithItemsById_whenOrderHasNoItems_shouldReturnOrderVmWithEmptyItems() {
            Order order = buildOrder(1L);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of());

            OrderVm result = orderService.getOrderWithItemsById(1L);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertNotNull(result.orderItemVms());
            assertTrue(result.orderItemVms().isEmpty());
        }

        @Test
        void getOrderWithItemsById_whenOrderNotFound_shouldThrowNotFoundException() {
            when(orderRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> orderService.getOrderWithItemsById(999L));
        }
    }

    @Nested
    class GetAllOrderTest {

        @Test
        void getAllOrder_whenOrdersExist_shouldReturnOrderListVm() {
            Order order = buildOrder(1L);
            Page<Order> orderPage = new PageImpl<>(List.of(order));

            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(orderPage);

            OrderListVm result = orderService.getAllOrder(
                    Pair.of(ZonedDateTime.now().minusDays(7), ZonedDateTime.now()),
                    "",
                    List.of(),
                    Pair.of("", ""),
                    "",
                    Pair.of(0, 10)
            );

            assertNotNull(result);
            assertNotNull(result.orderList());
            assertEquals(1, result.orderList().size());
            assertEquals(1L, result.totalElements());
        }

        @Test
        void getAllOrder_whenNoOrders_shouldReturnEmptyOrderListVm() {
            Page<Order> emptyPage = Page.empty();

            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            OrderListVm result = orderService.getAllOrder(
                    Pair.of(ZonedDateTime.now().minusDays(7), ZonedDateTime.now()),
                    "",
                    List.of(),
                    Pair.of("", ""),
                    "",
                    Pair.of(0, 10)
            );

            assertNull(result.orderList());
            assertEquals(0, result.totalElements());
            assertEquals(0, result.totalPages());
        }

        @Test
        void getAllOrder_whenOrderStatusNotEmpty_shouldPassStatusToSpec() {
            Page<Order> emptyPage = Page.empty();

            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            orderService.getAllOrder(
                    Pair.of(ZonedDateTime.now().minusDays(7), ZonedDateTime.now()),
                    "product",
                    List.of(OrderStatus.COMPLETED, OrderStatus.PAID),
                    Pair.of("US", "123"),
                    "test@email.com",
                    Pair.of(0, 10)
            );

            verify(orderRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Nested
    class GetLatestOrdersTest {

        @Test
        void getLatestOrders_whenCountIsPositive_shouldReturnOrders() {
            Order order = buildOrder(1L);
            when(orderRepository.getLatestOrders(any(Pageable.class)))
                    .thenReturn(List.of(order));

            List<OrderBriefVm> result = orderService.getLatestOrders(5);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void getLatestOrders_whenCountIsZero_shouldReturnEmptyList() {
            List<OrderBriefVm> result = orderService.getLatestOrders(0);

            assertThat(result).isEmpty();
            verify(orderRepository, never()).getLatestOrders(any());
        }

        @Test
        void getLatestOrders_whenCountIsNegative_shouldReturnEmptyList() {
            List<OrderBriefVm> result = orderService.getLatestOrders(-1);

            assertThat(result).isEmpty();
            verify(orderRepository, never()).getLatestOrders(any());
        }

        @Test
        void getLatestOrders_whenNoOrdersFound_shouldReturnEmptyList() {
            when(orderRepository.getLatestOrders(any(Pageable.class)))
                    .thenReturn(List.of());

            List<OrderBriefVm> result = orderService.getLatestOrders(5);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class IsOrderCompletedWithUserIdAndProductIdTest {

        @Test
        void isOrderCompleted_whenProductHasNoVariations_shouldSearchWithProductId() {
            setSubjectUpSecurityContext("user-1");
            Long productId = 100L;

            when(productService.getProductVariations(productId)).thenReturn(Collections.emptyList());
            when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.of(buildOrder(1L)));

            OrderExistsByProductAndUserGetVm result =
                    orderService.isOrderCompletedWithUserIdAndProductId(productId);

            assertTrue(result.isPresent());
        }

        @Test
        void isOrderCompleted_whenProductHasVariations_shouldSearchWithVariationIds() {
            setSubjectUpSecurityContext("user-1");
            Long productId = 100L;

            List<ProductVariationVm> variations = List.of(
                    new ProductVariationVm(200L, "Variant A", "SKU-A"),
                    new ProductVariationVm(201L, "Variant B", "SKU-B")
            );

            when(productService.getProductVariations(productId)).thenReturn(variations);
            when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

            OrderExistsByProductAndUserGetVm result =
                    orderService.isOrderCompletedWithUserIdAndProductId(productId);

            assertFalse(result.isPresent());
        }

        @Test
        void isOrderCompleted_whenNoOrderFound_shouldReturnFalse() {
            setSubjectUpSecurityContext("user-1");
            Long productId = 100L;

            when(productService.getProductVariations(productId)).thenReturn(null);
            when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

            OrderExistsByProductAndUserGetVm result =
                    orderService.isOrderCompletedWithUserIdAndProductId(productId);

            assertFalse(result.isPresent());
        }
    }

    @Nested
    class GetMyOrdersTest {

        @Test
        void getMyOrders_whenOrdersExist_shouldReturnOrderGetVmList() {
            setSubjectUpSecurityContext("user-1");
            Order order = buildOrder(1L);

            when(orderRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Sort.class)))
                    .thenReturn(List.of(order));

            List<OrderGetVm> result = orderService.getMyOrders("Product", OrderStatus.COMPLETED);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void getMyOrders_whenNoOrders_shouldReturnEmptyList() {
            setSubjectUpSecurityContext("user-1");

            when(orderRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Sort.class)))
                    .thenReturn(List.of());

            List<OrderGetVm> result = orderService.getMyOrders(null, null);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class FindOrderByCheckoutIdTest {

        @Test
        void findOrderVmByCheckoutId_whenOrderExists_shouldReturnOrderGetVm() {
            String checkoutId = "checkout-123";
            Order order = buildOrder(1L);
            order.setCheckoutId(checkoutId);
            List<OrderItem> items = List.of(buildOrderItem(1L, 1L));

            when(orderRepository.findByCheckoutId(checkoutId)).thenReturn(Optional.of(order));
            when(orderItemRepository.findAllByOrderId(1L)).thenReturn(items);

            OrderGetVm result = orderService.findOrderVmByCheckoutId(checkoutId);

            assertNotNull(result);
            assertEquals(1L, result.id());
        }

        @Test
        void findOrderVmByCheckoutId_whenOrderHasNoItems_shouldReturnOrderGetVmWithEmptyItems() {
            String checkoutId = "checkout-empty";
            Order order = buildOrder(2L);
            order.setCheckoutId(checkoutId);

            when(orderRepository.findByCheckoutId(checkoutId)).thenReturn(Optional.of(order));
            when(orderItemRepository.findAllByOrderId(2L)).thenReturn(List.of());

            OrderGetVm result = orderService.findOrderVmByCheckoutId(checkoutId);

            assertNotNull(result);
            assertEquals(2L, result.id());
            assertNotNull(result.orderItems());
            assertTrue(result.orderItems().isEmpty());
        }

        @Test
        void findOrderByCheckoutId_whenExists_shouldReturnOrder() {
            String checkoutId = "checkout-123";
            Order order = buildOrder(1L);
            order.setCheckoutId(checkoutId);

            when(orderRepository.findByCheckoutId(checkoutId)).thenReturn(Optional.of(order));

            Order result = orderService.findOrderByCheckoutId(checkoutId);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(checkoutId, result.getCheckoutId());
        }

        @Test
        void findOrderByCheckoutId_whenNotFound_shouldThrowNotFoundException() {
            when(orderRepository.findByCheckoutId("non-existent"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> orderService.findOrderByCheckoutId("non-existent"));
        }
    }

    @Nested
    class UpdateOrderPaymentStatusTest {

        @Test
        void updateOrderPaymentStatus_whenCompletedPayment_shouldSetOrderStatusToPaid() {
            Order order = buildOrder(1L);
            PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                    .orderId(1L)
                    .paymentId(100L)
                    .paymentStatus("COMPLETED")
                    .build();

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

            assertNotNull(result);
            assertEquals(OrderStatus.PAID, order.getOrderStatus());
            assertEquals(PaymentStatus.COMPLETED, order.getPaymentStatus());
            assertEquals(100L, order.getPaymentId());
        }

        @Test
        void updateOrderPaymentStatus_whenPendingPayment_shouldNotChangeOrderStatus() {
            Order order = buildOrder(1L);
            order.setOrderStatus(OrderStatus.ACCEPTED);
            PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                    .orderId(1L)
                    .paymentId(100L)
                    .paymentStatus("PENDING")
                    .build();

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            orderService.updateOrderPaymentStatus(request);

            assertEquals(OrderStatus.ACCEPTED, order.getOrderStatus());
            assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        }

        @Test
        void updateOrderPaymentStatus_whenCancelledPayment_shouldNotChangeOrderStatus() {
            Order order = buildOrder(1L);
            order.setOrderStatus(OrderStatus.ACCEPTED);
            PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                    .orderId(1L)
                    .paymentId(100L)
                    .paymentStatus("CANCELLED")
                    .build();

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

            assertNotNull(result);
            assertEquals(OrderStatus.ACCEPTED, order.getOrderStatus());
            assertEquals(PaymentStatus.CANCELLED, order.getPaymentStatus());
            assertEquals(100L, order.getPaymentId());
        }

        @Test
        void updateOrderPaymentStatus_whenOrderNotFound_shouldThrowNotFoundException() {
            PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                    .orderId(999L)
                    .paymentId(100L)
                    .paymentStatus("COMPLETED")
                    .build();

            when(orderRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> orderService.updateOrderPaymentStatus(request));
        }
    }

    @Nested
    class RejectOrderTest {

        @Test
        void rejectOrder_whenOrderExists_shouldSetStatusToReject() {
            Order order = buildOrder(1L);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            orderService.rejectOrder(1L, "Out of stock");

            assertEquals(OrderStatus.REJECT, order.getOrderStatus());
            assertEquals("Out of stock", order.getRejectReason());
            verify(orderRepository).save(order);
        }

        @Test
        void rejectOrder_whenOrderNotFound_shouldThrowNotFoundException() {
            when(orderRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> orderService.rejectOrder(999L, "reason"));
        }
    }

    @Nested
    class AcceptOrderTest {

        @Test
        void acceptOrder_whenOrderExists_shouldSetStatusToAccepted() {
            Order order = buildOrder(1L);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            orderService.acceptOrder(1L);

            assertEquals(OrderStatus.ACCEPTED, order.getOrderStatus());
            verify(orderRepository).save(order);
        }

        @Test
        void acceptOrder_whenOrderNotFound_shouldThrowNotFoundException() {
            when(orderRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> orderService.acceptOrder(999L));
        }
    }

    @Nested
    class ExportCsvTest {

        @Test
        void exportCsv_whenOrdersExist_shouldReturnCsvBytes() throws Exception {
            OrderRequest request = OrderRequest.builder()
                    .createdFrom(ZonedDateTime.now().minusDays(7))
                    .createdTo(ZonedDateTime.now())
                    .productName("")
                    .orderStatus(List.of())
                    .billingCountry("")
                    .billingPhoneNumber("")
                    .email("")
                    .pageNo(0)
                    .pageSize(10)
                    .build();

            Order order = buildOrder(1L);
            Page<Order> orderPage = new PageImpl<>(List.of(order));

            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(orderPage);

            OrderItemCsv csv = OrderItemCsv.builder()
                    .orderStatus(OrderStatus.PENDING)
                    .email("test@example.com")
                    .totalPrice(new BigDecimal("100.00"))
                    .build();
            when(orderMapper.toCsv(any(OrderBriefVm.class))).thenReturn(csv);

            byte[] result = orderService.exportCsv(request);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        void exportCsv_whenNoOrders_shouldReturnEmptyExport() throws Exception {
            OrderRequest request = OrderRequest.builder()
                    .createdFrom(ZonedDateTime.now().minusDays(7))
                    .createdTo(ZonedDateTime.now())
                    .productName("")
                    .orderStatus(List.of())
                    .billingCountry("")
                    .billingPhoneNumber("")
                    .email("")
                    .pageNo(0)
                    .pageSize(10)
                    .build();

            Page<Order> emptyPage = Page.empty();
            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            byte[] result = orderService.exportCsv(request);

            assertNotNull(result);
        }
    }

    // --- Helper methods ---

    private OrderPostVm buildOrderPostVm() {
        OrderAddressPostVm shippingAddressVm = OrderAddressPostVm.builder()
                .contactName("John Doe")
                .phone("+123456789")
                .addressLine1("123 Main St")
                .addressLine2("Apt 4B")
                .city("Springfield")
                .zipCode("62701")
                .districtId(101L)
                .districtName("Downtown")
                .stateOrProvinceId(201L)
                .stateOrProvinceName("Illinois")
                .countryId(301L)
                .countryName("USA")
                .build();

        OrderAddressPostVm billingAddressVm = OrderAddressPostVm.builder()
                .contactName("Jane Smith")
                .phone("+1987654321")
                .addressLine1("789 Elm Street")
                .addressLine2("Suite 5A")
                .city("Greenville")
                .zipCode("29601")
                .districtId(102L)
                .districtName("North District")
                .stateOrProvinceId(202L)
                .stateOrProvinceName("South Carolina")
                .countryId(302L)
                .countryName("United States")
                .build();

        List<OrderItemPostVm> items = List.of(
                OrderItemPostVm.builder()
                        .productId(123L)
                        .productName("Wireless Mouse")
                        .quantity(2)
                        .productPrice(new BigDecimal("25.99"))
                        .note("Includes batteries")
                        .discountAmount(new BigDecimal("5.00"))
                        .taxAmount(new BigDecimal("2.00"))
                        .taxPercent(new BigDecimal("8.00"))
                        .build(),
                OrderItemPostVm.builder()
                        .productId(456L)
                        .productName("Keyboard")
                        .quantity(1)
                        .productPrice(new BigDecimal("49.99"))
                        .note("Mechanical")
                        .discountAmount(new BigDecimal("0.00"))
                        .taxAmount(new BigDecimal("4.00"))
                        .taxPercent(new BigDecimal("8.00"))
                        .build()
        );

        return OrderPostVm.builder()
                .checkoutId("checkout-123")
                .email("customer@example.com")
                .shippingAddressPostVm(shippingAddressVm)
                .billingAddressPostVm(billingAddressVm)
                .note("Please handle with care.")
                .tax(5.00f)
                .discount(10.00f)
                .numberItem(2)
                .totalPrice(new BigDecimal("89.97"))
                .deliveryFee(new BigDecimal("5.00"))
                .couponCode("COUPON2024")
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.COMPLETED)
                .orderItemPostVms(items)
                .build();
    }

    private Order buildOrder(Long id) {
        Order order = Order.builder()
                .id(id)
                .email("customer@example.com")
                .note("Test note")
                .tax(5.0f)
                .discount(10.0f)
                .numberItem(2)
                .totalPrice(new BigDecimal("89.97"))
                .deliveryFee(new BigDecimal("5.00"))
                .couponCode("COUPON2024")
                .orderStatus(OrderStatus.PENDING)
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(PaymentStatus.PENDING)
                .shippingAddressId(shippingAddress)
                .billingAddressId(billingAddress)
                .checkoutId("checkout-123")
                .build();
        return order;
    }

    private OrderPostVm buildOrderPostVmWithNoItems() {
        OrderAddressPostVm shippingAddressVm = OrderAddressPostVm.builder()
                .contactName("John Doe")
                .phone("+123456789")
                .addressLine1("123 Main St")
                .addressLine2("Apt 4B")
                .city("Springfield")
                .zipCode("62701")
                .districtId(101L)
                .districtName("Downtown")
                .stateOrProvinceId(201L)
                .stateOrProvinceName("Illinois")
                .countryId(301L)
                .countryName("USA")
                .build();

        OrderAddressPostVm billingAddressVm = OrderAddressPostVm.builder()
                .contactName("Jane Smith")
                .phone("+1987654321")
                .addressLine1("789 Elm Street")
                .addressLine2("Suite 5A")
                .city("Greenville")
                .zipCode("29601")
                .districtId(102L)
                .districtName("North District")
                .stateOrProvinceId(202L)
                .stateOrProvinceName("South Carolina")
                .countryId(302L)
                .countryName("United States")
                .build();

        return OrderPostVm.builder()
                .checkoutId("checkout-empty")
                .email("customer@example.com")
                .shippingAddressPostVm(shippingAddressVm)
                .billingAddressPostVm(billingAddressVm)
                .note("Empty order")
                .tax(0f)
                .discount(0f)
                .numberItem(0)
                .totalPrice(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .couponCode(null)
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of())
                .build();
    }

    private OrderPostVm buildOrderPostVmNoCoupon() {
        OrderAddressPostVm shippingAddressVm = OrderAddressPostVm.builder()
                .contactName("John Doe")
                .phone("+123456789")
                .addressLine1("123 Main St")
                .addressLine2("Apt 4B")
                .city("Springfield")
                .zipCode("62701")
                .districtId(101L)
                .districtName("Downtown")
                .stateOrProvinceId(201L)
                .stateOrProvinceName("Illinois")
                .countryId(301L)
                .countryName("USA")
                .build();

        OrderAddressPostVm billingAddressVm = OrderAddressPostVm.builder()
                .contactName("Jane Smith")
                .phone("+1987654321")
                .addressLine1("789 Elm Street")
                .addressLine2("Suite 5A")
                .city("Greenville")
                .zipCode("29601")
                .districtId(102L)
                .districtName("North District")
                .stateOrProvinceId(202L)
                .stateOrProvinceName("South Carolina")
                .countryId(302L)
                .countryName("United States")
                .build();

        List<OrderItemPostVm> items = List.of(
                OrderItemPostVm.builder()
                        .productId(789L)
                        .productName("USB Cable")
                        .quantity(1)
                        .productPrice(new BigDecimal("9.99"))
                        .note("")
                        .discountAmount(BigDecimal.ZERO)
                        .taxAmount(BigDecimal.ZERO)
                        .taxPercent(BigDecimal.ZERO)
                        .build()
        );

        return OrderPostVm.builder()
                .checkoutId("checkout-no-coupon")
                .email("customer@example.com")
                .shippingAddressPostVm(shippingAddressVm)
                .billingAddressPostVm(billingAddressVm)
                .note("No coupon order")
                .tax(0f)
                .discount(0f)
                .numberItem(1)
                .totalPrice(new BigDecimal("9.99"))
                .deliveryFee(new BigDecimal("3.00"))
                .couponCode(null)
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(items)
                .build();
    }

    private Order buildOrderNoCoupon(Long id) {
        Order order = Order.builder()
                .id(id)
                .email("customer@example.com")
                .note("No coupon order")
                .tax(0f)
                .discount(0f)
                .numberItem(1)
                .totalPrice(new BigDecimal("9.99"))
                .deliveryFee(new BigDecimal("3.00"))
                .couponCode(null)
                .orderStatus(OrderStatus.PENDING)
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(PaymentStatus.PENDING)
                .shippingAddressId(shippingAddress)
                .billingAddressId(billingAddress)
                .checkoutId("checkout-no-coupon")
                .build();
        return order;
    }

    private OrderItem buildOrderItem(Long id, Long orderId) {
        return OrderItem.builder()
                .id(id)
                .productId(123L)
                .productName("Wireless Mouse")
                .quantity(2)
                .productPrice(new BigDecimal("25.99"))
                .note("Includes batteries")
                .orderId(orderId)
                .discountAmount(new BigDecimal("5.00"))
                .taxAmount(new BigDecimal("2.00"))
                .taxPercent(new BigDecimal("8.00"))
                .build();
    }
}

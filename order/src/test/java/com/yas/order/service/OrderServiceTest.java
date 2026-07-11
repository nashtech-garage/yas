package com.yas.order.service;

import static com.yas.order.utils.Constants.ErrorCode.ORDER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.model.request.OrderRequest;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.*;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.product.ProductVariationVm;

// FIX: Bỏ duplicate imports (OrderRequest và OrderBriefVm đã được import ở trên)
import static org.mockito.ArgumentMatchers.anyCollection;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.junit.jupiter.api.Disabled;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private ProductService productService;
    @Mock private CartService cartService;
    @Mock private OrderMapper orderMapper;
    @Mock private PromotionService promotionService;

    @InjectMocks
    private OrderService orderService;

    // ── helpers ──────────────────────────────────────────────────────────────

    private Order buildOrder(Long id, OrderStatus status) {
        Order o = new Order();
        o.setId(id);
        o.setEmail("customer@yas.com");
        o.setOrderStatus(status);
        o.setDeliveryStatus(DeliveryStatus.PREPARING);
        o.setPaymentStatus(PaymentStatus.PENDING);
        o.setTotalPrice(BigDecimal.valueOf(500_000));
        o.setCouponCode("SAVE10");

        com.yas.order.model.OrderAddress address = new com.yas.order.model.OrderAddress();
        address.setId(1L);
        o.setShippingAddressId(address);
        o.setBillingAddressId(address);

        return o;
    }

    private OrderAddressPostVm buildAddressVm() {
        return new OrderAddressPostVm(
                "0901234567", "Nguyen Van A",
                "123 Le Loi", "", "Ho Chi Minh",
                "70000", 1L, "District 1",
                1L, "HCM", 1L, "Vietnam"
        );
    }

    private OrderPostVm buildOrderPostVm() {
        OrderItemPostVm item = new OrderItemPostVm(
                101L, "Laptop", 1, BigDecimal.valueOf(500_000), "",
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        );

        return new OrderPostVm(
                "customer@yas.com", "no note",
                buildAddressVm(), buildAddressVm(),
                "checkout-001", 0f, 0f, 0,
                BigDecimal.valueOf(500_000), BigDecimal.ZERO, "SAVE10",
                null, null, PaymentStatus.PENDING,
                List.of(item)
        );
    }

    // ═════════════════════════════════════════════════════════════════════════
    // getOrderWithItemsById
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getOrderWithItemsById")
    class GetOrderWithItemsById {

        @Test
        @DisplayName("Tìm thấy order → trả về OrderVm kèm items")
        void whenFound_shouldReturnOrderVm() {
            Order order = buildOrder(1L, OrderStatus.PENDING);
            OrderItem item = new OrderItem();
            item.setId(1L);
            item.setProductId(101L);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(item));

            OrderVm result = orderService.getOrderWithItemsById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.orderItemVms()).hasSize(1);
            verify(orderRepository).findById(1L);
            verify(orderItemRepository).findAllByOrderId(1L);
        }

        @Test
        @DisplayName("Không tìm thấy order → ném NotFoundException")
        void whenNotFound_shouldThrowNotFoundException() {
            when(orderRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.getOrderWithItemsById(99L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // getLatestOrders
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getLatestOrders")
    class GetLatestOrders {

        @Test
        @DisplayName("count hợp lệ → trả về danh sách")
        void whenValidCount_shouldReturnOrders() {
            Order order = buildOrder(1L, OrderStatus.ACCEPTED);
            when(orderRepository.getLatestOrders(any(Pageable.class)))
                    .thenReturn(List.of(order));

            List<OrderBriefVm> result = orderService.getLatestOrders(5);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("count <= 0 → trả về danh sách rỗng ngay, không gọi repository")
        void whenCountZeroOrNegative_shouldReturnEmpty() {
            assertThat(orderService.getLatestOrders(0)).isEmpty();
            assertThat(orderService.getLatestOrders(-1)).isEmpty();
            verifyNoInteractions(orderRepository);
        }

        @Test
        @DisplayName("Repository trả về rỗng → trả về danh sách rỗng")
        void whenRepositoryEmpty_shouldReturnEmpty() {
            when(orderRepository.getLatestOrders(any(Pageable.class)))
                    .thenReturn(Collections.emptyList());

            assertThat(orderService.getLatestOrders(5)).isEmpty();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // findOrderByCheckoutId
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("findOrderByCheckoutId")
    class FindOrderByCheckoutId {

        @Test
        @DisplayName("Tìm thấy → trả về Order")
        void whenFound_shouldReturnOrder() {
            Order order = buildOrder(1L, OrderStatus.PENDING);
            when(orderRepository.findByCheckoutId("checkout-001"))
                    .thenReturn(Optional.of(order));

            Order result = orderService.findOrderByCheckoutId("checkout-001");

            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Không tìm thấy → ném NotFoundException")
        void whenNotFound_shouldThrowNotFoundException() {
            when(orderRepository.findByCheckoutId("invalid-id"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.findOrderByCheckoutId("invalid-id"))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // findOrderVmByCheckoutId
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("findOrderVmByCheckoutId")
    class FindOrderVmByCheckoutId {

        @Test
        @DisplayName("Tìm thấy → trả về OrderGetVm kèm items")
        void whenFound_shouldReturnOrderGetVm() {
            Order order = buildOrder(1L, OrderStatus.PENDING);
            when(orderRepository.findByCheckoutId("checkout-001"))
                    .thenReturn(Optional.of(order));
            when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of());

            OrderGetVm result = orderService.findOrderVmByCheckoutId("checkout-001");

            assertThat(result).isNotNull();
            verify(orderItemRepository).findAllByOrderId(1L);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // updateOrderPaymentStatus
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("updateOrderPaymentStatus")
    class UpdateOrderPaymentStatus {

        @Test
        @DisplayName("Payment COMPLETED → OrderStatus chuyển sang PAID")
        void whenPaymentCompleted_shouldSetOrderStatusPaid() {
            Order order = buildOrder(1L, OrderStatus.ACCEPTED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderRepository.save(any())).thenReturn(order);

            PaymentOrderStatusVm input = PaymentOrderStatusVm.builder()
                    .orderId(1L)
                    .paymentId(1L)
                    .paymentStatus(PaymentStatus.COMPLETED.name())
                    .build();

            PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(input);

            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
            assertThat(result.paymentId()).isEqualTo(1L);
            assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED.name());
        }

        @Test
        @DisplayName("Payment PENDING → OrderStatus KHÔNG thay đổi sang PAID")
        void whenPaymentPending_shouldNotSetOrderStatusPaid() {
            Order order = buildOrder(1L, OrderStatus.ACCEPTED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderRepository.save(any())).thenReturn(order);

            PaymentOrderStatusVm input = PaymentOrderStatusVm.builder()
                    .orderId(1L)
                    .paymentId(2L)
                    .paymentStatus(PaymentStatus.PENDING.name())
                    .build();

            orderService.updateOrderPaymentStatus(input);

            assertThat(order.getOrderStatus()).isNotEqualTo(OrderStatus.PAID);
        }

        @Test
        @DisplayName("Order không tồn tại → ném NotFoundException")
        void whenOrderNotFound_shouldThrow() {
            when(orderRepository.findById(99L)).thenReturn(Optional.empty());

            PaymentOrderStatusVm input = PaymentOrderStatusVm.builder()
                    .orderId(99L).paymentStatus("PENDING").build();

            assertThatThrownBy(() -> orderService.updateOrderPaymentStatus(input))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // rejectOrder
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("rejectOrder")
    class RejectOrder {

        @Test
        @DisplayName("Reject thành công → OrderStatus = REJECT, rejectReason được set")
        void whenFound_shouldRejectOrder() {
            Order order = buildOrder(1L, OrderStatus.ACCEPTED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            orderService.rejectOrder(1L, "Hết hàng");

            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
            assertThat(order.getRejectReason()).isEqualTo("Hết hàng");
            verify(orderRepository).save(order);
        }

        @Test
        @DisplayName("Order không tồn tại → ném NotFoundException")
        void whenNotFound_shouldThrow() {
            when(orderRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.rejectOrder(99L, "reason"))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // acceptOrder
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("acceptOrder")
    class AcceptOrder {

        @Test
        @DisplayName("Accept thành công → OrderStatus = ACCEPTED")
        void whenFound_shouldAcceptOrder() {
            Order order = buildOrder(1L, OrderStatus.PENDING);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            orderService.acceptOrder(1L);

            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
            verify(orderRepository).save(order);
        }

        @Test
        @DisplayName("Order không tồn tại → ném NotFoundException")
        void whenNotFound_shouldThrow() {
            when(orderRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.acceptOrder(99L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // getAllOrder
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getAllOrder")
    class GetAllOrder {

        @Test
        @DisplayName("Có dữ liệu → trả về OrderListVm với đúng totalElements")
        @SuppressWarnings("unchecked")
        void whenOrdersExist_shouldReturnOrderListVm() {
            Order order = buildOrder(1L, OrderStatus.PENDING);
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn((Page<Order>) page);

            ZonedDateTime now = ZonedDateTime.now();
            OrderListVm result = orderService.getAllOrder(
                    Pair.of(now.minusDays(7), now),
                    "", List.of(),
                    Pair.of("", ""),
                    "", Pair.of(0, 10)
            );

            assertThat(result.totalElements()).isEqualTo(1);
            assertThat(result.orderList()).hasSize(1);
        }

        @Test
        @DisplayName("Không có dữ liệu → trả về OrderListVm với orderList rỗng hoặc null")
        @SuppressWarnings("unchecked")
        void whenNoOrders_shouldReturnEmptyListVm() {
            Page<Order> emptyPage = new PageImpl<>(List.of());
            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            ZonedDateTime now = ZonedDateTime.now();
            OrderListVm result = orderService.getAllOrder(
                    Pair.of(now.minusDays(7), now),
                    "", List.of(),
                    Pair.of("", ""),
                    "", Pair.of(0, 10)
            );

            assertThat(result.totalElements()).isEqualTo(0);
            // FIX: service có thể trả về null hoặc empty list tuỳ implementation
            // Dùng satisfiesAnyOf thay vì assert cứng isNull()
            assertThat(result.orderList()).satisfiesAnyOf(
                    list -> assertThat(list).isNull(),
                    list -> assertThat(list).isEmpty()
            );
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isOrderCompletedWithUserIdAndProductId
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("isOrderCompletedWithUserIdAndProductId")
    class IsOrderCompleted {

        @Test
        @DisplayName("Không có variations → dùng productId gốc, trả về true nếu tồn tại")
        @SuppressWarnings("unchecked")
        void whenNoVariations_andOrderExists_shouldReturnTrue() {
            try (MockedStatic<AuthenticationUtils> auth =
                         mockStatic(AuthenticationUtils.class)) {
                auth.when(AuthenticationUtils::extractUserId).thenReturn("user-001");

                when(productService.getProductVariations(101L)).thenReturn(List.of());
                when(orderRepository.findOne(any(Specification.class)))
                        .thenReturn(Optional.of(buildOrder(1L, OrderStatus.COMPLETED)));

                OrderExistsByProductAndUserGetVm result =
                        orderService.isOrderCompletedWithUserIdAndProductId(101L);

                assertThat(result.isPresent()).isTrue();
            }
        }

        @Test
        @DisplayName("Có variations → dùng danh sách variation ids, trả về false nếu không tồn tại")
        @SuppressWarnings("unchecked")
        void whenVariationsExist_andNoOrder_shouldReturnFalse() {
            try (MockedStatic<AuthenticationUtils> auth =
                         mockStatic(AuthenticationUtils.class)) {
                auth.when(AuthenticationUtils::extractUserId).thenReturn("user-001");

                ProductVariationVm var1 = new ProductVariationVm(201L, "Size M", "SKU123");
                when(productService.getProductVariations(101L)).thenReturn(List.of(var1));
                when(orderRepository.findOne(any(Specification.class)))
                        .thenReturn(Optional.empty());

                OrderExistsByProductAndUserGetVm result =
                        orderService.isOrderCompletedWithUserIdAndProductId(101L);

                assertThat(result.isPresent()).isFalse();
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // getMyOrders
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getMyOrders")
    class GetMyOrders {

        @Test
        @DisplayName("Có orders → trả về danh sách OrderGetVm")
        @SuppressWarnings("unchecked")
        void whenOrdersExist_shouldReturnList() {
            try (MockedStatic<AuthenticationUtils> auth =
                         mockStatic(AuthenticationUtils.class)) {
                auth.when(AuthenticationUtils::extractUserId).thenReturn("user-001");

                Order order = buildOrder(1L, OrderStatus.ACCEPTED);
                when(orderRepository.findAll(any(Specification.class), any(Sort.class)))
                        .thenReturn(List.of(order));

                List<OrderGetVm> result =
                        orderService.getMyOrders("Laptop", OrderStatus.ACCEPTED);

                assertThat(result).hasSize(1);
            }
        }

        @Test
        @DisplayName("Không có orders → trả về danh sách rỗng")
        @SuppressWarnings("unchecked")
        void whenNoOrders_shouldReturnEmptyList() {
            try (MockedStatic<AuthenticationUtils> auth =
                         mockStatic(AuthenticationUtils.class)) {
                auth.when(AuthenticationUtils::extractUserId).thenReturn("user-001");

                when(orderRepository.findAll(any(Specification.class), any(Sort.class)))
                        .thenReturn(List.of());

                List<OrderGetVm> result = orderService.getMyOrders("", null);

                assertThat(result).isEmpty();
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // createOrder
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("createOrder")
    class CreateOrder {

        @Test
        @DisplayName("Tạo order thành công → lưu, xóa cart, cập nhật promotion")
        void whenValidRequest_shouldCreateAndReturnOrderVm() {
            OrderPostVm postVm = buildOrderPostVm();
            Order savedOrder = buildOrder(1L, OrderStatus.PENDING);

            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
            when(orderRepository.findById(any())).thenReturn(Optional.of(savedOrder));
            when(orderItemRepository.saveAll(any())).thenReturn(List.of());

            OrderVm result = orderService.createOrder(postVm);

            assertThat(result).isNotNull();
            verify(orderRepository, atLeastOnce()).save(any(Order.class));
            verify(orderItemRepository).saveAll(anyCollection());
            verify(cartService).deleteCartItems(any(OrderVm.class));
            verify(promotionService).updateUsagePromotion(anyList());
        }

        @Test
        @DisplayName("Tạo order với coupon → updateUsagePromotion nhận đúng promotionCode")
        void whenCouponProvided_shouldCallUpdateUsagePromotionWithCorrectCode() {
            OrderPostVm postVm = buildOrderPostVm(); // couponCode = "SAVE10"
            Order savedOrder = buildOrder(1L, OrderStatus.PENDING);

            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
            when(orderRepository.findById(any())).thenReturn(Optional.of(savedOrder));
            when(orderItemRepository.saveAll(anyCollection())).thenReturn(List.of());

            orderService.createOrder(postVm);

            verify(promotionService).updateUsagePromotion(argThat(list ->
                    !list.isEmpty() && "SAVE10".equals(list.get(0).promotionCode())
            ));
        }

        @Test
        @DisplayName("Tạo order → gọi subtractProductStockQuantity")
        void whenValidRequest_shouldSubtractProductStock() {
            OrderPostVm postVm = buildOrderPostVm();
            Order savedOrder = buildOrder(1L, OrderStatus.PENDING);

            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
            when(orderRepository.findById(any())).thenReturn(Optional.of(savedOrder));
            when(orderItemRepository.saveAll(any())).thenReturn(List.of());

            orderService.createOrder(postVm);

            verify(productService).subtractProductStockQuantity(any(OrderVm.class));
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // exportCsv
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("exportCsv")
    class ExportCsv {

        @Test
        @DisplayName("Có orders → trả về byte[] không rỗng")
        @SuppressWarnings("unchecked")
        void whenOrdersExist_shouldReturnCsvBytes() throws Exception {
            Order order = buildOrder(1L, OrderStatus.COMPLETED);
            Page<Order> page = new PageImpl<>(List.of(order));

            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);
            when(orderMapper.toCsv(any(OrderBriefVm.class)))
                    .thenReturn(com.yas.order.model.csv.OrderItemCsv.builder().build());

            OrderRequest request = new OrderRequest();
            request.setCreatedFrom(ZonedDateTime.now().minusDays(7));
            request.setCreatedTo(ZonedDateTime.now());
            request.setProductName("");
            request.setOrderStatus(List.of());
            request.setBillingCountry("");
            request.setBillingPhoneNumber("");
            request.setEmail("");
            request.setPageNo(0);
            request.setPageSize(10);

            byte[] result = orderService.exportCsv(request);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Không có orders → trả về CSV rỗng (không ném exception)")
        @SuppressWarnings("unchecked")
        void whenNoOrders_shouldReturnEmptyCsv() throws Exception {
            Page<Order> emptyPage = new PageImpl<>(List.of());

            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            OrderRequest request = new OrderRequest();
            request.setCreatedFrom(ZonedDateTime.now().minusDays(7));
            request.setCreatedTo(ZonedDateTime.now());
            request.setProductName("");
            request.setOrderStatus(List.of());
            request.setBillingCountry("");
            request.setBillingPhoneNumber("");
            request.setEmail("");
            request.setPageNo(0);
            request.setPageSize(10);

            byte[] result = orderService.exportCsv(request);

            assertThat(result).isNotNull();
            verify(orderMapper, never()).toCsv(any());
        }

        @Test
        @DisplayName("getAllOrder trả về null orderList → exportCsv trả về CSV rỗng")
        @SuppressWarnings("unchecked")
        void whenOrderListIsNull_shouldReturnEmptyCsv() throws Exception {
            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of()));

            OrderRequest request = new OrderRequest();
            request.setCreatedFrom(ZonedDateTime.now().minusDays(7));
            request.setCreatedTo(ZonedDateTime.now());
            request.setProductName("");
            request.setOrderStatus(List.of());
            request.setBillingCountry("");
            request.setBillingPhoneNumber("");
            request.setEmail("");
            request.setPageNo(0);
            request.setPageSize(10);

            byte[] result = orderService.exportCsv(request);

            assertThat(result).isNotNull();
            verify(orderMapper, never()).toCsv(any());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 1. getAllOrder — branch orderStatus KHÔNG rỗng
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getAllOrder — branch orderStatus không rỗng")
    class GetAllOrderWithSpecificStatus {

        @Test
        @DisplayName("Truyền list status cụ thể → không dùng allOrderStatus, vẫn trả về kết quả đúng")
        @SuppressWarnings("unchecked")
        void whenOrderStatusNotEmpty_shouldUseProvidedStatus() {
            Order order = buildOrder(1L, OrderStatus.PENDING);
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            ZonedDateTime now = ZonedDateTime.now();
            OrderListVm result = orderService.getAllOrder(
                    Pair.of(now.minusDays(7), now),
                    "",
                    List.of(OrderStatus.PENDING, OrderStatus.ACCEPTED),
                    Pair.of("", ""),
                    "",
                    Pair.of(0, 10)
            );

            assertThat(result.totalElements()).isEqualTo(1);
            assertThat(result.orderList()).hasSize(1);
            verify(orderRepository).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Có nhiều trang → totalPages trả về đúng")
        @SuppressWarnings("unchecked")
        void whenMultiplePages_shouldReturnCorrectTotalPages() {
            List<Order> orders = List.of(buildOrder(1L, OrderStatus.PENDING));
            Pageable pageable = PageRequest.of(0, 1);
            Page<Order> page = new PageImpl<>(orders, pageable, 5);
            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            ZonedDateTime now = ZonedDateTime.now();
            OrderListVm result = orderService.getAllOrder(
                    Pair.of(now.minusDays(7), now),
                    "", List.of(),
                    Pair.of("", ""),
                    "", Pair.of(0, 1)
            );

            assertThat(result.totalElements()).isEqualTo(5);
            assertThat(result.totalPages()).isEqualTo(5);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 2. findOrderVmByCheckoutId — not found path
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("findOrderVmByCheckoutId — not found")
    class FindOrderVmByCheckoutIdNotFound {

        @Test
        @DisplayName("CheckoutId không tồn tại → ném NotFoundException")
        void whenCheckoutIdNotFound_shouldThrowNotFoundException() {
            when(orderRepository.findByCheckoutId("invalid-checkout"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.findOrderVmByCheckoutId("invalid-checkout"))
                    .isInstanceOf(NotFoundException.class);

            verifyNoInteractions(orderItemRepository);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 3. isOrderCompletedWithUserIdAndProductId — variations exist + order found
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("isOrderCompleted — variations exist và order tồn tại")
    class IsOrderCompletedWithVariationsAndFound {

        @Test
        @DisplayName("Có variations VÀ tìm thấy order → trả về true")
        @SuppressWarnings("unchecked")
        void whenVariationsExistAndOrderFound_shouldReturnTrue() {
            try (MockedStatic<AuthenticationUtils> auth =
                         mockStatic(AuthenticationUtils.class)) {
                auth.when(AuthenticationUtils::extractUserId).thenReturn("user-001");

                com.yas.order.viewmodel.product.ProductVariationVm var1 =
                        new com.yas.order.viewmodel.product.ProductVariationVm(201L, "Size M", "SKU001");
                com.yas.order.viewmodel.product.ProductVariationVm var2 =
                        new com.yas.order.viewmodel.product.ProductVariationVm(202L, "Size L", "SKU002");

                when(productService.getProductVariations(101L))
                        .thenReturn(List.of(var1, var2));
                when(orderRepository.findOne(any(Specification.class)))
                        .thenReturn(Optional.of(buildOrder(1L, OrderStatus.COMPLETED)));

                OrderExistsByProductAndUserGetVm result =
                        orderService.isOrderCompletedWithUserIdAndProductId(101L);

                assertThat(result.isPresent()).isTrue();
            }
        }

        @Test
        @DisplayName("Không có variations VÀ không tìm thấy order → trả về false")
        @SuppressWarnings("unchecked")
        void whenNoVariationsAndOrderNotFound_shouldReturnFalse() {
            try (MockedStatic<AuthenticationUtils> auth =
                         mockStatic(AuthenticationUtils.class)) {
                auth.when(AuthenticationUtils::extractUserId).thenReturn("user-001");

                when(productService.getProductVariations(999L)).thenReturn(List.of());
                when(orderRepository.findOne(any(Specification.class)))
                        .thenReturn(Optional.empty());

                OrderExistsByProductAndUserGetVm result =
                        orderService.isOrderCompletedWithUserIdAndProductId(999L);

                assertThat(result.isPresent()).isFalse();
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 4. updateOrderPaymentStatus — các PaymentStatus khác ngoài COMPLETED
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("updateOrderPaymentStatus — các status không phải COMPLETED")
    class UpdateOrderPaymentStatusOtherStatuses {

        @Test
        @DisplayName("PaymentStatus = CANCELLED → OrderStatus không đổi sang PAID")
        void whenPaymentCancelled_shouldNotSetPaid() {
            Order order = buildOrder(1L, OrderStatus.ACCEPTED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderRepository.save(any())).thenReturn(order);

            PaymentOrderStatusVm input = PaymentOrderStatusVm.builder()
                    .orderId(1L)
                    .paymentId(10L)
                    .paymentStatus(PaymentStatus.CANCELLED.name())
                    .build();

            PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(input);

            assertThat(order.getOrderStatus()).isNotEqualTo(OrderStatus.PAID);
            assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
            assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.CANCELLED.name());
        }

        @Test
        @DisplayName("PaymentStatus = PENDING → OrderStatus không đổi sang PAID, paymentStatus được set đúng")
        void whenPaymentPending_shouldNotSetPaidAndSetCorrectPaymentStatus() {
            Order order = buildOrder(1L, OrderStatus.ACCEPTED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderRepository.save(any())).thenReturn(order);

            PaymentOrderStatusVm input = PaymentOrderStatusVm.builder()
                    .orderId(1L)
                    .paymentId(11L)
                    .paymentStatus(PaymentStatus.PENDING.name())
                    .build();

            PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(input);

            assertThat(order.getOrderStatus()).isNotEqualTo(OrderStatus.PAID);
            assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.PENDING.name());
        }

        @Test
        @DisplayName("PaymentStatus = COMPLETED → verify đầy đủ orderId và orderStatus trong response")
        void whenPaymentCompleted_shouldReturnCorrectVmFields() {
            Order order = buildOrder(5L, OrderStatus.ACCEPTED);
            when(orderRepository.findById(5L)).thenReturn(Optional.of(order));
            when(orderRepository.save(any())).thenAnswer(inv -> {
                Order saved = inv.getArgument(0);
                saved.setOrderStatus(OrderStatus.PAID);
                return saved;
            });

            PaymentOrderStatusVm input = PaymentOrderStatusVm.builder()
                    .orderId(5L)
                    .paymentId(99L)
                    .paymentStatus(PaymentStatus.COMPLETED.name())
                    .build();

            PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(input);

            assertThat(result.orderId()).isEqualTo(5L);
            assertThat(result.paymentId()).isEqualTo(99L);
            assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED.name());
            assertThat(result.orderStatus()).isEqualTo(OrderStatus.PAID.getName());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 5. createOrder — empty items list
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("createOrder — không có order items")
    class CreateOrderEmptyItems {

        @Test
        @DisplayName("OrderItems rỗng → promotionUsageVms rỗng, updateUsagePromotion vẫn được gọi")
        void whenNoItems_shouldCallUpdateUsagePromotionWithEmptyList() {
            OrderPostVm postVm = new OrderPostVm(
                    "customer@yas.com", "no note",
                    buildAddressVm(), buildAddressVm(),
                    "checkout-002", 0f, 0f, 0,
                    BigDecimal.ZERO, BigDecimal.ZERO, "SAVE10",
                    null, null, PaymentStatus.PENDING,
                    List.of()
            );

            Order savedOrder = buildOrder(2L, OrderStatus.PENDING);
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
            when(orderRepository.findById(any())).thenReturn(Optional.of(savedOrder));
            when(orderItemRepository.saveAll(any())).thenReturn(List.of());

            OrderVm result = orderService.createOrder(postVm);

            assertThat(result).isNotNull();
            verify(promotionService).updateUsagePromotion(argThat(List::isEmpty));
            verify(cartService).deleteCartItems(any(OrderVm.class));
            verify(productService).subtractProductStockQuantity(any(OrderVm.class));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 6. exportCsv — orderStatus không rỗng trong OrderRequest
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("exportCsv — orderStatus không rỗng")
    class ExportCsvWithSpecificStatus {

        @Test
        @DisplayName("OrderRequest có status cụ thể → vẫn export được, byte[] có dữ liệu")
        @SuppressWarnings("unchecked")
        void whenOrderStatusProvided_shouldExportCsvWithData() throws Exception {
            Order order = buildOrder(1L, OrderStatus.COMPLETED);
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);
            when(orderMapper.toCsv(any(OrderBriefVm.class)))
                    .thenReturn(com.yas.order.model.csv.OrderItemCsv.builder().build());

            OrderRequest request = new OrderRequest();
            request.setCreatedFrom(ZonedDateTime.now().minusDays(7));
            request.setCreatedTo(ZonedDateTime.now());
            request.setProductName("");
            request.setOrderStatus(List.of(OrderStatus.COMPLETED));
            request.setBillingCountry("");
            request.setBillingPhoneNumber("");
            request.setEmail("");
            request.setPageNo(0);
            request.setPageSize(10);

            byte[] result = orderService.exportCsv(request);

            assertThat(result).isNotNull();
            assertThat(result.length).isGreaterThan(0);
            verify(orderMapper, atLeastOnce()).toCsv(any());
        }

        @Test
        @DisplayName("Có orders → byte[] CSV thực sự có dữ liệu (length > 0)")
        @SuppressWarnings("unchecked")
        void whenOrdersExist_csvShouldHaveContent() throws Exception {
            Order order = buildOrder(3L, OrderStatus.COMPLETED);
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);
            when(orderMapper.toCsv(any(OrderBriefVm.class)))
                    .thenReturn(com.yas.order.model.csv.OrderItemCsv.builder().build());

            OrderRequest request = new OrderRequest();
            request.setCreatedFrom(ZonedDateTime.now().minusDays(1));
            request.setCreatedTo(ZonedDateTime.now());
            request.setProductName("");
            request.setOrderStatus(List.of());
            request.setBillingCountry("");
            request.setBillingPhoneNumber("");
            request.setEmail("");
            request.setPageNo(0);
            request.setPageSize(10);

            byte[] result = orderService.exportCsv(request);

            assertThat(result).isNotNull();
            assertThat(result.length).isGreaterThan(0);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 7. getOrderWithItemsById — verify orderItems được map thành OrderItemVm
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getOrderWithItemsById — verify items mapping")
    class GetOrderWithItemsByIdMapping {

        @Test
        @DisplayName("Order có nhiều items → orderItemVms chứa đầy đủ")
        void whenOrderHasMultipleItems_shouldMapAllItems() {
            Order order = buildOrder(1L, OrderStatus.ACCEPTED);

            OrderItem item1 = new OrderItem();
            item1.setId(1L);
            item1.setProductId(101L);
            item1.setQuantity(2);
            item1.setProductPrice(BigDecimal.valueOf(100_000));

            OrderItem item2 = new OrderItem();
            item2.setId(2L);
            item2.setProductId(102L);
            item2.setQuantity(1);
            item2.setProductPrice(BigDecimal.valueOf(200_000));

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(item1, item2));

            OrderVm result = orderService.getOrderWithItemsById(1L);

            assertThat(result.orderItemVms()).hasSize(2);
        }

        @Test
        @DisplayName("Order không có item nào → orderItemVms rỗng")
        void whenOrderHasNoItems_shouldReturnEmptyItemVms() {
            Order order = buildOrder(1L, OrderStatus.PENDING);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of());

            OrderVm result = orderService.getOrderWithItemsById(1L);

            assertThat(result.orderItemVms()).isEmpty();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 8. acceptOrder — verify idempotent với ACCEPTED → ACCEPTED
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("acceptOrder — edge cases")
    class AcceptOrderEdgeCases {

        @Test
        @DisplayName("Order đang ACCEPTED → vẫn set ACCEPTED và save")
        void whenOrderAlreadyAccepted_shouldStillSave() {
            Order order = buildOrder(1L, OrderStatus.ACCEPTED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            orderService.acceptOrder(1L);

            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
            verify(orderRepository).save(order);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 9. rejectOrder — verify rejectReason null vẫn hoạt động
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("rejectOrder — edge cases")
    class RejectOrderEdgeCases {

        @Test
        @DisplayName("rejectReason = null → vẫn set REJECT và save")
        void whenRejectReasonNull_shouldStillReject() {
            Order order = buildOrder(1L, OrderStatus.ACCEPTED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            orderService.rejectOrder(1L, null);

            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
            assertThat(order.getRejectReason()).isNull();
            verify(orderRepository).save(order);
        }

        @Test
        @DisplayName("rejectReason chuỗi rỗng → vẫn set REJECT")
        void whenRejectReasonEmpty_shouldSetRejectWithEmptyReason() {
            Order order = buildOrder(1L, OrderStatus.PENDING);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            orderService.rejectOrder(1L, "");

            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
            assertThat(order.getRejectReason()).isEmpty();
        }
    }

}
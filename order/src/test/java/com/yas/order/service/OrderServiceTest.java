package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setSubjectUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private OrderAddressPostVm addressPostVm;

    @BeforeEach
    void setUp() {
        addressPostVm = OrderAddressPostVm.builder()
                .contactName("John Doe")
                .phone("0123456789")
                .addressLine1("123 Main St")
                .addressLine2("")
                .city("Hanoi")
                .zipCode("10000")
                .districtId(1L)
                .districtName("Ba Dinh")
                .stateOrProvinceId(1L)
                .stateOrProvinceName("Hanoi")
                .countryId(1L)
                .countryName("Vietnam")
                .build();
    }

    private Order buildOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setEmail("test@example.com");
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShippingAddressId(new OrderAddress());
        order.setBillingAddressId(new OrderAddress());
        return order;
    }

    private OrderPostVm buildOrderPostVm(List<OrderItemPostVm> items) {
        return OrderPostVm.builder()
                .checkoutId("checkout-1")
                .email("test@example.com")
                .shippingAddressPostVm(addressPostVm)
                .billingAddressPostVm(addressPostVm)
                .note("note")
                .tax(0f)
                .discount(0f)
                .numberItem(items.size())
                .totalPrice(BigDecimal.TEN)
                .deliveryFee(BigDecimal.ONE)
                .couponCode(null)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(items)
                .build();
    }

    @Nested
    class CreateOrderTest {

        @Test
        void createOrder_validRequest_savesAndReturnsOrder() {
            OrderItemPostVm item = OrderItemPostVm.builder()
                    .productId(10L)
                    .productName("Product A")
                    .quantity(2)
                    .productPrice(BigDecimal.valueOf(50))
                    .note("")
                    .build();
            OrderPostVm postVm = buildOrderPostVm(List.of(item));

            Order savedOrder = buildOrder();
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
            when(orderItemRepository.saveAll(any())).thenReturn(Collections.emptyList());
            doNothing().when(productService).subtractProductStockQuantity(any(OrderVm.class));
            doNothing().when(cartService).deleteCartItems(any(OrderVm.class));
            // acceptOrder is called internally — mock the findById for it
            when(orderRepository.findById(any())).thenReturn(Optional.of(savedOrder));
            doNothing().when(promotionService).updateUsagePromotion(anyList());

            OrderVm result = orderService.createOrder(postVm);

            assertThat(result).isNotNull();
            verify(orderRepository, times(2)).save(any(Order.class));
            verify(orderItemRepository).saveAll(any());
        }

        @Test
        void createOrder_emptyItems_savesOrderWithNoItems() {
            OrderPostVm postVm = buildOrderPostVm(Collections.emptyList());

            Order savedOrder = buildOrder();
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
            when(orderItemRepository.saveAll(any())).thenReturn(Collections.emptyList());
            doNothing().when(productService).subtractProductStockQuantity(any(OrderVm.class));
            doNothing().when(cartService).deleteCartItems(any(OrderVm.class));
            when(orderRepository.findById(any())).thenReturn(Optional.of(savedOrder));
            doNothing().when(promotionService).updateUsagePromotion(anyList());

            OrderVm result = orderService.createOrder(postVm);

            assertThat(result).isNotNull();
            verify(orderItemRepository).saveAll(any());
        }
    }

    @Nested
    class GetOrderWithItemsByIdTest {

        @Test
        void getOrderWithItemsById_found_returnsOrderVm() {
            Order order = buildOrder();
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderItemRepository.findAllByOrderId(1L)).thenReturn(Collections.emptyList());

            OrderVm result = orderService.getOrderWithItemsById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        void getOrderWithItemsById_notFound_throwsNotFoundException() {
            when(orderRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(99L));
        }
    }

    @Nested
    class GetAllOrderTest {

        @Test
        @SuppressWarnings("unchecked")
        void getAllOrder_noFilters_returnsOrderListVm() {
            Order order = buildOrder();
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            OrderListVm result = orderService.getAllOrder(
                    Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
                    null,
                    Collections.emptyList(),
                    Pair.of("Vietnam", "0123456789"),
                    "test@example.com",
                    Pair.of(0, 10)
            );

            assertThat(result).isNotNull();
            assertThat(result.orderList()).hasSize(1);
        }

        @Test
        @SuppressWarnings("unchecked")
        void getAllOrder_withStatusFilter_returnsFilteredOrderListVm() {
            Order order = buildOrder();
            order.setOrderStatus(OrderStatus.PENDING);
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            OrderListVm result = orderService.getAllOrder(
                    Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
                    null,
                    List.of(OrderStatus.PENDING),
                    Pair.of("Vietnam", "0123456789"),
                    "test@example.com",
                    Pair.of(0, 10)
            );

            assertThat(result).isNotNull();
            assertThat(result.orderList()).isNotEmpty();
        }

        @Test
        @SuppressWarnings("unchecked")
        void getAllOrder_emptyPage_returnsEmptyOrderListVm() {
            Page<Order> emptyPage = Page.empty();
            when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

            OrderListVm result = orderService.getAllOrder(
                    Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
                    null,
                    Collections.emptyList(),
                    Pair.of("", ""),
                    "",
                    Pair.of(0, 10)
            );

            assertThat(result).isNotNull();
            assertThat(result.orderList()).isNull();
            assertThat(result.totalElements()).isZero();
        }
    }

    @Nested
    class UpdateOrderPaymentStatusTest {

        @Test
        void updateOrderPaymentStatus_completedPayment_setsOrderStatusToPaid() {
            Order order = buildOrder();
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            PaymentOrderStatusVm vm = PaymentOrderStatusVm.builder()
                    .orderId(1L)
                    .paymentId(100L)
                    .paymentStatus(PaymentStatus.COMPLETED.name())
                    .orderStatus(OrderStatus.PENDING.getName())
                    .build();

            PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(vm);

            assertThat(result).isNotNull();
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
            verify(orderRepository).save(order);
        }

        @Test
        void updateOrderPaymentStatus_orderNotFound_throwsNotFoundException() {
            when(orderRepository.findById(99L)).thenReturn(Optional.empty());

            PaymentOrderStatusVm vm = PaymentOrderStatusVm.builder()
                    .orderId(99L)
                    .paymentId(100L)
                    .paymentStatus(PaymentStatus.PENDING.name())
                    .orderStatus(OrderStatus.PENDING.getName())
                    .build();

            assertThrows(NotFoundException.class, () -> orderService.updateOrderPaymentStatus(vm));
        }
    }

    @Nested
    class RejectOrderTest {

        @Test
        void rejectOrder_found_setsStatusToReject() {
            Order order = buildOrder();
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            orderService.rejectOrder(1L, "Out of stock");

            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
            assertThat(order.getRejectReason()).isEqualTo("Out of stock");
            verify(orderRepository).save(order);
        }

        @Test
        void rejectOrder_notFound_throwsNotFoundException() {
            when(orderRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> orderService.rejectOrder(99L, "reason"));
        }
    }

    @Nested
    class AcceptOrderTest {

        @Test
        void acceptOrder_found_setsStatusToAccepted() {
            Order order = buildOrder();
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            orderService.acceptOrder(1L);

            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
            verify(orderRepository).save(order);
        }

        @Test
        void acceptOrder_notFound_throwsNotFoundException() {
            when(orderRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> orderService.acceptOrder(99L));
        }
    }

    @Nested
    class GetMyOrdersTest {

        @Test
        @SuppressWarnings("unchecked")
        void getMyOrders_returnsUserOrders() {
            setSubjectUpSecurityContext("user-123");
            Order order = buildOrder();
            when(orderRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Sort.class)))
                    .thenReturn(List.of(order));

            List<OrderGetVm> result = orderService.getMyOrders(null, null);

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    class IsOrderCompletedWithUserIdAndProductIdTest {

        @Test
        @SuppressWarnings("unchecked")
        void isOrderCompletedWithUserIdAndProductId_existsCompletedOrder_returnsTrue() {
            setSubjectUpSecurityContext("user-123");
            Order order = buildOrder();
            when(productService.getProductVariations(10L)).thenReturn(Collections.emptyList());
            when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.of(order));

            OrderExistsByProductAndUserGetVm result = orderService.isOrderCompletedWithUserIdAndProductId(10L);

            assertThat(result.isPresent()).isTrue();
        }

        @Test
        @SuppressWarnings("unchecked")
        void isOrderCompletedWithUserIdAndProductId_noCompletedOrder_returnsFalse() {
            setSubjectUpSecurityContext("user-123");
            when(productService.getProductVariations(10L)).thenReturn(
                    List.of(new ProductVariationVm(20L, "Variant A", "SKU-001"))
            );
            when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

            OrderExistsByProductAndUserGetVm result = orderService.isOrderCompletedWithUserIdAndProductId(10L);

            assertThat(result.isPresent()).isFalse();
        }
    }

    @Nested
    class ExportCsvTest {

        @Test
        @SuppressWarnings("unchecked")
        void exportCsv_withOrders_returnsCsvBytes() throws IOException {
            Order order = buildOrder();
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            when(orderMapper.toCsv(any(OrderBriefVm.class))).thenReturn(Instancio.create(OrderItemCsv.class));

            OrderRequest request = OrderRequest.builder()
                    .createdFrom(ZonedDateTime.now().minusDays(1))
                    .createdTo(ZonedDateTime.now())
                    .productName(null)
                    .orderStatus(Collections.emptyList())
                    .billingCountry("Vietnam")
                    .billingPhoneNumber("0123456789")
                    .email("test@example.com")
                    .pageNo(0)
                    .pageSize(10)
                    .build();

            byte[] result = orderService.exportCsv(request);

            assertThat(result).isNotNull();
        }

        @Test
        @SuppressWarnings("unchecked")
        void exportCsv_noOrders_returnsEmptyCsvBytes() throws IOException {
            Page<Order> emptyPage = Page.empty();
            when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

            OrderRequest request = OrderRequest.builder()
                    .createdFrom(ZonedDateTime.now().minusDays(1))
                    .createdTo(ZonedDateTime.now())
                    .productName(null)
                    .orderStatus(Collections.emptyList())
                    .billingCountry("")
                    .billingPhoneNumber("")
                    .email("")
                    .pageNo(0)
                    .pageSize(10)
                    .build();

            byte[] result = orderService.exportCsv(request);

            assertThat(result).isNotNull();
        }
    }
}

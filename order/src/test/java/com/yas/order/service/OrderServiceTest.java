package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.csv.BaseCsv;
import com.yas.commonlibrary.exception.AccessDeniedException;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

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
    private OrderItem orderItem;
    private OrderPostVm orderPostVm;

    @BeforeEach
    void setUp() {
        OrderAddress orderAddress = OrderAddress.builder()
                .id(1L)
                .phone("123")
                .contactName("contact")
                .addressLine1("line1")
                .city("city")
                .zipCode("zip")
                .districtId(1L)
                .stateOrProvinceId(2L)
                .countryId(3L)
                .build();

        order = Order.builder()
                .id(1L)
                .email("test@example.com")
                .orderStatus(OrderStatus.PENDING)
                .checkoutId("checkout-123")
                .billingAddressId(orderAddress)
                .shippingAddressId(orderAddress)
                .couponCode("DISCOUNT10")
                .build();

        orderItem = OrderItem.builder()
                .id(1L)
                .productId(1L)
                .quantity(2)
                .productPrice(BigDecimal.valueOf(10.0))
                .orderId(order.getId())
                .build();

        OrderAddressPostVm addressPostVm = new OrderAddressPostVm(
                "123", "contact", "line1", "line2", "city", "zip", 1L, "district", 2L, "state", 3L, "country"
        );

        OrderItemPostVm orderItemPostVm = OrderItemPostVm.builder()
                .productId(1L)
                .productName("Product 1")
                .quantity(2)
                .productPrice(BigDecimal.valueOf(10.0))
                .note("Note")
                .discountAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .taxPercent(BigDecimal.ZERO)
                .build();

        orderPostVm = OrderPostVm.builder()
                .checkoutId("checkout-123")
                .email("test@example.com")
                .shippingAddressPostVm(addressPostVm)
                .billingAddressPostVm(addressPostVm)
                .note("Note")
                .tax(0.0f)
                .discount(0.0f)
                .numberItem(1)
                .totalPrice(BigDecimal.valueOf(20.0))
                .deliveryFee(BigDecimal.ZERO)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of(orderItemPostVm))
                .couponCode("DISCOUNT10")
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupSecurityContext(String userId) {
        SecurityContext context = mock(SecurityContext.class);
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId);
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, List.of(), "test-name");
        when(context.getAuthentication()).thenReturn(token);
        SecurityContextHolder.setContext(context);
    }

    private void setupAnonymousSecurityContext() {
        SecurityContext context = mock(SecurityContext.class);
        AnonymousAuthenticationToken token = new AnonymousAuthenticationToken("key", "anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        when(context.getAuthentication()).thenReturn(token);
        SecurityContextHolder.setContext(context);
    }

    // --- createOrder ---

    @Test
    void createOrder_ShouldCreateAndReturnOrderVm() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            return savedOrder;
        });

        when(orderItemRepository.saveAll(any())).thenReturn(List.of(orderItem));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderVm result = orderService.createOrder(orderPostVm);

        verify(orderRepository, times(2)).save(any(Order.class));
        verify(orderItemRepository, times(1)).saveAll(any());
        verify(productService, times(1)).subtractProductStockQuantity(any(OrderVm.class));
        verify(cartService, times(1)).deleteCartItems(any(OrderVm.class));
        verify(promotionService, times(1)).updateUsagePromotion(any());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    // --- getOrderWithItemsById ---

    @Test
    void getOrderWithItemsById_WhenOrderExists_ShouldReturnOrderVm() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(orderItem));

        OrderVm result = orderService.getOrderWithItemsById(1L);

        verify(orderRepository, times(1)).findById(1L);
        verify(orderItemRepository, times(1)).findAllByOrderId(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.orderItemVms()).hasSize(1);
    }

    @Test
    void getOrderWithItemsById_WhenOrderNotFound_ShouldThrowNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(1L));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderItemRepository, times(0)).findAllByOrderId(any());
    }

    // --- findOrderByCheckoutId ---

    @Test
    void findOrderByCheckoutId_WhenOrderExists_ShouldReturnOrder() {
        when(orderRepository.findByCheckoutId("checkout-123")).thenReturn(Optional.of(order));

        Order result = orderService.findOrderByCheckoutId("checkout-123");

        verify(orderRepository, times(1)).findByCheckoutId("checkout-123");
        assertThat(result).isNotNull();
        assertThat(result.getCheckoutId()).isEqualTo("checkout-123");
    }

    @Test
    void findOrderByCheckoutId_WhenOrderNotFound_ShouldThrowNotFoundException() {
        when(orderRepository.findByCheckoutId("checkout-123")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.findOrderByCheckoutId("checkout-123"));

        verify(orderRepository, times(1)).findByCheckoutId("checkout-123");
    }

    // --- findOrderVmByCheckoutId ---

    @Test
    void findOrderVmByCheckoutId_WhenOrderExists_ShouldReturnOrderGetVm() {
        when(orderRepository.findByCheckoutId("checkout-123")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(orderItem));

        OrderGetVm result = orderService.findOrderVmByCheckoutId("checkout-123");

        verify(orderRepository, times(1)).findByCheckoutId("checkout-123");
        verify(orderItemRepository, times(1)).findAllByOrderId(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.orderItems()).hasSize(1);
    }

    // --- updateOrderPaymentStatus ---

    @Test
    void updateOrderPaymentStatus_WhenOrderExistsAndCompleted_ShouldUpdateStatusToPaid() {
        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentId(123L)
                .paymentStatus(PaymentStatus.COMPLETED.name())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order);

        assertThat(result.paymentId()).isEqualTo(123L);
        assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED.name());
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void updateOrderPaymentStatus_WhenPaymentStatusIsNotCompleted_ShouldNotChangeToPaid() {
        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentId(123L)
                .paymentStatus(PaymentStatus.PENDING.name())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order);

        assertThat(result.paymentId()).isEqualTo(123L);
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void updateOrderPaymentStatus_WhenOrderNotFound_ShouldThrowNotFoundException() {
        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentId(123L)
                .paymentStatus(PaymentStatus.COMPLETED.name())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.updateOrderPaymentStatus(request));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(0)).save(any());
    }

    // --- rejectOrder ---

    @Test
    void rejectOrder_WhenOrderExists_ShouldUpdateStatusToReject() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.rejectOrder(1L, "Out of stock");

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order);
        
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
        assertThat(order.getRejectReason()).isEqualTo("Out of stock");
    }

    @Test
    void rejectOrder_WhenOrderNotFound_ShouldThrowNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.rejectOrder(1L, "Out of stock"));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(0)).save(any());
    }

    // --- acceptOrder ---

    @Test
    void acceptOrder_WhenOrderExists_ShouldUpdateStatusToAccepted() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.acceptOrder(1L);

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void acceptOrder_WhenOrderNotFound_ShouldThrowNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.acceptOrder(1L));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(0)).save(any());
    }

    // --- getAllOrder ---

    @Test
    void getAllOrder_WhenOrdersExist_ShouldReturnOrderListVm() {
        Page<Order> orderPage = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(orderPage);

        var result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now(), ZonedDateTime.now()),
                "product",
                List.of(OrderStatus.PENDING),
                Pair.of("VN", "123456"),
                "test@example.com",
                Pair.of(0, 10)
        );

        verify(orderRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));

        assertThat(result).isNotNull();
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void getAllOrder_WhenPageIsEmpty_ShouldReturnEmptyOrderListVm() {
        Page<Order> emptyPage = new PageImpl<>(List.of());
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        var result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now(), ZonedDateTime.now()),
                "product",
                List.of(OrderStatus.PENDING),
                Pair.of("VN", "123456"),
                "test@example.com",
                Pair.of(0, 10)
        );

        verify(orderRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));

        assertThat(result).isNotNull();
        assertThat(result.totalElements()).isEqualTo(0);
        assertThat(result.orderList()).isNull();
    }

    @Test
    void getAllOrder_WhenOrderStatusIsEmpty_ShouldUseAllStatuses() {
        Page<Order> orderPage = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(orderPage);

        var result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now(), ZonedDateTime.now()),
                "product",
                List.of(),
                Pair.of("VN", "123456"),
                "test@example.com",
                Pair.of(0, 10)
        );

        verify(orderRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));

        assertThat(result).isNotNull();
        assertThat(result.totalElements()).isEqualTo(1);
    }

    // --- getLatestOrders ---

    @Test
    void getLatestOrders_WhenCountIsPositive_ShouldReturnOrderBriefVms() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of(order));

        var result = orderService.getLatestOrders(5);

        verify(orderRepository, times(1)).getLatestOrders(any(Pageable.class));
        assertThat(result).hasSize(1);
    }

    @Test
    void getLatestOrders_WhenCountIsZero_ShouldReturnEmptyList() {
        var result = orderService.getLatestOrders(0);

        verify(orderRepository, times(0)).getLatestOrders(any());
        assertThat(result).isEmpty();
    }

    @Test
    void getLatestOrders_WhenCountIsNegative_ShouldReturnEmptyList() {
        var result = orderService.getLatestOrders(-1);

        verify(orderRepository, times(0)).getLatestOrders(any());
        assertThat(result).isEmpty();
    }

    @Test
    void getLatestOrders_WhenRepositoryReturnsEmpty_ShouldReturnEmptyList() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of());

        var result = orderService.getLatestOrders(5);

        verify(orderRepository, times(1)).getLatestOrders(any(Pageable.class));
        assertThat(result).isEmpty();
    }

    // --- getMyOrders ---

    @Test
    void getMyOrders_ShouldReturnOrderGetVms() {
        setupSecurityContext("user123");
        when(orderRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(order));

        var result = orderService.getMyOrders("product", OrderStatus.COMPLETED);

        verify(orderRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
        assertThat(result).hasSize(1);
    }

    @Test
    void getMyOrders_WhenAnonymous_ShouldThrowAccessDeniedException() {
        setupAnonymousSecurityContext();

        assertThrows(AccessDeniedException.class, () -> orderService.getMyOrders("product", OrderStatus.COMPLETED));

        verify(orderRepository, times(0)).findAll(any(Specification.class), any(Sort.class));
    }

    // --- isOrderCompletedWithUserIdAndProductId ---

    @Test
    void isOrderCompletedWithUserIdAndProductId_WhenOrderExists_ShouldReturnTrue() {
        setupSecurityContext("user123");
        when(productService.getProductVariations(1L)).thenReturn(List.of());
        when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.of(order));

        var result = orderService.isOrderCompletedWithUserIdAndProductId(1L);

        verify(productService, times(1)).getProductVariations(1L);
        verify(orderRepository, times(1)).findOne(any(Specification.class));

        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void isOrderCompletedWithUserIdAndProductId_WhenHasVariations_ShouldReturnTrueIfOrderExists() {
        setupSecurityContext("user123");
        ProductVariationVm variation = new ProductVariationVm(10L, "Variant", "SKU-1");
        when(productService.getProductVariations(1L)).thenReturn(List.of(variation));
        when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.of(order));

        var result = orderService.isOrderCompletedWithUserIdAndProductId(1L);

        verify(productService, times(1)).getProductVariations(1L);
        verify(orderRepository, times(1)).findOne(any(Specification.class));

        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void isOrderCompletedWithUserIdAndProductId_WhenAnonymous_ShouldThrowAccessDeniedException() {
        setupAnonymousSecurityContext();

        assertThrows(AccessDeniedException.class, () -> orderService.isOrderCompletedWithUserIdAndProductId(1L));

        verify(productService, times(0)).getProductVariations(any());
        verify(orderRepository, times(0)).findOne(any(Specification.class));
    }

    // --- exportCsv ---

    @Test
    void exportCsv_WhenOrdersExist_ShouldReturnCsvBytes() throws IOException {
        OrderRequest request = new OrderRequest();
        request.setPageNo(0);
        request.setPageSize(10);
        request.setCreatedFrom(ZonedDateTime.now());
        request.setCreatedTo(ZonedDateTime.now());
        request.setProductName("product");
        request.setOrderStatus(List.of(OrderStatus.COMPLETED));
        request.setBillingCountry("VN");
        request.setBillingPhoneNumber("123456");
        request.setEmail("test@example.com");

        Page<Order> orderPage = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(orderPage);

        OrderItemCsv csvMock = org.mockito.Mockito.mock(OrderItemCsv.class);
        when(orderMapper.toCsv(any())).thenReturn(csvMock);

        byte[] result = orderService.exportCsv(request);
        
        verify(orderRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(orderMapper, times(1)).toCsv(any());

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void exportCsv_WhenOrderListIsNull_ShouldReturnEmptyCsv() throws IOException {
        OrderRequest request = new OrderRequest();
        request.setPageNo(0);
        request.setPageSize(10);
        request.setCreatedFrom(ZonedDateTime.now());
        request.setCreatedTo(ZonedDateTime.now());
        request.setProductName("product");
        request.setOrderStatus(List.of(OrderStatus.COMPLETED));
        request.setBillingCountry("VN");
        request.setBillingPhoneNumber("123456");
        request.setEmail("test@example.com");

        Page<Order> emptyPage = new PageImpl<>(List.of());
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        byte[] result = orderService.exportCsv(request);
        
        verify(orderRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(orderMapper, times(0)).toCsv(any());

        assertThat(result).isNotNull();
        // CsvExporter usually returns some bytes for headers even if list is empty
    }
}
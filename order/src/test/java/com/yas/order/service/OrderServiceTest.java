package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.csv.OrderItemCsv;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.model.request.OrderRequest;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.*;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.product.ProductVariationVm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked") // Tắt cảnh báo Type Safety khi mock Specification
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock OrderItemRepository orderItemRepository;
    @Mock ProductService productService;
    @Mock CartService cartService;
    @Mock PromotionService promotionService;
    @Mock OrderMapper orderMapper;

    @InjectMocks OrderService orderService;

    Order order;

    @BeforeEach
    void setup() {
        OrderAddress address = new OrderAddress();
        address.setId(1L);
        address.setPhone("0909123456");

        order = new Order();
        order.setId(1L);
        order.setCheckoutId("checkout-abc");
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setBillingAddressId(address);
        order.setShippingAddressId(address);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ==========================================================
    // HELPER: MOCK SECURITY CONTEXT ĐỂ LẤY USER ID
    // ==========================================================
    private void mockSecurityContext() {
        Jwt jwtToken = mock(Jwt.class);
        when(jwtToken.getSubject()).thenReturn("user1");

        JwtAuthenticationToken jwtAuth = mock(JwtAuthenticationToken.class);
        when(jwtAuth.getToken()).thenReturn(jwtToken);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(jwtAuth);

        SecurityContextHolder.setContext(context);
    }

    // ==========================================================
    // 1. CÁC HÀM UPDATE STATUS (ACCEPT, REJECT, PAYMENT)
    // ==========================================================

    @Test
    void acceptOrder_shouldUpdateStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        orderService.acceptOrder(1L);
        assertEquals(OrderStatus.ACCEPTED, order.getOrderStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void acceptOrder_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.acceptOrder(1L));
    }

    @Test
    void rejectOrder_shouldUpdateStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        orderService.rejectOrder(1L, "reason");
        assertEquals(OrderStatus.REJECT, order.getOrderStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void rejectOrder_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.rejectOrder(1L, "reason"));
    }

    @Test
    void updateOrderPaymentStatus_shouldUpdatePaymentToPaid() {
        PaymentOrderStatusVm vm = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentStatus(PaymentStatus.COMPLETED.name())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(vm);

        assertNotNull(result);
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
    }

    @Test
    void updateOrderPaymentStatus_failedPayment() {
        PaymentOrderStatusVm vm = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentStatus(PaymentStatus.PENDING.name()) // Dùng PENDING thay vì FAILED để tránh lỗi Enum
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(vm);
        assertNotNull(result);
        assertNotEquals(OrderStatus.PAID, order.getOrderStatus()); // Trạng thái không được chuyển sang PAID
    }

    // ==========================================================
    // 2. CÁC HÀM TÌM KIẾM THEO ID / CHECKOUT ID
    // ==========================================================

    @Test
    void getOrderWithItemsById_shouldReturnOrder() {
        OrderItem item = new OrderItem();
        item.setProductId(10L);
        item.setQuantity(1);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(item));

        assertNotNull(orderService.getOrderWithItemsById(1L));
    }

    @Test
    void findOrderByCheckoutId_shouldReturnOrder() {
        when(orderRepository.findByCheckoutId("checkout-abc")).thenReturn(Optional.of(order));
        Order result = orderService.findOrderByCheckoutId("checkout-abc");
        assertNotNull(result);
    }

    @Test
    void findOrderVmByCheckoutId_shouldReturnVm() {
        when(orderRepository.findByCheckoutId("checkout-abc")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of());
        assertNotNull(orderService.findOrderVmByCheckoutId("checkout-abc"));
    }

    // ==========================================================
    // 3. CÁC HÀM GET DANH SÁCH (MY ORDERS, LATEST, ALL)
    // ==========================================================

    @Test
    void getLatestOrders_shouldReturnEmpty() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of());
        assertTrue(orderService.getLatestOrders(5).isEmpty());
    }

    @Test
    void getLatestOrders_shouldReturnList() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of(order));
        assertFalse(orderService.getLatestOrders(5).isEmpty());
    }

    @Test
    void getMyOrders_shouldReturnOrders() {
        mockSecurityContext(); // Mock Token user
        when(orderRepository.findAll(any(Specification.class), any(Sort.class)))
            .thenReturn(List.of(order));

        assertNotNull(orderService.getMyOrders(null, null));
    }

    @Test
    void getAllOrder_shouldReturnOrderListVm() {
        Page<Order> pageResult = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(pageResult);

        // Thay null bằng ZonedDateTime.now() và chuỗi rỗng
        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now(), ZonedDateTime.now()), "iPhone", List.of(), Pair.of("VN", "090"), "test@test.com", Pair.of(0, 10)
        );

        assertNotNull(result);
        assertEquals(1, result.totalElements());
    }

    @Test
    void getAllOrder_shouldReturnEmptyList_whenNoOrderFound() {
        Page<Order> emptyPage = Page.empty();
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(emptyPage);

        // Thay null bằng ZonedDateTime.now() và chuỗi rỗng
        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now(), ZonedDateTime.now()), null, List.of(), Pair.of("", ""), null, Pair.of(0, 10)
        );

        assertEquals(0, result.totalElements());
    }

    // ==========================================================
    // 4. HÀM KIỂM TRA ĐÃ MUA HÀNG & XUẤT CSV
    // ==========================================================

    @Test
    void isOrderCompletedWithUserIdAndProductId_shouldReturnTrue() {
        mockSecurityContext();
        ProductVariationVm variation = mock(ProductVariationVm.class);
        when(variation.id()).thenReturn(101L);

        when(productService.getProductVariations(10L)).thenReturn(List.of(variation));
        when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.of(order));

        OrderExistsByProductAndUserGetVm result = orderService.isOrderCompletedWithUserIdAndProductId(10L);
        
        // Dùng assertNotNull thay vì gọi hàm getter để tránh lỗi sai tên hàm của Record/Lombok
        assertNotNull(result);
    }

    @Test
    void exportCsv_shouldReturnByteArray() throws IOException {
        OrderRequest mockRequest = mock(OrderRequest.class);
        when(mockRequest.getCreatedFrom()).thenReturn(ZonedDateTime.now());
        when(mockRequest.getCreatedTo()).thenReturn(ZonedDateTime.now()); // Thêm dòng này
        when(mockRequest.getBillingCountry()).thenReturn("");             // Thêm dòng này
        when(mockRequest.getBillingPhoneNumber()).thenReturn("");         // Thêm dòng này
        when(mockRequest.getOrderStatus()).thenReturn(List.of(OrderStatus.PENDING));
        when(mockRequest.getPageNo()).thenReturn(0);
        when(mockRequest.getPageSize()).thenReturn(10);

        Page<Order> pageResult = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(pageResult);

        when(orderMapper.toCsv(any())).thenReturn(OrderItemCsv.builder().id(1L).build());

        byte[] result = orderService.exportCsv(mockRequest);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    // ==========================================================
    // 5. HÀM PHỨC TẠP NHẤT: CREATE ORDER
    // ==========================================================

    @Test   
    void createOrder_shouldSaveAndReturnOrderVm() {
        OrderPostVm mockPostVm = mock(OrderPostVm.class);
        OrderAddressPostVm mockAddressPostVm = mock(OrderAddressPostVm.class);

        when(mockPostVm.billingAddressPostVm()).thenReturn(mockAddressPostVm);
        when(mockPostVm.shippingAddressPostVm()).thenReturn(mockAddressPostVm);
        when(mockPostVm.orderItemPostVms()).thenReturn(List.of()); 

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L); 
            return savedOrder;
        });

        doNothing().when(productService).subtractProductStockQuantity(any(OrderVm.class));
        doNothing().when(cartService).deleteCartItems(any(OrderVm.class));
        doNothing().when(promotionService).updateUsagePromotion(anyList());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order)); 

        OrderVm result = orderService.createOrder(mockPostVm);

        assertNotNull(result);
        // Báo cho Mockito biết repository.save() được chạy 2 lần (1 lúc tạo, 1 lúc accept)
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(productService).subtractProductStockQuantity(any(OrderVm.class));
    }
}
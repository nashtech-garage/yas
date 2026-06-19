package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    private PromotionService promotionService;
    // Bỏ qua OrderMapper tạm thời vì các hàm cơ bản chưa dùng tới

    @InjectMocks
    private OrderService orderService;

    private Order mockOrder;

    @BeforeEach
    void setUp() {
        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setCheckoutId("checkout-123");
        mockOrder.setOrderStatus(OrderStatus.PENDING);

        com.yas.order.model.OrderAddress address = new com.yas.order.model.OrderAddress();
        address.setId(1L);
        mockOrder.setBillingAddressId(address);
        mockOrder.setShippingAddressId(address);
    }

    // ==========================================
    // TESTS CHO CÁC HÀM CẬP NHẬT TRẠNG THÁI (DỄ & HIỆU QUẢ)
    // ==========================================

    @Test
    void acceptOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        orderService.acceptOrder(1L);

        assertEquals(OrderStatus.ACCEPTED, mockOrder.getOrderStatus());
        verify(orderRepository, times(1)).save(mockOrder);
    }

    @Test
    void acceptOrder_WhenOrderNotFound_ShouldThrowException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.acceptOrder(99L));
    }

    @Test
    void rejectOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        orderService.rejectOrder(1L, "Hết hàng");

        assertEquals(OrderStatus.REJECT, mockOrder.getOrderStatus());
        assertEquals("Hết hàng", mockOrder.getRejectReason());
        verify(orderRepository, times(1)).save(mockOrder);
    }

@Test
    void updateOrderPaymentStatus_Success() {
        // Arrange
        PaymentOrderStatusVm paymentVm = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentId(123L) // FIX: Đổi từ chuỗi "pay-123" sang kiểu số Long 123L
                .paymentStatus(PaymentStatus.COMPLETED.name())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // Act
        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(paymentVm);

        // Assert
        assertEquals(OrderStatus.PAID, mockOrder.getOrderStatus());
        assertEquals(123L, mockOrder.getPaymentId()); // FIX: Cập nhật lại giá trị kỳ vọng thành số 123L
        assertEquals(PaymentStatus.COMPLETED, mockOrder.getPaymentStatus());
        assertEquals(PaymentStatus.COMPLETED.name(), result.paymentStatus());
    }

    // ==========================================
    // TESTS CHO CÁC HÀM TRUY XUẤT (GET) CƠ BẢN
    // ==========================================

    @Test
    void getOrderWithItemsById_Success() {
        OrderItem item = new OrderItem();
        item.setId(10L);
        item.setOrderId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(item));

        OrderVm result = orderService.getOrderWithItemsById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void findOrderByCheckoutId_Success() {
        when(orderRepository.findByCheckoutId("checkout-123")).thenReturn(Optional.of(mockOrder));

        Order result = orderService.findOrderByCheckoutId("checkout-123");

        assertNotNull(result);
        assertEquals("checkout-123", result.getCheckoutId());
    }

    @Test
    void getLatestOrders_WhenCountIsPositive_ShouldReturnList() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of(mockOrder));

        var result = orderService.getLatestOrders(5);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getLatestOrders_WhenCountIsZero_ShouldReturnEmptyList() {
        var result = orderService.getLatestOrders(0);
        assertTrue(result.isEmpty());
    }

    // ==========================================
    // TESTS CHO HÀM TẠO ĐƠN HÀNG (BOSS LỚN)
    // ==========================================

    @Test
    void createOrder_Success() {
        // Arrange
        com.yas.order.viewmodel.orderaddress.OrderAddressPostVm addressPostVm = mock(com.yas.order.viewmodel.orderaddress.OrderAddressPostVm.class);
        lenient().when(addressPostVm.phone()).thenReturn("0123456789");
        
        com.yas.order.viewmodel.order.OrderItemPostVm itemPostVm = mock(com.yas.order.viewmodel.order.OrderItemPostVm.class);
        lenient().when(itemPostVm.productId()).thenReturn(1L);
        lenient().when(itemPostVm.quantity()).thenReturn(2);
        
        com.yas.order.viewmodel.order.OrderPostVm postVm = mock(com.yas.order.viewmodel.order.OrderPostVm.class);
        lenient().when(postVm.billingAddressPostVm()).thenReturn(addressPostVm);
        lenient().when(postVm.shippingAddressPostVm()).thenReturn(addressPostVm);
        lenient().when(postVm.orderItemPostVms()).thenReturn(List.of(itemPostVm));
        lenient().when(postVm.couponCode()).thenReturn("SALE20");
        
        lenient().when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            
            com.yas.order.model.OrderAddress address = new com.yas.order.model.OrderAddress();
            address.setId(1L);
            savedOrder.setBillingAddressId(address);
            savedOrder.setShippingAddressId(address);
            return savedOrder;
        });
        
        lenient().when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        
        // Act
        OrderVm result = orderService.createOrder(postVm);

        // Assert
        assertNotNull(result);
        // FIX: Đổi từ 1 thành 2 lần (do gọi save lúc khởi tạo order và gọi save lúc acceptOrder)
        verify(orderRepository, times(2)).save(any(Order.class)); 
        verify(orderItemRepository, times(1)).saveAll(any());
        verify(productService, times(1)).subtractProductStockQuantity(any());
        verify(cartService, times(1)).deleteCartItems(any());
        verify(promotionService, times(1)).updateUsagePromotion(anyList());
    }

    // ==========================================
    // TESTS CHO HÀM LẤY DANH SÁCH & XUẤT CSV
    // ==========================================

    @Test
    void findOrderVmByCheckoutId_Success() {
        lenient().when(orderRepository.findByCheckoutId("checkout-123")).thenReturn(Optional.of(mockOrder));
        OrderItem item = new OrderItem();
        item.setId(10L);
        lenient().when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(item));

        OrderGetVm result = orderService.findOrderVmByCheckoutId("checkout-123");

        assertNotNull(result);
    }

    @Test
    void getAllOrder_WithEmptyPage_ShouldReturnEmptyListVm() {
        org.springframework.data.domain.Page<Order> emptyPage = org.springframework.data.domain.Page.empty();
        lenient().when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        // FIX: Khởi tạo thời gian cụ thể thay vì truyền null vào Pair
        java.time.ZonedDateTime now = java.time.ZonedDateTime.now();
        
        OrderListVm result = orderService.getAllOrder(
            org.springframework.data.util.Pair.of(now, now), // Thay vì null, null
            "prod",
            List.of(),
            org.springframework.data.util.Pair.of("VN", "090"),
            "email@a.com",
            org.springframework.data.util.Pair.of(0, 10)
        );

        assertNotNull(result);
        assertEquals(0, result.totalElements()); 
    }

    @Test
    void getAllOrder_WithData_ShouldReturnListVm() {
        org.springframework.data.domain.Page<Order> page = new org.springframework.data.domain.PageImpl<>(List.of(mockOrder));
        lenient().when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class)))
                .thenReturn(page);

        OrderListVm result = orderService.getAllOrder(
            org.springframework.data.util.Pair.of(java.time.ZonedDateTime.now(), java.time.ZonedDateTime.now()),
            "prod",
            List.of(OrderStatus.PENDING),
            org.springframework.data.util.Pair.of("VN", "090"),
            "email@a.com",
            org.springframework.data.util.Pair.of(0, 10)
        );

        assertNotNull(result);
        assertEquals(1, result.orderList().size());
    }

   @Test
    void exportCsv_WithEmptyData_ShouldReturnEmptyBytes() throws java.io.IOException {
        com.yas.order.model.request.OrderRequest request = new com.yas.order.model.request.OrderRequest();
        request.setPageNo(0);
        request.setPageSize(10);
        request.setOrderStatus(List.of());
        
        // FIX: Gán sẵn giá trị để tránh Pair bị null bên trong ruột Service
        java.time.ZonedDateTime now = java.time.ZonedDateTime.now();
        request.setCreatedFrom(now);
        request.setCreatedTo(now);
        request.setBillingCountry("VN");
        request.setBillingPhoneNumber("090");
        
        org.springframework.data.domain.Page<Order> emptyPage = org.springframework.data.domain.Page.empty();
        lenient().when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        byte[] result = orderService.exportCsv(request);
        
        assertNotNull(result);
    }
}
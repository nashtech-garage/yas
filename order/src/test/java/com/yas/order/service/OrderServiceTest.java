package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.OrderAddress;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {OrderService.class})
class OrderServiceTest {

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private OrderItemRepository orderItemRepository;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private OrderMapper orderMapper;

    @MockitoBean
    private PromotionService promotionService;

    @Autowired
    private OrderService orderService;

    private Order order;
    private OrderItem orderItem;
    private OrderPostVm orderPostVm;
    private OrderAddress orderAddress;

    @BeforeEach
    void setUp() {
        orderAddress = OrderAddress.builder()
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
                .build();

        orderItem = OrderItem.builder()
                .id(1L)
                .productId(1L)
                .quantity(2)
                .productPrice(10.0)
                .orderId(order.getId())
                .build();
                
        OrderAddressPostVm billingAddressPostVm = new OrderAddressPostVm(
            "123", "contact", "line1", "line2", "city", "zip", 1L, "district", 2L, "state", 3L, "country"
        );
        OrderAddressPostVm shippingAddressPostVm = new OrderAddressPostVm(
            "123", "contact", "line1", "line2", "city", "zip", 1L, "district", 2L, "state", 3L, "country"
        );
        
        OrderItemPostVm orderItemPostVm = new OrderItemPostVm(
            1L, "Product 1", 2, 10.0, "Note"
        );
        
        orderPostVm = new OrderPostVm(
            "test@example.com", "Note", 0.0, 0.0, 1, 20.0, "COUPON", 0.0, DeliveryMethod.GRAB_EXPRESS, 
            PaymentStatus.PENDING, "checkout-123", shippingAddressPostVm, billingAddressPostVm, List.of(orderItemPostVm)
        );
    }

    @Test
    void getOrderWithItemsById_WhenOrderExists_ShouldReturnOrderVm() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(orderItem));

        OrderVm result = orderService.getOrderWithItemsById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.orderItemVms()).hasSize(1);
    }

    @Test
    void getOrderWithItemsById_WhenOrderNotFound_ShouldThrowNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(1L));
    }

    @Test
    void findOrderByCheckoutId_WhenOrderExists_ShouldReturnOrder() {
        when(orderRepository.findByCheckoutId("checkout-123")).thenReturn(Optional.of(order));

        Order result = orderService.findOrderByCheckoutId("checkout-123");

        assertThat(result).isNotNull();
        assertThat(result.getCheckoutId()).isEqualTo("checkout-123");
    }

    @Test
    void findOrderByCheckoutId_WhenOrderNotFound_ShouldThrowNotFoundException() {
        when(orderRepository.findByCheckoutId("checkout-123")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.findOrderByCheckoutId("checkout-123"));
    }

    @Test
    void findOrderVmByCheckoutId_WhenOrderExists_ShouldReturnOrderGetVm() {
        when(orderRepository.findByCheckoutId("checkout-123")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(orderItem));

        var result = orderService.findOrderVmByCheckoutId("checkout-123");

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.orderItemVms()).hasSize(1);
    }

    @Test
    void updateOrderPaymentStatus_WhenOrderExists_ShouldUpdateStatus() {
        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentId("payment-123")
                .paymentStatus(PaymentStatus.COMPLETED.name())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

        assertThat(result.paymentId()).isEqualTo("payment-123");
        assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED.name());
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void rejectOrder_WhenOrderExists_ShouldUpdateStatusToReject() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.rejectOrder(1L, "Out of stock");

        verify(orderRepository).save(order);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
        assertThat(order.getRejectReason()).isEqualTo("Out of stock");
    }

    @Test
    void acceptOrder_WhenOrderExists_ShouldUpdateStatusToAccepted() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.acceptOrder(1L);

        verify(orderRepository).save(order);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }
    
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
        
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(any());
        verify(productService).subtractProductStockQuantity(any(OrderVm.class));
        verify(cartService).deleteCartItems(any(OrderVm.class));
        verify(promotionService).updateUsagePromotion(any());
        
        assertThat(result).isNotNull();
    }
}

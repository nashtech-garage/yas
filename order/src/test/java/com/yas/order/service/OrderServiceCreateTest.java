package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceCreateTest {

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

    @BeforeEach
    void setUp() {
        OrderAddressPostVm address = OrderAddressPostVm.builder()
                .phone("123")
                .contactName("Test")
                .addressLine1("Line 1")
                .city("City")
                .zipCode("12345")
                .build();

        OrderItemPostVm item = OrderItemPostVm.builder()
                .productId(1L)
                .productName("Product 1")
                .quantity(2)
                .productPrice(BigDecimal.TEN)
                .build();

        orderPostVm = OrderPostVm.builder()
                .email("test@example.com")
                .billingAddressPostVm(address)
                .shippingAddressPostVm(address)
                .orderItemPostVms(List.of(item))
                .paymentStatus(PaymentStatus.PENDING)
                .deliveryMethod(DeliveryMethod.VIETTEL_POST)
                .build();
    }

    @Test
    void createOrder_whenValidInput_thenReturnsOrderVmAndUpdatesDependencies() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        
        when(orderItemRepository.saveAll(any())).thenAnswer(invocation -> {
            Iterable<OrderItem> arg = invocation.getArgument(0);
            List<OrderItem> list = new java.util.ArrayList<>();
            arg.forEach(list::add);
            return list;
        });

        when(orderRepository.findById(1L)).thenAnswer(invocation -> {
            Order o = new Order();
            o.setId(1L);
            return java.util.Optional.of(o);
        });

        OrderVm result = orderService.createOrder(orderPostVm);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("test@example.com");

        verify(productService).subtractProductStockQuantity(any(OrderVm.class));
        verify(cartService).deleteCartItems(any(OrderVm.class));
        verify(promotionService).updateUsagePromotion(anyList());
        verify(orderRepository, times(2)).save(any(Order.class));
    }
}

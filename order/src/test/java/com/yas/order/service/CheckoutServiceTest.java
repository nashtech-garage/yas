package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.ForbiddenException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.CheckoutMapper;
import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.model.Order;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.viewmodel.checkout.CheckoutItemPostVm;
import com.yas.order.viewmodel.checkout.CheckoutItemVm;
import com.yas.order.viewmodel.checkout.CheckoutPaymentMethodPutVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutStatusPutVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import com.yas.order.viewmodel.product.ProductCheckoutListVm;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @Mock
    private CheckoutMapper checkoutMapper;

    @InjectMocks
    private CheckoutService checkoutService;

    private Checkout checkout;
    private CheckoutItem checkoutItem;
    private CheckoutPostVm checkoutPostVm;
    private CheckoutItemPostVm checkoutItemPostVm;
    private ProductCheckoutListVm productCheckoutListVm;

    @BeforeEach
    void setUp() {
        checkout = Checkout.builder()
                .id("checkout-123")
                .checkoutState(CheckoutState.PENDING)
                .email("test@test.com")
                .note("Test note")
                .promotionCode("PROMO")
                .build();
        checkout.setCreatedBy("user123");

        checkoutItem = CheckoutItem.builder()
                .id(1L)
                .productId(100L)
                .quantity(2)
                .checkout(checkout)
                .productPrice(BigDecimal.valueOf(50))
                .build();

        checkoutItemPostVm = new CheckoutItemPostVm(100L, "Test description", 2);
        checkoutPostVm = new CheckoutPostVm(
                "test@test.com", "Test note", "PROMO", "10", "11", "12", List.of(checkoutItemPostVm)
        );

        productCheckoutListVm = ProductCheckoutListVm.builder()
                .id(100L)
                .name("Product Name")
                .price(50.0)
                .taxClassId(10L)
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

    // --- createCheckout ---

    @Test
    void createCheckout_ShouldCreateAndReturnCheckoutVm() {
        setupSecurityContext("user123");

        Checkout mockCheckout = new Checkout();
        when(checkoutMapper.toModel(checkoutPostVm)).thenReturn(mockCheckout);
        when(checkoutMapper.toModel(checkoutItemPostVm)).thenReturn(checkoutItem);

        when(productService.getProductInfomation(any(Set.class), anyInt(), anyInt()))
                .thenReturn(Map.of(100L, productCheckoutListVm));

        when(checkoutRepository.save(any(Checkout.class))).thenAnswer(i -> {
            Checkout c = i.getArgument(0);
            c.setId("checkout-123");
            return c;
        });

        CheckoutVm checkoutVmMock = CheckoutVm.builder().id("checkout-123").email("test@test.com").note("note").promotionCode("PROMO").build();
        when(checkoutMapper.toVm(any(Checkout.class))).thenReturn(checkoutVmMock);

        CheckoutItemVm itemVmMock = new CheckoutItemVm(1L, 100L, "Name", "Desc", 2, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "checkout-123");
        when(checkoutMapper.toVm(any(CheckoutItem.class))).thenReturn(itemVmMock);

        CheckoutVm result = checkoutService.createCheckout(checkoutPostVm);

        verify(checkoutMapper, times(1)).toModel(checkoutPostVm);
        verify(checkoutMapper, times(1)).toModel(checkoutItemPostVm);
        verify(productService, times(1)).getProductInfomation(any(Set.class), anyInt(), anyInt());
        verify(checkoutRepository, times(1)).save(any(Checkout.class));
        verify(checkoutMapper, times(1)).toVm(any(Checkout.class));
        verify(checkoutMapper, times(1)).toVm(any(CheckoutItem.class));

        assertThat(result).isNotNull();
        assertThat(result.checkoutItemVms()).hasSize(1);
    }

    @Test
    void createCheckout_WhenProductNotFound_ShouldThrowNotFoundException() {
        setupSecurityContext("user123");

        Checkout mockCheckout = new Checkout();
        when(checkoutMapper.toModel(checkoutPostVm)).thenReturn(mockCheckout);
        when(checkoutMapper.toModel(checkoutItemPostVm)).thenReturn(checkoutItem);

        when(productService.getProductInfomation(any(Set.class), anyInt(), anyInt()))
                .thenReturn(Map.of());

        assertThrows(NotFoundException.class, () -> checkoutService.createCheckout(checkoutPostVm));

        verify(checkoutMapper, times(1)).toModel(checkoutPostVm);
        verify(checkoutMapper, times(1)).toModel(checkoutItemPostVm);
        verify(productService, times(1)).getProductInfomation(any(Set.class), anyInt(), anyInt());
        verify(checkoutRepository, times(0)).save(any(Checkout.class));
    }

    // --- getCheckoutPendingStateWithItemsById ---

    @Test
    void getCheckoutPendingStateWithItemsById_WhenValidAndOwnedByUser_ShouldReturnVmWithItems() {
        setupSecurityContext("user123");
        checkout.setCheckoutItems(List.of(checkoutItem));

        when(checkoutRepository.findByIdAndCheckoutState("checkout-123", CheckoutState.PENDING))
                .thenReturn(Optional.of(checkout));

        CheckoutVm checkoutVmMock = CheckoutVm.builder().id("checkout-123").email("test@test.com").note("note").promotionCode("PROMO").build();
        when(checkoutMapper.toVm(checkout)).thenReturn(checkoutVmMock);

        CheckoutItemVm itemVmMock = new CheckoutItemVm(1L, 100L, "Name", "Desc", 2, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "checkout-123");
        when(checkoutMapper.toVm(checkoutItem)).thenReturn(itemVmMock);

        CheckoutVm result = checkoutService.getCheckoutPendingStateWithItemsById("checkout-123");

        verify(checkoutRepository, times(1)).findByIdAndCheckoutState("checkout-123", CheckoutState.PENDING);
        verify(checkoutMapper, times(1)).toVm(checkout);
        verify(checkoutMapper, times(1)).toVm(checkoutItem);

        assertThat(result).isNotNull();
        assertThat(result.checkoutItemVms()).hasSize(1);
    }

    @Test
    void getCheckoutPendingStateWithItemsById_WhenValidButNoItems_ShouldReturnVmWithoutItems() {
        setupSecurityContext("user123");
        checkout.setCheckoutItems(List.of());

        when(checkoutRepository.findByIdAndCheckoutState("checkout-123", CheckoutState.PENDING))
                .thenReturn(Optional.of(checkout));

        CheckoutVm checkoutVmMock = CheckoutVm.builder().id("checkout-123").email("test@test.com").note("note").promotionCode("PROMO").build();
        when(checkoutMapper.toVm(checkout)).thenReturn(checkoutVmMock);

        CheckoutVm result = checkoutService.getCheckoutPendingStateWithItemsById("checkout-123");

        verify(checkoutRepository, times(1)).findByIdAndCheckoutState("checkout-123", CheckoutState.PENDING);
        verify(checkoutMapper, times(1)).toVm(checkout);
        verify(checkoutMapper, times(0)).toVm(any(CheckoutItem.class));

        assertThat(result).isNotNull();
        assertThat(result.checkoutItemVms()).isNull();
    }

    @Test
    void getCheckoutPendingStateWithItemsById_WhenNotOwnedByUser_ShouldThrowForbiddenException() {
        setupSecurityContext("user999");

        when(checkoutRepository.findByIdAndCheckoutState("checkout-123", CheckoutState.PENDING))
                .thenReturn(Optional.of(checkout));

        assertThrows(ForbiddenException.class, () -> checkoutService.getCheckoutPendingStateWithItemsById("checkout-123"));

        verify(checkoutRepository, times(1)).findByIdAndCheckoutState("checkout-123", CheckoutState.PENDING);
        verify(checkoutMapper, times(0)).toVm(any(Checkout.class));
    }

    @Test
    void getCheckoutPendingStateWithItemsById_WhenNotFound_ShouldThrowNotFoundException() {
        when(checkoutRepository.findByIdAndCheckoutState("checkout-123", CheckoutState.PENDING))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> checkoutService.getCheckoutPendingStateWithItemsById("checkout-123"));

        verify(checkoutRepository, times(1)).findByIdAndCheckoutState("checkout-123", CheckoutState.PENDING);
    }

    // --- updateCheckoutStatus ---

    @Test
    void updateCheckoutStatus_WhenOwnedByUser_ShouldUpdateAndReturnOrderId() {
        setupSecurityContext("user123");
        com.yas.order.viewmodel.checkout.CheckoutStatusPutVm putVm =
            new com.yas.order.viewmodel.checkout.CheckoutStatusPutVm("checkout-123", CheckoutState.COMPLETED.name());

        when(checkoutRepository.findById("checkout-123")).thenReturn(Optional.of(checkout));
        when(checkoutRepository.save(checkout)).thenReturn(checkout);

        Order order = new Order();
        order.setId(10L);
        when(orderService.findOrderByCheckoutId("checkout-123")).thenReturn(order);

        Long result = checkoutService.updateCheckoutStatus(putVm);

        verify(checkoutRepository, times(1)).findById("checkout-123");
        verify(checkoutRepository, times(1)).save(checkout);
        verify(orderService, times(1)).findOrderByCheckoutId("checkout-123");

        assertThat(result).isEqualTo(10L);
        assertThat(checkout.getCheckoutState()).isEqualTo(CheckoutState.COMPLETED);
    }

    @Test
    void updateCheckoutStatus_WhenNotOwnedByUser_ShouldThrowForbiddenException() {
        setupSecurityContext("user999");
        com.yas.order.viewmodel.checkout.CheckoutStatusPutVm putVm =
            new com.yas.order.viewmodel.checkout.CheckoutStatusPutVm("checkout-123", CheckoutState.COMPLETED.name());

        when(checkoutRepository.findById("checkout-123")).thenReturn(Optional.of(checkout));

        assertThrows(ForbiddenException.class, () -> checkoutService.updateCheckoutStatus(putVm));

        verify(checkoutRepository, times(1)).findById("checkout-123");
        verify(checkoutRepository, times(0)).save(any());
        verify(orderService, times(0)).findOrderByCheckoutId(any());
    }

    @Test
    void updateCheckoutStatus_WhenNotFound_ShouldThrowNotFoundException() {
        com.yas.order.viewmodel.checkout.CheckoutStatusPutVm putVm =
            new com.yas.order.viewmodel.checkout.CheckoutStatusPutVm("checkout-123", CheckoutState.COMPLETED.name());

        when(checkoutRepository.findById("checkout-123")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> checkoutService.updateCheckoutStatus(putVm));

        verify(checkoutRepository, times(1)).findById("checkout-123");
    }

    // --- updateCheckoutPaymentMethod ---

    @Test
    void updateCheckoutPaymentMethod_WhenFound_ShouldUpdate() {
        CheckoutPaymentMethodPutVm putVm = new CheckoutPaymentMethodPutVm("METHOD_1");

        when(checkoutRepository.findById("checkout-123")).thenReturn(Optional.of(checkout));
        when(checkoutRepository.save(checkout)).thenReturn(checkout);

        checkoutService.updateCheckoutPaymentMethod("checkout-123", putVm);

        verify(checkoutRepository, times(1)).findById("checkout-123");
        verify(checkoutRepository, times(1)).save(checkout);

        assertThat(checkout.getPaymentMethodId()).isEqualTo("METHOD_1");
    }

    @Test
    void updateCheckoutPaymentMethod_WhenNotFound_ShouldThrowNotFoundException() {
        CheckoutPaymentMethodPutVm putVm = new CheckoutPaymentMethodPutVm("METHOD_1");

        when(checkoutRepository.findById("checkout-123")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> checkoutService.updateCheckoutPaymentMethod("checkout-123", putVm));

        verify(checkoutRepository, times(1)).findById("checkout-123");
        verify(checkoutRepository, times(0)).save(any());
    }
}
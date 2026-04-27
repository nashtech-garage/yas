package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setSubjectUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.ForbiddenException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.CheckoutMapperImpl;
import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.model.Order;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.repository.CheckoutItemRepository;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.viewmodel.checkout.CheckoutPaymentMethodPutVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutStatusPutVm;
import com.yas.order.viewmodel.product.ProductCheckoutListVm;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.instancio.Select.field;

@SpringBootTest(classes = { CheckoutMapperImpl.class, CheckoutService.class })
class CheckoutServiceTest {

    @MockitoBean
    CheckoutRepository checkoutRepository;

    @MockitoBean
    CheckoutItemRepository checkoutItemRepository;

    @MockitoBean
    OrderService orderService;

    @MockitoBean
    ProductService productService;

    @Autowired
    CheckoutService checkoutService;

    CheckoutPostVm checkoutPostVm;
    List<CheckoutItem> checkoutItems;
    Checkout checkoutCreated;
    String checkoutId = UUID.randomUUID().toString();
    List<ProductCheckoutListVm> productCheckoutListVms;
    Map<Long, ProductCheckoutListVm> productCheckoutListVmMap;

    private void init() {
        checkoutPostVm = Instancio.of(CheckoutPostVm.class)
                .supply(field(CheckoutPostVm.class, "shippingAddressId"), gen -> Long.toString(gen.longRange(1, 10000)))
                .create();
        checkoutCreated = Checkout.builder()
                .id(checkoutId)
                .checkoutState(CheckoutState.PENDING)
                .note(checkoutPostVm.note())
                .email(checkoutPostVm.email())
                .promotionCode(checkoutPostVm.promotionCode())
                .build();
        checkoutCreated.setCreatedBy("test-create-by");
        setSubjectUpSecurityContext(checkoutCreated.getCreatedBy());
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(Jwt.class));

        checkoutItems = checkoutPostVm.checkoutItemPostVms().stream()
                .map(itemVm -> CheckoutItem.builder()
                        .id(Instancio.create(Long.class))
                        .productId(itemVm.productId())
                        .quantity(itemVm.quantity())
                        .description(itemVm.description())
                        .checkout(checkoutCreated)
                        .build())
                .toList();

        productCheckoutListVms = checkoutItems.stream()
                .map(item -> Instancio.of(ProductCheckoutListVm.class)
                        .set(field(ProductCheckoutListVm.class, "id"), item.getProductId())
                        .create())
                .toList();
        productCheckoutListVmMap = productCheckoutListVms.stream()
                .collect(Collectors.toMap(ProductCheckoutListVm::getId, Function.identity()));
    }

    @Test
    void testCreateCheckout_whenNormalCase_returnCheckout() {
        init();
        checkoutCreated.setCheckoutItems(checkoutItems);
        when(checkoutRepository.save(any())).thenReturn(checkoutCreated);
        when(checkoutItemRepository.saveAll(anyCollection())).thenReturn(checkoutItems);
        when(productService.getProductInfomation(anySet(), anyInt(), anyInt())).thenReturn(productCheckoutListVmMap);

        var res = checkoutService.createCheckout(checkoutPostVm);

        assertThat(res)
                .hasFieldOrPropertyWithValue("id", checkoutId)
                .hasFieldOrPropertyWithValue("email", checkoutPostVm.email())
                .hasFieldOrPropertyWithValue("promotionCode", checkoutPostVm.promotionCode())
                .hasFieldOrPropertyWithValue("note", checkoutPostVm.note());
        assertThat(res.checkoutItemVms())
                .hasSize(checkoutPostVm.checkoutItemPostVms().size())
                .allMatch(item -> item.checkoutId().equals(checkoutId));
    }

    @Test
    void testCreateCheckout_whenCheckoutItemsIsEmpty_throwError() {
        init();
        when(checkoutRepository.save(any())).thenReturn(checkoutCreated);
        when(checkoutItemRepository.saveAll(anyCollection())).thenReturn(List.of());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> checkoutService.createCheckout(checkoutPostVm));
        assertThat(exception).hasMessage("PRODUCT_NOT_FOUND");
    }

    @Test
    void testGetCheckoutPendingStateWithItemsById_whenNormalCase_returnCheckoutVm() {
        init();
        checkoutCreated.setCheckoutItems(checkoutItems);
        when(checkoutRepository.findByIdAndCheckoutState(anyString(), eq(CheckoutState.PENDING)))
                .thenReturn(Optional.ofNullable(checkoutCreated));
        when(checkoutItemRepository.findAllByCheckoutId(anyString())).thenReturn(checkoutItems);

        var res = checkoutService.getCheckoutPendingStateWithItemsById("1");

        assertThat(res)
                .hasFieldOrPropertyWithValue("id", checkoutId)
                .hasFieldOrPropertyWithValue("promotionCode", checkoutPostVm.promotionCode())
                .hasFieldOrPropertyWithValue("email", checkoutPostVm.email())
                .hasFieldOrPropertyWithValue("note", checkoutPostVm.note());
        assertThat(res.checkoutItemVms())
                .allMatch(item -> item.checkoutId().equals(checkoutId))
                .hasSize(checkoutPostVm.checkoutItemPostVms().size());
    }

    @Test
    void testGetCheckoutPendingStateWithItemsById_whenNotEqualsCreateBy_throwForbidden() {
        init();
        when(checkoutRepository.findByIdAndCheckoutState(anyString(), eq(CheckoutState.PENDING)))
                .thenReturn(Optional.ofNullable(checkoutCreated));
        setSubjectUpSecurityContext("test--by");

        try {
            checkoutService.getCheckoutPendingStateWithItemsById("1");
        } catch (ForbiddenException exception) {
            assertThat(exception).hasMessage("Forbidden");
            return;
        }
        throw new AssertionError("Expected ForbiddenException");
    }

    @Test
    void testGetCheckoutPendingStateWithItemsById_whenNormalCase_returnCheckoutVmWithoutCheckoutItems() {
        init();
        when(checkoutRepository.findByIdAndCheckoutState(anyString(), eq(CheckoutState.PENDING)))
                .thenReturn(Optional.ofNullable(checkoutCreated));
        when(checkoutItemRepository.findAllByCheckoutId(anyString())).thenReturn(List.of());

        var res = checkoutService.getCheckoutPendingStateWithItemsById("1");

        assertThat(res)
                .hasFieldOrPropertyWithValue("id", checkoutId)
                .hasFieldOrPropertyWithValue("promotionCode", checkoutPostVm.promotionCode())
                .hasFieldOrPropertyWithValue("note", checkoutPostVm.note())
                .hasFieldOrPropertyWithValue("email", checkoutPostVm.email());
        assertThat(res.checkoutItemVms()).isNull();
    }

    @Test
    void testUpdateCheckoutPaymentMethod_whenCheckoutExists_thenUpdatePaymentMethod() {
        init();
        String id = "123";
        Checkout checkout = new Checkout();
        checkout.setId(id);

        CheckoutPaymentMethodPutVm request = new CheckoutPaymentMethodPutVm("new-payment-method-id");
        when(checkoutRepository.findById(id)).thenReturn(Optional.of(checkout));

        checkoutService.updateCheckoutPaymentMethod(id, request);

        verify(checkoutRepository).save(checkout);
        assertThat(checkout.getPaymentMethodId()).isEqualTo(request.paymentMethodId());
    }

    @Test
    void testUpdateCheckoutPaymentMethod_whenCheckoutNotFound_thenThrowNotFoundException() {
        init();
        String id = "invalid-id";
        CheckoutPaymentMethodPutVm request = new CheckoutPaymentMethodPutVm("new-payment-method-id");

        when(checkoutRepository.findById(id)).thenReturn(Optional.empty());

        try {
            checkoutService.updateCheckoutPaymentMethod(id, request);
        } catch (NotFoundException exception) {
            assertThat(exception).hasMessage("Checkout invalid-id is not found");
            return;
        }
        throw new AssertionError("Expected NotFoundException");
    }

    @Test
    void testUpdateCheckoutPaymentMethod_whenPaymentMethodIdIsNull_thenDoNotUpdate() {
        init();
        String id = "123";
        Checkout checkout = new Checkout();
        checkout.setId(id);

        CheckoutPaymentMethodPutVm request = new CheckoutPaymentMethodPutVm(null);
        when(checkoutRepository.findById(id)).thenReturn(Optional.of(checkout));

        checkoutService.updateCheckoutPaymentMethod(id, request);

        verify(checkoutRepository).save(checkout);
        assertThat(checkout.getPaymentMethodId()).isNull();
    }

    @Test
    void testUpdateCheckoutStatus_whenCheckoutExistsAndOwned_returnOrderId() {
        init();
        Checkout checkout = new Checkout();
        checkout.setId("checkout-1");
        checkout.setCreatedBy("test-create-by");

        CheckoutStatusPutVm request = new CheckoutStatusPutVm("checkout-1", CheckoutState.CHECKED_OUT.name());
        when(checkoutRepository.findById("checkout-1")).thenReturn(Optional.of(checkout));
        when(orderService.findOrderByCheckoutId("checkout-1")).thenReturn(Order.builder().id(999L).build());
        setSubjectUpSecurityContext("test-create-by");

        Long result = checkoutService.updateCheckoutStatus(request);

        assertThat(result).isEqualTo(999L);
        assertThat(checkout.getCheckoutState()).isEqualTo(CheckoutState.CHECKED_OUT);
        verify(checkoutRepository).save(checkout);
    }

    @Test
    void testUpdateCheckoutStatus_whenCheckoutNotFound_throwNotFoundException() {
        init();
        CheckoutStatusPutVm request = new CheckoutStatusPutVm("missing", CheckoutState.CHECKED_OUT.name());
        when(checkoutRepository.findById("missing")).thenReturn(Optional.empty());

        try {
            checkoutService.updateCheckoutStatus(request);
        } catch (NotFoundException exception) {
            assertThat(exception).hasMessage("Checkout missing is not found");
            return;
        }
        throw new AssertionError("Expected NotFoundException");
    }

    @Test
    void testUpdateCheckoutStatus_whenNotOwned_throwForbiddenException() {
        init();
        Checkout checkout = new Checkout();
        checkout.setId("checkout-2");
        checkout.setCreatedBy("owner-1");

        CheckoutStatusPutVm request = new CheckoutStatusPutVm("checkout-2", CheckoutState.CHECKED_OUT.name());
        when(checkoutRepository.findById("checkout-2")).thenReturn(Optional.of(checkout));
        setSubjectUpSecurityContext("owner-2");

        assertThatThrownBy(() -> checkoutService.updateCheckoutStatus(request))
                .isInstanceOf(ForbiddenException.class);
    }
}

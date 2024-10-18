package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setSubjectUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.Forbidden;
import com.yas.order.mapper.CheckoutMapperImpl;
import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.repository.CheckoutItemRepository;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CheckoutMapperImpl.class, CheckoutService.class})
class CheckoutServiceTest {

    @MockBean
    CheckoutRepository checkoutRepository;

    @MockBean
    CheckoutItemRepository checkoutItemRepository;

    @MockBean
    OrderService orderService;

    @Autowired
    CheckoutService checkoutService;

    CheckoutPostVm checkoutPostVm;
    List<CheckoutItem> checkoutItems;
    Checkout checkoutCreated;
    String checkoutId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {

        checkoutPostVm = Instancio.create(CheckoutPostVm.class);
        checkoutCreated = Checkout.builder()
            .id(checkoutId)
            .checkoutState(CheckoutState.PENDING)
            .note(checkoutPostVm.note())
            .email(checkoutPostVm.email())
            .couponCode(checkoutPostVm.couponCode())
            .build();

        checkoutItems = checkoutPostVm.checkoutItemPostVms().stream()
            .map(itemVm -> CheckoutItem.builder()
                .id(Instancio.create(Long.class))
                .productId(itemVm.productId())
                .productName(itemVm.productName())
                .quantity(itemVm.quantity())
                .productPrice(itemVm.productPrice())
                .discountAmount(itemVm.discountAmount())
                .taxAmount(itemVm.taxAmount())
                .taxPercent(itemVm.taxPercent())
                .note(itemVm.note())
                .checkoutId(checkoutId)
                .build()
            ).toList();

    }

    @Test
    void testCreateCheckout_whenNormalCase_returnCheckout() {

        when(checkoutRepository.save(any())).thenReturn(checkoutCreated);
        when(checkoutItemRepository.saveAll(anyCollection())).thenReturn(checkoutItems);
        var res = checkoutService.createCheckout(checkoutPostVm);

        assertThat(res)
            .hasFieldOrPropertyWithValue("id", checkoutId)
            .hasFieldOrPropertyWithValue("email", checkoutPostVm.email())
            .hasFieldOrPropertyWithValue("couponCode", checkoutPostVm.couponCode())
            .hasFieldOrPropertyWithValue("note", checkoutPostVm.note());

        assertThat(res.checkoutItemVms())
            .hasSize(checkoutPostVm.checkoutItemPostVms().size())
            .allMatch(item -> item.checkoutId().equals(checkoutId));
    }

    @Test
    void testCreateCheckout_whenCheckoutItemsIsEmpty_returnCheckoutWithoutCheckoutItems() {

        when(checkoutRepository.save(any())).thenReturn(checkoutCreated);
        when(checkoutItemRepository.saveAll(anyCollection())).thenReturn(List.of());
        var res = checkoutService.createCheckout(checkoutPostVm);

        assertThat(res)
            .hasFieldOrPropertyWithValue("id", checkoutId)
            .hasFieldOrPropertyWithValue("email", checkoutPostVm.email())
            .hasFieldOrPropertyWithValue("couponCode", checkoutPostVm.couponCode())
            .hasFieldOrPropertyWithValue("note", checkoutPostVm.note());

        assertThat(res.checkoutItemVms()).isEmpty();
    }

    @Test
    void testGetCheckoutPendingStateWithItemsById_whenNormalCase_returnCheckoutVm() {

        checkoutCreated.setCreatedBy("test-create-by");
        when(checkoutRepository.findByIdAndCheckoutState(anyString(), eq(CheckoutState.PENDING)))
            .thenReturn(Optional.ofNullable(checkoutCreated));

        setSubjectUpSecurityContext(checkoutCreated.getCreatedBy());
        when(checkoutItemRepository.findAllByCheckoutId(anyString())).thenReturn(checkoutItems);
        var res = checkoutService.getCheckoutPendingStateWithItemsById("1");

        assertThat(res)
            .hasFieldOrPropertyWithValue("id", checkoutId)
            .hasFieldOrPropertyWithValue("couponCode", checkoutPostVm.couponCode())
            .hasFieldOrPropertyWithValue("email", checkoutPostVm.email())
            .hasFieldOrPropertyWithValue("note", checkoutPostVm.note());

        assertThat(res.checkoutItemVms())
            .allMatch(item -> item.checkoutId().equals(checkoutId))
            .hasSize(checkoutPostVm.checkoutItemPostVms().size());
    }

    @Test
    void testGetCheckoutPendingStateWithItemsById_whenNotEqualsCreateBy_throwForbidden() {

        checkoutCreated.setCreatedBy("test-create-by");
        when(checkoutRepository.findByIdAndCheckoutState(anyString(), eq(CheckoutState.PENDING)))
            .thenReturn(Optional.ofNullable(checkoutCreated));
        setSubjectUpSecurityContext("test--by");

        Assertions.assertThrows(Forbidden.class,
            () -> checkoutService.getCheckoutPendingStateWithItemsById("1"),
            "You don't have permission to access this page");


    }

    @Test
    void testGetCheckoutPendingStateWithItemsById_whenNormalCase_returnCheckoutVmWithoutCheckoutItems() {

        checkoutCreated.setCreatedBy("test-create-by");
        setSubjectUpSecurityContext(checkoutCreated.getCreatedBy());
        when(checkoutRepository.findByIdAndCheckoutState(anyString(), eq(CheckoutState.PENDING)))
            .thenReturn(Optional.ofNullable(checkoutCreated));
        when(checkoutItemRepository.findAllByCheckoutId(anyString())).thenReturn(List.of());

        var res = checkoutService.getCheckoutPendingStateWithItemsById("1");

        assertThat(res)
            .hasFieldOrPropertyWithValue("id", checkoutId)
            .hasFieldOrPropertyWithValue("couponCode", checkoutPostVm.couponCode())
            .hasFieldOrPropertyWithValue("note", checkoutPostVm.note())
            .hasFieldOrPropertyWithValue("email", checkoutPostVm.email());

        assertThat(res.checkoutItemVms()).isNull();
    }
}
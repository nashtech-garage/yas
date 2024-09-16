package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.order.mapper.CheckoutMapperImpl;
import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import java.util.UUID;
import java.util.stream.Collectors;
import org.instancio.Instancio;
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
    OrderService orderService;

    @Autowired
    CheckoutService checkoutService;

    CheckoutPostVm checkoutPostVm;
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
            .checkoutItem(checkoutPostVm.checkoutItemPostVms().stream()
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
                    .build()
                ).collect(Collectors.toSet()))
            .build();
        checkoutCreated.getCheckoutItem()
            .forEach(item-> item.setCheckoutId(checkoutCreated));
    }

    @Test
    void createCheckout() {

        when(checkoutRepository.save(any())).thenReturn(checkoutCreated);

         var res = checkoutService.createCheckout(checkoutPostVm);

        assertThat(res)
            .hasFieldOrPropertyWithValue("id", checkoutId)
            .hasFieldOrPropertyWithValue("email", checkoutPostVm.email())
            .hasFieldOrPropertyWithValue("couponCode", checkoutPostVm.couponCode())
            .hasFieldOrPropertyWithValue("note", checkoutPostVm.note());

        assertThat(res.checkoutItemVms())
            .hasSize(checkoutPostVm.checkoutItemPostVms().size())
            .allMatch(item->item.checkoutId().equals(checkoutId));
    }
}
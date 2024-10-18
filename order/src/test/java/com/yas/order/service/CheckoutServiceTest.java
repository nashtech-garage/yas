package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

import com.yas.order.mapper.CheckoutMapperImpl;
import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.repository.CheckoutItemRepository;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import java.util.List;
import java.util.UUID;
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
    void createCheckout() {

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
}
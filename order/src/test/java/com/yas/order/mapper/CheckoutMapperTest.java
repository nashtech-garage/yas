package com.yas.order.mapper;

import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.viewmodel.checkout.CheckoutItemPostVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CheckoutMapperImpl.class})
class CheckoutMapperTest {

    @Autowired
    CheckoutMapper checkoutMapper;

    @Test
    void testCheckoutItemPostVmToModel_convertToCorrectCheckoutItem(){

        var src = Instancio.create(CheckoutItemPostVm.class);

        var res = checkoutMapper.toModel(src);

        Assertions.assertThat(res)
            .hasFieldOrPropertyWithValue("productId", src.productId())
            .hasFieldOrPropertyWithValue("productName", src.productName())
            .hasFieldOrPropertyWithValue("quantity", src.quantity())
            .hasFieldOrPropertyWithValue("productPrice", src.productPrice())
            .hasFieldOrPropertyWithValue("note", src.note())
            .hasFieldOrPropertyWithValue("discountAmount", src.discountAmount())
            .hasFieldOrPropertyWithValue("taxAmount", src.taxAmount())
            .hasFieldOrPropertyWithValue("taxPercent", src.taxPercent());
    }

    @Test
    void testCheckoutPostVmToModel_convertToCorrectCheckout(){

        CheckoutPostVm checkoutPostVm = Instancio.of(CheckoutPostVm.class)
            .create();

        var res = checkoutMapper.toModel(checkoutPostVm);

        Assertions.assertThat(res)
            .hasFieldOrPropertyWithValue("email", checkoutPostVm.email())
            .hasFieldOrPropertyWithValue("note", checkoutPostVm.note())
            .hasFieldOrPropertyWithValue("couponCode", checkoutPostVm.couponCode());

        Assertions.assertThat(res.getCheckoutItem())
            .hasSize(checkoutPostVm.checkoutItemPostVms().size());
    }

    @Test
    void testCheckoutToVm_convertToCheckoutVmCorrectly(){

        Checkout checkout = Instancio.create(Checkout.class);

        var res = checkoutMapper.toVm(checkout);

        Assertions.assertThat(res).hasFieldOrPropertyWithValue("id", checkout.getId())
            .hasFieldOrPropertyWithValue("email", checkout.getEmail())
            .hasFieldOrPropertyWithValue("note", checkout.getNote())
            .hasFieldOrPropertyWithValue("couponCode", checkout.getCouponCode());

        Assertions.assertThat(res.checkoutItemVms()).hasSize(checkout.getCheckoutItem().size());
    }

    @Test
    void testCheckoutItemToVm_convertCheckoutItemCorrectly(){

        CheckoutItem checkoutItem = Instancio.create(CheckoutItem.class);

        var res = checkoutMapper.toVm(checkoutItem);

        Assertions.assertThat(res)
            .hasFieldOrPropertyWithValue("id", checkoutItem.getId())
            .hasFieldOrPropertyWithValue("productId", checkoutItem.getProductId())
            .hasFieldOrPropertyWithValue("productName", checkoutItem.getProductName())
            .hasFieldOrPropertyWithValue("quantity", checkoutItem.getQuantity())
            .hasFieldOrPropertyWithValue("productPrice", checkoutItem.getProductPrice())
            .hasFieldOrPropertyWithValue("note", checkoutItem.getNote())
            .hasFieldOrPropertyWithValue("discountAmount", checkoutItem.getDiscountAmount())
            .hasFieldOrPropertyWithValue("taxAmount", checkoutItem.getTaxAmount())
            .hasFieldOrPropertyWithValue("taxPercent", checkoutItem.getTaxPercent());
    }
}

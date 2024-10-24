package com.yas.order.mapper;

import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.viewmodel.checkout.CheckoutItemPostVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import static org.instancio.Select.field;
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
    void testCheckoutItemPostVmToModel_convertToCorrectCheckoutItem() {

        var src = Instancio.create(CheckoutItemPostVm.class);

        var res = checkoutMapper.toModel(src);

        Assertions.assertThat(res)
                .hasFieldOrPropertyWithValue("productId", src.productId())
                .hasFieldOrPropertyWithValue("quantity", src.quantity())
                .hasFieldOrPropertyWithValue("description", src.description());
    }

    @Test
    void testCheckoutPostVmToModel_convertToCorrectCheckout() {

        CheckoutPostVm checkoutPostVm = Instancio.of(CheckoutPostVm.class)
                .supply(field(CheckoutPostVm.class, "shippingAddressId"), gen -> Long.toString(gen.longRange(1, 10000)))
                .create();
        System.out.println(checkoutPostVm.toString());
        var res = checkoutMapper.toModel(checkoutPostVm);

        Assertions.assertThat(res)
                .hasFieldOrPropertyWithValue("email", checkoutPostVm.email())
                .hasFieldOrPropertyWithValue("note", checkoutPostVm.note())
                .hasFieldOrPropertyWithValue("promotionCode", checkoutPostVm.promotionCode())
                .hasFieldOrPropertyWithValue("shipmentMethodId", checkoutPostVm.shipmentMethodId())
                .hasFieldOrPropertyWithValue("paymentMethodId", checkoutPostVm.paymentMethodId())
                .hasFieldOrPropertyWithValue("shippingAddressId", Long.valueOf(checkoutPostVm.shippingAddressId()));

    }

    @Test
    void testCheckoutToVm_convertToCheckoutVmCorrectly() {

        Checkout checkout = Instancio.create(Checkout.class);

        var res = checkoutMapper.toVm(checkout);

        Assertions.assertThat(res).hasFieldOrPropertyWithValue("id", checkout.getId())
                .hasFieldOrPropertyWithValue("email", checkout.getEmail())
                .hasFieldOrPropertyWithValue("note", checkout.getNote())
                .hasFieldOrPropertyWithValue("promotionCode", checkout.getPromotionCode())
                .hasFieldOrPropertyWithValue("shipmentMethodId", checkout.getShipmentMethodId())
                .hasFieldOrPropertyWithValue("paymentMethodId", checkout.getPaymentMethodId())
                .hasFieldOrPropertyWithValue("shippingAddressId", checkout.getShippingAddressId());

        Assertions.assertThat(res.checkoutItemVms()).isNull();
    }

    @Test
    void testCheckoutItemToVm_convertCheckoutItemCorrectly() {

        CheckoutItem checkoutItem = Instancio.create(CheckoutItem.class);

        var res = checkoutMapper.toVm(checkoutItem);

        Assertions.assertThat(res)
                .hasFieldOrPropertyWithValue("id", checkoutItem.getId())
                .hasFieldOrPropertyWithValue("productId", checkoutItem.getProductId())
                .hasFieldOrPropertyWithValue("productName", checkoutItem.getProductName())
                .hasFieldOrPropertyWithValue("description", checkoutItem.getDescription())
                .hasFieldOrPropertyWithValue("quantity", checkoutItem.getQuantity())
                .hasFieldOrPropertyWithValue("productPrice", checkoutItem.getProductPrice())
                .hasFieldOrPropertyWithValue("tax", checkoutItem.getTax())
                .hasFieldOrPropertyWithValue("discountAmount", checkoutItem.getDiscountAmount())
                .hasFieldOrPropertyWithValue("shipmentFee", checkoutItem.getShipmentFee())
                .hasFieldOrPropertyWithValue("shipmentTax", checkoutItem.getShipmentTax())
                .hasFieldOrPropertyWithValue("checkoutId", checkoutItem.getCheckoutId());
    }
}

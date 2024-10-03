package com.yas.order.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CartItemId implements Serializable {
    private String customerId;
    private Long productId;

    public static CartItemId of(String customerId, Long productId) {
        CartItemId cartItemId = new CartItemId();
        cartItemId.setCustomerId(customerId);
        cartItemId.setProductId(productId);
        return cartItemId;
    }
}

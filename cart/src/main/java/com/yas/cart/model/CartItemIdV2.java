package com.yas.cart.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@lombok.Getter
@lombok.Setter
@EqualsAndHashCode
public class CartItemIdV2 {
    private String customerId;
    private Long productId;

    public static CartItemIdV2 of(String customerId, Long productId) {
        return new CartItemIdV2(customerId, productId);
    }
}

package com.yas.cart.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@lombok.Getter
@lombok.Setter
@EqualsAndHashCode
public class CartItemV2Id {
    private String customerId;
    private Long productId;
}

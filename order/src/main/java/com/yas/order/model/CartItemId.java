package com.yas.order.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@lombok.Getter
@lombok.Setter
@EqualsAndHashCode
public class CartItemId implements Serializable {
    private String customerId;
    private Long productId;

    public static CartItemId of(String customerId, Long productId) {
        return new CartItemId(customerId, productId);
    }
}

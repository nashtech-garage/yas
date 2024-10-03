package com.yas.order.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CartItemId implements Serializable {
    private String customerId;
    private Long productId;

    private CartItemId(String customerId, Long productId) {
        this.customerId = customerId;
        this.productId = productId;
    }

    public static CartItemId of(String customerId, Long productId) {
        return new CartItemId(customerId, productId);
    }
}

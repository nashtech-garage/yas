package com.yas.order.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CartItemId implements Serializable {
    private String customerId;
    private Long productId;

    public static CartItemId of(String customerId, Long productId) {
        return new CartItemId(customerId, productId);
    }

    public String getCustomerId() {
        return customerId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}

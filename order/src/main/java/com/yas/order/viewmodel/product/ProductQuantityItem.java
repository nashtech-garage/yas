package com.yas.order.viewmodel.product;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Builder
public record ProductQuantityItem(Long productId, Long quantity) {
}

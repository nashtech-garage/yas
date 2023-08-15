package com.yas.saga.product.command;

import lombok.Builder;

@Builder
public record ProductQuantityItem(Long productId, Integer quantity) {
}

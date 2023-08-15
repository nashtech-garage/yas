package com.yas.saga.product.reply;

import lombok.Builder;

@Builder
public record SubtractProductStockQuantitySuccess(String message) {
}

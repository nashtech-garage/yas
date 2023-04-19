package com.yas.inventory.viewmodel.stock;

import jakarta.validation.constraints.NotNull;

public record StockRequest(@NotNull Long productId, @NotNull Long quantity) {
}

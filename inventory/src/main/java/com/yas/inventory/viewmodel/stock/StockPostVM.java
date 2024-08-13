package com.yas.inventory.viewmodel.stock;

import jakarta.validation.constraints.NotNull;

public record StockPostVM(@NotNull Long productId, @NotNull Long warehouseId) {
}

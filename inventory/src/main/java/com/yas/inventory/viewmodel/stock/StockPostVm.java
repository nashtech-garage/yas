package com.yas.inventory.viewmodel.stock;

import jakarta.validation.constraints.NotNull;

public record StockPostVm(@NotNull Long productId, @NotNull Long warehouseId) {
}

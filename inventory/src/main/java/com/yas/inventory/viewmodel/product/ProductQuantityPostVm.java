package com.yas.inventory.viewmodel.product;

import com.yas.inventory.model.Stock;

public record ProductQuantityPostVm(Long productId, Long stockQuantity) {
    public static ProductQuantityPostVm fromModel(Stock stock) {
        return new ProductQuantityPostVm(stock.getProductId(), stock.getQuantity());
    }
}

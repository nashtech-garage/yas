package com.yas.inventory.viewmodel.stock;

import com.yas.inventory.model.Stock;

public record StockVM (Long id, Long productId, Long warehouseId) {
    public static StockVM fromStock(Stock stock) {
        return new StockVM(stock.getId(), stock.getProductId(), stock.getWarehouse().getId());
    }
}

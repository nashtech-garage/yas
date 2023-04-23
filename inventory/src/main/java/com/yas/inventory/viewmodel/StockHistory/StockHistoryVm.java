package com.yas.inventory.viewmodel.StockHistory;

import com.yas.inventory.model.StockHistory;
import com.yas.inventory.viewmodel.product.ProductInfoVm;

public record StockHistoryVm(
        Long id,
        String productName,
        Long adjustedQuantity,
        String note
) {
    public static StockHistoryVm fromModel(StockHistory stockHistory,
                                           ProductInfoVm productInfoVm) {
        return new StockHistoryVm(
                stockHistory.getId(),
                productInfoVm.name(),
                stockHistory.getAdjustedQuantity(),
                stockHistory.getNote()
        );
    }
}
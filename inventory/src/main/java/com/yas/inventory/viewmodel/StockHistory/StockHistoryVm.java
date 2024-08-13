package com.yas.inventory.viewmodel.StockHistory;

import com.yas.inventory.model.StockHistory;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import java.time.ZonedDateTime;

public record StockHistoryVm(
    Long id,
    String productName,
    Long adjustedQuantity,
    String createdBy,
    ZonedDateTime createdOn,
    String note
) {
    public static StockHistoryVm fromModel(StockHistory stockHistory,
                                           ProductInfoVm productInfoVm) {
        return new StockHistoryVm(
            stockHistory.getId(),
            productInfoVm.name(),
            stockHistory.getAdjustedQuantity(),
            stockHistory.getCreatedBy(),
            stockHistory.getCreatedOn(),
            stockHistory.getNote()
        );
    }
}
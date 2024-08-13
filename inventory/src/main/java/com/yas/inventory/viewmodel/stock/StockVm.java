package com.yas.inventory.viewmodel.stock;

import com.yas.inventory.model.Stock;
import com.yas.inventory.viewmodel.product.ProductInfoVm;

public record StockVm(
    Long id,
    Long productId,
    String productName,
    String productSku,
    Long quantity,
    Long reservedQuantity,
    Long warehouseId
) {
    public static StockVm fromModel(Stock stock,
                                    ProductInfoVm productInfoVm) {
        return new StockVm(
            stock.getId(),
            stock.getProductId(),
            productInfoVm.name(),
            productInfoVm.sku(),
            stock.getQuantity(),
            stock.getReservedQuantity(),
            stock.getWarehouse().getId()
        );
    }
}

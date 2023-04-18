package com.yas.inventory.viewmodel.stock;

import com.yas.inventory.model.Stock;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import liquibase.util.ObjectUtil;
import org.springframework.util.ObjectUtils;

public record StockVm(
        Long id,
        Long productId,
        String productName,
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
                stock.getQuantity(),
                stock.getReservedQuantity(),
                stock.getWarehouse().getId()
        );
    }
}

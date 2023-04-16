package com.yas.inventory.viewmodel.product;

public record ProductInfoVm(
        long id,
        String name,
        String sku,
        boolean existInWH) {
}

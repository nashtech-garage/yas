package com.yas.inventory.viewmodel.warehouse;

import com.yas.inventory.model.Warehouse;

public record WarehouseVM (Long id, String name, Long addressId) {
    public static WarehouseVM fromWarehouse(Warehouse wh) {
        return new WarehouseVM(wh.getId(), wh.getName(), wh.getAddressId());
    }
}

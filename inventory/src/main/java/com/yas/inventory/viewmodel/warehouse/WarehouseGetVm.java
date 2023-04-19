package com.yas.inventory.viewmodel.warehouse;

import com.yas.inventory.model.Warehouse;

public record WarehouseGetVm(Long id, String name) {

    public static WarehouseGetVm fromModel(Warehouse warehouse) {
        return new WarehouseGetVm(warehouse.getId(), warehouse.getName());
    }
}
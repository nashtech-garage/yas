package com.yas.inventory.controller;

import com.yas.inventory.model.enumeration.FilterExistInWHSelection;
import com.yas.inventory.service.WarehouseService;
import com.yas.inventory.utils.Constants;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseVM;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.ApiConstant.WAREHOUSE_URL)
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<List<WarehouseVM>> getWarehouses() {
        return ResponseEntity.ok(warehouseService.getAll());
    }

    @GetMapping("/{warehouseId}/products")
    public ResponseEntity<List<ProductInfoVm>> getProductByWarehouse(
            @PathVariable Long warehouseId, @RequestParam String productName,
            @RequestParam String productSku, @RequestParam FilterExistInWHSelection existStatus) {
        return ResponseEntity.ok(warehouseService.getProductWarehouse(warehouseId, productName, productSku, existStatus));
    }
}

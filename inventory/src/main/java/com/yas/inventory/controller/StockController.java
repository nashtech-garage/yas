package com.yas.inventory.controller;

import com.yas.inventory.constants.ApiConstant;
import com.yas.inventory.service.StockService;
import com.yas.inventory.viewmodel.stock.StockPostVM;
import com.yas.inventory.viewmodel.stock.StockVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstant.STOCK_URL)
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @PostMapping
    public ResponseEntity<Void> addProductIntoWarehouse(@RequestBody List<StockPostVM> postVMs) {
        stockService.addProductIntoWarehouse(postVMs);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<StockVm>> getStocksByWarehouseIdAndProductNameAndSku(@RequestParam(name = "warehouseId") Long warehouseId,
                                                                                    @RequestParam(required = false) String productName,
                                                                                    @RequestParam(required = false) String productSku) {
        return ResponseEntity.ok(stockService.getStocksByWarehouseIdAndProductNameAndSku(
                warehouseId,
                productName,
                productSku
        ));
    }
}

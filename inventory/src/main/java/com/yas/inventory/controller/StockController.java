package com.yas.inventory.controller;

import com.yas.inventory.constants.ApiConstant;
import com.yas.inventory.service.StockService;
import com.yas.inventory.viewmodel.stock.StockPostVm;
import com.yas.inventory.viewmodel.stock.StockQuantityUpdateVm;
import com.yas.inventory.viewmodel.stock.StockVm;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.STOCK_URL)
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @PostMapping
    public ResponseEntity<Void> addProductIntoWarehouse(@RequestBody List<StockPostVm> postVms) {
        stockService.addProductIntoWarehouse(postVms);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<StockVm>> getStocksByWarehouseIdAndProductNameAndSku(
        @RequestParam(name = "warehouseId") Long warehouseId,
        @RequestParam(required = false) String productName,
        @RequestParam(required = false) String productSku) {
        return ResponseEntity.ok(stockService.getStocksByWarehouseIdAndProductNameAndSku(
            warehouseId,
            productName,
            productSku
        ));
    }

    @PutMapping
    public ResponseEntity<Void> updateProductQuantityInStock(@RequestBody StockQuantityUpdateVm requestBody) {
        stockService.updateProductQuantityInStock(requestBody);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/products-in-warehouse")
    public List<Long> getProductsInWarehouse() {
        return stockService.findProductIdsAddedWarehouse();
    }

}

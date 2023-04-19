package com.yas.inventory.controller;

import com.yas.inventory.constants.ApiConstant;
import com.yas.inventory.service.StockService;
import com.yas.inventory.viewmodel.stock.StockPostVM;
import com.yas.inventory.viewmodel.stock.StockRequest;
import com.yas.inventory.viewmodel.stock.StockVM;
import jakarta.validation.Valid;
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
    public ResponseEntity<Void> addProductIntoWarehouse(@Valid @RequestBody List<StockPostVM> postVMs) {
        stockService.addProductIntoWarehouse(postVMs);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/check-stock-available")
    public ResponseEntity<List<StockVM>> checkStockAvailable(@Valid @RequestBody List<StockRequest> stockRequests) {
        return ResponseEntity.ok(stockService.getStockAvalables(stockRequests));
    }
}

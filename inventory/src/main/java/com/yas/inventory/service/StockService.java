package com.yas.inventory.service;

import com.yas.inventory.exception.NotFoundException;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.utils.Constants;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockPostVM;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StockService {
    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;
    private final ProductService productService;
    public void addProductIntoWarehouse(List<StockPostVM> postVMs) {

        List<Stock> stocks = postVMs.stream().map(postVM -> {
            ProductInfoVm product = productService.getProduct(postVM.productId());

            if (product == null) {
                throw new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, postVM.productId());
            }

            Optional<Warehouse> warehouseOp = warehouseRepository.findById(postVM.warehouseId());

            if (warehouseOp.isEmpty()) {
                throw new NotFoundException(Constants.ERROR_CODE.WAREHOUSE_NOT_FOUND, postVM.warehouseId());
            }

            return Stock.builder()
                    .productId(postVM.productId())
                    .warehouse(warehouseOp.get())
                    .quantity(0L)
                    .reservedQuantity(0L)
                    .build();
        }).toList();


        stockRepository.saveAll(stocks);

    }

}

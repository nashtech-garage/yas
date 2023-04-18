package com.yas.inventory.service;

import com.yas.inventory.constants.MessageCode;
import com.yas.inventory.exception.NotFoundException;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWHSelection;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockPostVM;
import com.yas.inventory.viewmodel.stock.StockVm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StockService {
    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;
    private final ProductService productService;

    private final WarehouseService warehouseService;

    public void addProductIntoWarehouse(List<StockPostVM> postVMs) {

        List<Stock> stocks = postVMs.stream().map(postVM -> {
            ProductInfoVm product = productService.getProduct(postVM.productId());

            if (product == null) {
                throw new NotFoundException(MessageCode.PRODUCT_NOT_FOUND, postVM.productId());
            }

            Optional<Warehouse> warehouseOp = warehouseRepository.findById(postVM.warehouseId());

            if (warehouseOp.isEmpty()) {
                throw new NotFoundException(MessageCode.WAREHOUSE_NOT_FOUND, postVM.warehouseId());
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

    public List<StockVm> getStocksByWarehouseId(Long warehouseId) {
        List<Stock> stocks = stockRepository.findByWarehouseId(warehouseId);

        return stocks.stream().map(
                stock -> {
                    ProductInfoVm productInfoVm = productService.getProduct(stock.getProductId());

                    return StockVm.fromModel(stock, productInfoVm);
                }
        ).toList();
    }

    public List<StockVm> getStocksByWarehouseIdAndProductNameAndSku(Long warehouseId,
                                                                    String productName,
                                                                    String productSku) {
        Set<ProductInfoVm> productInfoVmList = new HashSet<>(warehouseService.getProductWarehouse(
                warehouseId,
                productName,
                productSku,
                FilterExistInWHSelection.YES));
//                .sorted(Comparator.comparingLong(ProductInfoVm::id)).toList();

        List<Stock> stocks = stockRepository.findByWarehouseIdAndProductIdIn(
                warehouseId,
                productInfoVmList.stream().map(ProductInfoVm::id).toList()
        );

        return stocks.stream().map(
                stock -> {
                    ProductInfoVm productInfoVm = productInfoVmList.stream().filter(productInfoVm1 -> productInfoVm1.id() == stock.getId()).findFirst().get();
                }
        )
    }
}

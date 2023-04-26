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
import com.yas.inventory.viewmodel.stock.StockQuantityUpdateVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stock.StockVm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StockService {
    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;
    private final ProductService productService;

    private final WarehouseService warehouseService;

    private final StockHistoryService stockHistoryService;

    public StockService(WarehouseRepository warehouseRepository,
                        StockRepository stockRepository,
                        ProductService productService,
                        WarehouseService warehouseService,
                        StockHistoryService stockHistoryService) {
        this.warehouseRepository = warehouseRepository;
        this.stockRepository = stockRepository;
        this.productService = productService;
        this.warehouseService = warehouseService;
        this.stockHistoryService = stockHistoryService;
    }

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

    public List<StockVm> getStocksByWarehouseIdAndProductNameAndSku(Long warehouseId,
                                                                    String productName,
                                                                    String productSku) {
        HashMap<Long, ProductInfoVm> productInfoVmHashMap = (HashMap<Long, ProductInfoVm>) warehouseService.getProductWarehouse(
                        warehouseId,
                        productName,
                        productSku,
                        FilterExistInWHSelection.YES)
                .parallelStream()
                .collect(Collectors.toMap(ProductInfoVm::id, productInfoVm -> productInfoVm));

        List<Stock> stocks = stockRepository.findByWarehouseIdAndProductIdIn(
                warehouseId,
                productInfoVmHashMap.values().parallelStream().map(ProductInfoVm::id).toList()
        );

        return stocks.stream().map(
                stock -> {
                    ProductInfoVm productInfoVm = productInfoVmHashMap.get(stock.getProductId());

                    return StockVm.fromModel(stock, productInfoVm);
                }
        ).toList();
    }

    public void updateProductQuantityInStock(final StockQuantityUpdateVm requestBody) {
        List<StockQuantityVm> stockQuantityVms = requestBody.stockQuantityList();
        List<Stock> stocks = stockRepository.findAllById(stockQuantityVms.parallelStream().map(StockQuantityVm::stockId).toList());

        for (final Stock stock : stocks) {
            StockQuantityVm stockQuantityVm = stockQuantityVms
                    .parallelStream()
                    .filter(stockQuantityPostVm -> stockQuantityPostVm.stockId().equals(stock.getId()))
                    .findFirst()
                    .orElse(null);

            if (stockQuantityVm == null) {
                continue;
            }

            Long adjustedQuantity = stockQuantityVm.quantity() != null ? stockQuantityVm.quantity() : 0;
            stock.setQuantity(stock.getQuantity() + adjustedQuantity);
        }
        stockRepository.saveAllAndFlush(stocks);
        stockHistoryService.createStockHistories(stocks, stockQuantityVms);
    }
}

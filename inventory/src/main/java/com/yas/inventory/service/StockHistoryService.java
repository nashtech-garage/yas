package com.yas.inventory.service;

import com.yas.inventory.model.Stock;
import com.yas.inventory.model.StockHistory;
import com.yas.inventory.repository.StockHistoryRepository;
import com.yas.inventory.viewmodel.StockHistory.StockHistoryListVm;
import com.yas.inventory.viewmodel.StockHistory.StockHistoryVm;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StockHistoryService {
    private final StockHistoryRepository stockHistoryRepository;

    private final ProductService productService;

    public StockHistoryService(StockHistoryRepository stockHistoryRepository,
                               ProductService productService) {
        this.stockHistoryRepository = stockHistoryRepository;
        this.productService = productService;
    }

    public void createStockHistories(final List<Stock> stocks,
                                     final List<StockQuantityVm> stockQuantityVms) {
        List<StockHistory> stockHistories = new ArrayList<>();

        for (final Stock stock : stocks) {
            StockQuantityVm stockQuantityVm = stockQuantityVms
                    .parallelStream()
                    .filter(stockQuantityVm1 -> stockQuantityVm1.stockId().equals(stock.getId()))
                    .findFirst()
                    .orElse(null);

            if (stockQuantityVm == null) {
                continue;
            }

            stockHistories.add(
                    StockHistory.builder()
                            .productId(stock.getProductId())
                            .note(stockQuantityVm.note())
                            .adjustedQuantity(stockQuantityVm.quantity())
                            .warehouse(stock.getWarehouse())
                            .build()
            );
        }
        stockHistoryRepository.saveAll(stockHistories);
    }

    public StockHistoryListVm getStockHistories(final Long productId,
                                                final Long warehouseId) {
        List<StockHistory> stockHistories = stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(
                productId,
                warehouseId
        );
        ProductInfoVm productInfoVm = productService.getProduct(productId);

        return new StockHistoryListVm(
                stockHistories.stream().map(
                        stockHistory -> StockHistoryVm.fromModel(
                                stockHistory,
                                productInfoVm
                        )
                ).toList()
        );
    }
}

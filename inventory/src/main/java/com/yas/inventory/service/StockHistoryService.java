package com.yas.inventory.service;

import com.yas.inventory.model.Stock;
import com.yas.inventory.model.StockHistory;
import com.yas.inventory.repository.StockHistoryRepository;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StockHistoryService {
    private final StockHistoryRepository stockHistoryRepository;

    public StockHistoryService(StockHistoryRepository stockHistoryRepository) {
        this.stockHistoryRepository = stockHistoryRepository;
    }

    public void createStockHistories(final List<Stock> stocks,
                                     final List<StockQuantityVm> stockQuantityVms) {
        List<StockHistory> stockHistories = new ArrayList<>();

        for (final Stock stock : stocks) {
            StockQuantityVm stockQuantityVm = stockQuantityVms
                    .stream()
                    .filter(stockQuantityVm1 -> stockQuantityVm1.stockId().equals(stock.getQuantity()))
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
        stockHistoryRepository.saveAllAndFlush(stockHistories);
    }
}

package com.yas.inventory.service;

import com.yas.inventory.model.Stock;
import com.yas.inventory.model.StockHistory;
import com.yas.inventory.repository.StockHistoryRepository;
import com.yas.inventory.viewmodel.stock.StockQuantityPostVm;
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
                                     final List<StockQuantityPostVm> stockQuantityPostVms) {
        List<StockHistory> stockHistories = new ArrayList<>();

        for (final Stock stock : stocks) {
            StockQuantityPostVm stockQuantityPostVm = stockQuantityPostVms
                    .stream()
                    .filter(stockQuantityPostVm1 -> stockQuantityPostVm1.stockId().equals(stock.getQuantity()))
                    .findFirst()
                    .orElse(null);

            if (stockQuantityPostVm == null) {
                continue;
            }

            stockHistories.add(
                    StockHistory.builder()
                            .productId(stock.getProductId())
                            .note(stockQuantityPostVm.note())
                            .adjustedQuantity(stockQuantityPostVm.quantity())
                            .warehouse(stock.getWarehouse())
                            .build()
            );
        }
        stockHistoryRepository.saveAllAndFlush(stockHistories);
    }
}

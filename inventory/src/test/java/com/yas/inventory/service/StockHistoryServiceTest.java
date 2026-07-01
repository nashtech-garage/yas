package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.inventory.model.Stock;
import com.yas.inventory.model.StockHistory;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockHistoryRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stockhistory.StockHistoryListVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockHistoryServiceTest {

    private StockHistoryRepository stockHistoryRepository;
    private ProductService productService;
    private StockHistoryService stockHistoryService;

    @BeforeEach
    void setUp() {
        stockHistoryRepository = mock(StockHistoryRepository.class);
        productService = mock(ProductService.class);
        stockHistoryService = new StockHistoryService(stockHistoryRepository, productService);
    }

    @Test
    void createStockHistories_shouldSaveAll() {
        Stock stock = new Stock();
        stock.setId(1L);
        stock.setProductId(100L);
        stock.setWarehouse(new Warehouse());
        
        StockQuantityVm vm = new StockQuantityVm(1L, 5L, "Note");
        
        stockHistoryService.createStockHistories(List.of(stock), List.of(vm));

        verify(stockHistoryRepository).saveAll(any());
    }

    @Test
    void getStockHistories_shouldReturnList() {
        StockHistory history = StockHistory.builder()
            .productId(100L)
            .adjustedQuantity(5L)
            .warehouse(new Warehouse())
            .build();
        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(100L, 1L))
            .thenReturn(List.of(history));
        when(productService.getProduct(100L)).thenReturn(new ProductInfoVm(100L, "P1", "S1", true));

        StockHistoryListVm result = stockHistoryService.getStockHistories(100L, 1L);

        assertThat(result.data()).hasSize(1);
    }
}

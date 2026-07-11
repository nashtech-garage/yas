package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    void createStockHistories_whenNormalCase_saveHistories() {
        Stock stock = new Stock();
        stock.setId(1L);
        stock.setProductId(100L);
        Warehouse warehouse = new Warehouse();
        warehouse.setId(10L);
        stock.setWarehouse(warehouse);

        StockQuantityVm quantityVm = new StockQuantityVm(1L, 10L, "Adjust note");
        
        stockHistoryService.createStockHistories(List.of(stock), List.of(quantityVm));

        verify(stockHistoryRepository).saveAll(any());
    }

    @Test
    void getStockHistories_whenNormalCase_returnList() {
        StockHistory history = StockHistory.builder()
                .productId(100L)
                .warehouse(new Warehouse())
                .adjustedQuantity(10L)
                .note("Test")
                .build();
        
        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(100L, 10L))
                .thenReturn(List.of(history));
        
        when(productService.getProduct(100L))
                .thenReturn(new ProductInfoVm(100L, "Product 1", "SKU1", true));

        StockHistoryListVm result = stockHistoryService.getStockHistories(100L, 10L);

        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).productName()).isEqualTo("Product 1");
        verify(stockHistoryRepository).findByProductIdAndWarehouseIdOrderByCreatedOnDesc(100L, 10L);
    }
}

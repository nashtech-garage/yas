package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
import org.mockito.ArgumentCaptor;

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
    void createStockHistories_whenNormalCase_shouldSaveAll() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);

        Stock stock = Stock.builder()
            .id(1L)
            .productId(100L)
            .warehouse(warehouse)
            .build();

        StockQuantityVm stockQuantityVm = new StockQuantityVm(1L, 10L, "Note");

        stockHistoryService.createStockHistories(List.of(stock), List.of(stockQuantityVm));

        ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
        verify(stockHistoryRepository, times(1)).saveAll(captor.capture());

        List<StockHistory> savedHistories = captor.getValue();
        assertEquals(1, savedHistories.size());
        assertEquals(100L, savedHistories.get(0).getProductId());
        assertEquals(10L, savedHistories.get(0).getAdjustedQuantity());
        assertEquals("Note", savedHistories.get(0).getNote());
        assertEquals(warehouse, savedHistories.get(0).getWarehouse());
    }

    @Test
    void getStockHistories_whenNormalCase_returnStockHistoryListVm() {
        Long productId = 100L;
        Long warehouseId = 1L;

        Warehouse warehouse = new Warehouse();
        warehouse.setId(warehouseId);

        StockHistory stockHistory = StockHistory.builder()
            .id(1L)
            .productId(productId)
            .adjustedQuantity(10L)
            .note("Note")
            .warehouse(warehouse)
            .build();

        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(productId, warehouseId))
            .thenReturn(List.of(stockHistory));

        ProductInfoVm productInfoVm = new ProductInfoVm(productId, "Product Name", "SKU", true);
        when(productService.getProduct(productId)).thenReturn(productInfoVm);

        StockHistoryListVm result = stockHistoryService.getStockHistories(productId, warehouseId);

        assertEquals(1, result.data().size());
        assertEquals("Product Name", result.data().get(0).productName());
        assertEquals(10L, result.data().get(0).adjustedQuantity());
    }
}

package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockHistoryServiceTest {

    @Mock
    private StockHistoryRepository stockHistoryRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private StockHistoryService stockHistoryService;

    @Nested
    class CreateStockHistoriesTest {

        @Test
        void testCreateStockHistories_whenStocksAndVmsMatch_shouldSaveHistories() {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(1L);

            Stock stock = Stock.builder()
                .id(1L)
                .productId(100L)
                .warehouse(warehouse)
                .quantity(10L)
                .build();

            StockQuantityVm stockQuantityVm = new StockQuantityVm(1L, 5L, "Restock");

            stockHistoryService.createStockHistories(List.of(stock), List.of(stockQuantityVm));

            verify(stockHistoryRepository).saveAll(anyList());
        }

        @Test
        void testCreateStockHistories_whenVmNotMatchStock_shouldSaveEmptyList() {
            Stock stock = Stock.builder()
                .id(1L)
                .productId(100L)
                .quantity(10L)
                .build();

            StockQuantityVm stockQuantityVm = new StockQuantityVm(99L, 5L, "Restock");

            stockHistoryService.createStockHistories(List.of(stock), List.of(stockQuantityVm));

            verify(stockHistoryRepository).saveAll(List.of());
        }

        @Test
        void testCreateStockHistories_whenEmptyStocks_shouldSaveEmptyList() {
            stockHistoryService.createStockHistories(List.of(), List.of());

            verify(stockHistoryRepository).saveAll(List.of());
        }
    }

    @Nested
    class GetStockHistoriesTest {

        @Test
        void testGetStockHistories_whenNormalCase_shouldReturnHistoryList() {
            Long productId = 1L;
            Long warehouseId = 1L;
            Warehouse warehouse = new Warehouse();
            warehouse.setId(warehouseId);

            StockHistory stockHistory = StockHistory.builder()
                .productId(productId)
                .warehouse(warehouse)
                .adjustedQuantity(5L)
                .note("Restock")
                .build();

            ProductInfoVm productInfoVm = new ProductInfoVm(productId, "Product A", "SKU-001", true);

            when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(productId, warehouseId))
                .thenReturn(List.of(stockHistory));
            when(productService.getProduct(productId)).thenReturn(productInfoVm);

            StockHistoryListVm result = stockHistoryService.getStockHistories(productId, warehouseId);

            assertThat(result).isNotNull();
            assertThat(result.data()).hasSize(1);
        }

        @Test
        void testGetStockHistories_whenNoHistory_shouldReturnEmptyList() {
            Long productId = 1L;
            Long warehouseId = 1L;
            ProductInfoVm productInfoVm = new ProductInfoVm(productId, "Product A", "SKU-001", true);

            when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(productId, warehouseId))
                .thenReturn(List.of());
            when(productService.getProduct(productId)).thenReturn(productInfoVm);

            StockHistoryListVm result = stockHistoryService.getStockHistories(productId, warehouseId);

            assertThat(result.data()).isEmpty();
        }
    }
}

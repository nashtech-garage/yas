package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        stockHistoryRepository = org.mockito.Mockito.mock(StockHistoryRepository.class);
        productService = org.mockito.Mockito.mock(ProductService.class);
        stockHistoryService = new StockHistoryService(stockHistoryRepository, productService);
    }

    @Test
    void createStockHistories_whenOnlyOneStockHasAdjustment_saveOneHistoryRow() {
        Warehouse warehouse = Warehouse.builder().id(10L).name("WH-1").addressId(100L).build();

        Stock stock1 = Stock.builder()
            .id(1L)
            .productId(100L)
            .quantity(10L)
            .reservedQuantity(0L)
            .warehouse(warehouse)
            .build();
        Stock stock2 = Stock.builder()
            .id(2L)
            .productId(200L)
            .quantity(20L)
            .reservedQuantity(0L)
            .warehouse(warehouse)
            .build();

        List<StockQuantityVm> adjustments = List.of(
            new StockQuantityVm(1L, 5L, "restock")
        );

        stockHistoryService.createStockHistories(List.of(stock1, stock2), adjustments);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
        verify(stockHistoryRepository).saveAll(captor.capture());

        List<StockHistory> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.getFirst().getProductId()).isEqualTo(100L);
        assertThat(saved.getFirst().getAdjustedQuantity()).isEqualTo(5L);
        assertThat(saved.getFirst().getNote()).isEqualTo("restock");
        assertThat(saved.getFirst().getWarehouse().getId()).isEqualTo(10L);
    }

    @Test
    void getStockHistories_whenDataExists_returnMappedListWithProductInfo() {
        Warehouse warehouse = Warehouse.builder().id(10L).name("WH-1").addressId(100L).build();
        StockHistory history = StockHistory.builder()
            .id(99L)
            .productId(100L)
            .adjustedQuantity(3L)
            .note("manual adjustment")
            .warehouse(warehouse)
            .build();

        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(100L, 10L))
            .thenReturn(List.of(history));
        when(productService.getProduct(100L)).thenReturn(new ProductInfoVm(100L, "Product A", "SKU-A", true));

        StockHistoryListVm result = stockHistoryService.getStockHistories(100L, 10L);

        assertThat(result.data()).hasSize(1);
        assertThat(result.data().getFirst().id()).isEqualTo(99L);
        assertThat(result.data().getFirst().productName()).isEqualTo("Product A");
        assertThat(result.data().getFirst().adjustedQuantity()).isEqualTo(3L);

        verify(stockHistoryRepository).findByProductIdAndWarehouseIdOrderByCreatedOnDesc(100L, 10L);
        verify(productService).getProduct(100L);
    }
}

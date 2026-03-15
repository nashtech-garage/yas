package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void testCreateStockHistories_whenStockIdsDoNotMatch_saveEmptyList() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("WH A").addressId(5L).build();
        Stock stock = Stock.builder().id(99L).productId(2L).quantity(10L).warehouse(warehouse).build();
        ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);

        stockHistoryService.createStockHistories(
            List.of(stock),
            List.of(new StockQuantityVm(1L, 5L, "Restock"))
        );

        verify(stockHistoryRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).isEmpty();
    }

    @Test
    void testCreateStockHistories_whenStockIdsMatch_saveMappedHistories() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("WH A").addressId(5L).build();
        Stock firstStock = Stock.builder().id(1L).productId(2L).quantity(10L).warehouse(warehouse).build();
        Stock secondStock = Stock.builder().id(2L).productId(3L).quantity(20L).warehouse(warehouse).build();
        ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);

        stockHistoryService.createStockHistories(
            List.of(firstStock, secondStock),
            List.of(
                new StockQuantityVm(1L, 5L, "Restock"),
                new StockQuantityVm(2L, -3L, "Adjust")
            )
        );

        verify(stockHistoryRepository).saveAll(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertEquals(2L, captor.getValue().getFirst().getProductId());
        assertEquals("Restock", captor.getValue().getFirst().getNote());
        assertEquals(-3L, captor.getValue().getLast().getAdjustedQuantity());
    }

    @Test
    void testGetStockHistories_whenHistoryExists_returnViewModelList() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("WH A").addressId(5L).build();
        StockHistory stockHistory = StockHistory.builder()
            .id(1L)
            .productId(2L)
            .adjustedQuantity(5L)
            .note("Restock")
            .warehouse(warehouse)
            .build();

        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(2L, 1L))
            .thenReturn(List.of(stockHistory));
        when(productService.getProduct(2L)).thenReturn(new ProductInfoVm(2L, "Keyboard", "K1", true));

        StockHistoryListVm result = stockHistoryService.getStockHistories(2L, 1L);

        assertEquals(1, result.data().size());
        assertEquals(1L, result.data().getFirst().id());
        assertEquals("Keyboard", result.data().getFirst().productName());
        assertEquals(5L, result.data().getFirst().adjustedQuantity());
    }
}
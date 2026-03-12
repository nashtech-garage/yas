package com.yas.inventory.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.yas.inventory.model.Stock;
import com.yas.inventory.model.StockHistory;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockHistoryRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stockhistory.StockHistoryListVm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class StockHistoryServiceTest {

    @Mock
    private StockHistoryRepository stockHistoryRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private StockHistoryService stockHistoryService;

    private Stock stock;
    private Warehouse warehouse;

    @BeforeEach
    void setup() {

        warehouse = Warehouse.builder()
                .id(100L)
                .build();

        stock = Stock.builder()
                .id(1L)
                .productId(10L)
                .warehouse(warehouse)
                .build();
    }

    @Test
    void createStockHistories_shouldSaveStockHistory() {

        StockQuantityVm quantityVm =
                new StockQuantityVm(
                        1L,
                        5L,
                        "test note"
                );

        List<Stock> stocks = List.of(stock);
        List<StockQuantityVm> quantities = List.of(quantityVm);

        stockHistoryService.createStockHistories(stocks, quantities);

        verify(stockHistoryRepository, times(1)).saveAll(any());
    }

    @Test
    void createStockHistories_shouldSkipWhenStockQuantityVmNotFound() {

        List<Stock> stocks = List.of(stock);
        List<StockQuantityVm> quantities = List.of();

        stockHistoryService.createStockHistories(stocks, quantities);

        verify(stockHistoryRepository, times(1)).saveAll(any());
    }

    @Test
    void getStockHistories_shouldReturnStockHistoryListVm() {

        StockHistory history = StockHistory.builder()
                .productId(10L)
                .warehouse(warehouse)
                .adjustedQuantity(5L)
                .note("note")
                .build();

        when(stockHistoryRepository
                .findByProductIdAndWarehouseIdOrderByCreatedOnDesc(10L, 100L))
                .thenReturn(List.of(history));

        ProductInfoVm productInfoVm = mock(ProductInfoVm.class);

        when(productService.getProduct(10L))
                .thenReturn(productInfoVm);

        StockHistoryListVm result =
                stockHistoryService.getStockHistories(10L, 100L);

        assertNotNull(result);

        verify(stockHistoryRepository)
                .findByProductIdAndWarehouseIdOrderByCreatedOnDesc(10L, 100L);

        verify(productService)
                .getProduct(10L);
    }
}
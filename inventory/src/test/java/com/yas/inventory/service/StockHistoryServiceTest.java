package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;

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

    private Stock stock;
    private StockQuantityVm stockQuantityVm;
    private ProductInfoVm productInfoVm;

    @BeforeEach
    void setUp() {
        Warehouse warehouse = Warehouse.builder().name("Warehouse").addressId(1L).build();
        warehouse.setId(1L);

        stock = Stock.builder().productId(1L).warehouse(warehouse).quantity(10L).reservedQuantity(0L).build();
        stock.setId(1L);

        stockQuantityVm = new StockQuantityVm(1L, 5L, "Note");
        productInfoVm = new ProductInfoVm(1L, "Product", "SKU", true);
    }

    @Test
    void createStockHistories_shouldSaveHistories() {
        stockHistoryService.createStockHistories(List.of(stock), List.of(stockQuantityVm));

        verify(stockHistoryRepository).saveAll(anyList());
    }

    @Test
    void createStockHistories_shouldSkipWhenVmNotFound() {
        StockQuantityVm unmatchedVm = new StockQuantityVm(2L, 5L, "Note");

        stockHistoryService.createStockHistories(List.of(stock), List.of(unmatchedVm));

        ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
        verify(stockHistoryRepository).saveAll(captor.capture());
        assertTrue(captor.getValue().isEmpty());
    }

    @Test
    void getStockHistories_shouldReturnList() {
        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(1L, 1L))
            .thenReturn(List.of());
        when(productService.getProduct(1L)).thenReturn(productInfoVm);

        StockHistoryListVm result = stockHistoryService.getStockHistories(1L, 1L);

        assertNotNull(result);
        assertEquals(0, result.data().size());
    }
}

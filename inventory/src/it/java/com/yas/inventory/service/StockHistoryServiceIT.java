package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.yas.inventory.viewmodel.stockhistory.StockHistoryVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class StockHistoryServiceIT {

    private StockHistoryRepository stockHistoryRepository;
    private ProductService productService;
    private StockHistoryService stockHistoryService;

    @BeforeEach
    void setUp() {
        stockHistoryRepository = Mockito.mock(StockHistoryRepository.class);
        productService = Mockito.mock(ProductService.class);
        stockHistoryService = new StockHistoryService(stockHistoryRepository, productService);
    }

    @Test
    void testCreateStockHistories_whenStockQuantityVmNull_saveStockHistoriesEmpty() {

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);

        warehouse.setName("Warehouse B");
        warehouse.setAddressId(456L);

        Stock stock1 = new Stock();
        stock1.setProductId(1L);
        stock1.setQuantity(10L);
        stock1.setWarehouse(warehouse);

        Stock stock2 = new Stock();
        stock2.setProductId(2L);
        stock2.setQuantity(15L);
        stock2.setWarehouse(warehouse);

        List<Stock> stocks = List.of(stock1, stock2);

        StockQuantityVm stockQuantityVm1 = new StockQuantityVm(1L, 10L, "Initial stock");
        StockQuantityVm stockQuantityVm2 = new StockQuantityVm(2L, 20L, "Restock");
        List<StockQuantityVm> stockQuantityVms = List.of(stockQuantityVm1, stockQuantityVm2);

        ArgumentCaptor<List<StockHistory>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        stockHistoryService.createStockHistories(stocks, stockQuantityVms);

        verify(stockHistoryRepository, times(1)).saveAll(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEmpty();
    }


    @Test
    void testCreateStockHistories_whenStockQuantityVmNotNull_saveStockHistories() {


        List<Stock> stocks = getStocks();

        StockQuantityVm stockQuantityVm1 = new StockQuantityVm(1L, 10L, "Initial stock");
        StockQuantityVm stockQuantityVm2 = new StockQuantityVm(2L, 20L, "Restock");
        List<StockQuantityVm> stockQuantityVms = List.of(stockQuantityVm1, stockQuantityVm2);

        ArgumentCaptor<List<StockHistory>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        stockHistoryService.createStockHistories(stocks, stockQuantityVms);

        verify(stockHistoryRepository, times(1)).saveAll(argumentCaptor.capture());

        StockHistory stockHistory = argumentCaptor.getValue().getFirst();

        assertThat(stockHistory.getProductId()).isEqualTo(1L);
        assertThat(stockHistory.getAdjustedQuantity()).isEqualTo(10L);
        assertThat(stockHistory.getNote()).isEqualTo("Initial stock");
        assertThat(stockHistory.getWarehouse().getId()).isEqualTo(1L);

        StockHistory stockHistory2 = argumentCaptor.getValue().getLast();
        assertThat(stockHistory2.getProductId()).isEqualTo(2L);
        assertThat(stockHistory2.getAdjustedQuantity()).isEqualTo(20L);
        assertThat(stockHistory2.getNote()).isEqualTo("Restock");
        assertThat(stockHistory2.getWarehouse().getId()).isEqualTo(1L);
    }

    @Test
    void testGetStockHistories_whenStockQuantityVmNotNull_returnStockHistoryListVm() {

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);

        StockHistory stockHistory = StockHistory.builder()
            .id(1L)
            .productId(1L)
            .adjustedQuantity(10L)
            .note("Initial stock")
            .warehouse(warehouse)
            .build();

        List<StockHistory> stockHistories = List.of(stockHistory);
        ProductInfoVm productInfoVm = new ProductInfoVm(1L, "Product Name", "Abc", true);

        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(1L, 1L))
            .thenReturn(stockHistories);
        when(productService.getProduct(1L)).thenReturn(productInfoVm);

        StockHistoryListVm result = stockHistoryService.getStockHistories(1L, 1L);

        assertEquals(1, result.data().size());
        StockHistoryVm stockHistoryVm = result.data().getFirst();
        assertEquals(1L, stockHistoryVm.id());
        assertEquals(10, stockHistoryVm.adjustedQuantity());
        assertEquals("Initial stock", stockHistoryVm.note());
    }

    private static List<Stock> getStocks() {

        Stock stock1 = new Stock();
        stock1.setId(1L);
        stock1.setProductId(1L);
        stock1.setQuantity(10L);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Warehouse B");
        warehouse.setAddressId(456L);
        stock1.setWarehouse(warehouse);

        Stock stock2 = new Stock();
        stock2.setId(2L);
        stock2.setProductId(2L);
        stock2.setQuantity(20L);
        stock2.setWarehouse(warehouse);

        return List.of(stock1, stock2);
    }

}

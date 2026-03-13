package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
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

    private Warehouse buildWarehouse(Long id, String name) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setName(name);
        return warehouse;
    }

    private Stock buildStock(Long id, Long productId, Long quantity, Warehouse warehouse) {
        return Stock.builder()
            .id(id)
            .productId(productId)
            .quantity(quantity)
            .reservedQuantity(0L)
            .warehouse(warehouse)
            .build();
    }

    @Test
    void testCreateStockHistories_whenNormalCase_savesStockHistories() {
        Warehouse warehouse = buildWarehouse(1L, "Warehouse A");
        Stock stock = buildStock(1L, 100L, 50L, warehouse);
        StockQuantityVm stockQuantityVm = new StockQuantityVm(1L, 10L, "Restock");

        ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
        stockHistoryService.createStockHistories(List.of(stock), List.of(stockQuantityVm));

        verify(stockHistoryRepository).saveAll(captor.capture());
        List<StockHistory> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getProductId()).isEqualTo(100L);
        assertThat(saved.get(0).getAdjustedQuantity()).isEqualTo(10L);
        assertThat(saved.get(0).getNote()).isEqualTo("Restock");
    }

    @Test
    void testCreateStockHistories_whenMultipleStocks_savesAllMatchingHistories() {
        Warehouse warehouse = buildWarehouse(1L, "Warehouse A");
        Stock stock1 = buildStock(1L, 100L, 50L, warehouse);
        Stock stock2 = buildStock(2L, 200L, 30L, warehouse);
        StockQuantityVm quantityVm1 = new StockQuantityVm(1L, 5L, "Note 1");
        StockQuantityVm quantityVm2 = new StockQuantityVm(2L, 15L, "Note 2");

        ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
        stockHistoryService.createStockHistories(List.of(stock1, stock2), List.of(quantityVm1, quantityVm2));

        verify(stockHistoryRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    void testCreateStockHistories_whenStockHasNoMatchingQuantityVm_skipsStock() {
        Warehouse warehouse = buildWarehouse(1L, "Warehouse A");
        Stock stock = buildStock(1L, 100L, 50L, warehouse);
        StockQuantityVm stockQuantityVm = new StockQuantityVm(999L, 10L, "Note");

        ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
        stockHistoryService.createStockHistories(List.of(stock), List.of(stockQuantityVm));

        verify(stockHistoryRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).isEmpty();
    }

    @Test
    void testCreateStockHistories_whenEmptyLists_savesEmptyList() {
        ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
        stockHistoryService.createStockHistories(List.of(), List.of());

        verify(stockHistoryRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).isEmpty();
    }

    @Test
    void testGetStockHistories_whenNormalCase_returnsStockHistoryListVm() {
        Long productId = 100L;
        Long warehouseId = 1L;
        Warehouse warehouse = buildWarehouse(warehouseId, "Warehouse A");
        StockHistory stockHistory = StockHistory.builder()
            .id(1L)
            .productId(productId)
            .adjustedQuantity(10L)
            .note("test note")
            .warehouse(warehouse)
            .build();
        ProductInfoVm productInfoVm = new ProductInfoVm(productId, "Product A", "SKU-A", true);

        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(productId, warehouseId))
            .thenReturn(List.of(stockHistory));
        when(productService.getProduct(productId)).thenReturn(productInfoVm);

        StockHistoryListVm result = stockHistoryService.getStockHistories(productId, warehouseId);

        assertThat(result).isNotNull();
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).productName()).isEqualTo("Product A");
        assertThat(result.data().get(0).adjustedQuantity()).isEqualTo(10L);
        assertThat(result.data().get(0).note()).isEqualTo("test note");
    }

    @Test
    void testGetStockHistories_whenNoHistoryFound_returnsEmptyList() {
        Long productId = 100L;
        Long warehouseId = 1L;
        ProductInfoVm productInfoVm = new ProductInfoVm(productId, "Product A", "SKU-A", true);

        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(productId, warehouseId))
            .thenReturn(List.of());
        when(productService.getProduct(productId)).thenReturn(productInfoVm);

        StockHistoryListVm result = stockHistoryService.getStockHistories(productId, warehouseId);

        assertThat(result).isNotNull();
        assertThat(result.data()).isEmpty();
    }
}

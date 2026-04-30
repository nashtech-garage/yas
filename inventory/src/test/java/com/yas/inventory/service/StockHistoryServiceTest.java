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
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @Test
    void createStockHistories_shouldPersistOnlyMatchingStocks() {
        Stock stock1 = stock(1L, 100L, 10L);
        Stock stock2 = stock(2L, 200L, 10L);
        List<StockQuantityVm> updates = List.of(
                new StockQuantityVm(1L, 5L, "add"),
                new StockQuantityVm(99L, 1L, "ignore"));

        stockHistoryService.createStockHistories(List.of(stock1, stock2), updates);

        ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
        verify(stockHistoryRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().getFirst().getProductId()).isEqualTo(100L);
    }

    @Test
    void getStockHistories_shouldReturnMappedVm() {
        StockHistory history = StockHistory.builder()
                .id(1L)
                .productId(100L)
                .adjustedQuantity(5L)
                .note("note")
                .warehouse(warehouse(10L))
                .build();
        history.setCreatedBy("user-1");
        history.setCreatedOn(ZonedDateTime.now());

        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(100L, 10L))
                .thenReturn(List.of(history));
        when(productService.getProduct(100L)).thenReturn(new ProductInfoVm(100L, "Product", "SKU", true));

        StockHistoryListVm result = stockHistoryService.getStockHistories(100L, 10L);

        assertThat(result.data()).hasSize(1);
        assertThat(result.data().getFirst().productName()).isEqualTo("Product");
    }

    private static Stock stock(Long id, Long productId, Long warehouseId) {
        Stock stock = new Stock();
        stock.setId(id);
        stock.setProductId(productId);
        stock.setWarehouse(warehouse(warehouseId));
        stock.setQuantity(0L);
        stock.setReservedQuantity(0L);
        return stock;
    }

    private static Warehouse warehouse(Long id) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setName("W");
        warehouse.setAddressId(1L);
        return warehouse;
    }
}

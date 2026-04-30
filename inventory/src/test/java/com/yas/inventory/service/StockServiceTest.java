package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.StockExistingException;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.product.ProductQuantityPostVm;
import com.yas.inventory.viewmodel.stock.StockPostVm;
import com.yas.inventory.viewmodel.stock.StockQuantityUpdateVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stock.StockVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private ProductService productService;
    @Mock
    private WarehouseService warehouseService;
    @Mock
    private StockHistoryService stockHistoryService;

    @InjectMocks
    private StockService stockService;

    @Test
    void addProductIntoWarehouse_whenValid_shouldSaveStocks() {
        StockPostVm postVm = new StockPostVm(100L, 1L);
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 100L)).thenReturn(false);
        when(productService.getProduct(100L)).thenReturn(new ProductInfoVm(100L, "P1", "S1", true));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse(1L)));

        stockService.addProductIntoWarehouse(List.of(postVm));

        ArgumentCaptor<List<Stock>> captor = ArgumentCaptor.forClass(List.class);
        verify(stockRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().getFirst().getProductId()).isEqualTo(100L);
    }

    @Test
    void addProductIntoWarehouse_whenStockExists_shouldThrow() {
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 100L)).thenReturn(true);

        assertThrows(
                StockExistingException.class,
                () -> stockService.addProductIntoWarehouse(List.of(new StockPostVm(100L, 1L))));
    }

    @Test
    void addProductIntoWarehouse_whenProductMissing_shouldThrow() {
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 100L)).thenReturn(false);
        when(productService.getProduct(100L)).thenReturn(null);

        assertThrows(
                NotFoundException.class,
                () -> stockService.addProductIntoWarehouse(List.of(new StockPostVm(100L, 1L))));
    }

    @Test
    void addProductIntoWarehouse_whenWarehouseMissing_shouldThrow() {
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 100L)).thenReturn(false);
        when(productService.getProduct(100L)).thenReturn(new ProductInfoVm(100L, "P1", "S1", true));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> stockService.addProductIntoWarehouse(List.of(new StockPostVm(100L, 1L))));
    }

    @Test
    void getStocksByWarehouseIdAndProductNameAndSku_shouldReturnMappedStocks() {
        when(warehouseService.getProductWarehouse(1L, "p", "s", com.yas.inventory.model.enumeration.FilterExistInWhSelection.YES))
                .thenReturn(List.of(new ProductInfoVm(100L, "P1", "S1", true)));
        when(stockRepository.findByWarehouseIdAndProductIdIn(1L, List.of(100L)))
                .thenReturn(List.of(stock(1L, 100L, 1L, 5L)));

        List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(1L, "p", "s");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().productName()).isEqualTo("P1");
    }

    @Test
    void updateProductQuantityInStock_whenStocksFound_shouldSaveAndSync() {
        Stock stock = stock(1L, 100L, 1L, 10L);
        List<StockQuantityVm> updateList = List.of(new StockQuantityVm(1L, 3L, "add"));
        when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock));

        stockService.updateProductQuantityInStock(new StockQuantityUpdateVm(updateList));

        assertThat(stock.getQuantity()).isEqualTo(13L);
        verify(stockRepository).saveAll(List.of(stock));
        verify(stockHistoryService).createStockHistories(List.of(stock), updateList);
        verify(productService).updateProductQuantity(List.of(new ProductQuantityPostVm(100L, 13L)));
    }

    @Test
    void updateProductQuantityInStock_whenNoStocks_shouldNotSyncProduct() {
        List<StockQuantityVm> updateList = List.of(new StockQuantityVm(1L, 3L, "add"));
        when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of());

        stockService.updateProductQuantityInStock(new StockQuantityUpdateVm(updateList));

        verify(stockRepository).saveAll(List.of());
        verify(stockHistoryService).createStockHistories(List.of(), updateList);
        verify(productService, never()).updateProductQuantity(any());
    }

    private static Warehouse warehouse(Long id) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setName("W1");
        warehouse.setAddressId(11L);
        return warehouse;
    }

    private static Stock stock(Long id, Long productId, Long warehouseId, Long quantity) {
        Stock stock = new Stock();
        stock.setId(id);
        stock.setProductId(productId);
        stock.setWarehouse(warehouse(warehouseId));
        stock.setQuantity(quantity);
        stock.setReservedQuantity(0L);
        return stock;
    }
}

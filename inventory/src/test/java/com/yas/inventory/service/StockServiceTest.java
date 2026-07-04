package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.StockExistingException;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockPostVm;
import com.yas.inventory.viewmodel.stock.StockQuantityUpdateVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stock.StockVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockServiceTest {

    private WarehouseRepository warehouseRepository;
    private StockRepository stockRepository;
    private ProductService productService;
    private WarehouseService warehouseService;
    private StockHistoryService stockHistoryService;
    private StockService stockService;

    @BeforeEach
    void setUp() {
        warehouseRepository = mock(WarehouseRepository.class);
        stockRepository = mock(StockRepository.class);
        productService = mock(ProductService.class);
        warehouseService = mock(WarehouseService.class);
        stockHistoryService = mock(StockHistoryService.class);
        stockService = new StockService(warehouseRepository, stockRepository, productService, warehouseService, stockHistoryService);
    }

    @Test
    void addProductIntoWarehouse_whenExists_shouldThrowStockExistingException() {
        StockPostVm postVm = new StockPostVm(1L, 1L);
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(true);
        List<StockPostVm> postVms = List.of(postVm);
        assertThrows(StockExistingException.class, () -> stockService.addProductIntoWarehouse(postVms));
    }

    @Test
    void addProductIntoWarehouse_whenProductNotFound_shouldThrowNotFoundException() {
        StockPostVm postVm = new StockPostVm(1L, 1L);
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(null);
        List<StockPostVm> postVms = List.of(postVm);
        assertThrows(NotFoundException.class, () -> stockService.addProductIntoWarehouse(postVms));
    }

    @Test
    void addProductIntoWarehouse_whenWarehouseNotFound_shouldThrowNotFoundException() {
        StockPostVm postVm = new StockPostVm(1L, 1L);
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(new ProductInfoVm(1L, "P1", "S1", false));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());
        List<StockPostVm> postVms = List.of(postVm);
        assertThrows(NotFoundException.class, () -> stockService.addProductIntoWarehouse(postVms));
    }

    @Test
    void addProductIntoWarehouse_whenValid_shouldSaveAll() {
        StockPostVm postVm = new StockPostVm(1L, 1L);
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(new ProductInfoVm(1L, "P1", "S1", false));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(new Warehouse()));
        
        stockService.addProductIntoWarehouse(List.of(postVm));

        verify(stockRepository).saveAll(any());
    }

    @Test
    void getStocksByWarehouseIdAndProductNameAndSku_shouldReturnList() {
        ProductInfoVm productInfoVm = new ProductInfoVm(1L, "P1", "S1", true);
        when(warehouseService.getProductWarehouse(1L, "P", "S", FilterExistInWhSelection.YES)).thenReturn(List.of(productInfoVm));
        
        Stock stock = Stock.builder().productId(1L).quantity(10L).reservedQuantity(2L).warehouse(new Warehouse()).build();
        when(stockRepository.findByWarehouseIdAndProductIdIn(any(), any())).thenReturn(List.of(stock));

        List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(1L, "P", "S");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).productId()).isEqualTo(1L);
    }


    @Test
    void updateProductQuantityInStock_whenValid_shouldUpdateAndSave() {
        Stock stock = new Stock();
        stock.setId(1L);
        stock.setQuantity(10L);
        stock.setProductId(100L);
        when(stockRepository.findAllById(any())).thenReturn(List.of(stock));
        
        StockQuantityVm vm = new StockQuantityVm(1L, 5L, "Note");
        StockQuantityUpdateVm request = new StockQuantityUpdateVm(List.of(vm));

        stockService.updateProductQuantityInStock(request);

        assertThat(stock.getQuantity()).isEqualTo(15L);
        verify(stockRepository).saveAll(any());
        verify(productService).updateProductQuantity(any());
    }
}

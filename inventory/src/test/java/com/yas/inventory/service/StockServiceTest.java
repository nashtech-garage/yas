package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
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
    void addProductIntoWarehouse_whenNormalCase_shouldSaveAll() {
        StockPostVm postVm = new StockPostVm(100L, 1L);
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 100L)).thenReturn(false);
        when(productService.getProduct(100L)).thenReturn(new ProductInfoVm(100L, "Product", "SKU", true));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(new Warehouse()));

        stockService.addProductIntoWarehouse(List.of(postVm));

        verify(stockRepository, times(1)).saveAll(anyList());
    }

    @Test
    void addProductIntoWarehouse_whenStockExists_shouldThrowException() {
        StockPostVm postVm = new StockPostVm(100L, 1L);
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 100L)).thenReturn(true);

        assertThrows(StockExistingException.class, () -> stockService.addProductIntoWarehouse(List.of(postVm)));
    }

    @Test
    void addProductIntoWarehouse_whenProductNotFound_shouldThrowException() {
        StockPostVm postVm = new StockPostVm(100L, 1L);
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 100L)).thenReturn(false);
        when(productService.getProduct(100L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> stockService.addProductIntoWarehouse(List.of(postVm)));
    }

    @Test
    void getStocksByWarehouseIdAndProductNameAndSku_whenNormalCase_returnList() {
        Long warehouseId = 1L;
        ProductInfoVm productInfoVm = new ProductInfoVm(100L, "Product", "SKU", true);
        when(warehouseService.getProductWarehouse(eq(warehouseId), anyString(), anyString(), eq(FilterExistInWhSelection.YES)))
            .thenReturn(List.of(productInfoVm));

        Warehouse warehouse = new Warehouse();
        warehouse.setId(warehouseId);
        Stock stock = Stock.builder().id(1L).productId(100L).warehouse(warehouse).quantity(10L).reservedQuantity(1L).build();
        when(stockRepository.findByWarehouseIdAndProductIdIn(eq(warehouseId), anyList())).thenReturn(List.of(stock));

        List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(warehouseId, "Product", "SKU");

        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).productId());
    }

    @Test
    void updateProductQuantityInStock_whenNormalCase_shouldUpdateAndSave() {
        StockQuantityVm quantityVm = new StockQuantityVm(1L, 10L, "Note");
        StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(quantityVm));

        Stock stock = Stock.builder().id(1L).productId(100L).quantity(50L).build();
        when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));

        stockService.updateProductQuantityInStock(updateVm);

        assertEquals(60L, stock.getQuantity());
        verify(stockRepository, times(1)).saveAll(anyList());
        verify(stockHistoryService, times(1)).createStockHistories(anyList(), anyList());
        verify(productService, times(1)).updateProductQuantity(anyList());
    }

    @Test
    void updateProductQuantityInStock_whenInvalidQuantity_shouldThrowException() {
        StockQuantityVm quantityVm = new StockQuantityVm(1L, -100L, "Note");
        StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(quantityVm));

        Stock stock = Stock.builder().id(1L).productId(100L).quantity(50L).build();
        when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));

        assertThrows(BadRequestException.class, () -> stockService.updateProductQuantityInStock(updateVm));
    }
}

package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.StockExistingException;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
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
import org.mockito.ArgumentCaptor;

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
        stockService = new StockService(
            warehouseRepository,
            stockRepository,
            productService,
            warehouseService,
            stockHistoryService
        );
    }

    @Test
    void testAddProductIntoWarehouse_whenInputValid_saveNewStock() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("WH A").addressId(5L).build();
        StockPostVm stockPostVm = new StockPostVm(2L, 1L);
        ArgumentCaptor<List<Stock>> stockCaptor = ArgumentCaptor.forClass(List.class);

        when(stockRepository.existsByWarehouseIdAndProductId(1L, 2L)).thenReturn(false);
        when(productService.getProduct(2L)).thenReturn(new ProductInfoVm(2L, "Keyboard", "K1", true));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(stockRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        stockService.addProductIntoWarehouse(List.of(stockPostVm));

        verify(stockRepository).saveAll(stockCaptor.capture());
        assertEquals(1, stockCaptor.getValue().size());
        assertEquals(2L, stockCaptor.getValue().getFirst().getProductId());
        assertEquals(0L, stockCaptor.getValue().getFirst().getQuantity());
    }

    @Test
    void testAddProductIntoWarehouse_whenStockAlreadyExists_throwStockExistingException() {
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 2L)).thenReturn(true);

        StockExistingException exception = assertThrows(
            StockExistingException.class,
            () -> stockService.addProductIntoWarehouse(List.of(new StockPostVm(2L, 1L)))
        );

        assertThat(exception).hasMessageContaining("2");
        verify(productService, never()).getProduct(anyLong());
    }

    @Test
    void testAddProductIntoWarehouse_whenProductNotFound_throwNotFoundException() {
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 2L)).thenReturn(false);
        when(productService.getProduct(2L)).thenReturn(null);

        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> stockService.addProductIntoWarehouse(List.of(new StockPostVm(2L, 1L)))
        );

        assertThat(exception).hasMessageContaining("2");
    }

    @Test
    void testGetStocksByWarehouseIdAndProductNameAndSku_whenInputValid_returnMappedStockVms() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("WH A").addressId(5L).build();
        Stock stock = Stock.builder()
            .id(7L)
            .productId(2L)
            .quantity(10L)
            .reservedQuantity(1L)
            .warehouse(warehouse)
            .build();
        ProductInfoVm productInfoVm = new ProductInfoVm(2L, "Keyboard", "K1", true);

        when(warehouseService.getProductWarehouse(anyLong(), anyString(), anyString(), any()))
            .thenReturn(List.of(productInfoVm));
        when(stockRepository.findByWarehouseIdAndProductIdIn(1L, List.of(2L))).thenReturn(List.of(stock));

        List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(1L, "Key", "K1");

        assertEquals(1, result.size());
        assertEquals(2L, result.getFirst().productId());
        assertEquals("Keyboard", result.getFirst().productName());
    }

    @Test
    void testUpdateProductQuantityInStock_whenInputValid_updateStockAndNotifyDependencies() {
        Stock stock = Stock.builder().id(1L).productId(2L).quantity(10L).build();
        StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(new StockQuantityVm(1L, 5L, "Restock")));

        when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock));
        when(stockRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(stockHistoryService).createStockHistories(anyList(), anyList());
        doNothing().when(productService).updateProductQuantity(anyList());

        stockService.updateProductQuantityInStock(requestBody);

        assertEquals(15L, stock.getQuantity());
        verify(stockRepository).saveAll(List.of(stock));
        verify(stockHistoryService).createStockHistories(List.of(stock), requestBody.stockQuantityList());
        verify(productService).updateProductQuantity(anyList());
    }

    @Test
    void testUpdateProductQuantityInStock_whenAdjustedQuantityInvalid_throwBadRequestException() {
        Stock stock = Stock.builder().id(1L).productId(2L).quantity(-15L).build();
        StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(new StockQuantityVm(1L, -10L, "Deduct")));

        when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock));

        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> stockService.updateProductQuantityInStock(requestBody)
        );

        assertThat(exception).hasMessageContaining("Invalid adjusted quantity");
        verify(stockRepository, never()).saveAll(anyList());
    }
}
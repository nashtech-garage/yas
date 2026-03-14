package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
import org.junit.jupiter.api.extension.ExtendWith;
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

    private StockPostVm stockPostVm;
    private Warehouse warehouse;
    private ProductInfoVm productInfoVm;
    private Stock stock;

    @BeforeEach
    void setUp() {
        stockPostVm = new StockPostVm(1L, 1L);
        warehouse = Warehouse.builder().name("Warehouse").addressId(1L).build();
        warehouse.setId(1L);
        productInfoVm = new ProductInfoVm(1L, "Product", "SKU", true);
        stock = Stock.builder().productId(1L).warehouse(warehouse).quantity(10L).reservedQuantity(0L).build();
        stock.setId(1L);
    }

    @Test
    void addProductIntoWarehouse_shouldThrowStockExistingException_whenExists() {
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(true);

        assertThrows(StockExistingException.class, () -> stockService.addProductIntoWarehouse(List.of(stockPostVm)));
    }

    @Test
    void addProductIntoWarehouse_shouldThrowNotFoundException_whenProductNull() {
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> stockService.addProductIntoWarehouse(List.of(stockPostVm)));
    }

    @Test
    void addProductIntoWarehouse_shouldThrowNotFoundException_whenWarehouseEmpty() {
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(productInfoVm);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> stockService.addProductIntoWarehouse(List.of(stockPostVm)));
    }

    @Test
    void addProductIntoWarehouse_shouldSaveStock() {
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(productInfoVm);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        stockService.addProductIntoWarehouse(List.of(stockPostVm));

        verify(stockRepository).saveAll(anyList());
    }

    @Test
    void getStocksByWarehouseIdAndProductNameAndSku_shouldReturnStocks() {
        when(warehouseService.getProductWarehouse(1L, "Product", "SKU", FilterExistInWhSelection.YES))
            .thenReturn(List.of(productInfoVm));
        when(stockRepository.findByWarehouseIdAndProductIdIn(eq(1L), anyList())).thenReturn(List.of(stock));

        List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(1L, "Product", "SKU");

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).productId());
    }

    @Test
    void updateProductQuantityInStock_shouldUpdateQuantity() {
        StockQuantityVm sqVm = new StockQuantityVm(1L, 5L, "Note");
        StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(sqVm));

        when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock));

        stockService.updateProductQuantityInStock(requestBody);

        assertEquals(15L, stock.getQuantity());
        verify(stockRepository).saveAll(anyList());
        verify(stockHistoryService).createStockHistories(anyList(), anyList());
        verify(productService).updateProductQuantity(anyList());
    }

    @Test
    void updateProductQuantityInStock_shouldSkip_whenVmNotFound() {
        StockQuantityVm sqVm = new StockQuantityVm(2L, 5L, "Note"); // Unmatched ID
        StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(sqVm));

        when(stockRepository.findAllById(List.of(2L))).thenReturn(List.of(stock)); // Mock returns stock ID 1

        stockService.updateProductQuantityInStock(requestBody);

        assertEquals(10L, stock.getQuantity()); // Unchanged
        verify(stockRepository).saveAll(anyList());
    }
}

package com.yas.inventory.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

    // =========================================================================
    // TEST CHO HÀM addProductIntoWarehouse
    // =========================================================================

    @Test
    void addProductIntoWarehouse_WhenStockExists_ThrowsException() {
        StockPostVm postVm = mock(StockPostVm.class);
        when(postVm.warehouseId()).thenReturn(1L);
        when(postVm.productId()).thenReturn(2L);

        when(stockRepository.existsByWarehouseIdAndProductId(1L, 2L)).thenReturn(true);

        assertThrows(StockExistingException.class, () -> stockService.addProductIntoWarehouse(List.of(postVm)));
    }

    @Test
    void addProductIntoWarehouse_WhenProductNotFound_ThrowsException() {
        StockPostVm postVm = mock(StockPostVm.class);
        when(postVm.warehouseId()).thenReturn(1L);
        when(postVm.productId()).thenReturn(2L);

        when(stockRepository.existsByWarehouseIdAndProductId(1L, 2L)).thenReturn(false);
        when(productService.getProduct(2L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> stockService.addProductIntoWarehouse(List.of(postVm)));
    }

    @Test
    void addProductIntoWarehouse_WhenWarehouseNotFound_ThrowsException() {
        StockPostVm postVm = mock(StockPostVm.class);
        when(postVm.warehouseId()).thenReturn(1L);
        when(postVm.productId()).thenReturn(2L);

        when(stockRepository.existsByWarehouseIdAndProductId(1L, 2L)).thenReturn(false);
        when(productService.getProduct(2L)).thenReturn(mock(ProductInfoVm.class));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> stockService.addProductIntoWarehouse(List.of(postVm)));
    }

    @Test
    void addProductIntoWarehouse_Success() {
        StockPostVm postVm = mock(StockPostVm.class);
        when(postVm.warehouseId()).thenReturn(1L);
        when(postVm.productId()).thenReturn(2L);

        when(stockRepository.existsByWarehouseIdAndProductId(1L, 2L)).thenReturn(false);
        when(productService.getProduct(2L)).thenReturn(mock(ProductInfoVm.class));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(new Warehouse()));

        stockService.addProductIntoWarehouse(List.of(postVm));

        verify(stockRepository).saveAll(anyList());
    }

    // =========================================================================
    // TEST CHO HÀM getStocksByWarehouseIdAndProductNameAndSku
    // =========================================================================

@Test
    void getStocksByWarehouseIdAndProductNameAndSku_Success() {
        ProductInfoVm productInfoVm = new ProductInfoVm(1L, "Product 1", "SKU1", true);
        
        when(warehouseService.getProductWarehouse(1L, "Name", "SKU", FilterExistInWhSelection.YES))
                .thenReturn(List.of(productInfoVm));

        // TẠO WAREHOUSE GIẢ ĐỂ TRÁNH LỖI NULL POINTER
        Warehouse mockWarehouse = new Warehouse();
        mockWarehouse.setId(1L);

        Stock stock = new Stock();
        stock.setProductId(1L);
        stock.setWarehouse(mockWarehouse); // GÁN WAREHOUSE VÀO ĐÂY
        stock.setQuantity(10L);
        stock.setReservedQuantity(2L);

        when(stockRepository.findByWarehouseIdAndProductIdIn(eq(1L), anyList())).thenReturn(List.of(stock));

        List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(1L, "Name", "SKU");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // =========================================================================
    // TEST CHO HÀM updateProductQuantityInStock
    // =========================================================================

    @Test
    void updateProductQuantityInStock_MissingQuantityVm_ContinuesGracefully() {
        StockQuantityUpdateVm requestBody = mock(StockQuantityUpdateVm.class);
        StockQuantityVm vm = mock(StockQuantityVm.class);
        when(vm.stockId()).thenReturn(99L); // ID không khớp với Stock bên dưới
        when(requestBody.stockQuantityList()).thenReturn(List.of(vm));

        Stock stock = new Stock();
        stock.setId(1L); // Stock ID là 1
        stock.setProductId(2L); // Tránh NullPointer khi map sang ProductQuantityPostVm

        when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));

        stockService.updateProductQuantityInStock(requestBody);

        // Vẫn chạy hết hàm và gọi saveAll (nhưng quantity của stock giữ nguyên)
        verify(stockRepository).saveAll(anyList());
    }

    @Test
    void updateProductQuantityInStock_InvalidAdjustedQuantity_ThrowsException() {
        StockQuantityUpdateVm requestBody = mock(StockQuantityUpdateVm.class);
        StockQuantityVm vm = mock(StockQuantityVm.class);
        when(vm.stockId()).thenReturn(1L);
        when(vm.quantity()).thenReturn(-10L);
        when(requestBody.stockQuantityList()).thenReturn(List.of(vm));

        Stock stock = new Stock();
        stock.setId(1L);
        // Thiết lập quantity sao cho thỏa mãn if: adjustedQuantity < 0 (-10 < 0) VÀ adjustedQuantity > stock.getQuantity() (-10 > -20)
        stock.setQuantity(-20L);

        when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));

        assertThrows(BadRequestException.class, () -> stockService.updateProductQuantityInStock(requestBody));
    }

    @Test
    void updateProductQuantityInStock_Success() {
        StockQuantityUpdateVm requestBody = mock(StockQuantityUpdateVm.class);
        StockQuantityVm vm = mock(StockQuantityVm.class);
        when(vm.stockId()).thenReturn(1L);
        when(vm.quantity()).thenReturn(15L);
        when(requestBody.stockQuantityList()).thenReturn(List.of(vm));

        Stock stock = new Stock();
        stock.setId(1L);
        stock.setProductId(2L);
        stock.setQuantity(5L);

        when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));

        stockService.updateProductQuantityInStock(requestBody);

        assertEquals(20L, stock.getQuantity()); // 5 + 15 = 20
        verify(stockRepository).saveAll(anyList());
        verify(stockHistoryService).createStockHistories(anyList(), anyList());
        verify(productService).updateProductQuantity(anyList());
    }
}
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
import static org.mockito.Mockito.times;
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

class StockServiceIT {

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
        stockService = new StockService(warehouseRepository, stockRepository,
            productService, warehouseService, stockHistoryService);
    }

    @Test
    void testAddProductIntoWarehouse_whenNormalCase_shouldSavingSuccess() {

        Long warehouseId = 1L;
        Long productId = 1L;

        Warehouse warehouse = new Warehouse();
        warehouse.setId(warehouseId);

        ProductInfoVm productInfoVm = new ProductInfoVm(productId, "Product", "SKU", true);

        when(stockRepository.existsByWarehouseIdAndProductId(warehouseId, productId)).thenReturn(false);
        when(productService.getProduct(productId)).thenReturn(productInfoVm);
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
        when(stockRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArguments()[0]);
        StockPostVm stockPostVm = new StockPostVm(warehouseId, productId);
        stockService.addProductIntoWarehouse(List.of(stockPostVm));

        verify(stockRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testAddProductIntoWarehouse_whenExistingInStockIsTrue_throwsStockExistingException() {

        Long warehouseId = 1L;
        Long productId = 1L;
        StockPostVm stockPostVm = new StockPostVm(warehouseId, productId);

        when(stockRepository.existsByWarehouseIdAndProductId(warehouseId, productId)).thenReturn(true);

        try {
            stockService.addProductIntoWarehouse(List.of(stockPostVm));
        } catch (Exception e) {
            assertThat(e).isInstanceOf(StockExistingException.class);
            assertEquals("The product id 1 already existing warehouse.", e.getMessage());
        }

    }

    @Test
    void testAddProductIntoWarehouse_whenNotFound_throwNotFoundException() {

        Long warehouseId = 1L;
        Long productId = 1L;
        StockPostVm stockPostVm = new StockPostVm(warehouseId, productId);

        when(stockRepository.existsByWarehouseIdAndProductId(warehouseId, productId)).thenReturn(false);
        when(productService.getProduct(productId)).thenReturn(null);

        try {
            stockService.addProductIntoWarehouse(List.of(stockPostVm));
        } catch (Exception e) {
            assertThat(e).isInstanceOf(NotFoundException.class);
            assertEquals("The product 1 is not found", e.getMessage());
        }
    }

    @Test
    void testAddProductIntoWarehouse_whenWarehouseNotFound_throwNotFoundException() {

        Long warehouseId = 1L;
        Long productId = 1L;
        StockPostVm stockPostVm = new StockPostVm(warehouseId, productId);

        when(stockRepository.existsByWarehouseIdAndProductId(warehouseId, productId)).thenReturn(false);
        when(productService.getProduct(productId)).thenReturn(new ProductInfoVm(productId, "Product", "SKU", true));
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        try {
            stockService.addProductIntoWarehouse(List.of(stockPostVm));
        } catch (Exception e) {
            assertThat(e).isInstanceOf(NotFoundException.class);
            assertEquals("The warehouse 1 is not found", e.getMessage());
        }
    }

    @Test
    void testGetStocksByWarehouseIdAndProductNameAndSku_whenNormalCase_returnListStockVm() {

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);

        warehouse.setName("Warehouse B");
        warehouse.setAddressId(456L);
        Stock stock = new Stock();
        stock.setProductId(1L);
        stock.setQuantity(10L);
        stock.setWarehouse(warehouse);

        Long warehouseId = 1L;
        String productName = "Product";
        String productSku = "SKU";
        ProductInfoVm productInfoVm = new ProductInfoVm(1L, productName, productSku, true);

        when(warehouseService.getProductWarehouse(anyLong(), anyString(), anyString(), any()))
            .thenReturn(List.of(productInfoVm));
        when(stockRepository.findByWarehouseIdAndProductIdIn(anyLong(), anyList())).thenReturn(List.of(stock));

        List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(warehouseId,
            productName, productSku);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().productId());
    }

    @Test
    void testUpdateProductQuantityInStock_whenNormalCase_shouldSavingSuccess() {

        Long stockId = 1L;
        Stock stock = new Stock();
        stock.setId(stockId);
        stock.setQuantity(10L);

        when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));
        when(stockRepository.saveAllAndFlush(anyList())).thenAnswer(invocation -> invocation.getArguments()[0]);
        doNothing().when(stockHistoryService).createStockHistories(anyList(), anyList());
        Long quantity = 5L;
        doNothing().when(productService).updateProductQuantity(anyList());
        StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(
            List.of(new StockQuantityVm(stockId, quantity, ""))
        );
        stockService.updateProductQuantityInStock(requestBody);

        assertEquals(15L, stock.getQuantity());
        verify(stockHistoryService, times(1)).createStockHistories(anyList(), anyList());
    }

    @Test
    void testUpdateProductQuantityInStock_isInvalidAdjustedQuantity_throwBadRequestException() {

        Long stockId = 1L;
        Stock stock = new Stock();
        stock.setId(stockId);
        stock.setQuantity(-15L);

        when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));
        Long quantity = -10L;
        StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(
            List.of(new StockQuantityVm(stockId, quantity, ""))
        );

        BadRequestException thrown = assertThrows(BadRequestException.class, () ->
            stockService.updateProductQuantityInStock(requestBody)
        );

        assertEquals("Invalid adjusted quantity make a negative quantity", thrown.getMessage());
    }
}

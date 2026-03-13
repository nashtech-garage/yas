package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import org.mockito.ArgumentCaptor;

class StockServiceTest {

    private WarehouseRepository warehouseRepository;

    private StockRepository stockRepository;

    private ProductService productService;

    private WarehouseService warehouseService;

    private StockHistoryService stockHistoryService;

    private StockService stockService;

    private static final Long WAREHOUSE_ID = 1L;

    private static final Long PRODUCT_ID = 100L;

    @BeforeEach
    void setUp() {
        warehouseRepository = mock(WarehouseRepository.class);
        stockRepository = mock(StockRepository.class);
        productService = mock(ProductService.class);
        warehouseService = mock(WarehouseService.class);
        stockHistoryService = mock(StockHistoryService.class);
        stockService = new StockService(
            warehouseRepository, stockRepository, productService, warehouseService, stockHistoryService);
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
    void testAddProductIntoWarehouse_whenNormalCase_savesStock() {
        StockPostVm postVm = new StockPostVm(PRODUCT_ID, WAREHOUSE_ID);
        ProductInfoVm productInfoVm = new ProductInfoVm(PRODUCT_ID, "Product A", "SKU-A", false);
        Warehouse warehouse = buildWarehouse(WAREHOUSE_ID, "Warehouse A");

        when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, PRODUCT_ID)).thenReturn(false);
        when(productService.getProduct(PRODUCT_ID)).thenReturn(productInfoVm);
        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse));

        assertDoesNotThrow(() -> stockService.addProductIntoWarehouse(List.of(postVm)));

        ArgumentCaptor<List<Stock>> captor = ArgumentCaptor.forClass(List.class);
        verify(stockRepository).saveAll(captor.capture());
        List<Stock> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(saved.get(0).getQuantity()).isZero();
        assertThat(saved.get(0).getReservedQuantity()).isZero();
    }

    @Test
    void testAddProductIntoWarehouse_whenStockAlreadyExists_throwsStockExistingException() {
        StockPostVm postVm = new StockPostVm(PRODUCT_ID, WAREHOUSE_ID);
        when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, PRODUCT_ID)).thenReturn(true);

        StockExistingException thrown = assertThrows(StockExistingException.class,
            () -> stockService.addProductIntoWarehouse(List.of(postVm)));

        assertThat(thrown.getMessage()).contains(String.valueOf(PRODUCT_ID));
    }

    @Test
    void testAddProductIntoWarehouse_whenProductNotFound_throwsNotFoundException() {
        StockPostVm postVm = new StockPostVm(PRODUCT_ID, WAREHOUSE_ID);
        when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, PRODUCT_ID)).thenReturn(false);
        when(productService.getProduct(PRODUCT_ID)).thenReturn(null);

        NotFoundException thrown = assertThrows(NotFoundException.class,
            () -> stockService.addProductIntoWarehouse(List.of(postVm)));

        assertThat(thrown.getMessage()).contains(String.valueOf(PRODUCT_ID));
    }

    @Test
    void testAddProductIntoWarehouse_whenWarehouseNotFound_throwsNotFoundException() {
        StockPostVm postVm = new StockPostVm(PRODUCT_ID, WAREHOUSE_ID);
        ProductInfoVm productInfoVm = new ProductInfoVm(PRODUCT_ID, "Product A", "SKU-A", false);

        when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, PRODUCT_ID)).thenReturn(false);
        when(productService.getProduct(PRODUCT_ID)).thenReturn(productInfoVm);
        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
            () -> stockService.addProductIntoWarehouse(List.of(postVm)));

        assertThat(thrown.getMessage()).contains(String.valueOf(WAREHOUSE_ID));
    }

    @Test
    void testAddProductIntoWarehouse_whenMultipleProducts_savesAllStocks() {
        Long productId2 = 200L;
        Long warehouseId2 = 2L;
        StockPostVm postVm1 = new StockPostVm(PRODUCT_ID, WAREHOUSE_ID);
        StockPostVm postVm2 = new StockPostVm(productId2, warehouseId2);
        ProductInfoVm product1 = new ProductInfoVm(PRODUCT_ID, "Product A", "SKU-A", false);
        ProductInfoVm product2 = new ProductInfoVm(productId2, "Product B", "SKU-B", false);
        Warehouse warehouse1 = buildWarehouse(WAREHOUSE_ID, "Warehouse A");
        Warehouse warehouse2 = buildWarehouse(warehouseId2, "Warehouse B");

        when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, PRODUCT_ID)).thenReturn(false);
        when(stockRepository.existsByWarehouseIdAndProductId(warehouseId2, productId2)).thenReturn(false);
        when(productService.getProduct(PRODUCT_ID)).thenReturn(product1);
        when(productService.getProduct(productId2)).thenReturn(product2);
        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse1));
        when(warehouseRepository.findById(warehouseId2)).thenReturn(Optional.of(warehouse2));

        assertDoesNotThrow(() -> stockService.addProductIntoWarehouse(List.of(postVm1, postVm2)));

        ArgumentCaptor<List<Stock>> captor = ArgumentCaptor.forClass(List.class);
        verify(stockRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    void testGetStocksByWarehouseIdAndProductNameAndSku_whenNormalCase_returnsStockVmList() {
        String productName = "Product A";
        String productSku = "SKU-A";
        Warehouse warehouse = buildWarehouse(WAREHOUSE_ID, "Warehouse A");
        ProductInfoVm productInfoVm = new ProductInfoVm(PRODUCT_ID, productName, productSku, true);
        Stock stock = buildStock(1L, PRODUCT_ID, 50L, warehouse);

        when(warehouseService.getProductWarehouse(WAREHOUSE_ID, productName, productSku, FilterExistInWhSelection.YES))
            .thenReturn(List.of(productInfoVm));
        when(stockRepository.findByWarehouseIdAndProductIdIn(WAREHOUSE_ID, List.of(PRODUCT_ID)))
            .thenReturn(List.of(stock));

        List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(
            WAREHOUSE_ID, productName, productSku);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).productId()).isEqualTo(PRODUCT_ID);
        assertThat(result.get(0).productName()).isEqualTo(productName);
        assertThat(result.get(0).quantity()).isEqualTo(50L);
        assertThat(result.get(0).warehouseId()).isEqualTo(WAREHOUSE_ID);
    }

    @Test
    void testGetStocksByWarehouseIdAndProductNameAndSku_whenNoProducts_returnsEmptyList() {
        when(warehouseService.getProductWarehouse(WAREHOUSE_ID, null, null, FilterExistInWhSelection.YES))
            .thenReturn(List.of());
        when(stockRepository.findByWarehouseIdAndProductIdIn(WAREHOUSE_ID, List.of()))
            .thenReturn(List.of());

        List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(WAREHOUSE_ID, null, null);

        assertThat(result).isEmpty();
    }

    @Test
    void testUpdateProductQuantityInStock_whenNormalCase_updatesQuantityAndSaves() {
        Warehouse warehouse = buildWarehouse(WAREHOUSE_ID, "Warehouse A");
        Stock stock = buildStock(1L, PRODUCT_ID, 50L, warehouse);
        StockQuantityVm quantityVm = new StockQuantityVm(1L, 10L, "Restock");
        StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(quantityVm));

        when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock));

        assertDoesNotThrow(() -> stockService.updateProductQuantityInStock(updateVm));

        assertThat(stock.getQuantity()).isEqualTo(60L);
        verify(stockRepository).saveAll(anyList());
        verify(stockHistoryService).createStockHistories(anyList(), anyList());
        verify(productService).updateProductQuantity(anyList());
    }

    @Test
    void testUpdateProductQuantityInStock_whenNullQuantity_treatsAsZero() {
        Warehouse warehouse = buildWarehouse(WAREHOUSE_ID, "Warehouse A");
        Stock stock = buildStock(1L, PRODUCT_ID, 30L, warehouse);
        StockQuantityVm quantityVm = new StockQuantityVm(1L, null, "Note");
        StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(quantityVm));

        when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock));

        assertDoesNotThrow(() -> stockService.updateProductQuantityInStock(updateVm));

        assertThat(stock.getQuantity()).isEqualTo(30L);
        verify(stockRepository).saveAll(anyList());
    }

    @Test
    void testUpdateProductQuantityInStock_whenInvalidAdjustedQuantity_throwsBadRequestException() {
        Warehouse warehouse = buildWarehouse(WAREHOUSE_ID, "Warehouse A");
        // stock.quantity is negative, and adjustedQuantity is negative but greater (closer to zero)
        Stock stock = buildStock(1L, PRODUCT_ID, -20L, warehouse);
        StockQuantityVm quantityVm = new StockQuantityVm(1L, -5L, "Note");
        StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(quantityVm));

        when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock));

        assertThrows(BadRequestException.class,
            () -> stockService.updateProductQuantityInStock(updateVm));
    }

    @Test
    void testUpdateProductQuantityInStock_whenEmptyStockList_doesNotCallUpdateProductQuantity() {
        StockQuantityVm quantityVm = new StockQuantityVm(999L, 10L, "Note");
        StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(quantityVm));

        when(stockRepository.findAllById(List.of(999L))).thenReturn(List.of());

        assertDoesNotThrow(() -> stockService.updateProductQuantityInStock(updateVm));

        verify(productService, never()).updateProductQuantity(anyList());
    }

    @Test
    void testUpdateProductQuantityInStock_whenStockNotMatchedInQuantityVms_skipsStock() {
        Warehouse warehouse = buildWarehouse(WAREHOUSE_ID, "Warehouse A");
        Stock stock = buildStock(1L, PRODUCT_ID, 50L, warehouse);
        // The stockId in quantityVm does not match stock id
        StockQuantityVm quantityVm = new StockQuantityVm(999L, 10L, "Note");
        StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(quantityVm));

        when(stockRepository.findAllById(List.of(999L))).thenReturn(List.of(stock));

        assertDoesNotThrow(() -> stockService.updateProductQuantityInStock(updateVm));

        // Quantity should remain unchanged
        assertThat(stock.getQuantity()).isEqualTo(50L);
    }
}

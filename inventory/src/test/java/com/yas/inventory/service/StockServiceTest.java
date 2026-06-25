package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
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

    private static final Long PRODUCT_ID = 100L;
    private static final Long WAREHOUSE_ID = 1L;

    @Nested
    class AddProductIntoWarehouseTest {

        @Test
        void testAddProduct_whenStockAlreadyExists_shouldThrowStockExistingException() {
            StockPostVm postVm = new StockPostVm(PRODUCT_ID, WAREHOUSE_ID);
            when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, PRODUCT_ID)).thenReturn(true);

            assertThrows(StockExistingException.class,
                () -> stockService.addProductIntoWarehouse(List.of(postVm)));
        }

        @Test
        void testAddProduct_whenProductNotFound_shouldThrowNotFoundException() {
            StockPostVm postVm = new StockPostVm(PRODUCT_ID, WAREHOUSE_ID);
            when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, PRODUCT_ID)).thenReturn(false);
            when(productService.getProduct(PRODUCT_ID)).thenReturn(null);

            assertThrows(NotFoundException.class,
                () -> stockService.addProductIntoWarehouse(List.of(postVm)));
        }

        @Test
        void testAddProduct_whenWarehouseNotFound_shouldThrowNotFoundException() {
            StockPostVm postVm = new StockPostVm(PRODUCT_ID, WAREHOUSE_ID);
            ProductInfoVm productInfoVm = new ProductInfoVm(PRODUCT_ID, "Product A", "SKU-001", true);

            when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, PRODUCT_ID)).thenReturn(false);
            when(productService.getProduct(PRODUCT_ID)).thenReturn(productInfoVm);
            when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                () -> stockService.addProductIntoWarehouse(List.of(postVm)));
        }

        @Test
        void testAddProduct_whenValidRequest_shouldSaveStock() {
            StockPostVm postVm = new StockPostVm(PRODUCT_ID, WAREHOUSE_ID);
            ProductInfoVm productInfoVm = new ProductInfoVm(PRODUCT_ID, "Product A", "SKU-001", true);
            Warehouse warehouse = new Warehouse();
            warehouse.setId(WAREHOUSE_ID);

            when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, PRODUCT_ID)).thenReturn(false);
            when(productService.getProduct(PRODUCT_ID)).thenReturn(productInfoVm);
            when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse));

            stockService.addProductIntoWarehouse(List.of(postVm));

            verify(stockRepository).saveAll(anyList());
        }
    }

    @Nested
    class UpdateProductQuantityInStockTest {

        @Test
        void testUpdateQuantity_whenValidRequest_shouldUpdateAndSave() {
            Stock stock = Stock.builder()
                .id(1L)
                .productId(PRODUCT_ID)
                .quantity(10L)
                .reservedQuantity(0L)
                .build();

            StockQuantityVm quantityVm = new StockQuantityVm(1L, 5L, "Restock");
            StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(quantityVm));

            when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));
            when(stockRepository.saveAll(anyList())).thenReturn(List.of(stock));

            stockService.updateProductQuantityInStock(updateVm);

            verify(stockRepository).saveAll(anyList());
            verify(stockHistoryService).createStockHistories(anyList(), anyList());
            verify(productService).updateProductQuantity(anyList());
        }

        @Test
        void testUpdateQuantity_whenStockNotFound_shouldSkipAndNotCallProductService() {
            StockQuantityVm quantityVm = new StockQuantityVm(99L, 5L, "Restock");
            StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(quantityVm));

            when(stockRepository.findAllById(anyList())).thenReturn(List.of());
            when(stockRepository.saveAll(anyList())).thenReturn(List.of());

            stockService.updateProductQuantityInStock(updateVm);

            verify(stockRepository).saveAll(List.of());
        }
    }
}

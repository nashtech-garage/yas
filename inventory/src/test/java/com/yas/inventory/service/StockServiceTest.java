package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
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
import com.yas.inventory.viewmodel.product.ProductQuantityPostVm;
import com.yas.inventory.viewmodel.stock.StockPostVm;
import com.yas.inventory.viewmodel.stock.StockQuantityUpdateVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stock.StockVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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

    private static final Long WAREHOUSE_ID = 1L;
    private static final Long PRODUCT_ID = 10L;

    @Nested
    class AddProductIntoWarehouseTest {

        @Test
        void testAddProductIntoWarehouse_whenStockAlreadyExists_shouldThrowStockExistingException() {
            StockPostVm postVm = new StockPostVm(PRODUCT_ID, WAREHOUSE_ID);

            when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, PRODUCT_ID))
                .thenReturn(true);

            assertThrows(StockExistingException.class,
                () -> stockService.addProductIntoWarehouse(List.of(postVm)));

            verify(stockRepository, never()).saveAll(anyList());
        }

        @Test
        void testAddProductIntoWarehouse_whenProductNotFound_shouldThrowNotFoundException() {
            StockPostVm postVm = new StockPostVm(PRODUCT_ID, WAREHOUSE_ID);

            when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, PRODUCT_ID))
                .thenReturn(false);
            when(productService.getProduct(PRODUCT_ID)).thenReturn(null);

            assertThrows(NotFoundException.class,
                () -> stockService.addProductIntoWarehouse(List.of(postVm)));

            verify(stockRepository, never()).saveAll(anyList());
        }

        @Test
        void testAddProductIntoWarehouse_whenWarehouseNotFound_shouldThrowNotFoundException() {
            StockPostVm postVm = new StockPostVm(PRODUCT_ID, WAREHOUSE_ID);
            ProductInfoVm product = new ProductInfoVm(PRODUCT_ID, "Product", "SKU-001", false);

            when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, PRODUCT_ID))
                .thenReturn(false);
            when(productService.getProduct(PRODUCT_ID)).thenReturn(product);
            when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                () -> stockService.addProductIntoWarehouse(List.of(postVm)));

            verify(stockRepository, never()).saveAll(anyList());
        }

        @Test
        void testAddProductIntoWarehouse_whenRequestIsValid_shouldSaveStocks() {
            StockPostVm postVm = new StockPostVm(PRODUCT_ID, WAREHOUSE_ID);
            ProductInfoVm product = new ProductInfoVm(PRODUCT_ID, "Product", "SKU-001", false);
            Warehouse warehouse = Warehouse.builder().id(WAREHOUSE_ID).name("Main WH").build();

            when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, PRODUCT_ID))
                .thenReturn(false);
            when(productService.getProduct(PRODUCT_ID)).thenReturn(product);
            when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse));
            when(stockRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            stockService.addProductIntoWarehouse(List.of(postVm));

            verify(stockRepository).saveAll(anyList());
        }

        @Test
        void testAddProductIntoWarehouse_whenMultipleProducts_shouldSaveAllStocks() {
            Long productId2 = 20L;
            StockPostVm postVm1 = new StockPostVm(PRODUCT_ID, WAREHOUSE_ID);
            StockPostVm postVm2 = new StockPostVm(productId2, WAREHOUSE_ID);
            ProductInfoVm product1 = new ProductInfoVm(PRODUCT_ID, "Product 1", "SKU-001", false);
            ProductInfoVm product2 = new ProductInfoVm(productId2, "Product 2", "SKU-002", false);
            Warehouse warehouse = Warehouse.builder().id(WAREHOUSE_ID).name("Main WH").build();

            when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, PRODUCT_ID))
                .thenReturn(false);
            when(stockRepository.existsByWarehouseIdAndProductId(WAREHOUSE_ID, productId2))
                .thenReturn(false);
            when(productService.getProduct(PRODUCT_ID)).thenReturn(product1);
            when(productService.getProduct(productId2)).thenReturn(product2);
            when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse));
            when(stockRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            stockService.addProductIntoWarehouse(List.of(postVm1, postVm2));

            verify(stockRepository).saveAll(anyList());
        }
    }

    @Nested
    class GetStocksByWarehouseIdAndProductNameAndSkuTest {

        @Test
        void testGetStocks_whenValid_shouldReturnStockVms() {
            ProductInfoVm product = new ProductInfoVm(PRODUCT_ID, "Product", "SKU-001", true);
            Warehouse warehouse = Warehouse.builder().id(WAREHOUSE_ID).name("Main WH").build();
            Stock stock = Stock.builder()
                .id(1L).productId(PRODUCT_ID).quantity(100L).reservedQuantity(0L)
                .warehouse(warehouse).build();

            when(warehouseService.getProductWarehouse(
                WAREHOUSE_ID, "Product", "SKU-001", FilterExistInWhSelection.YES))
                .thenReturn(List.of(product));
            when(stockRepository.findByWarehouseIdAndProductIdIn(WAREHOUSE_ID, List.of(PRODUCT_ID)))
                .thenReturn(List.of(stock));

            List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(
                WAREHOUSE_ID, "Product", "SKU-001");

            assertEquals(1, result.size());
            assertEquals(PRODUCT_ID, result.get(0).productId());
            assertEquals(100L, result.get(0).quantity());
        }

        @Test
        void testGetStocks_whenNoMatchingProducts_shouldReturnEmptyList() {
            when(warehouseService.getProductWarehouse(
                WAREHOUSE_ID, "NonExistent", "SKU-XXX", FilterExistInWhSelection.YES))
                .thenReturn(List.of());
            when(stockRepository.findByWarehouseIdAndProductIdIn(WAREHOUSE_ID, List.of()))
                .thenReturn(List.of());

            List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(
                WAREHOUSE_ID, "NonExistent", "SKU-XXX");

            assertEquals(0, result.size());
        }
    }

    @Nested
    class UpdateProductQuantityInStockTest {

        @Test
        void testUpdateProductQuantityInStock_whenValid_shouldUpdateQuantity() {
            Warehouse warehouse = Warehouse.builder().id(WAREHOUSE_ID).name("Main WH").build();
            Stock stock = Stock.builder()
                .id(1L).productId(PRODUCT_ID).quantity(50L).reservedQuantity(0L)
                .warehouse(warehouse).build();
            StockQuantityVm quantityVm = new StockQuantityVm(1L, 10L, "Restock");
            StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(quantityVm));

            when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock));
            when(stockRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            stockService.updateProductQuantityInStock(updateVm);

            verify(stockRepository).saveAll(anyList());
            verify(stockHistoryService).createStockHistories(anyList(), anyList());
            assertEquals(60L, stock.getQuantity());
        }

        @Test
        void testUpdateProductQuantityInStock_whenStockIdNotInRepository_shouldSkip() {
            StockQuantityVm quantityVm = new StockQuantityVm(999L, 10L, "Restock");
            StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(quantityVm));

            when(stockRepository.findAllById(List.of(999L))).thenReturn(List.of());
            when(stockRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            stockService.updateProductQuantityInStock(updateVm);

            verify(stockRepository).saveAll(List.of());
        }

        @Test
        void testUpdateProductQuantityInStock_whenNullQuantity_shouldTreatAsZero() {
            Warehouse warehouse = Warehouse.builder().id(WAREHOUSE_ID).name("Main WH").build();
            Stock stock = Stock.builder()
                .id(1L).productId(PRODUCT_ID).quantity(50L).reservedQuantity(0L)
                .warehouse(warehouse).build();
            StockQuantityVm quantityVm = new StockQuantityVm(1L, null, "No change");
            StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(quantityVm));

            when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock));
            when(stockRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            stockService.updateProductQuantityInStock(updateVm);

            assertEquals(50L, stock.getQuantity());
        }

        @Test
        void testUpdateProductQuantityInStock_whenEmptyList_shouldNotUpdateProduct() {
            StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of());

            when(stockRepository.findAllById(List.of())).thenReturn(List.of());
            when(stockRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            stockService.updateProductQuantityInStock(updateVm);

            verify(productService, never()).updateProductQuantity(anyList());
        }
    }
}

package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import java.util.Collections;
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
    private StockRepository stockRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ProductService productService;

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private StockHistoryService stockHistoryService;

    @InjectMocks
    private StockService stockService;

    @Nested
    class AddProductIntoWarehouseTest {

        @Test
        void addProductIntoWarehouse_validList_savesAllStocks() {
            StockPostVm postVm = new StockPostVm(1L, 10L);
            Warehouse warehouse = Warehouse.builder().id(10L).name("Main").build();
            ProductInfoVm product = new ProductInfoVm(1L, "Product A", "SKU-A", true);

            when(stockRepository.existsByWarehouseIdAndProductId(10L, 1L)).thenReturn(false);
            when(productService.getProduct(1L)).thenReturn(product);
            when(warehouseRepository.findById(10L)).thenReturn(Optional.of(warehouse));

            stockService.addProductIntoWarehouse(List.of(postVm));

            verify(stockRepository).saveAll(any());
        }

        @Test
        void addProductIntoWarehouse_emptyList_savesEmptyList() {
            stockService.addProductIntoWarehouse(Collections.emptyList());

            verify(stockRepository).saveAll(Collections.emptyList());
        }

        @Test
        void addProductIntoWarehouse_duplicateProductInWarehouse_throwsStockExistingException() {
            StockPostVm postVm = new StockPostVm(1L, 10L);

            when(stockRepository.existsByWarehouseIdAndProductId(10L, 1L)).thenReturn(true);

            assertThrows(StockExistingException.class,
                () -> stockService.addProductIntoWarehouse(List.of(postVm)));

            verify(stockRepository, never()).saveAll(any());
        }

        @Test
        void addProductIntoWarehouse_warehouseNotFound_throwsNotFoundException() {
            StockPostVm postVm = new StockPostVm(1L, 10L);
            ProductInfoVm product = new ProductInfoVm(1L, "Product A", "SKU-A", true);

            when(stockRepository.existsByWarehouseIdAndProductId(10L, 1L)).thenReturn(false);
            when(productService.getProduct(1L)).thenReturn(product);
            when(warehouseRepository.findById(10L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                () -> stockService.addProductIntoWarehouse(List.of(postVm)));

            verify(stockRepository, never()).saveAll(any());
        }
    }

    @Nested
    class UpdateProductQuantityInStockTest {

        @Test
        void updateProductQuantityInStock_increaseQuantity_updatesAndCreatesHistory() {
            Stock stock = Stock.builder().id(1L).productId(5L).quantity(10L).reservedQuantity(0L).build();
            StockQuantityVm quantityVm = new StockQuantityVm(1L, 5L, "restock");
            StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(quantityVm));

            when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock));

            stockService.updateProductQuantityInStock(requestBody);

            verify(stockRepository).saveAll(anyList());
            verify(stockHistoryService).createStockHistories(anyList(), anyList());
        }

        @Test
        void updateProductQuantityInStock_decreaseQuantity_updatesStock() {
            Stock stock = Stock.builder().id(1L).productId(5L).quantity(20L).reservedQuantity(0L).build();
            StockQuantityVm quantityVm = new StockQuantityVm(1L, -5L, "sale");
            StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(quantityVm));

            when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock));

            stockService.updateProductQuantityInStock(requestBody);

            verify(stockRepository).saveAll(anyList());
            verify(stockHistoryService).createStockHistories(anyList(), anyList());
        }

        @Test
        void updateProductQuantityInStock_negativeQuantityExceedsStock_throwsBadRequestException() {
            // adjustedQuantity < 0 AND adjustedQuantity > stock.getQuantity() triggers BadRequestException
            // e.g. stock.quantity = 5, adjustedQuantity = -3 → -3 < 0 but -3 < 5, so no exception
            // To trigger: stock.quantity = -10 (so adjustedQuantity(-3) > stock.quantity(-10))
            // The condition: adjustedQuantity < 0 && adjustedQuantity > stock.getQuantity()
            Stock stock = Stock.builder().id(1L).productId(5L).quantity(-10L).reservedQuantity(0L).build();
            StockQuantityVm quantityVm = new StockQuantityVm(1L, -3L, "bad decrease");
            StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(quantityVm));

            when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock));

            assertThrows(BadRequestException.class,
                () -> stockService.updateProductQuantityInStock(requestBody));
        }

        @Test
        void updateProductQuantityInStock_nullQuantity_treatsAsZero() {
            Stock stock = Stock.builder().id(1L).productId(5L).quantity(10L).reservedQuantity(0L).build();
            StockQuantityVm quantityVm = new StockQuantityVm(1L, null, "no change");
            StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(quantityVm));

            when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock));

            stockService.updateProductQuantityInStock(requestBody);

            verify(stockRepository).saveAll(anyList());
        }
    }
}

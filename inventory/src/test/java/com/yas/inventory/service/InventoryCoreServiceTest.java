package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.StockExistingException;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.StockHistory;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.repository.StockHistoryRepository;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressVm;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockPostVm;
import com.yas.inventory.viewmodel.stock.StockQuantityUpdateVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.warehouse.WarehousePostVm;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class InventoryCoreServiceTest {

    @Mock private WarehouseRepository warehouseRepository;
    @Mock private StockRepository stockRepository;
    @Mock private ProductService productService;
    @Mock private LocationService locationService;
    @Mock private StockHistoryService stockHistoryService;
    @Mock private StockHistoryRepository stockHistoryRepository;

    @InjectMocks private WarehouseService warehouseService;
    @InjectMocks private StockService stockService;

    @Test
    void warehouseServiceShouldMapCrudAndProductWarehouseFlows() {
        Warehouse warehouse = warehouse(1L, "Main", 100L);
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));
        when(stockRepository.getProductIdsInWarehouse(1L)).thenReturn(List.of(10L));
        when(productService.filterProducts("phone", "sku", List.of(10L), FilterExistInWhSelection.YES))
            .thenReturn(List.of(new ProductInfoVm(10L, "Phone", "sku", false), new ProductInfoVm(20L, "Case", "case", false)));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(locationService.getAddressById(100L)).thenReturn(addressDetail(100L));
        when(warehouseRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(warehouse)));

        assertThat(warehouseService.findAllWarehouses()).hasSize(1);
        assertThat(warehouseService.getProductWarehouse(1L, "phone", "sku", FilterExistInWhSelection.YES))
            .extracting(ProductInfoVm::existInWh)
            .containsExactly(true, false);
        assertThat(warehouseService.findById(1L).name()).isEqualTo("Main");
        assertThat(warehouseService.getPageableWarehouses(0, 10).warehouseContent()).hasSize(1);
    }

    @Test
    void warehouseCreateUpdateDeleteShouldValidateDuplicatesAndMissingRows() {
        WarehousePostVm postVm = warehousePostVm("Main");
        Warehouse warehouse = warehouse(1L, "Old", 100L);
        when(locationService.createAddress(any())).thenReturn(new AddressVm(100L, "Contact", "0909", "Line 1", "HCM", "70000", 1L, 2L, 3L));
        when(warehouseRepository.save(any(Warehouse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(warehouseService.create(postVm).getAddressId()).isEqualTo(100L);

        when(warehouseRepository.existsByName("Main")).thenReturn(true);
        assertThatThrownBy(() -> warehouseService.create(postVm)).isInstanceOf(DuplicatedException.class);

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        warehouseService.update(warehousePostVm("Updated"), 1L);
        assertThat(warehouse.getName()).isEqualTo("Updated");
        verify(locationService).updateAddress(any(), any());

        warehouseService.delete(1L);
        verify(warehouseRepository).deleteById(1L);
        verify(locationService).deleteAddress(100L);

        when(warehouseRepository.findById(404L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> warehouseService.findById(404L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void stockServiceShouldAddProductsAndRejectInvalidRows() {
        Warehouse warehouse = warehouse(1L, "Main", 100L);
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 10L)).thenReturn(false);
        when(productService.getProduct(10L)).thenReturn(new ProductInfoVm(10L, "Phone", "sku", false));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(stockRepository.saveAll(any())).thenAnswer(invocation -> List.copyOf((Collection<Stock>) invocation.getArgument(0)));

        stockService.addProductIntoWarehouse(List.of(new StockPostVm(10L, 1L)));

        verify(stockRepository).saveAll(any());

        when(stockRepository.existsByWarehouseIdAndProductId(1L, 11L)).thenReturn(true);
        assertThatThrownBy(() -> stockService.addProductIntoWarehouse(List.of(new StockPostVm(11L, 1L))))
            .isInstanceOf(StockExistingException.class);

        when(stockRepository.existsByWarehouseIdAndProductId(1L, 12L)).thenReturn(false);
        when(productService.getProduct(12L)).thenReturn(null);
        assertThatThrownBy(() -> stockService.addProductIntoWarehouse(List.of(new StockPostVm(12L, 1L))))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void stockServiceShouldMapStocksAndUpdateQuantities() {
        Warehouse warehouse = warehouse(1L, "Main", 100L);
        Stock stock = stock(7L, 10L, 1L, warehouse);
        WarehouseService warehouseServiceMock = mock(WarehouseService.class);
        StockService localStockService = new StockService(
            warehouseRepository, stockRepository, productService, warehouseServiceMock, stockHistoryService);
        when(warehouseServiceMock.getProductWarehouse(1L, "phone", "sku", FilterExistInWhSelection.YES))
            .thenReturn(List.of(new ProductInfoVm(10L, "Phone", "sku", true)));
        when(stockRepository.findByWarehouseIdAndProductIdIn(1L, List.of(10L))).thenReturn(List.of(stock));
        when(stockRepository.findAllById(List.of(7L))).thenReturn(List.of(stock));
        when(stockRepository.saveAll(any())).thenAnswer(invocation -> List.copyOf((Collection<Stock>) invocation.getArgument(0)));

        assertThat(localStockService.getStocksByWarehouseIdAndProductNameAndSku(1L, "phone", "sku")).hasSize(1);

        localStockService.updateProductQuantityInStock(new StockQuantityUpdateVm(List.of(new StockQuantityVm(7L, 5L, "restock"))));

        assertThat(stock.getQuantity()).isEqualTo(6L);
        verify(stockHistoryService).createStockHistories(anyList(), anyList());
        verify(productService).updateProductQuantity(anyList());
    }

    @Test
    void stockHistoryServiceShouldCreateAndReadHistories() {
        StockHistoryService service = new StockHistoryService(stockHistoryRepository, productService);
        Warehouse warehouse = warehouse(1L, "Main", 100L);
        Stock stock = stock(7L, 10L, 1L, warehouse);
        StockHistory history = StockHistory.builder()
            .id(1L)
            .productId(10L)
            .warehouse(warehouse)
            .adjustedQuantity(5L)
            .note("restock")
            .build();
        when(stockHistoryRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(10L, 1L)).thenReturn(List.of(history));
        when(productService.getProduct(10L)).thenReturn(new ProductInfoVm(10L, "Phone", "sku", true));

        service.createStockHistories(List.of(stock), List.of(new StockQuantityVm(7L, 5L, "restock")));
        var result = service.getStockHistories(10L, 1L);

        verify(stockHistoryRepository).saveAll(any());
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().getFirst().productName()).isEqualTo("Phone");
    }

    private Warehouse warehouse(Long id, String name, Long addressId) {
        return Warehouse.builder().id(id).name(name).addressId(addressId).build();
    }

    private Stock stock(Long id, Long productId, Long quantity, Warehouse warehouse) {
        return Stock.builder()
            .id(id)
            .productId(productId)
            .quantity(quantity)
            .reservedQuantity(0L)
            .warehouse(warehouse)
            .build();
    }

    private WarehousePostVm warehousePostVm(String name) {
        return WarehousePostVm.builder()
            .name(name)
            .contactName("Contact")
            .phone("0909")
            .addressLine1("Line 1")
            .addressLine2("Line 2")
            .city("HCM")
            .zipCode("70000")
            .districtId(1L)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build();
    }

    private AddressDetailVm addressDetail(Long id) {
        return new AddressDetailVm(id, "Contact", "0909", "Line 1", "Line 2", "HCM", "70000",
            1L, "District", 2L, "State", 3L, "Vietnam");
    }
}

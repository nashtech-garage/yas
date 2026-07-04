package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressVm;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseDetailVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseListGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehousePostVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class WarehouseServiceTest {

    private WarehouseRepository warehouseRepository;
    private StockRepository stockRepository;
    private ProductService productService;
    private LocationService locationService;
    private WarehouseService warehouseService;

    @BeforeEach
    void setUp() {
        warehouseRepository = mock(WarehouseRepository.class);
        stockRepository = mock(StockRepository.class);
        productService = mock(ProductService.class);
        locationService = mock(LocationService.class);
        warehouseService = new WarehouseService(warehouseRepository, stockRepository, productService, locationService);
    }

    @Test
    void findAllWarehouses_shouldReturnList() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Warehouse 1");
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Warehouse 1");
    }

    @Test
    void getProductWarehouse_whenProductIdsNotEmpty_shouldReturnMappedList() {
        when(stockRepository.getProductIdsInWarehouse(1L)).thenReturn(List.of(10L));
        when(productService.filterProducts(any(), any(), any(), any())).thenReturn(List.of(
            new ProductInfoVm(10L, "Product 10", "SKU10", false)
        ));

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(1L, "p", "s", FilterExistInWhSelection.ALL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).existInWh()).isTrue();
    }

    @Test
    void findById_whenExists_shouldReturnDetail() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setAddressId(100L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(locationService.getAddressById(100L)).thenReturn(AddressDetailVm.builder()
            .contactName("C").phone("P").addressLine1("L1").city("Ci").zipCode("Z").build());

        WarehouseDetailVm result = warehouseService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void findById_whenNotExists_shouldThrowNotFound() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> warehouseService.findById(1L));
    }

    @Test
    void create_whenDuplicateName_shouldThrowDuplicated() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("W1").build();
        when(warehouseRepository.existsByName("W1")).thenReturn(true);
        assertThrows(DuplicatedException.class, () -> warehouseService.create(postVm));
    }

    @Test
    void create_whenValid_shouldSaveAndReturn() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("W1").build();
        when(warehouseRepository.existsByName("W1")).thenReturn(false);
        when(locationService.createAddress(any())).thenReturn(AddressVm.builder().id(100L).build());
        
        warehouseService.create(postVm);

        verify(warehouseRepository).save(any());
    }

    @Test
    void update_whenValid_shouldUpdate() {
        Warehouse warehouse = new Warehouse();
        warehouse.setAddressId(100L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId("W2", 1L)).thenReturn(false);
        WarehousePostVm postVm = WarehousePostVm.builder().name("W2").build();

        warehouseService.update(postVm, 1L);

        assertThat(warehouse.getName()).isEqualTo("W2");
        verify(warehouseRepository).save(warehouse);
        verify(locationService).updateAddress(anyLong(), any());
    }

    @Test
    void delete_whenExists_shouldDelete() {
        Warehouse warehouse = new Warehouse();
        warehouse.setAddressId(100L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        warehouseService.delete(1L);

        verify(warehouseRepository).deleteById(1L);
        verify(locationService).deleteAddress(100L);
    }

    @Test
    void getPageableWarehouses_shouldReturnPage() {
        Page<Warehouse> page = new PageImpl<>(List.of(new Warehouse()));
        when(warehouseRepository.findAll(any(PageRequest.class))).thenReturn(page);

        WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

        assertThat(result.warehouseContent()).hasSize(1);
    }
}

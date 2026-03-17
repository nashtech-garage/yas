package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressPostVm;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private ProductService productService;
    @Mock
    private LocationService locationService;

    @InjectMocks
    private WarehouseService warehouseService;

    private Warehouse warehouse;
    private WarehousePostVm warehousePostVm;
    private AddressDetailVm addressDetailVm;

    @BeforeEach
    void setUp() {
        warehouse = Warehouse.builder().name("Main Warehouse").addressId(1L).build();
        warehouse.setId(1L);

        warehousePostVm = new WarehousePostVm(
            "wh-1", "Main Warehouse", "John Doe", "123456", "Line 1", "Line 2",
            "City", "1000", 1L, 1L, 1L
        );

        addressDetailVm = new AddressDetailVm(
            1L, "John Doe", "123456", "Line 1", "Line 2", "City", "1000",
            1L, "District", 1L, "State", 1L, "Country"
        );
    }

    @Test
    void findAllWarehouses_shouldReturnListOfWarehouseGetVm() {
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

        assertEquals(1, result.size());
        assertEquals("Main Warehouse", result.get(0).name());
    }

    @Test
    void getProductWarehouse_withProducts_shouldReturnProductInfoVmList() {
        when(stockRepository.getProductIdsInWarehouse(1L)).thenReturn(List.of(1L));
        ProductInfoVm productVm = new ProductInfoVm(1L, "Product", "SKU", false);
        when(productService.filterProducts(any(), any(), any(), any())).thenReturn(List.of(productVm));

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(1L, "Product", "SKU", FilterExistInWhSelection.ALL);

        assertEquals(1, result.size());
        assertTrue(result.get(0).existInWh());
    }
    
    @Test
    void getProductWarehouse_emptyProducts_shouldReturnFilters() {
        when(stockRepository.getProductIdsInWarehouse(1L)).thenReturn(List.of());
        ProductInfoVm productVm = new ProductInfoVm(1L, "Product", "SKU", false);
        when(productService.filterProducts(any(), any(), any(), any())).thenReturn(List.of(productVm));

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(1L, "Product", "SKU", FilterExistInWhSelection.ALL);

        assertEquals(1, result.size());
        assertFalse(result.get(0).existInWh());
    }


    @Test
    void findById_shouldReturnWarehouseDetailVm_whenFound() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(locationService.getAddressById(1L)).thenReturn(addressDetailVm);

        WarehouseDetailVm result = warehouseService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Main Warehouse", result.name());
    }

    @Test
    void findById_shouldThrowNotFoundException_whenNotFound() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.findById(1L));
    }

    @Test
    void create_shouldReturnSavedWarehouse_whenValid() {
        when(warehouseRepository.existsByName("Main Warehouse")).thenReturn(false);
        AddressVm addressVm = new AddressVm(1L, "John Doe", "123456", "Line 1", "City", "1000", 1L, 1L, 1L);
        when(locationService.createAddress(any(AddressPostVm.class))).thenReturn(addressVm);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        Warehouse result = warehouseService.create(warehousePostVm);

        assertNotNull(result);
        assertEquals("Main Warehouse", result.getName());
    }

    @Test
    void create_shouldThrowDuplicatedException_whenNameExists() {
        when(warehouseRepository.existsByName("Main Warehouse")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.create(warehousePostVm));
    }

    @Test
    void update_shouldUpdateWarehouse_whenValid() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId("Main Warehouse", 1L)).thenReturn(false);

        warehouseService.update(warehousePostVm, 1L);

        verify(locationService).updateAddress(eq(1L), any(AddressPostVm.class));
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void update_shouldThrowDuplicatedException_whenNameExistsForOtherId() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId("Main Warehouse", 1L)).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.update(warehousePostVm, 1L));
    }

    @Test
    void update_shouldThrowNotFoundException_whenNotFound() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.update(warehousePostVm, 1L));
    }

    @Test
    void delete_shouldDeleteWarehouse_whenFound() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        warehouseService.delete(1L);

        verify(warehouseRepository).deleteById(1L);
        verify(locationService).deleteAddress(1L);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenNotFound() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.delete(1L));
    }

    @Test
    void getPageableWarehouses_shouldReturnPageOfWarehouses() {
        Page<Warehouse> page = new PageImpl<>(List.of(warehouse));
        when(warehouseRepository.findAll(any(Pageable.class))).thenReturn(page);

        WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

        assertNotNull(result);
        assertEquals(1, result.warehouseContent().size());
        assertEquals(1, result.totalElements());
    }
}

package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void findAllWarehouses_ShouldReturnList() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Warehouse 1");
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

        assertEquals(1, result.size());
        assertEquals("Warehouse 1", result.get(0).name());
    }

    @Test
    void getProductWarehouse_WhenNormalCase_ReturnList() {
        Long warehouseId = 1L;
        List<Long> productIds = List.of(100L);
        when(stockRepository.getProductIdsInWarehouse(warehouseId)).thenReturn(productIds);

        ProductInfoVm productInfoVm = new ProductInfoVm(100L, "Product", "SKU", false);
        when(productService.filterProducts(anyString(), anyString(), eq(productIds), any(FilterExistInWhSelection.class)))
            .thenReturn(List.of(productInfoVm));

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(warehouseId, "Product", "SKU", FilterExistInWhSelection.ALL);

        assertEquals(1, result.size());
        assertTrue(result.get(0).existInWh());
    }

    @Test
    void findById_WhenFound_ShouldReturnDetail() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Warehouse 1");
        warehouse.setAddressId(2L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        AddressDetailVm addressDetailVm = AddressDetailVm.builder()
            .contactName("John")
            .phone("123")
            .build();
        when(locationService.getAddressById(2L)).thenReturn(addressDetailVm);

        WarehouseDetailVm result = warehouseService.findById(1L);

        assertNotNull(result);
        assertEquals("Warehouse 1", result.name());
        assertEquals("John", result.contactName());
    }

    @Test
    void findById_WhenNotFound_ShouldThrowException() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.findById(1L));
    }

    @Test
    void create_WhenNormalCase_ShouldSave() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("New Wh").build();
        when(warehouseRepository.existsByName("New Wh")).thenReturn(false);

        AddressVm addressVm = AddressVm.builder().id(2L).build();
        when(locationService.createAddress(any(AddressPostVm.class))).thenReturn(addressVm);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("New Wh");
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        Warehouse result = warehouseService.create(postVm);

        assertNotNull(result);
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    void create_WhenNameDuplicated_ShouldThrowException() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("Duplicate").build();
        when(warehouseRepository.existsByName("Duplicate")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.create(postVm));
    }

    @Test
    void update_WhenNormalCase_ShouldSave() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setAddressId(2L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId(anyString(), anyLong())).thenReturn(false);

        WarehousePostVm postVm = WarehousePostVm.builder().name("Updated Name").build();

        warehouseService.update(postVm, 1L);

        verify(warehouseRepository, times(1)).save(warehouse);
        verify(locationService, times(1)).updateAddress(eq(2L), any(AddressPostVm.class));
    }

    @Test
    void update_WhenNotFound_ShouldThrowException() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("New Name").build();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.update(postVm, 1L));
    }

    @Test
    void update_WhenNameDuplicated_ShouldThrowException() {
        Warehouse warehouse = new Warehouse();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId("Dup", 1L)).thenReturn(true);

        WarehousePostVm postVm = WarehousePostVm.builder().name("Dup").build();

        assertThrows(DuplicatedException.class, () -> warehouseService.update(postVm, 1L));
    }

    @Test
    void delete_WhenNormalCase_ShouldDelete() {
        Warehouse warehouse = new Warehouse();
        warehouse.setAddressId(2L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(stockRepository.existsByWarehouseId(1L)).thenReturn(false);

        warehouseService.delete(1L);

        verify(warehouseRepository, times(1)).deleteById(1L);
        verify(locationService, times(1)).deleteAddress(2L);
    }

    @Test
    void delete_WhenWarehouseHasStock_ShouldThrowException() {
        Warehouse warehouse = new Warehouse();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(stockRepository.existsByWarehouseId(1L)).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.delete(1L));
    }

    @Test
    void getPageableWarehouses_ShouldReturnPage() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        Page<Warehouse> warehousePage = new PageImpl<>(List.of(warehouse), PageRequest.of(0, 10), 1);
        when(warehouseRepository.findAll(any(PageRequest.class))).thenReturn(warehousePage);

        WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

        assertEquals(1, result.warehouseContent().size());
        assertEquals(1, result.totalElements());
    }
}

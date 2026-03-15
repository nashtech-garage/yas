package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import org.mockito.ArgumentCaptor;
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
    void testFindAllWarehouses_whenDataExists_returnMappedVmList() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("Warehouse A").addressId(10L).build();

        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().id());
        assertEquals("Warehouse A", result.getFirst().name());
    }

    @Test
    void testGetProductWarehouse_whenWarehouseContainsProducts_markExistenceFlags() {
        when(stockRepository.getProductIdsInWarehouse(1L)).thenReturn(List.of(1L, 3L));
        when(productService.filterProducts("keyboard", "sku", List.of(1L, 3L), FilterExistInWhSelection.YES))
            .thenReturn(List.of(
                new ProductInfoVm(1L, "Keyboard", "K1", false),
                new ProductInfoVm(2L, "Mouse", "M1", false)
            ));

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(
            1L,
            "keyboard",
            "sku",
            FilterExistInWhSelection.YES
        );

        assertEquals(2, result.size());
        assertThat(result).extracting(ProductInfoVm::existInWh).containsExactly(true, false);
    }

    @Test
    void testFindById_whenWarehouseExists_returnCombinedWarehouseDetail() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("Warehouse A").addressId(5L).build();
        AddressDetailVm address = AddressDetailVm.builder()
            .id(5L)
            .contactName("John Doe")
            .phone("123456")
            .addressLine1("Line 1")
            .addressLine2("Line 2")
            .city("Da Nang")
            .zipCode("550000")
            .districtId(11L)
            .districtName("Hai Chau")
            .stateOrProvinceId(22L)
            .stateOrProvinceName("Da Nang")
            .countryId(33L)
            .countryName("VN")
            .build();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(locationService.getAddressById(5L)).thenReturn(address);

        WarehouseDetailVm result = warehouseService.findById(1L);

        assertEquals("Warehouse A", result.name());
        assertEquals("John Doe", result.contactName());
        assertEquals(33L, result.countryId());
    }

    @Test
    void testFindById_whenWarehouseMissing_throwNotFoundException() {
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> warehouseService.findById(99L));

        assertThat(exception).hasMessageContaining("99");
    }

    @Test
    void testCreate_whenNameNotDuplicated_saveWarehouseWithCreatedAddress() {
        WarehousePostVm warehousePostVm = WarehousePostVm.builder()
            .name("Central Warehouse")
            .contactName("Jane")
            .phone("0987654321")
            .addressLine1("123 Street")
            .addressLine2("Floor 2")
            .city("HCM")
            .zipCode("700000")
            .districtId(1L)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build();
        AddressVm addressVm = AddressVm.builder().id(42L).build();
        ArgumentCaptor<Warehouse> warehouseCaptor = ArgumentCaptor.forClass(Warehouse.class);

        when(warehouseRepository.existsByName("Central Warehouse")).thenReturn(false);
        when(locationService.createAddress(any())).thenReturn(addressVm);
        when(warehouseRepository.save(any(Warehouse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Warehouse result = warehouseService.create(warehousePostVm);

        verify(warehouseRepository).save(warehouseCaptor.capture());
        assertEquals("Central Warehouse", result.getName());
        assertEquals(42L, result.getAddressId());
        assertEquals("Central Warehouse", warehouseCaptor.getValue().getName());
        assertEquals(42L, warehouseCaptor.getValue().getAddressId());
    }

    @Test
    void testCreate_whenNameDuplicated_throwDuplicatedException() {
        WarehousePostVm warehousePostVm = WarehousePostVm.builder().name("Central Warehouse").build();

        when(warehouseRepository.existsByName("Central Warehouse")).thenReturn(true);

        DuplicatedException exception = assertThrows(
            DuplicatedException.class,
            () -> warehouseService.create(warehousePostVm)
        );

        assertThat(exception).hasMessageContaining("Central Warehouse");
        verify(locationService, never()).createAddress(any());
    }

    @Test
    void testUpdate_whenWarehouseExists_updateAddressAndPersistName() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("Old Name").addressId(5L).build();
        WarehousePostVm warehousePostVm = WarehousePostVm.builder()
            .name("New Name")
            .contactName("Jane")
            .phone("0987654321")
            .addressLine1("123 Street")
            .addressLine2("Floor 2")
            .city("HCM")
            .zipCode("700000")
            .districtId(1L)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId("New Name", 1L)).thenReturn(false);
        when(warehouseRepository.save(any(Warehouse.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(locationService).updateAddress(anyLong(), any());

        warehouseService.update(warehousePostVm, 1L);

        verify(locationService).updateAddress(anyLong(), any());
        verify(warehouseRepository).save(warehouse);
        assertEquals("New Name", warehouse.getName());
    }

    @Test
    void testDelete_whenWarehouseExists_deleteWarehouseAndAddress() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("Warehouse A").addressId(5L).build();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        doNothing().when(locationService).deleteAddress(5L);

        warehouseService.delete(1L);

        verify(warehouseRepository).deleteById(1L);
        verify(locationService).deleteAddress(5L);
    }

    @Test
    void testGetPageableWarehouses_whenPageRequested_returnPageMetadata() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("Warehouse A").addressId(10L).build();
        Page<Warehouse> warehousePage = new PageImpl<>(List.of(warehouse), PageRequest.of(0, 10), 1);

        when(warehouseRepository.findAll(PageRequest.of(0, 10))).thenReturn(warehousePage);

        WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

        assertFalse(result.warehouseContent().isEmpty());
        assertEquals(0, result.pageNo());
        assertEquals(10, result.pageSize());
        assertEquals(1, result.totalElements());
    }
}
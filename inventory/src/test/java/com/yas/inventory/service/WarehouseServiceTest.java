package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
        warehouseRepository = org.mockito.Mockito.mock(WarehouseRepository.class);
        stockRepository = org.mockito.Mockito.mock(StockRepository.class);
        productService = org.mockito.Mockito.mock(ProductService.class);
        locationService = org.mockito.Mockito.mock(LocationService.class);
        warehouseService = new WarehouseService(warehouseRepository, stockRepository, productService, locationService);
    }

    @Test
    void findAllWarehouses_whenHasData_returnMappedWarehouses() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("WH-1").addressId(11L).build();
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(1L);
        assertThat(result.getFirst().name()).isEqualTo("WH-1");
    }

    @Test
    void getProductWarehouse_whenWarehouseHasProducts_markExistInWarehouse() {
        when(stockRepository.getProductIdsInWarehouse(1L)).thenReturn(List.of(2L));
        List<ProductInfoVm> filtered = List.of(
            new ProductInfoVm(2L, "Product-2", "SKU-2", false),
            new ProductInfoVm(3L, "Product-3", "SKU-3", false)
        );
        when(productService.filterProducts("p", "s", List.of(2L), FilterExistInWhSelection.ALL)).thenReturn(filtered);

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(1L, "p", "s", FilterExistInWhSelection.ALL);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).existInWh()).isTrue();
        assertThat(result.get(1).existInWh()).isFalse();
    }

    @Test
    void getProductWarehouse_whenNoProductIdInWarehouse_returnFilterResultDirectly() {
        when(stockRepository.getProductIdsInWarehouse(1L)).thenReturn(List.of());
        List<ProductInfoVm> filtered = List.of(new ProductInfoVm(3L, "Product-3", "SKU-3", false));
        when(productService.filterProducts("p", "s", List.of(), FilterExistInWhSelection.NO)).thenReturn(filtered);

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(1L, "p", "s", FilterExistInWhSelection.NO);

        assertThat(result).isEqualTo(filtered);
    }

    @Test
    void findById_whenWarehouseNotFound_throwNotFoundException() {
        when(warehouseRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.findById(10L));
    }

    @Test
    void findById_whenFound_returnWarehouseDetailVm() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("WH-1").addressId(11L).build();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        AddressDetailVm address = new AddressDetailVm(
            11L, "John", "0123", "Street", "Block A", "City", "70000",
            100L, "District", 200L, "State", 300L, "Country"
        );
        when(locationService.getAddressById(11L)).thenReturn(address);

        WarehouseDetailVm result = warehouseService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("WH-1");
        assertThat(result.contactName()).isEqualTo("John");
        assertThat(result.countryId()).isEqualTo(300L);
    }

    @Test
    void create_whenNameExisted_throwDuplicatedException() {
        WarehousePostVm request = WarehousePostVm.builder()
            .name("WH-1")
            .districtId(100L)
            .stateOrProvinceId(200L)
            .countryId(300L)
            .build();
        when(warehouseRepository.existsByName("WH-1")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.create(request));
    }

    @Test
    void create_whenValidRequest_saveWarehouseWithAddressId() {
        WarehousePostVm request = WarehousePostVm.builder()
            .name("WH-1")
            .contactName("John")
            .phone("0123")
            .addressLine1("Street")
            .addressLine2("Block A")
            .city("City")
            .zipCode("70000")
            .districtId(100L)
            .stateOrProvinceId(200L)
            .countryId(300L)
            .build();

        when(warehouseRepository.existsByName("WH-1")).thenReturn(false);
        when(locationService.createAddress(any())).thenReturn(AddressVm.builder().id(11L).build());

        Warehouse saved = Warehouse.builder().id(1L).name("WH-1").addressId(11L).build();
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(saved);

        Warehouse result = warehouseService.create(request);

        assertThat(result.getId()).isEqualTo(1L);
        ArgumentCaptor<Warehouse> captor = ArgumentCaptor.forClass(Warehouse.class);
        verify(warehouseRepository).save(captor.capture());
        Warehouse toSave = captor.getValue();
        assertThat(toSave.getName()).isEqualTo("WH-1");
        assertThat(toSave.getAddressId()).isEqualTo(11L);
    }

    @Test
    void update_whenWarehouseNotFound_throwNotFoundException() {
        WarehousePostVm request = WarehousePostVm.builder().name("WH").districtId(1L).stateOrProvinceId(1L).countryId(1L).build();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.update(request, 1L));
    }

    @Test
    void update_whenNameExistedInOtherWarehouse_throwDuplicatedException() {
        Warehouse existing = Warehouse.builder().id(1L).name("WH-1").addressId(11L).build();
        WarehousePostVm request = WarehousePostVm.builder().name("WH-2").districtId(1L).stateOrProvinceId(1L).countryId(1L).build();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(warehouseRepository.existsByNameWithDifferentId("WH-2", 1L)).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.update(request, 1L));
    }

    @Test
    void update_whenValidRequest_updateAddressAndSaveWarehouse() {
        Warehouse existing = Warehouse.builder().id(1L).name("WH-1").addressId(11L).build();
        WarehousePostVm request = WarehousePostVm.builder()
            .name("WH-2")
            .contactName("Jane")
            .phone("0999")
            .addressLine1("New Street")
            .addressLine2("Block B")
            .city("New City")
            .zipCode("80000")
            .districtId(101L)
            .stateOrProvinceId(201L)
            .countryId(301L)
            .build();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(warehouseRepository.existsByNameWithDifferentId("WH-2", 1L)).thenReturn(false);

        warehouseService.update(request, 1L);

        verify(locationService).updateAddress(any(), any());
        verify(warehouseRepository).save(existing);
        assertThat(existing.getName()).isEqualTo("WH-2");
    }

    @Test
    void delete_whenWarehouseNotFound_throwNotFoundException() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.delete(1L));
    }

    @Test
    void delete_whenWarehouseExists_deleteWarehouseAndAddress() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("WH").addressId(11L).build();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        warehouseService.delete(1L);

        verify(warehouseRepository).deleteById(1L);
        verify(locationService).deleteAddress(11L);
    }

    @Test
    void getPageableWarehouses_whenHasPageData_returnWarehouseListVm() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("WH-1").addressId(11L).build();
        Page<Warehouse> page = new PageImpl<>(List.of(warehouse), PageRequest.of(0, 5), 1);
        when(warehouseRepository.findAll(any(PageRequest.class))).thenReturn(page);

        WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 5);

        assertThat(result.pageNo()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(5);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.warehouseContent()).hasSize(1);
    }
}

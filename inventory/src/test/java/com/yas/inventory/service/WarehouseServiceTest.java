package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class WarehouseServiceTest {
    private WarehouseRepository warehouseRepository;
    private StockRepository stockRepository;
    private ProductService productService;
    private LocationService locationService;
    private WarehouseService warehouseService;

    @BeforeEach
    void setUp() {
        warehouseRepository = Mockito.mock(WarehouseRepository.class);
        stockRepository = Mockito.mock(StockRepository.class);
        productService = Mockito.mock(ProductService.class);
        locationService = Mockito.mock(LocationService.class);
        warehouseService = new WarehouseService(warehouseRepository, stockRepository, productService, locationService);
    }

    @Test
    void findAllWarehouses_whenNormalCase_returnList() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Warehouse 1");
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).name()).isEqualTo("Warehouse 1");
    }

    @Test
    void getProductWarehouse_whenStockNotEmpty_returnList() {
        Long warehouseId = 1L;
        List<Long> productIds = List.of(100L, 101L);
        when(stockRepository.getProductIdsInWarehouse(warehouseId)).thenReturn(productIds);
        
        ProductInfoVm product1 = new ProductInfoVm(100L, "Product 1", "SKU1", true);
        ProductInfoVm product2 = new ProductInfoVm(102L, "Product 2", "SKU2", false);
        when(productService.filterProducts(anyString(), anyString(), any(), any())).thenReturn(List.of(product1, product2));

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(warehouseId, "name", "sku", FilterExistInWhSelection.YES);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(100L);
        assertThat(result.get(0).existInWh()).isTrue();
        assertThat(result.get(1).id()).isEqualTo(102L);
        assertThat(result.get(1).existInWh()).isFalse();
    }

    @Test
    void findById_whenWarehouseNotFound_throwNotFoundException() {
        when(warehouseRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> warehouseService.findById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_whenNormalCase_returnDetail() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Warehouse 1");
        warehouse.setAddressId(10L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        AddressDetailVm address = AddressDetailVm.builder()
                .id(10L)
                .contactName("Contact")
                .phone("123")
                .addressLine1("Line1")
                .addressLine2("Line2")
                .city("City")
                .zipCode("Zip")
                .districtId(1L)
                .districtName("District")
                .stateOrProvinceId(1L)
                .stateOrProvinceName("State")
                .countryId(1L)
                .countryName("Country")
                .build();
        when(locationService.getAddressById(10L)).thenReturn(address);

        WarehouseDetailVm result = warehouseService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Warehouse 1");
        assertThat(result.contactName()).isEqualTo("Contact");
    }

    @Test
    void create_whenNameDuplicated_throwDuplicatedException() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("Warehouse 1").build();
        when(warehouseRepository.existsByName("Warehouse 1")).thenReturn(true);

        assertThatThrownBy(() -> warehouseService.create(postVm))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void create_whenNormalCase_returnWarehouse() {
        WarehousePostVm postVm = WarehousePostVm.builder()
                .name("Warehouse 1")
                .contactName("Contact")
                .phone("123")
                .addressLine1("Line1")
                .addressLine2("Line2")
                .city("City")
                .zipCode("Zip")
                .districtId(1L)
                .stateOrProvinceId(1L)
                .countryId(1L)
                .build();
        when(warehouseRepository.existsByName("Warehouse 1")).thenReturn(false);
        when(locationService.createAddress(any())).thenReturn(AddressVm.builder()
                .id(10L)
                .contactName("Contact")
                .phone("123")
                .addressLine1("Line1")
                .city("City")
                .zipCode("Zip")
                .districtId(1L)
                .stateOrProvinceId(1L)
                .countryId(1L)
                .build());
        
        Warehouse warehouse = new Warehouse();
        warehouse.setName("Warehouse 1");
        when(warehouseRepository.save(any())).thenReturn(warehouse);

        Warehouse result = warehouseService.create(postVm);

        assertThat(result.getName()).isEqualTo("Warehouse 1");
        verify(warehouseRepository).save(any());
    }

    @Test
    void update_whenWarehouseNotFound_throwNotFoundException() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("New Name").build();
        when(warehouseRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> warehouseService.update(postVm, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_whenNameDuplicated_throwDuplicatedException() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId("New Name", 1L)).thenReturn(true);

        WarehousePostVm postVm = WarehousePostVm.builder().name("New Name").build();
        assertThatThrownBy(() -> warehouseService.update(postVm, 1L))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void delete_whenNormalCase_callRepository() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setAddressId(10L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        warehouseService.delete(1L);

        verify(warehouseRepository).deleteById(1L);
        verify(locationService).deleteAddress(10L);
    }

    @Test
    void getPageableWarehouses_whenNormalCase_returnList() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Warehouse 1");
        Page<Warehouse> page = new PageImpl<>(List.of(warehouse), PageRequest.of(0, 10), 1);
        when(warehouseRepository.findAll(any(Pageable.class))).thenReturn(page);

        WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

        assertThat(result.warehouseContent()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }
}

package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.data.domain.Pageable;

class WarehouseServiceTest {

    private WarehouseRepository warehouseRepository;

    private StockRepository stockRepository;

    private ProductService productService;

    private LocationService locationService;

    private WarehouseService warehouseService;

    private static final Long WAREHOUSE_ID = 1L;

    private static final String WAREHOUSE_NAME = "Warehouse Alpha";

    @BeforeEach
    void setUp() {
        warehouseRepository = mock(WarehouseRepository.class);
        stockRepository = mock(StockRepository.class);
        productService = mock(ProductService.class);
        locationService = mock(LocationService.class);
        warehouseService = new WarehouseService(warehouseRepository, stockRepository, productService, locationService);
    }

    private Warehouse buildWarehouse(Long id, String name, Long addressId) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setName(name);
        warehouse.setAddressId(addressId);
        return warehouse;
    }

    private WarehousePostVm buildWarehousePostVm(String name) {
        return WarehousePostVm.builder()
            .name(name)
            .contactName("John Doe")
            .phone("123-456-7890")
            .addressLine1("123 Main St")
            .addressLine2("Suite 1")
            .city("Metropolis")
            .zipCode("12345")
            .districtId(1L)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build();
    }

    private AddressDetailVm buildAddressDetailVm(Long id) {
        return AddressDetailVm.builder()
            .id(id)
            .contactName("John Doe")
            .phone("123-456-7890")
            .addressLine1("123 Main St")
            .addressLine2("Suite 1")
            .city("Metropolis")
            .zipCode("12345")
            .districtId(1L)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build();
    }

    @Test
    void testFindAllWarehouses_whenNormalCase_returnsWarehouseGetVmList() {
        Warehouse warehouse1 = buildWarehouse(1L, "Warehouse A", 10L);
        Warehouse warehouse2 = buildWarehouse(2L, "Warehouse B", 11L);
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse1, warehouse2));

        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).name()).isEqualTo("Warehouse A");
        assertThat(result.get(1).name()).isEqualTo("Warehouse B");
    }

    @Test
    void testFindAllWarehouses_whenNoWarehouses_returnsEmptyList() {
        when(warehouseRepository.findAll()).thenReturn(List.of());

        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

        assertThat(result).isEmpty();
    }

    @Test
    void testGetProductWarehouse_whenProductIdsNonEmpty_mapsExistFlagCorrectly() {
        List<Long> productIds = List.of(1L, 2L);
        ProductInfoVm product1 = new ProductInfoVm(1L, "Product A", "SKU-A", false);
        ProductInfoVm product2 = new ProductInfoVm(2L, "Product B", "SKU-B", false);

        when(stockRepository.getProductIdsInWarehouse(WAREHOUSE_ID)).thenReturn(productIds);
        when(productService.filterProducts(anyString(), anyString(), eq(productIds), any()))
            .thenReturn(List.of(product1, product2));

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(
            WAREHOUSE_ID, "Product", "SKU", FilterExistInWhSelection.YES);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).existInWh()).isTrue();
        assertThat(result.get(1).existInWh()).isTrue();
    }

    @Test
    void testGetProductWarehouse_whenProductIdsEmpty_returnsProductVmListDirectly() {
        List<Long> productIds = List.of();
        ProductInfoVm product1 = new ProductInfoVm(1L, "Product A", "SKU-A", false);

        when(stockRepository.getProductIdsInWarehouse(WAREHOUSE_ID)).thenReturn(productIds);
        when(productService.filterProducts(anyString(), anyString(), eq(productIds), any()))
            .thenReturn(List.of(product1));

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(
            WAREHOUSE_ID, "Product", "SKU", FilterExistInWhSelection.NO);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).existInWh()).isFalse();
    }

    @Test
    void testFindById_whenNormalCase_returnsWarehouseDetailVm() {
        Warehouse warehouse = buildWarehouse(WAREHOUSE_ID, WAREHOUSE_NAME, 10L);
        AddressDetailVm addressDetailVm = buildAddressDetailVm(10L);

        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse));
        when(locationService.getAddressById(10L)).thenReturn(addressDetailVm);

        WarehouseDetailVm result = warehouseService.findById(WAREHOUSE_ID);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(WAREHOUSE_ID);
        assertThat(result.name()).isEqualTo(WAREHOUSE_NAME);
        assertThat(result.contactName()).isEqualTo("John Doe");
        assertThat(result.city()).isEqualTo("Metropolis");
    }

    @Test
    void testFindById_whenWarehouseNotFound_throwsNotFoundException() {
        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
            () -> warehouseService.findById(WAREHOUSE_ID));

        assertThat(thrown.getMessage()).contains(String.valueOf(WAREHOUSE_ID));
    }

    @Test
    void testCreate_whenNormalCase_createsAndReturnsWarehouse() {
        WarehousePostVm postVm = buildWarehousePostVm(WAREHOUSE_NAME);
        AddressVm addressVm = AddressVm.builder().id(10L).build();
        Warehouse savedWarehouse = buildWarehouse(WAREHOUSE_ID, WAREHOUSE_NAME, 10L);

        when(warehouseRepository.existsByName(WAREHOUSE_NAME)).thenReturn(false);
        when(locationService.createAddress(any())).thenReturn(addressVm);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(savedWarehouse);

        Warehouse result = warehouseService.create(postVm);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(WAREHOUSE_NAME);
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    void testCreate_whenNameAlreadyExists_throwsDuplicatedException() {
        WarehousePostVm postVm = buildWarehousePostVm(WAREHOUSE_NAME);
        when(warehouseRepository.existsByName(WAREHOUSE_NAME)).thenReturn(true);

        DuplicatedException thrown = assertThrows(DuplicatedException.class,
            () -> warehouseService.create(postVm));

        assertThat(thrown.getMessage()).contains(WAREHOUSE_NAME);
    }

    @Test
    void testUpdate_whenNormalCase_updatesWarehouse() {
        WarehousePostVm postVm = buildWarehousePostVm("Updated Warehouse");
        Warehouse existing = buildWarehouse(WAREHOUSE_ID, WAREHOUSE_NAME, 10L);

        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(existing));
        when(warehouseRepository.existsByNameWithDifferentId("Updated Warehouse", WAREHOUSE_ID)).thenReturn(false);

        assertDoesNotThrow(() -> warehouseService.update(postVm, WAREHOUSE_ID));

        verify(warehouseRepository).save(existing);
        verify(locationService).updateAddress(eq(10L), any());
    }

    @Test
    void testUpdate_whenWarehouseNotFound_throwsNotFoundException() {
        WarehousePostVm postVm = buildWarehousePostVm(WAREHOUSE_NAME);
        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
            () -> warehouseService.update(postVm, WAREHOUSE_ID));

        assertThat(thrown.getMessage()).contains(String.valueOf(WAREHOUSE_ID));
    }

    @Test
    void testUpdate_whenNameAlreadyExistsWithDifferentId_throwsDuplicatedException() {
        WarehousePostVm postVm = buildWarehousePostVm("Duplicate Name");
        Warehouse existing = buildWarehouse(WAREHOUSE_ID, WAREHOUSE_NAME, 10L);

        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(existing));
        when(warehouseRepository.existsByNameWithDifferentId("Duplicate Name", WAREHOUSE_ID)).thenReturn(true);

        DuplicatedException thrown = assertThrows(DuplicatedException.class,
            () -> warehouseService.update(postVm, WAREHOUSE_ID));

        assertThat(thrown.getMessage()).contains("Duplicate Name");
    }

    @Test
    void testDelete_whenNormalCase_deletesWarehouseAndAddress() {
        Warehouse warehouse = buildWarehouse(WAREHOUSE_ID, WAREHOUSE_NAME, 10L);
        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse));

        assertDoesNotThrow(() -> warehouseService.delete(WAREHOUSE_ID));

        verify(warehouseRepository).deleteById(WAREHOUSE_ID);
        verify(locationService).deleteAddress(10L);
    }

    @Test
    void testDelete_whenWarehouseNotFound_throwsNotFoundException() {
        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
            () -> warehouseService.delete(WAREHOUSE_ID));

        assertThat(thrown.getMessage()).contains(String.valueOf(WAREHOUSE_ID));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetPageableWarehouses_whenNormalCase_returnsWarehouseListGetVm() {
        Warehouse warehouse = buildWarehouse(WAREHOUSE_ID, WAREHOUSE_NAME, 10L);
        Page<Warehouse> page = mock(Page.class);

        when(warehouseRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(page.getContent()).thenReturn(List.of(warehouse));
        when(page.getNumber()).thenReturn(0);
        when(page.getSize()).thenReturn(10);
        when(page.getTotalElements()).thenReturn(1L);
        when(page.getTotalPages()).thenReturn(1);
        when(page.isLast()).thenReturn(true);

        WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.warehouseContent()).hasSize(1);
        assertThat(result.warehouseContent().get(0).name()).isEqualTo(WAREHOUSE_NAME);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.isLast()).isTrue();
    }
}

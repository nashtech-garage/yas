package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class WarehouseServiceIT {

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
        warehouseService = new WarehouseService(warehouseRepository,
            stockRepository, productService, locationService);
    }

    @Test
    void testFindAllWarehouses_NormalCase_MethodSuccess() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Test Warehouse");

        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Warehouse", result.getFirst().name());
    }

    @Test
    void testGetProductWarehouse_NormalCase_MethodSuccess() {
        Long warehouseId = 1L;
        String productName = "Product";
        String productSku = "SKU";
        FilterExistInWhSelection existStatus = FilterExistInWhSelection.YES;

        when(stockRepository.getProductIdsInWarehouse(anyLong())).thenReturn(List.of(1L));
        when(productService.filterProducts(anyString(), anyString(), anyList(), any()))
            .thenReturn(List.of(new ProductInfoVm(1L, "Product", "SKU", true)));

        List<ProductInfoVm> result
            = warehouseService.getProductWarehouse(warehouseId, productName, productSku, existStatus);

        assertEquals(1, result.size());
        assertTrue(result.getFirst().existInWh());
    }

    @Test
    void testFindById_NormalCase_MethodSuccess() {
        Long id = 1L;
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setName("Test Warehouse");
        warehouse.setAddressId(2L);

        AddressDetailVm addressDetailVm = new AddressDetailVm(
            1L,
            "John Doe",
            "+1234567890",
            "123 Elm Street",
            "Apt 4B",
            "Springfield",
            "12345",
            101L,
            "Downtown",
            10L,
            "Illinois",
            5L,
            "United States"
        );

        when(warehouseRepository.findById(anyLong())).thenReturn(Optional.of(warehouse));
        when(locationService.getAddressById(anyLong())).thenReturn(addressDetailVm);

        WarehouseDetailVm result = warehouseService.findById(id);

        assertEquals(id, result.id());
        assertEquals("Test Warehouse", result.name());
        assertEquals("John Doe", result.contactName());
    }

    @Test
    void testFindById_whenNotFound_throwNotFoundException() {

        Long id = 1L;
        when(warehouseRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
            warehouseService.findById(id)
        );

        assertEquals("The warehouse 1 is not found", thrown.getMessage());
    }

    @Test
    void testCreate_NormalCase_MethodSuccess() {

        AddressVm addressVm = new AddressVm(
            1L,
            "Alice Johnson",
            "+0987654321",
            "456 Oak Avenue",
            "Metropolis",
            "67890",
            202L,
            15L,
            8L
        );

        when(warehouseRepository.existsByName(anyString())).thenReturn(false);
        when(locationService.createAddress(any())).thenReturn(addressVm);

        Warehouse warehouse = new Warehouse();
        warehouse.setName("Test Warehouse");
        when(warehouseRepository.save(any())).thenReturn(warehouse);

        WarehousePostVm warehousePostVm = WarehousePostVm.builder()
            .id("WH123")
            .name("Main Warehouse")
            .contactName("John Smith")
            .phone("+11234567890")
            .addressLine1("789 Pine Street")
            .addressLine2("Suite 100")
            .city("Big City")
            .zipCode("54321")
            .districtId(301L)
            .stateOrProvinceId(25L)
            .countryId(12L)
            .build();

        Warehouse result = warehouseService.create(warehousePostVm);

        assertNotNull(result);
        assertEquals("Test Warehouse", result.getName());
    }

    @Test
    void testCreate_Duplicated_ThrowDuplicatedException() {

        when(warehouseRepository.existsByName(anyString())).thenReturn(true);
        try {
            warehouseService.create(WarehousePostVm.builder().id("WH123").name("Main Warehouse").build());
        } catch (Exception e) {
            assertThat(e).isInstanceOf(DuplicatedException.class);
            assertEquals("Request name Main Warehouse is already existed", e.getMessage());
        }
    }

    @Test
    void testUpdate_NormalCase_MethodSuccess() {

        Long id = 1L;
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setName("Old Warehouse");

        when(warehouseRepository.findById(anyLong())).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId(anyString(), anyLong())).thenReturn(false);
        doNothing().when(locationService).updateAddress(anyLong(), any());

        WarehousePostVm warehousePostVm = WarehousePostVm.builder()
            .id("WH123")
            .name("Main Warehouse")
            .contactName("John Smith")
            .phone("+11234567890")
            .addressLine1("789 Pine Street")
            .addressLine2("Suite 100")
            .city("Big City")
            .zipCode("54321")
            .districtId(301L)
            .stateOrProvinceId(25L)
            .countryId(12L)
            .build();
        warehouseService.update(warehousePostVm, id);

        verify(warehouseRepository, times(1)).save(warehouse);
    }

    @Test
    void testUpdate_Duplicated_throwDuplicatedException() {

        Long id = 1L;
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setName("Old Warehouse");

        when(warehouseRepository.findById(anyLong())).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId(anyString(), anyLong())).thenReturn(true);

        try {
            warehouseService.update(WarehousePostVm.builder().id("WH123").name("Warehouse1").build(), 1L);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(DuplicatedException.class);
            assertEquals("Request name Warehouse1 is already existed", e.getMessage());
        }

    }

    @Test
    void testDelete_NormalCase_MethodSuccess() {
        Long id = 1L;
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setAddressId(1L);

        when(warehouseRepository.findById(anyLong())).thenReturn(Optional.of(warehouse));
        doNothing().when(warehouseRepository).deleteById(anyLong());
        doNothing().when(locationService).deleteAddress(anyLong());

        warehouseService.delete(id);

        verify(warehouseRepository, times(1)).deleteById(id);
        verify(locationService, times(1)).deleteAddress(warehouse.getAddressId());
    }

    @Test
    void testGetPageableWarehouses_NormalCase_MethodSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Test Warehouse");

        Page<Warehouse> warehousePage = Page.empty(pageable);
        when(warehouseRepository.findAll(pageable)).thenReturn(warehousePage);

        WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

        assertNotNull(result);
        assertEquals(0, result.pageNo());
        assertEquals(10, result.pageSize());
    }

}
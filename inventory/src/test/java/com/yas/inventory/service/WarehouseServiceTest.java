package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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

    private static final Long WAREHOUSE_ID = 1L;
    private static final Long ADDRESS_ID = 100L;

    private WarehousePostVm buildWarehousePostVm(String name) {
        return WarehousePostVm.builder()
            .name(name)
            .contactName("Alice")
            .phone("111")
            .addressLine1("Street A")
            .addressLine2("Suite 1")
            .city("City A")
            .zipCode("11111")
            .districtId(1L)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();
    }

    private AddressDetailVm buildAddressDetailVm() {
        return AddressDetailVm.builder()
            .id(ADDRESS_ID).contactName("Alice").phone("111")
            .addressLine1("Street A").addressLine2("Suite 1").city("City A").zipCode("11111")
            .districtId(1L).districtName("D1").stateOrProvinceId(1L)
            .stateOrProvinceName("SP1").countryId(1L).countryName("Country1").build();
    }

    @Nested
    class FindAllWarehousesTest {

        @Test
        void testFindAllWarehouses_whenWarehousesExist_shouldReturnList() {
            Warehouse wh1 = Warehouse.builder().id(1L).name("WH1").build();
            Warehouse wh2 = Warehouse.builder().id(2L).name("WH2").build();

            when(warehouseRepository.findAll()).thenReturn(List.of(wh1, wh2));

            List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

            assertEquals(2, result.size());
            assertEquals("WH1", result.get(0).name());
            assertEquals("WH2", result.get(1).name());
        }

        @Test
        void testFindAllWarehouses_whenNoWarehouses_shouldReturnEmptyList() {
            when(warehouseRepository.findAll()).thenReturn(List.of());

            List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

            assertNotNull(result);
            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindByIdTest {

        @Test
        void testFindById_whenWarehouseNotFound_shouldThrowNotFoundException() {
            when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> warehouseService.findById(WAREHOUSE_ID));
        }

        @Test
        void testFindById_whenWarehouseExists_shouldReturnWarehouseDetailVm() {
            Warehouse warehouse = Warehouse.builder().id(WAREHOUSE_ID).name("Main WH")
                .addressId(ADDRESS_ID).build();
            AddressDetailVm addressDetailVm = buildAddressDetailVm();

            when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse));
            when(locationService.getAddressById(ADDRESS_ID)).thenReturn(addressDetailVm);

            WarehouseDetailVm result = warehouseService.findById(WAREHOUSE_ID);

            assertNotNull(result);
            assertEquals(WAREHOUSE_ID, result.id());
            assertEquals("Main WH", result.name());
            assertEquals("Alice", result.contactName());
        }
    }

    @Nested
    class CreateTest {

        @Test
        void testCreate_whenNameAlreadyExists_shouldThrowDuplicatedException() {
            WarehousePostVm postVm = buildWarehousePostVm("Existing WH");

            when(warehouseRepository.existsByName("Existing WH")).thenReturn(true);

            assertThrows(DuplicatedException.class, () -> warehouseService.create(postVm));
        }

        @Test
        void testCreate_whenRequestIsValid_shouldSaveAndReturnWarehouse() {
            WarehousePostVm postVm = buildWarehousePostVm("New WH");
            AddressVm addressVm = AddressVm.builder().id(ADDRESS_ID).contactName("Alice")
                .phone("111").addressLine1("Street A").city("City A").zipCode("11111")
                .districtId(1L).stateOrProvinceId(1L).countryId(1L).build();
            Warehouse savedWarehouse = Warehouse.builder().id(WAREHOUSE_ID).name("New WH")
                .addressId(ADDRESS_ID).build();

            when(warehouseRepository.existsByName("New WH")).thenReturn(false);
            when(locationService.createAddress(any())).thenReturn(addressVm);
            when(warehouseRepository.save(any(Warehouse.class))).thenReturn(savedWarehouse);

            Warehouse result = warehouseService.create(postVm);

            assertNotNull(result);
            assertEquals("New WH", result.getName());
            verify(warehouseRepository).save(any(Warehouse.class));
        }
    }

    @Nested
    class UpdateTest {

        @Test
        void testUpdate_whenWarehouseNotFound_shouldThrowNotFoundException() {
            WarehousePostVm postVm = buildWarehousePostVm("Updated WH");

            when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> warehouseService.update(postVm, WAREHOUSE_ID));
        }

        @Test
        void testUpdate_whenNameConflictsWithAnotherWarehouse_shouldThrowDuplicatedException() {
            WarehousePostVm postVm = buildWarehousePostVm("Conflicting WH");
            Warehouse warehouse = Warehouse.builder().id(WAREHOUSE_ID).name("Old WH")
                .addressId(ADDRESS_ID).build();

            when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse));
            when(warehouseRepository.existsByNameWithDifferentId("Conflicting WH", WAREHOUSE_ID))
                .thenReturn(true);

            assertThrows(DuplicatedException.class, () -> warehouseService.update(postVm, WAREHOUSE_ID));
        }

        @Test
        void testUpdate_whenRequestIsValid_shouldUpdateWarehouse() {
            WarehousePostVm postVm = buildWarehousePostVm("Updated WH");
            Warehouse warehouse = Warehouse.builder().id(WAREHOUSE_ID).name("Old WH")
                .addressId(ADDRESS_ID).build();

            when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse));
            when(warehouseRepository.existsByNameWithDifferentId("Updated WH", WAREHOUSE_ID))
                .thenReturn(false);
            when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

            warehouseService.update(postVm, WAREHOUSE_ID);

            assertEquals("Updated WH", warehouse.getName());
            verify(locationService).updateAddress(anyLong(), any());
            verify(warehouseRepository).save(warehouse);
        }
    }

    @Nested
    class DeleteTest {

        @Test
        void testDelete_whenWarehouseNotFound_shouldThrowNotFoundException() {
            when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> warehouseService.delete(WAREHOUSE_ID));
        }

        @Test
        void testDelete_whenWarehouseExists_shouldDeleteAndCleanupAddress() {
            Warehouse warehouse = Warehouse.builder().id(WAREHOUSE_ID).name("Main WH")
                .addressId(ADDRESS_ID).build();

            when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse));

            warehouseService.delete(WAREHOUSE_ID);

            verify(warehouseRepository).deleteById(WAREHOUSE_ID);
            verify(locationService).deleteAddress(ADDRESS_ID);
        }
    }

    @Nested
    class GetPageableWarehousesTest {

        @Test
        void testGetPageableWarehouses_whenWarehousesExist_shouldReturnPagedResult() {
            Warehouse wh1 = Warehouse.builder().id(1L).name("WH1").build();
            Warehouse wh2 = Warehouse.builder().id(2L).name("WH2").build();
            Page<Warehouse> page = new PageImpl<>(List.of(wh1, wh2),
                PageRequest.of(0, 10), 2);

            when(warehouseRepository.findAll(any(PageRequest.class))).thenReturn(page);

            WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

            assertNotNull(result);
            assertEquals(2, result.warehouseContent().size());
            assertEquals(2, result.totalElements());
            assertEquals(1, result.totalPages());
        }
    }

    @Nested
    class GetProductWarehouseTest {

        @Test
        void testGetProductWarehouse_whenProductIdsEmpty_shouldReturnProductVmsDirectly() {
            ProductInfoVm product = new ProductInfoVm(10L, "Product A", "SKU-A", false);

            when(stockRepository.getProductIdsInWarehouse(WAREHOUSE_ID)).thenReturn(List.of());
            when(productService.filterProducts(anyString(), anyString(), anyList(), any()))
                .thenReturn(List.of(product));

            List<ProductInfoVm> result = warehouseService.getProductWarehouse(
                WAREHOUSE_ID, "Product A", "SKU-A", FilterExistInWhSelection.NO);

            assertEquals(1, result.size());
            assertEquals("Product A", result.get(0).name());
        }

        @Test
        void testGetProductWarehouse_whenProductIdsExist_shouldMapExistInWh() {
            Long productId = 10L;
            ProductInfoVm product = new ProductInfoVm(productId, "Product A", "SKU-A", false);

            when(stockRepository.getProductIdsInWarehouse(WAREHOUSE_ID)).thenReturn(List.of(productId));
            when(productService.filterProducts(anyString(), anyString(), anyList(), any()))
                .thenReturn(List.of(product));

            List<ProductInfoVm> result = warehouseService.getProductWarehouse(
                WAREHOUSE_ID, "Product A", "SKU-A", FilterExistInWhSelection.YES);

            assertEquals(1, result.size());
            assertEquals(productId, result.get(0).id());
        }
    }
}

package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressVm;
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

    @Nested
    class FindAllWarehousesTest {

        @Test
        void testFindAll_whenNormalCase_shouldReturnWarehouseList() {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(1L);
            warehouse.setName("WH1");

            when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

            List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("WH1");
        }

        @Test
        void testFindAll_whenEmpty_shouldReturnEmptyList() {
            when(warehouseRepository.findAll()).thenReturn(List.of());

            List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class FindByIdTest {

        @Test
        void testFindById_whenWarehouseNotFound_shouldThrowNotFoundException() {
            when(warehouseRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> warehouseService.findById(1L));
        }

        @Test
        void testFindById_whenNormalCase_shouldReturnWarehouseDetail() {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(1L);
            warehouse.setName("WH1");
            warehouse.setAddressId(10L);

            AddressDetailVm addressDetailVm = AddressDetailVm.builder()
                .id(10L).contactName("John").phone("123").addressLine1("Street 1")
                .city("City").zipCode("12345").districtId(1L).stateOrProvinceId(2L).countryId(3L)
                .build();

            when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
            when(locationService.getAddressById(10L)).thenReturn(addressDetailVm);

            WarehouseDetailVm result = warehouseService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("WH1");
            assertThat(result.contactName()).isEqualTo("John");
        }
    }

    @Nested
    class CreateWarehouseTest {

        @Test
        void testCreate_whenNameAlreadyExists_shouldThrowDuplicatedException() {
            WarehousePostVm postVm = WarehousePostVm.builder()
                .name("WH1").contactName("John").phone("123")
                .addressLine1("Street").city("City").zipCode("12345")
                .districtId(1L).stateOrProvinceId(2L).countryId(3L)
                .build();

            when(warehouseRepository.existsByName("WH1")).thenReturn(true);

            assertThrows(DuplicatedException.class, () -> warehouseService.create(postVm));
        }

        @Test
        void testCreate_whenValidRequest_shouldSaveAndReturnWarehouse() {
            WarehousePostVm postVm = WarehousePostVm.builder()
                .name("WH1").contactName("John").phone("123")
                .addressLine1("Street").city("City").zipCode("12345")
                .districtId(1L).stateOrProvinceId(2L).countryId(3L)
                .build();

            AddressVm addressVm = AddressVm.builder().id(10L).build();

            when(warehouseRepository.existsByName("WH1")).thenReturn(false);
            when(locationService.createAddress(any())).thenReturn(addressVm);
            when(warehouseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Warehouse result = warehouseService.create(postVm);

            assertNotNull(result);
            verify(warehouseRepository).save(any());
        }
    }

    @Nested
    class UpdateWarehouseTest {

        @Test
        void testUpdate_whenWarehouseNotFound_shouldThrowNotFoundException() {
            WarehousePostVm postVm = WarehousePostVm.builder()
                .name("WH2").contactName("Jane").phone("456")
                .addressLine1("Avenue").city("Town").zipCode("67890")
                .districtId(1L).stateOrProvinceId(2L).countryId(3L)
                .build();

            when(warehouseRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> warehouseService.update(postVm, 1L));
        }

        @Test
        void testUpdate_whenNameDuplicated_shouldThrowDuplicatedException() {
            WarehousePostVm postVm = WarehousePostVm.builder()
                .name("WH-DUPLICATE").contactName("Jane").phone("456")
                .addressLine1("Avenue").city("Town").zipCode("67890")
                .districtId(1L).stateOrProvinceId(2L).countryId(3L)
                .build();
            Warehouse warehouse = new Warehouse();
            warehouse.setId(1L);
            warehouse.setName("WH-OLD");

            when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
            when(warehouseRepository.existsByNameWithDifferentId("WH-DUPLICATE", 1L)).thenReturn(true);

            assertThrows(DuplicatedException.class, () -> warehouseService.update(postVm, 1L));
        }
    }

    @Nested
    class DeleteWarehouseTest {

        @Test
        void testDelete_whenWarehouseNotFound_shouldThrowNotFoundException() {
            when(warehouseRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> warehouseService.delete(1L));
        }

        @Test
        void testDelete_whenNormalCase_shouldDeleteAndRemoveAddress() {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(1L);
            warehouse.setAddressId(10L);

            when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

            warehouseService.delete(1L);

            verify(warehouseRepository).deleteById(1L);
            verify(locationService).deleteAddress(10L);
        }
    }

    @Nested
    class GetPageableWarehousesTest {

        @Test
        void testGetPageable_whenNormalCase_shouldReturnPagedResult() {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(1L);
            warehouse.setName("WH1");

            Page<Warehouse> page = new PageImpl<>(List.of(warehouse), PageRequest.of(0, 10), 1);
            when(warehouseRepository.findAll(any(PageRequest.class))).thenReturn(page);

            WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

            assertThat(result).isNotNull();
            assertThat(result.warehouseContent()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(1);
        }
    }
}

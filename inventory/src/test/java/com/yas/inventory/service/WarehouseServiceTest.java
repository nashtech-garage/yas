package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.address.AddressVm;
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

    @Nested
    class CreateTest {

        @Test
        void create_happyPath_returnsWarehouse() {
            WarehousePostVm postVm = WarehousePostVm.builder()
                .name("New Warehouse")
                .contactName("John")
                .phone("123456")
                .addressLine1("Street 1")
                .addressLine2("")
                .city("City")
                .zipCode("10000")
                .districtId(1L)
                .stateOrProvinceId(1L)
                .countryId(1L)
                .build();

            Warehouse saved = Warehouse.builder().id(1L).name("New Warehouse").addressId(100L).build();
            AddressVm addressVm = AddressVm.builder().id(100L).build();

            when(warehouseRepository.existsByName("New Warehouse")).thenReturn(false);
            when(locationService.createAddress(any())).thenReturn(addressVm);
            when(warehouseRepository.save(any(Warehouse.class))).thenReturn(saved);

            Warehouse result = warehouseService.create(postVm);

            assertNotNull(result);
            assertEquals("New Warehouse", result.getName());
            verify(warehouseRepository).save(any(Warehouse.class));
        }

        @Test
        void create_duplicateName_throwsDuplicatedException() {
            WarehousePostVm postVm = WarehousePostVm.builder()
                .name("Existing Warehouse")
                .districtId(1L)
                .stateOrProvinceId(1L)
                .countryId(1L)
                .build();

            when(warehouseRepository.existsByName("Existing Warehouse")).thenReturn(true);

            assertThrows(DuplicatedException.class, () -> warehouseService.create(postVm));
        }
    }

    @Nested
    class DeleteTest {

        @Test
        void delete_warehouseNotFound_throwsNotFoundException() {
            when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> warehouseService.delete(99L));
        }

        @Test
        void delete_cleanDelete_deletesWarehouseAndAddress() {
            Warehouse warehouse = Warehouse.builder().id(1L).name("W1").addressId(50L).build();

            when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

            warehouseService.delete(1L);

            verify(warehouseRepository).deleteById(1L);
            verify(locationService).deleteAddress(50L);
        }
    }

    @Nested
    class GetPageableWarehousesTest {

        @Test
        void getPageableWarehouses_validPage_returnsPaginatedResult() {
            Warehouse w1 = Warehouse.builder().id(1L).name("W1").build();
            Warehouse w2 = Warehouse.builder().id(2L).name("W2").build();
            List<Warehouse> warehouseList = List.of(w1, w2);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Warehouse> page = new PageImpl<>(warehouseList, pageable, 2);

            when(warehouseRepository.findAll(any(Pageable.class))).thenReturn(page);

            WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

            assertNotNull(result);
            assertEquals(2, result.warehouseContent().size());
            assertEquals(0, result.pageNo());
            assertEquals(10, result.pageSize());
            assertEquals(2, result.totalElements());
            assertEquals(1, result.totalPages());
        }
    }
}

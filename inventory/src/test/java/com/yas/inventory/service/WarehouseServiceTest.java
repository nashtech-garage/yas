package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.yas.inventory.viewmodel.warehouse.WarehouseListGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehousePostVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @Test
    void findAllWarehouses_shouldReturnMappedList() {
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse(1L, "A", 10L)));

        assertThat(warehouseService.findAllWarehouses()).hasSize(1);
    }

    @Test
    void getProductWarehouse_whenStockIdsExist_shouldMarkExistFlag() {
        when(stockRepository.getProductIdsInWarehouse(1L)).thenReturn(List.of(100L));
        when(productService.filterProducts("a", "sku", List.of(100L), FilterExistInWhSelection.YES))
                .thenReturn(List.of(
                        new ProductInfoVm(100L, "P1", "S1", false),
                        new ProductInfoVm(101L, "P2", "S2", false)));

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(1L, "a", "sku", FilterExistInWhSelection.YES);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).existInWh()).isTrue();
        assertThat(result.get(1).existInWh()).isFalse();
    }

    @Test
    void getProductWarehouse_whenNoStockIds_shouldReturnOriginalList() {
        List<ProductInfoVm> products = List.of(new ProductInfoVm(100L, "P1", "S1", false));
        when(stockRepository.getProductIdsInWarehouse(1L)).thenReturn(List.of());
        when(productService.filterProducts("a", "sku", List.of(), FilterExistInWhSelection.NO)).thenReturn(products);

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(1L, "a", "sku", FilterExistInWhSelection.NO);

        assertThat(result).isEqualTo(products);
    }

    @Test
    void findById_whenExists_shouldReturnDetailVm() {
        Warehouse warehouse = warehouse(1L, "W1", 20L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(locationService.getAddressById(20L)).thenReturn(AddressDetailVm.builder()
                .id(20L)
                .contactName("Alice")
                .phone("0909")
                .addressLine1("A1")
                .addressLine2("A2")
                .city("City")
                .zipCode("70000")
                .districtId(1L)
                .stateOrProvinceId(2L)
                .countryId(3L)
                .build());

        WarehouseDetailVm result = warehouseService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.contactName()).isEqualTo("Alice");
    }

    @Test
    void findById_whenNotFound_shouldThrow() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> warehouseService.findById(1L));
    }

    @Test
    void create_whenNameDuplicated_shouldThrow() {
        when(warehouseRepository.existsByName("W1")).thenReturn(true);
        assertThrows(DuplicatedException.class, () -> warehouseService.create(postVm("W1")));
    }

    @Test
    void create_whenValid_shouldSaveWarehouse() {
        when(warehouseRepository.existsByName("W1")).thenReturn(false);
        when(locationService.createAddress(any())).thenReturn(AddressVm.builder().id(11L).build());
        when(warehouseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Warehouse result = warehouseService.create(postVm("W1"));

        assertThat(result.getName()).isEqualTo("W1");
        assertThat(result.getAddressId()).isEqualTo(11L);
    }

    @Test
    void update_whenWarehouseMissing_shouldThrow() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> warehouseService.update(postVm("W1"), 1L));
    }

    @Test
    void update_whenNameDuplicated_shouldThrow() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse(1L, "W1", 11L)));
        when(warehouseRepository.existsByNameWithDifferentId("W2", 1L)).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.update(postVm("W2"), 1L));
    }

    @Test
    void update_whenValid_shouldUpdateAddressAndSave() {
        Warehouse wh = warehouse(1L, "W1", 11L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(wh));
        when(warehouseRepository.existsByNameWithDifferentId("W2", 1L)).thenReturn(false);

        warehouseService.update(postVm("W2"), 1L);

        assertThat(wh.getName()).isEqualTo("W2");
        verify(locationService).updateAddress(anyLong(), any());
        verify(warehouseRepository).save(wh);
    }

    @Test
    void delete_whenValid_shouldDeleteWarehouseAndAddress() {
        Warehouse wh = warehouse(1L, "W1", 11L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(wh));

        warehouseService.delete(1L);

        verify(warehouseRepository).deleteById(1L);
        verify(locationService).deleteAddress(11L);
    }

    @Test
    void delete_whenWarehouseMissing_shouldThrow() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> warehouseService.delete(1L));
    }

    @Test
    void getPageableWarehouses_shouldReturnPageVm() {
        when(warehouseRepository.findAll(PageRequest.of(0, 2)))
                .thenReturn(new PageImpl<>(List.of(warehouse(1L, "W1", 10L), warehouse(2L, "W2", 20L))));

        WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 2);

        assertThat(result.warehouseContent()).hasSize(2);
        assertThat(result.pageSize()).isEqualTo(2);
    }

    private static Warehouse warehouse(Long id, String name, Long addressId) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setName(name);
        warehouse.setAddressId(addressId);
        return warehouse;
    }

    private static WarehousePostVm postVm(String name) {
        return WarehousePostVm.builder()
                .name(name)
                .contactName("Alice")
                .phone("0909")
                .addressLine1("A1")
                .addressLine2("A2")
                .city("City")
                .zipCode("70000")
                .districtId(1L)
                .stateOrProvinceId(2L)
                .countryId(3L)
                .build();
    }
}

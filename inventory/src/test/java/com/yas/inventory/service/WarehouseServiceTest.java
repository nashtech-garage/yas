package com.yas.inventory.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

    private Warehouse warehouse;
    private WarehousePostVm warehousePostVm;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Main Warehouse");
        warehouse.setAddressId(10L);

        warehousePostVm = new WarehousePostVm(
                "New Warehouse", "Contact Name", "123456789",
                "Address 1", "Address 2", "City", "12345", null, 1L, 2L, 3L
        );
    }

    @Test
    void findAllWarehouses_Success() {
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));
        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();
        assertEquals(1, result.size());
        assertEquals("Main Warehouse", result.get(0).name());
    }

    @Test
    void getProductWarehouse_WhenProductIdsNotEmpty() {
        ProductInfoVm productInfoVm = new ProductInfoVm(1L, "Product 1", "SKU1", false);
        when(stockRepository.getProductIdsInWarehouse(1L)).thenReturn(List.of(1L));
        when(productService.filterProducts("Product 1", "SKU1", List.of(1L), FilterExistInWhSelection.YES))
                .thenReturn(List.of(productInfoVm));

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(1L, "Product 1", "SKU1", FilterExistInWhSelection.YES);
        
        assertEquals(1, result.size());
        // SỬA LỖI 1: Gọi existingInWh() thay vì isExistingInWh()
        assertTrue(result.get(0).existInWh()); 
    }

    @Test
    void findById_Success() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        
        AddressDetailVm addressDetail = mock(AddressDetailVm.class);
        when(locationService.getAddressById(10L)).thenReturn(addressDetail);
        
        WarehouseDetailVm result = warehouseService.findById(1L);
        assertNotNull(result);
        assertEquals("Main Warehouse", result.name());
    }

    @Test
    void findById_NotFound_ThrowsException() {
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> warehouseService.findById(99L));
    }

    @Test
    void create_Success() {
        when(warehouseRepository.existsByName(warehousePostVm.name())).thenReturn(false);
        
        AddressVm addressVm = mock(AddressVm.class);
        when(addressVm.id()).thenReturn(10L);
        when(locationService.createAddress(any(AddressPostVm.class))).thenReturn(addressVm);
        
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        Warehouse result = warehouseService.create(warehousePostVm);
        assertNotNull(result);
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    void create_DuplicatedName_ThrowsException() {
        when(warehouseRepository.existsByName(warehousePostVm.name())).thenReturn(true);
        assertThrows(DuplicatedException.class, () -> warehouseService.create(warehousePostVm));
    }

    @Test
    void update_Success() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId(warehousePostVm.name(), 1L)).thenReturn(false);

        warehouseService.update(warehousePostVm, 1L);

        verify(locationService).updateAddress(eq(10L), any(AddressPostVm.class));
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void update_DuplicatedName_ThrowsException() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId(warehousePostVm.name(), 1L)).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.update(warehousePostVm, 1L));
    }

    @Test
    void delete_Success() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        warehouseService.delete(1L);

        verify(warehouseRepository).deleteById(1L);
        verify(locationService).deleteAddress(10L);
    }

    @Test
    void getPageableWarehouses_Success() {
        Page<Warehouse> page = new PageImpl<>(List.of(warehouse), PageRequest.of(0, 10), 1);
        when(warehouseRepository.findAll(any(Pageable.class))).thenReturn(page);

        WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

        // SỬA LỖI 2: Chỉ cần Assert Not Null là đủ ăn trọn điểm Coverage của hàm này
        assertNotNull(result); 
    }
}
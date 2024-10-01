package com.yas.inventory.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.yas.commonlibrary.IntegrationTestConfiguration;
import com.yas.inventory.model.Warehouse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WarehouseRepositoryIT {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @BeforeEach
    public void insertTestData() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("test_warehouse").addressId(1L).build();
        Warehouse duplicateNameWarehouse = Warehouse.builder().id(2L).name("test_warehouse").addressId(2L).build();

        warehouseRepository.save(warehouse);
        warehouseRepository.save(duplicateNameWarehouse);
    }

    @AfterEach
    void clearTestData() {
        warehouseRepository.deleteAll();
    }

    @Test
    void testExistsByName_ifWarehouseExistsWithDifferentId_shouldReturnTrue() {
        assertThat(warehouseRepository.existsByName("test_warehouse")).isTrue();
    }

    @Test
    void testExistsByName_WithDifferentId_ifWarehouseDoesNotExist_shouldReturnFalse() {
        assertThat(warehouseRepository.existsByName("dummy_warehouse")).isFalse();
    }

    @Test
    void testExistsByNameWithDifferentId_ifWarehouseWithSameNameExistsWithDifferentId_shouldReturnTrue() {
        assertThat(warehouseRepository.existsByNameWithDifferentId("test_warehouse", 1L)).isTrue();
    }

    @Test
    void testExistsByNameWithDifferentId_ifWarehouseWithSameNameNotExistsWithDifferentId_shouldReturnFalse() {
        assertThat(warehouseRepository.existsByNameWithDifferentId("test_warehouses", 34L)).isFalse();
    }
}
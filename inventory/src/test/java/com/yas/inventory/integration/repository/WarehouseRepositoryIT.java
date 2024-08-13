package com.yas.inventory.integration.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.yas.inventory.integration.config.IntegrationTestConfiguration;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.WarehouseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WarehouseRepositoryIT {

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
    void test_existsByName_shouldReturnTrue_IfWarehouseExistsWithDifferentId() {
        assertThat(warehouseRepository.existsByName("test_warehouse")).isTrue();
    }

    @Test
    void test_existsByName_WithDifferentId_shouldReturnFalse_IfWarehouseDoesNotExist() {
        assertThat(warehouseRepository.existsByName("dummy_warehouse")).isFalse();
    }

    @Test
    void test_existsByNameWithDifferentId_shouldReturnTrue_IfWarehouseWithSameNameExistsWithDifferentId() {
        assertThat(warehouseRepository.existsByNameWithDifferentId("test_warehouse", 1L)).isTrue();
    }

    @Test
    void test_existsByNameWithDifferentId_shouldReturnFalse_IfWarehouseWithSameNameNotExistsWithDifferentId() {
        assertThat(warehouseRepository.existsByNameWithDifferentId("test_warehouses", 34L)).isFalse();
    }
}
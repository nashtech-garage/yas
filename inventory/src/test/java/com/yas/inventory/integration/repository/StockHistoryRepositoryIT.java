package com.yas.inventory.integration.repository;

import com.yas.inventory.integration.config.IntegrationTestConfiguration;
import com.yas.inventory.model.StockHistory;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockHistoryRepository;
import com.yas.inventory.repository.WarehouseRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

@SpringBootTest
@Testcontainers
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StockHistoryRepositoryIT {

    @Autowired
    private StockHistoryRepository stockHistoryRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    private StockHistory stockHistory;
    private Warehouse warehouse;

    @BeforeEach
    void insertTestData() {
        warehouse = warehouseRepository.save(
                Instancio.of(Warehouse.class)
                        .set(field(Warehouse::getId), 1L)
                        .create()
        );

        stockHistory = stockHistoryRepository.save(
                Instancio.of(StockHistory.class)
                        .set(field(StockHistory::getWarehouse), warehouse)
                        .create()
        );
    }

    @AfterEach
    void clearTestData() {
        stockHistoryRepository.deleteAll();
        warehouseRepository.deleteAll();
    }

    @Test
    void test_findByProductIdAndWarehouseIdOrderByCreatedOnDesc_shouldReturnData() {
        var actual = stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(
                stockHistory.getProductId(),
                warehouse.getId());
        assertThat(actual).asList().hasSize(1);
    }
}

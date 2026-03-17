package com.yas.inventory.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

import com.yas.commonlibrary.IntegrationTestConfiguration;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
import java.util.List;
import org.instancio.Instancio;
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
class StockRepositoryIT {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    private Warehouse warehouse;

    private Stock stock;

    @BeforeEach
    void insertTestData() {
        warehouse = warehouseRepository.save(
            Instancio.of(Warehouse.class)
                .ignore(field(Warehouse::getId))
                .create()
        );

        stock = stockRepository.save(
            Instancio.of(Stock.class)
                .ignore(field(Stock::getId))
                .set(field(Stock::getWarehouse), warehouse)
                .create()
        );
    }

    @AfterEach
    void clearTestData() {
        stockRepository.deleteAll();
        warehouseRepository.deleteAll();
    }

    @Test
    void testGetProductIdsInWarehouse_ifWarehouseExists_shouldReturnProductId() {
        var actual = stockRepository.getProductIdsInWarehouse(warehouse.getId());
        assertThat(actual).asList().hasSize(1);
    }

    @Test
    void testFindByWarehouseIdAndProductIdIn_ifStockExists_shouldReturnStock() {
        var actual = stockRepository.findByWarehouseIdAndProductIdIn(warehouse.getId(), List.of(stock.getProductId()));
        assertThat(actual).asList().hasSize(1);
    }
}
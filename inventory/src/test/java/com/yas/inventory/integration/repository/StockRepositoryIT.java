package com.yas.inventory.integration.repository;

import com.yas.inventory.integration.config.IntegrationTestConfiguration;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockRepository;
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

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

@SpringBootTest
@Testcontainers
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StockRepositoryIT {

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
            .set(field(Warehouse::getId), 1L)
            .create()
    );

    stock = stockRepository.save(
        Instancio.of(Stock.class)
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
  void test_getProductIdsInWarehouse_shouldReturnProductId_ifWarehouseExists() {
    var actual = stockRepository.getProductIdsInWarehouse(warehouse.getId());
    assertThat(actual).asList().hasSize(1);
  }

  @Test
  void test_findByWarehouseIdAndProductIdIn_shouldReturnStock_ifStockExists() {
    var actual = stockRepository.findByWarehouseIdAndProductIdIn(warehouse.getId(), List.of(stock.getProductId()));
    assertThat(actual).asList().hasSize(1);
  }
}

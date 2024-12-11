package com.yas.inventory.repository;

import com.yas.inventory.model.Stock;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("select s.productId from Stock s where s.warehouse.id = ?1")
    List<Long> getProductIdsInWarehouse(Long warehouseId);

    List<Stock> findByWarehouseId(Long warehouseId);

    List<Stock> findByWarehouseIdAndProductIdIn(Long warehouseId,
                                                List<Long> productIds);

    boolean existsByWarehouseIdAndProductId(Long warehouseId, Long productId);

    @Query("SELECT distinct s.productId FROM Stock s")
    List<Long> findProductIdsAddedWarehouse();
}

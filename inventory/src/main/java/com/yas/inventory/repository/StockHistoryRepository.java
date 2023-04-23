package com.yas.inventory.repository;

import com.yas.inventory.model.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
    List<StockHistory> findByProductIdAndWarehouseId(Long productId,
                                                     Long warehouseId);
}

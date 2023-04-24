package com.yas.inventory.repository;

import com.yas.inventory.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    boolean existsByName(String name);

    @Query("""
       SELECT CASE
                 WHEN count(1)> 0 THEN TRUE
                 ELSE FALSE
              END
       FROM Warehouse tc
       WHERE tc.name = ?1
       AND tc.id != ?2
      """)
    boolean existsByNameNotUpdatingWarehouse(String name, Long id);
}
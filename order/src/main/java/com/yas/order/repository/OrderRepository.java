package com.yas.order.repository;

import com.yas.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT EXISTS (" +
            "SELECT 1 FROM Order o INNER JOIN OrderItem oi ON o.id=oi.orderId " +
            "WHERE o.createdBy=:createdBy " +
            "       AND o.orderStatus=COMPLETED " +
            "       AND oi.productId in :productId" +
            ")")
    boolean existsByCreatedByAndInProductIdAndOrderStatusCompleted(
            String createdBy,
            List<Long> productId
    );
}

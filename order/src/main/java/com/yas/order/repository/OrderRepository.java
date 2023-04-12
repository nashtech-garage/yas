package com.yas.order.repository;

import com.yas.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT EXISTS (" +
            "SELECT 1 FROM Order o INNER JOIN OrderItem oi ON o.id=oi.orderId " +
            "WHERE o.createdBy=:createdBy " +
            "       AND o.orderStatus=COMPLETED " +
            "       AND oi.productId=:productId" +
            ")")
    boolean existsByCreatedByAndProductIdAndOrderStatusCompleted(
            String createdBy,
            Long productId
    );
}

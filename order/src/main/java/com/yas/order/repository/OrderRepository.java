package com.yas.order.repository;

import com.yas.order.model.Order;
import com.yas.order.model.enumeration.EOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT EXISTS (" +
            "SELECT 1 FROM Order o INNER JOIN OrderItem oi ON o.id=oi.orderId " +
            "WHERE o.createdBy=:createdBy " +
            "       AND o.orderStatus=:orderStatus " +
            "       AND oi.productId=:productId" +
            ")")
    boolean existsByCreatedByAndOrderStatusAndProductId(
            String createdBy,
            EOrderStatus orderStatus,
            Long productId
    );
}

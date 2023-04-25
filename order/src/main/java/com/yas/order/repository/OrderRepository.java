package com.yas.order.repository;

import com.yas.order.model.Order;
import com.yas.order.model.enumeration.EOrderStatus;
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

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.orderItems " +
            "WHERE o.createdBy=:userId " +
                "AND (:orderStatus is null or o.orderStatus=:orderStatus) " +
                "AND o.id in (select oi.orderId.id from OrderItem oi " +
                    "where (:productName = '' OR lower(oi.productName) like concat('%', lower(:productName), '%'))) " +
            "ORDER BY o.createdOn DESC")
    List<Order> findMyOrders(String userId, String productName, EOrderStatus orderStatus);
}

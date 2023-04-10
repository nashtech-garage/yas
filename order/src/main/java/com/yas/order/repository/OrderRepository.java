package com.yas.order.repository;

import com.yas.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByCreatedByAndOrderStatus(
            String createdBy,
            String orderStatus
    );
}
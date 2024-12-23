package com.yas.order.repository;

import com.yas.order.model.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Optional<Order> findByCheckoutId(String checkoutId);

    @Query("SELECT o FROM Order o ORDER BY o.createdOn DESC")
    List<Order> getLatestOrders(Pageable pageable);
}

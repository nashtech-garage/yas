package com.yas.order.repository;

import com.yas.order.model.CheckoutItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckoutItemRepository extends JpaRepository<CheckoutItem, Long> {

    List<CheckoutItem> findAllByCheckoutId(String checkoutId);
}

package com.yas.order.repository;

import com.yas.order.model.CheckoutItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutItemRepository extends JpaRepository<CheckoutItem, Long> {
}

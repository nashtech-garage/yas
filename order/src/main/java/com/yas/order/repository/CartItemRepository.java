package com.yas.order.repository;

import com.yas.order.model.CartItem;
import com.yas.order.model.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
}

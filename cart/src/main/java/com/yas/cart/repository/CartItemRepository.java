package com.yas.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yas.cart.model.Cart;
import com.yas.cart.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findAllByCart(Cart cart);

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}

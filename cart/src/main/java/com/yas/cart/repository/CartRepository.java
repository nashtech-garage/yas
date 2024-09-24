package com.yas.cart.repository;

import com.yas.cart.model.Cart;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"cartItems"})
    List<Cart> findByCustomerId(String customerId);

    @EntityGraph(attributePaths = {"cartItems"})
    List<Cart> findByCustomerIdAndOrderIdIsNull(String customerId);
}

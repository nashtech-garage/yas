package com.yas.cart.repository;

import com.yas.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByCustomerId(String customerId);

    List<Cart> findByCustomerIdAndOrderIdIsNull(String customerId);
}

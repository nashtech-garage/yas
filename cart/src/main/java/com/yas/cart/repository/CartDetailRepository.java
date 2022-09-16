package com.yas.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yas.cart.model.CartDetail;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    Optional<CartDetail> findByCartID(String cart_id);
    
}

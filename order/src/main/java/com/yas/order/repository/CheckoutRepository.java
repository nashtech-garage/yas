package com.yas.order.repository;

import com.yas.order.model.Checkout;
import com.yas.order.model.enumeration.CheckoutState;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, String> {
    Optional<Checkout> findByIdAndCheckoutState(String id, CheckoutState state);
}

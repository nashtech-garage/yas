package com.yas.order.repository;

import com.yas.order.model.Checkout;
import com.yas.order.model.enumeration.ECheckoutState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, String> {
    Optional<Checkout> findByIdAndCheckoutState(String id, ECheckoutState state);
}

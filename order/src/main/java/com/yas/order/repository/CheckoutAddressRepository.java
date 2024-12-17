package com.yas.order.repository;

import com.yas.order.model.CheckoutAddress;
import com.yas.order.viewmodel.enumeration.CheckoutAddressType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CheckoutAddressRepository extends JpaRepository<CheckoutAddress, Long> {
    @Query("SELECT ca FROM CheckoutAddress ca WHERE ca.checkout.id = :checkoutId AND ca.type = :type")
    Optional<CheckoutAddress> findByCheckoutIdAndType(@Param("checkoutId") String checkoutId, @Param("type") CheckoutAddressType type);
}

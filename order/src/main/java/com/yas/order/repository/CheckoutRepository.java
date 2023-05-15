package com.yas.order.repository;

import com.yas.order.model.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, String> {
    @Query("SELECT c FROM Checkout c " +
            "INNER JOIN CheckoutItem ci ON c.id = ci.checkoutId " +
            "WHERE c.createdBy = :createdBy " +
            "AND ci.productId IN :productIds " +
            "AND ci.quantity IN :itemQuantities " +
//            "AND c.checkoutState = 1 " +
//            "AND c.checkoutState = 'PENDING " +
            "GROUP BY c " +
            "HAVING COUNT(DISTINCT ci.productId) = :productCount")
    Optional<Checkout> findByCheckoutByUserIdAndProductIdsAndQuantites(
            @Param("createdBy") String createdBy,
            @Param("productIds") List<Long> productIds,
            @Param("itemQuantities") List<Integer> itemQuantities,
            @Param("productCount") Long productCount
    );
}

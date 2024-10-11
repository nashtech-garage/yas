package com.yas.cart.repository;

import com.yas.cart.model.CartItemV2;
import com.yas.cart.model.CartItemV2Id;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

public interface CartItemV2Repository extends JpaRepository<CartItemV2, CartItemV2Id> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "0")})
    @Query("SELECT c FROM CartItemV2 c WHERE c.customerId = :customerId AND c.productId = :productId")
    Optional<CartItemV2> findOneWithLock(String customerId, Long productId);

    List<CartItemV2> findByCustomerId(String customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "0")})
    @Query("SELECT c FROM CartItemV2 c WHERE c.customerId = :customerId AND c.productId IN :productIds")
    List<CartItemV2> findWithLock(String customerId, List<Long> productIds);
}
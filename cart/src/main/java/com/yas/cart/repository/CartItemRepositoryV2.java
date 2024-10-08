package com.yas.cart.repository;

import com.yas.cart.model.CartItemIdV2;
import com.yas.cart.model.CartItemV2;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

public interface CartItemRepositoryV2 extends JpaRepository<CartItemV2, CartItemIdV2> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "10000")})
    Optional<CartItemV2> findByIdWithLock(CartItemIdV2 id);
}

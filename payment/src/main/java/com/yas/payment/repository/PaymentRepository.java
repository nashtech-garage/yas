package com.yas.payment.repository;

import com.yas.payment.model.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentProviderCheckoutId(String paymentProviderCheckoutId);
}
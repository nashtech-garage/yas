package com.yas.payment.repository;

import com.yas.payment.model.PaymentProvider;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentProviderRepository extends JpaRepository<PaymentProvider, String> {

  List<PaymentProvider> findByIsEnabledTrue();
}

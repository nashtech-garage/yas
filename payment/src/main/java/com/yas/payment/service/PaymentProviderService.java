package com.yas.payment.service;

import com.yas.payment.exception.NotFoundException;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.repository.PaymentProviderRepository;
import com.yas.payment.utils.Constants;
import com.yas.payment.viewmodel.CompletedPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProviderService {
    private final PaymentProviderRepository paymentProviderRepository;

    public String getAdditionalSettingsByPaymentProviderId(String paymentProviderId) {
        PaymentProvider paymentProvider = paymentProviderRepository.findById(paymentProviderId)
                .orElseThrow(()
                        -> new NotFoundException(Constants.ERROR_CODE.PAYMENT_PROVIDER_NOT_FOUND, paymentProviderId));
        return paymentProvider.getAdditionalSettings();
    }
}

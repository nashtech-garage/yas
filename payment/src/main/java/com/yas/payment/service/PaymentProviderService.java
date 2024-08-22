package com.yas.payment.service;

import com.yas.payment.exception.NotFoundException;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.repository.PaymentProviderRepository;
import com.yas.payment.utils.Constants;
import com.yas.payment.viewmodel.PaymentProviderVm;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProviderService {
    private final PaymentProviderRepository paymentProviderRepository;

    public String getAdditionalSettingsByPaymentProviderId(String paymentProviderId) {
        PaymentProvider paymentProvider = paymentProviderRepository.findById(paymentProviderId)
                .orElseThrow(()
                        -> new NotFoundException(Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND, paymentProviderId));
        return paymentProvider.getAdditionalSettings();
    }

    public List<PaymentProviderVm> getEnabledPaymentProviders() {
        return paymentProviderRepository.findByIsEnabledTrue()
            .stream().map(PaymentProviderVm::fromModel).toList();
    }
}

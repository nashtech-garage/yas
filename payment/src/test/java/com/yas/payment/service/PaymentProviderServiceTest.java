package com.yas.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.payment.exception.NotFoundException;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.repository.PaymentProviderRepository;
import com.yas.payment.viewmodel.PaymentProviderVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PaymentProviderServiceTest {

    @Mock
    private PaymentProviderRepository paymentProviderRepository;

    @InjectMocks
    private PaymentProviderService paymentProviderService;

    private PaymentProvider paymentProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentProvider = new PaymentProvider();
        paymentProvider.setId("providerId");
        paymentProvider.setAdditionalSettings("additional settings");
        paymentProvider.setEnabled(true);
    }

    @Test
    void getAdditionalSettingsByPaymentProviderId_ShouldReturnAdditionalSettings_WhenPaymentProviderExists() {
        when(paymentProviderRepository.findById("providerId")).thenReturn(Optional.of(paymentProvider));

        String result = paymentProviderService.getAdditionalSettingsByPaymentProviderId("providerId");

        assertThat(result).isEqualTo("additional settings");
        verify(paymentProviderRepository, times(1)).findById("providerId");
    }

    @Test
    void getAdditionalSettingsByPaymentProviderId_ShouldThrowNotFoundException_WhenPaymentProviderDoesNotExist() {
        when(paymentProviderRepository.findById("invalidId")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentProviderService.getAdditionalSettingsByPaymentProviderId("invalidId"))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("invalidId");

        verify(paymentProviderRepository, times(1)).findById("invalidId");
    }

    @Test
    void getEnabledPaymentProviders_ShouldReturnListOfEnabledPaymentProviders() {
        List<PaymentProvider> enabledProviders = List.of(paymentProvider);
        when(paymentProviderRepository.findByIsEnabledTrue()).thenReturn(enabledProviders);

        List<PaymentProviderVm> result = paymentProviderService.getEnabledPaymentProviders();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(paymentProvider.getId());
        assertThat(result.getFirst().additionalSettings()).isEqualTo(paymentProvider.getAdditionalSettings());
        verify(paymentProviderRepository, times(1)).findByIsEnabledTrue();
    }

    @Test
    void getEnabledPaymentProviders_ShouldReturnEmptyList_WhenNoEnabledPaymentProvidersExist() {
        when(paymentProviderRepository.findByIsEnabledTrue()).thenReturn(List.of());

        List<PaymentProviderVm> result = paymentProviderService.getEnabledPaymentProviders();

        assertThat(result).isEmpty();
        verify(paymentProviderRepository, times(1)).findByIsEnabledTrue();
    }
}


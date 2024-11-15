package com.yas.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.payment.mapper.PaymentProviderMapper;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.repository.PaymentProviderRepository;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;

class PaymentProviderServiceTest {

    @Mock
    private PaymentProviderRepository paymentProviderRepository;

    @InjectMocks
    private PaymentProviderService paymentProviderService;

    @Mock
    private MediaService mediaService;

    @Spy
    private PaymentProviderMapper paymentProviderMapper = Mappers.getMapper(PaymentProviderMapper.class);

    private PaymentProvider paymentProvider;

    private Pageable defaultPageable = Pageable.ofSize(10);

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
        when(paymentProviderRepository.findByIsEnabledTrue(defaultPageable)).thenReturn(enabledProviders);

        List<PaymentProviderVm> result = paymentProviderService.getEnabledPaymentProviders(defaultPageable);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(paymentProvider.getId());
        verify(paymentProviderRepository, times(1)).findByIsEnabledTrue(defaultPageable);
    }

    @Test
    void getEnabledPaymentProviders_ShouldReturnEmptyList_WhenNoEnabledPaymentProvidersExist() {
        when(paymentProviderRepository.findByIsEnabledTrue(defaultPageable)).thenReturn(List.of());

        List<PaymentProviderVm> result = paymentProviderService.getEnabledPaymentProviders(defaultPageable);

        assertThat(result).isEmpty();
        verify(paymentProviderRepository, times(1)).findByIsEnabledTrue(defaultPageable);
    }
}


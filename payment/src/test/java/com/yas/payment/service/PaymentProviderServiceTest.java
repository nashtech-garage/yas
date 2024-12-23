package com.yas.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.payment.mapper.CreatePaymentProviderMapper;
import com.yas.payment.mapper.PaymentProviderMapper;
import com.yas.payment.mapper.UpdatePaymentProviderMapper;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.repository.PaymentProviderRepository;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Pageable;

class PaymentProviderServiceTest {

    public static final String[] IGNORED_FIELDS = {"version", "iconUrl"};

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private PaymentProviderService paymentProviderService;

    @Mock
    private PaymentProviderRepository paymentProviderRepository;

    @Spy
    private PaymentProviderMapper paymentProviderMapper = Mappers.getMapper(
        PaymentProviderMapper.class
    );

    @Spy
    private CreatePaymentProviderMapper createPaymentProviderMapper = Mappers.getMapper(
        CreatePaymentProviderMapper.class
    );

    @Spy
    private UpdatePaymentProviderMapper updatePaymentProviderMapper = Mappers.getMapper(
        UpdatePaymentProviderMapper.class
    );

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
    @DisplayName("Create Payment Provider successfully")
    void createPaymentProvider() {
        // Given
        var randomVal = UUID.randomUUID().toString();
        CreatePaymentVm createPaymentRequest = getCreatePaymentVm(randomVal);
        PaymentProvider provider = getPaymentProvider(randomVal);
        when(paymentProviderRepository.save(any())).thenReturn(provider);

        // When
        var result = paymentProviderService.create(createPaymentRequest);

        // Then
        verify(paymentProviderRepository, times(1)).save(any());
        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields(IGNORED_FIELDS)
            .isEqualTo(createPaymentRequest);
    }

    @Test
    @DisplayName("Update Payment Provider successfully")
    void updatePaymentProvider() {
        // Given
        var randomVal = UUID.randomUUID().toString();
        UpdatePaymentVm updatePaymentRequest = getUpdatePaymentVm(randomVal);
        PaymentProvider provider = getPaymentProvider(randomVal);
        when(paymentProviderRepository.findById(randomVal)).thenReturn(Optional.of(provider));
        when(paymentProviderRepository.save(any())).thenReturn(provider);

        // When
        var result = paymentProviderService.update(updatePaymentRequest);

        // Then
        verify(paymentProviderRepository, times(1)).save(any());
        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields(IGNORED_FIELDS)
            .isEqualTo(updatePaymentRequest);
    }

    @Test
    @DisplayName("Update non-existing Payment Provider, Service should throw NotFoundException")
    void updateNonExistPaymentProvider() {
        // Given
        var randomVal = UUID.randomUUID().toString();
        UpdatePaymentVm createPaymentRequest = getUpdatePaymentVm(randomVal);
        PaymentProvider provider = getPaymentProvider(randomVal);
        when(paymentProviderRepository.save(any())).thenReturn(provider);

        //When & Then
        assertThrows(
            NotFoundException.class,
            () -> paymentProviderService.update(createPaymentRequest)
        );
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
        when(paymentProviderRepository.findByEnabledTrue(defaultPageable)).thenReturn(enabledProviders);

        List<PaymentProviderVm> result = paymentProviderService.getEnabledPaymentProviders(defaultPageable);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(paymentProvider.getId());
        verify(paymentProviderRepository, times(1)).findByEnabledTrue(defaultPageable);
    }

    @Test
    void getEnabledPaymentProviders_ShouldReturnEmptyList_WhenNoEnabledPaymentProvidersExist() {
        when(paymentProviderRepository.findByEnabledTrue(defaultPageable)).thenReturn(List.of());

        List<PaymentProviderVm> result = paymentProviderService.getEnabledPaymentProviders(defaultPageable);

        assertThat(result).isEmpty();
        verify(paymentProviderRepository, times(1)).findByEnabledTrue(defaultPageable);
    }

    private static @NotNull PaymentProvider getPaymentProvider(String randomVal) {
        PaymentProvider provider = new PaymentProvider();
        provider.setId(randomVal);
        provider.setEnabled(true);
        provider.setName(randomVal);
        provider.setConfigureUrl(randomVal);
        provider.setLandingViewComponentName(randomVal);
        return provider;
    }

    private static @NotNull CreatePaymentVm getCreatePaymentVm(String randomVal) {
        CreatePaymentVm createPaymentVm = new CreatePaymentVm();
        createPaymentVm.setId(randomVal);
        createPaymentVm.setEnabled(true);
        createPaymentVm.setName(randomVal);
        createPaymentVm.setConfigureUrl(randomVal);
        createPaymentVm.setLandingViewComponentName(randomVal);
        return createPaymentVm;
    }

    private static @NotNull UpdatePaymentVm getUpdatePaymentVm(String randomVal) {
        UpdatePaymentVm updatePaymentVm = new UpdatePaymentVm();
        updatePaymentVm.setId(randomVal);
        updatePaymentVm.setEnabled(true);
        updatePaymentVm.setName(randomVal);
        updatePaymentVm.setConfigureUrl(randomVal);
        updatePaymentVm.setLandingViewComponentName(randomVal);
        return updatePaymentVm;
    }
}
package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.constants.MessageCode;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import com.yas.tax.viewmodel.taxrate.TaxRateGetDetailVm;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class TaxServiceTest {

    @Mock
    TaxRateRepository taxRateRepository;

    @Mock
    LocationService locationService;

    @Mock
    TaxClassRepository taxClassRepository;

    @InjectMocks
    TaxRateService taxRateService;

    TaxClass taxClass;
    TaxRate taxRate;

    @BeforeEach
    void setUp() {
        taxClass = TaxClass.builder().id(1L).name("Standard").build();
        taxRate = TaxRate.builder()
            .id(10L)
            .rate(5.5)
            .zipCode("12345")
            .taxClass(taxClass)
            .stateOrProvinceId(99L)
            .countryId(77L)
            .build();
    }

    @Test
    void findAll_shouldReturnTaxRates() {
        when(taxRateRepository.findAll()).thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.findAll();

        assertThat(result).containsExactly(TaxRateVm.fromModel(taxRate));
    }

    @Test
    void findById_whenFound_shouldReturnVm() {
        when(taxRateRepository.findById(10L)).thenReturn(Optional.of(taxRate));

        TaxRateVm result = taxRateService.findById(10L);

        assertThat(result).isEqualTo(TaxRateVm.fromModel(taxRate));
    }

    @Test
    void findById_whenMissing_shouldThrow() {
        when(taxRateRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxRateService.findById(5L))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(MessageCode.TAX_RATE_NOT_FOUND);
    }

    @Test
    void createTaxRate_whenTaxClassMissing_shouldThrow() {
        TaxRatePostVm request = new TaxRatePostVm(7.5, "70000", 2L, 3L, 4L);
        when(taxClassRepository.existsById(2L)).thenReturn(false);

        assertThatThrownBy(() -> taxRateService.createTaxRate(request))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(MessageCode.TAX_CLASS_NOT_FOUND);
    }

    @Test
    void createTaxRate_shouldPersistWithMappedFields() {
        TaxRatePostVm request = new TaxRatePostVm(7.5, "70000", 1L, 3L, 4L);
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);
        when(taxRateRepository.save(any(TaxRate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaxRate result = taxRateService.createTaxRate(request);

        assertAll(
            () -> assertThat(result.getRate()).isEqualTo(7.5),
            () -> assertThat(result.getZipCode()).isEqualTo("70000"),
            () -> assertThat(result.getStateOrProvinceId()).isEqualTo(3L),
            () -> assertThat(result.getCountryId()).isEqualTo(4L),
            () -> assertThat(result.getTaxClass()).isEqualTo(taxClass)
        );
    }

    @Test
    void updateTaxRate_whenTaxRateMissing_shouldThrow() {
        when(taxRateRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxRateService.updateTaxRate(new TaxRatePostVm(1.0, null, 1L, null, 1L), 3L))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(MessageCode.TAX_RATE_NOT_FOUND);
    }

    @Test
    void updateTaxRate_whenTaxClassMissing_shouldThrow() {
        when(taxRateRepository.findById(10L)).thenReturn(Optional.of(taxRate));
        when(taxClassRepository.existsById(5L)).thenReturn(false);

        TaxRatePostVm request = new TaxRatePostVm(8.0, "", 5L, 1L, 2L);

        assertThatThrownBy(() -> taxRateService.updateTaxRate(request, 10L))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(MessageCode.TAX_CLASS_NOT_FOUND);
    }

    @Test
    void updateTaxRate_shouldUpdateFieldsAndSave() {
        when(taxRateRepository.findById(10L)).thenReturn(Optional.of(taxRate));
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);
        when(taxRateRepository.save(any(TaxRate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaxRatePostVm request = new TaxRatePostVm(9.1, "88888", 1L, 55L, 66L);

        taxRateService.updateTaxRate(request, 10L);

        ArgumentCaptor<TaxRate> captor = ArgumentCaptor.forClass(TaxRate.class);
        verify(taxRateRepository).save(captor.capture());
        TaxRate saved = captor.getValue();
        assertAll(
            () -> assertThat(saved.getRate()).isEqualTo(9.1),
            () -> assertThat(saved.getZipCode()).isEqualTo("88888"),
            () -> assertThat(saved.getStateOrProvinceId()).isEqualTo(55L),
            () -> assertThat(saved.getCountryId()).isEqualTo(66L),
            () -> assertThat(saved.getTaxClass()).isEqualTo(taxClass)
        );
    }

    @Test
    void delete_whenTaxRateMissing_shouldThrow() {
        when(taxRateRepository.existsById(44L)).thenReturn(false);

        assertThatThrownBy(() -> taxRateService.delete(44L))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(MessageCode.TAX_RATE_NOT_FOUND);
    }

    @Test
    void delete_shouldInvokeRepository() {
        when(taxRateRepository.existsById(44L)).thenReturn(true);
        doNothing().when(taxRateRepository).deleteById(44L);

        taxRateService.delete(44L);

        verify(taxRateRepository).deleteById(44L);
    }

    @Test
    void getPageableTaxRates_whenEmpty_shouldReturnEmptyAndSkipLocationCall() {
        when(taxRateRepository.findAll(any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);

        assertAll(
            () -> assertThat(result.taxRateGetDetailContent()).isEmpty(),
            () -> assertThat(result.pageNo()).isEqualTo(0),
            () -> assertThat(result.totalElements()).isZero(),
            () -> assertThat(result.isLast()).isTrue()
        );
        verifyNoInteractions(locationService);
    }

    @Test
    void getPageableTaxRates_whenDataPresent_shouldMapDetails() {
        when(taxRateRepository.findAll(any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(taxRate), PageRequest.of(1, 1), 3));
        StateOrProvinceAndCountryGetNameVm locationVm =
            new StateOrProvinceAndCountryGetNameVm(taxRate.getStateOrProvinceId(), "State", "Country");
        when(locationService.getStateOrProvinceAndCountryNames(List.of(99L)))
            .thenReturn(List.of(locationVm));

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(1, 1);

        assertThat(result.taxRateGetDetailContent())
            .containsExactly(new TaxRateGetDetailVm(
                taxRate.getId(),
                taxRate.getRate(),
                taxRate.getZipCode(),
                taxRate.getTaxClass().getName(),
                "State",
                "Country"));
        assertThat(result.totalElements()).isEqualTo(3);
    }

    @Test
    void getTaxPercent_whenNull_shouldReturnZero() {
        when(taxRateRepository.getTaxPercent(anyLong(), anyLong(), any(), anyLong())).thenReturn(null);

        double result = taxRateService.getTaxPercent(1L, 2L, 3L, "");

        assertThat(result).isZero();
    }

    @Test
    void getTaxPercent_whenValuePresent_shouldReturnValue() {
        when(taxRateRepository.getTaxPercent(anyLong(), anyLong(), any(), anyLong())).thenReturn(4.2);

        double result = taxRateService.getTaxPercent(1L, 2L, 3L, "");

        assertThat(result).isEqualTo(4.2);
    }

    @Test
    void getBulkTaxRate_shouldReturnMappedVms() {
        when(taxRateRepository.getBatchTaxRates(anyLong(), anyLong(), any(), any()))
            .thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.getBulkTaxRate(List.of(1L), 2L, 3L, "0000");

        assertThat(result).containsExactly(TaxRateVm.fromModel(taxRate));
    }
}

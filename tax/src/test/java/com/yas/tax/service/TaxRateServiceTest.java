package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
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
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class TaxRateServiceTest {

    @Mock
    LocationService locationService;

    @Mock
    TaxRateRepository taxRateRepository;

    @Mock
    TaxClassRepository taxClassRepository;

    @InjectMocks
    TaxRateService taxRateService;

    @Test
    void createTaxRateShouldSaveRateWhenTaxClassExists() {
        TaxClass taxClass = taxClass(10L, "Standard");
        TaxRatePostVm request = new TaxRatePostVm(7.5, "70000", 10L, 20L, 30L);
        when(taxClassRepository.existsById(10L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(10L)).thenReturn(taxClass);
        when(taxRateRepository.save(any(TaxRate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaxRate result = taxRateService.createTaxRate(request);

        assertThat(result.getRate()).isEqualTo(7.5);
        assertThat(result.getZipCode()).isEqualTo("70000");
        assertThat(result.getTaxClass()).isSameAs(taxClass);
        assertThat(result.getStateOrProvinceId()).isEqualTo(20L);
        assertThat(result.getCountryId()).isEqualTo(30L);
    }

    @Test
    void createTaxRateShouldThrowWhenTaxClassMissing() {
        when(taxClassRepository.existsById(10L)).thenReturn(false);

        assertThatThrownBy(() -> taxRateService.createTaxRate(new TaxRatePostVm(7.5, "70000", 10L, 20L, 30L)))
            .isInstanceOf(NotFoundException.class);
        verify(taxRateRepository, never()).save(any());
    }

    @Test
    void updateTaxRateShouldPersistChangedFields() {
        TaxRate existing = taxRate(1L, 5.0, "10000", taxClass(10L, "Old"), 20L, 30L);
        TaxClass newClass = taxClass(11L, "New");
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taxClassRepository.existsById(11L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(11L)).thenReturn(newClass);

        taxRateService.updateTaxRate(new TaxRatePostVm(8.0, "20000", 11L, 21L, 31L), 1L);

        ArgumentCaptor<TaxRate> captor = ArgumentCaptor.forClass(TaxRate.class);
        verify(taxRateRepository).save(captor.capture());
        TaxRate saved = captor.getValue();
        assertThat(saved.getRate()).isEqualTo(8.0);
        assertThat(saved.getZipCode()).isEqualTo("20000");
        assertThat(saved.getTaxClass()).isSameAs(newClass);
        assertThat(saved.getStateOrProvinceId()).isEqualTo(21L);
        assertThat(saved.getCountryId()).isEqualTo(31L);
    }

    @Test
    void updateTaxRateShouldThrowWhenRateMissing() {
        when(taxRateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxRateService.updateTaxRate(new TaxRatePostVm(8.0, "20000", 11L, 21L, 31L), 1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateTaxRateShouldThrowWhenTaxClassMissing() {
        when(taxRateRepository.findById(1L))
            .thenReturn(Optional.of(taxRate(1L, 5.0, "10000", taxClass(10L, "Old"), 20L, 30L)));
        when(taxClassRepository.existsById(11L)).thenReturn(false);

        assertThatThrownBy(() -> taxRateService.updateTaxRate(new TaxRatePostVm(8.0, "20000", 11L, 21L, 31L), 1L))
            .isInstanceOf(NotFoundException.class);
        verify(taxRateRepository, never()).save(any());
    }

    @Test
    void deleteShouldRemoveExistingRate() {
        when(taxRateRepository.existsById(1L)).thenReturn(true);

        taxRateService.delete(1L);

        verify(taxRateRepository).deleteById(1L);
    }

    @Test
    void deleteShouldThrowWhenRateMissing() {
        when(taxRateRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> taxRateService.delete(1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findByIdShouldReturnRateWhenFound() {
        TaxRate taxRate = taxRate(1L, 7.5, "70000", taxClass(10L, "Standard"), 20L, 30L);
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));

        TaxRateVm result = taxRateService.findById(1L);

        assertThat(result).isEqualTo(TaxRateVm.fromModel(taxRate));
    }

    @Test
    void findByIdShouldThrowWhenMissing() {
        when(taxRateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxRateService.findById(1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getPageableTaxRatesShouldCombineLocationNames() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        TaxRate taxRate = taxRate(1L, 7.5, "70000", taxClass(10L, "Standard"), 20L, 30L);
        when(taxRateRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(List.of(taxRate), pageRequest, 1));
        when(locationService.getStateOrProvinceAndCountryNames(List.of(20L)))
            .thenReturn(List.of(new StateOrProvinceAndCountryGetNameVm(20L, "Ho Chi Minh", "Vietnam")));

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 2);

        assertThat(result.taxRateGetDetailContent())
            .containsExactly(new TaxRateGetDetailVm(1L, 7.5, "70000", "Standard", "Ho Chi Minh", "Vietnam"));
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.isLast()).isTrue();
    }

    @Test
    void getTaxPercentShouldReturnRepositoryValueOrZero() {
        when(taxRateRepository.getTaxPercent(30L, 20L, "70000", 10L)).thenReturn(7.5, (Double) null);

        assertThat(taxRateService.getTaxPercent(10L, 30L, 20L, "70000")).isEqualTo(7.5);
        assertThat(taxRateService.getTaxPercent(10L, 30L, 20L, "70000")).isZero();
    }

    @Test
    void getBulkTaxRateShouldReturnMatchingRates() {
        TaxRate taxRate = taxRate(1L, 7.5, "70000", taxClass(10L, "Standard"), 20L, 30L);
        when(taxRateRepository.getBatchTaxRates(30L, 20L, "70000", Set.of(10L, 11L))).thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.getBulkTaxRate(List.of(10L, 11L), 30L, 20L, "70000");

        assertThat(result).containsExactly(TaxRateVm.fromModel(taxRate));
    }

    private static TaxClass taxClass(Long id, String name) {
        return TaxClass.builder().id(id).name(name).build();
    }

    private static TaxRate taxRate(Long id, Double rate, String zipCode, TaxClass taxClass,
                                   Long stateOrProvinceId, Long countryId) {
        return TaxRate.builder()
            .id(id)
            .rate(rate)
            .zipCode(zipCode)
            .taxClass(taxClass)
            .stateOrProvinceId(stateOrProvinceId)
            .countryId(countryId)
            .build();
    }
}

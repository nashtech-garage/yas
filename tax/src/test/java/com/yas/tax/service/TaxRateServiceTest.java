package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class TaxRateServiceTest {

    @Mock
    private TaxRateRepository taxRateRepository;
    @Mock
    private TaxClassRepository taxClassRepository;
    @Mock
    private LocationService locationService;

    @InjectMocks
    private TaxRateService taxRateService;

    private TaxRate taxRate;
    private TaxClass taxClass;

    @BeforeEach
    void setUp() {
        taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Standard");

        taxRate = new TaxRate();
        taxRate.setId(1L);
        taxRate.setRate(10.0);
        taxRate.setZipCode("12345");
        taxRate.setTaxClass(taxClass);
        taxRate.setStateOrProvinceId(1L);
        taxRate.setCountryId(1L);
    }

    @Test
    void createTaxRate_WhenTaxClassNotExists_ShouldThrowException() {
        TaxRatePostVm request = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        when(taxClassRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> taxRateService.createTaxRate(request));
    }

    @Test
    void createTaxRate_ValidRequest_ShouldSaveAndReturn() {
        TaxRatePostVm request = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);
        when(taxRateRepository.save(any(TaxRate.class))).thenReturn(taxRate);

        TaxRate result = taxRateService.createTaxRate(request);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void updateTaxRate_WhenNotFound_ShouldThrowException() {
        TaxRatePostVm request = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        when(taxRateRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> taxRateService.updateTaxRate(request, 1L));
    }

    @Test
    void updateTaxRate_WhenTaxClassNotFound_ShouldThrowException() {
        TaxRatePostVm request = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));
        when(taxClassRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> taxRateService.updateTaxRate(request, 1L));
    }

    @Test
    void updateTaxRate_ValidRequest_ShouldSave() {
        TaxRatePostVm request = new TaxRatePostVm(20.0, "54321", 1L, 1L, 1L);
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);

        taxRateService.updateTaxRate(request, 1L);
        verify(taxRateRepository).save(taxRate);
        assertThat(taxRate.getRate()).isEqualTo(20.0);
    }

    @Test
    void delete_WhenNotFound_ShouldThrowException() {
        when(taxRateRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> taxRateService.delete(1L));
    }

    @Test
    void delete_WhenValid_ShouldDelete() {
        when(taxRateRepository.existsById(1L)).thenReturn(true);
        taxRateService.delete(1L);
        verify(taxRateRepository).deleteById(1L);
    }

    @Test
    void findById_WhenExist_ShouldReturnVm() {
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));
        TaxRateVm result = taxRateService.findById(1L);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void findAll_ShouldReturnList() {
        when(taxRateRepository.findAll()).thenReturn(List.of(taxRate));
        List<TaxRateVm> result = taxRateService.findAll();
        assertThat(result).hasSize(1);
    }

    @Test
    void getPageableTaxRates_ShouldReturnPageableList() {
        Page<TaxRate> page = new PageImpl<>(List.of(taxRate), PageRequest.of(0, 10), 1);
        when(taxRateRepository.findAll(any(PageRequest.class))).thenReturn(page);
        
        StateOrProvinceAndCountryGetNameVm location = new StateOrProvinceAndCountryGetNameVm(1L, "State", "Country");
        when(locationService.getStateOrProvinceAndCountryNames(anyList())).thenReturn(List.of(location));

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);
        assertThat(result.taxRateGetDetailContent()).hasSize(1);
    }

    @Test
    void getTaxPercent_WhenExists_ShouldReturnPercent() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "12345", 1L)).thenReturn(10.0);
        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "12345");
        assertThat(result).isEqualTo(10.0);
    }

    @Test
    void getTaxPercent_WhenNotExists_ShouldReturnZero() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "12345", 1L)).thenReturn(null);
        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "12345");
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    void getBulkTaxRate_ShouldReturnList() {
        when(taxRateRepository.getBatchTaxRates(eq(1L), eq(1L), eq("12345"), any(Set.class)))
            .thenReturn(List.of(taxRate));
        List<TaxRateVm> result = taxRateService.getBulkTaxRate(List.of(1L), 1L, 1L, "12345");
        assertThat(result).hasSize(1);
    }
}

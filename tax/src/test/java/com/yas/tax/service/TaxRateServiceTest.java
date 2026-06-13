package com.yas.tax.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    private TaxRate mockTaxRate() {
        TaxClass taxClass = mock(TaxClass.class);
        lenient().when(taxClass.getId()).thenReturn(1L);
        lenient().when(taxClass.getName()).thenReturn("VAT");

        TaxRate taxRate = mock(TaxRate.class);
        lenient().when(taxRate.getId()).thenReturn(1L);
        lenient().when(taxRate.getRate()).thenReturn(10.0);
        lenient().when(taxRate.getZipCode()).thenReturn("10000");
        lenient().when(taxRate.getTaxClass()).thenReturn(taxClass);
        lenient().when(taxRate.getStateOrProvinceId()).thenReturn(1L);
        lenient().when(taxRate.getCountryId()).thenReturn(1L);
        return taxRate;
    }

    // ========== findById ==========

    @Test
    void findById_WhenExists_ShouldReturnTaxRateVm() {
        TaxRate taxRate = mockTaxRate();
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));

        TaxRateVm result = taxRateService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(10.0, result.rate());
    }

    @Test
    void findById_WhenNotExists_ShouldThrowNotFoundException() {
        when(taxRateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxRateService.findById(99L));
    }

    // ========== findAll ==========

    @Test
    void findAll_ShouldReturnListOfTaxRateVm() {
        TaxRate taxRate = mockTaxRate();
        when(taxRateRepository.findAll()).thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }

    @Test
    void findAll_WhenEmpty_ShouldReturnEmptyList() {
        when(taxRateRepository.findAll()).thenReturn(List.of());

        List<TaxRateVm> result = taxRateService.findAll();

        assertTrue(result.isEmpty());
    }

    // ========== createTaxRate ==========

    @Test
    void createTaxRate_WhenTaxClassExists_ShouldReturnSavedTaxRate() {
        TaxClass taxClass = mock(TaxClass.class);
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "10000", 1L, 1L, 1L);
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);
        when(taxRateRepository.save(any(TaxRate.class))).thenReturn(mock(TaxRate.class));

        TaxRate result = taxRateService.createTaxRate(postVm);

        assertNotNull(result);
        verify(taxRateRepository).save(any(TaxRate.class));
    }

    @Test
    void createTaxRate_WhenTaxClassNotExists_ShouldThrowNotFoundException() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "10000", 99L, 1L, 1L);
        when(taxClassRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.createTaxRate(postVm));
        verify(taxRateRepository, never()).save(any());
    }

    // ========== updateTaxRate ==========

    @Test
    void updateTaxRate_WhenBothExist_ShouldUpdateSuccessfully() {
        TaxClass taxClass = mock(TaxClass.class);
        TaxRate realTaxRate = new TaxRate();
        TaxRatePostVm postVm = new TaxRatePostVm(12.0, "20000", 1L, 1L, 1L);

        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(realTaxRate));
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);
        when(taxRateRepository.save(any(TaxRate.class))).thenReturn(realTaxRate);

        taxRateService.updateTaxRate(postVm, 1L);

        verify(taxRateRepository).save(any(TaxRate.class));
    }

    @Test
    void updateTaxRate_WhenTaxRateNotExists_ShouldThrowNotFoundException() {
        TaxRatePostVm postVm = new TaxRatePostVm(12.0, "20000", 1L, 1L, 1L);
        when(taxRateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxRateService.updateTaxRate(postVm, 99L));
        verify(taxRateRepository, never()).save(any());
    }

    @Test
    void updateTaxRate_WhenTaxClassNotExists_ShouldThrowNotFoundException() {
        TaxRate realTaxRate = new TaxRate();
        TaxRatePostVm postVm = new TaxRatePostVm(12.0, "20000", 99L, 1L, 1L);

        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(realTaxRate));
        when(taxClassRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.updateTaxRate(postVm, 1L));
        verify(taxRateRepository, never()).save(any());
    }

    // ========== delete ==========

    @Test
    void delete_WhenExists_ShouldDeleteSuccessfully() {
        when(taxRateRepository.existsById(1L)).thenReturn(true);

        taxRateService.delete(1L);

        verify(taxRateRepository).deleteById(1L);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowNotFoundException() {
        when(taxRateRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.delete(99L));
        verify(taxRateRepository, never()).deleteById(any());
    }

    // ========== getTaxPercent ==========

    @Test
    void getTaxPercent_WhenFound_ShouldReturnPercent() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "10000", 1L)).thenReturn(10.0);

        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "10000");

        assertEquals(10.0, result);
    }

    @Test
    void getTaxPercent_WhenNotFound_ShouldReturnZero() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "10000", 1L)).thenReturn(null);

        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "10000");

        assertEquals(0, result);
    }

    // ========== getBulkTaxRate ==========

    @Test
    void getBulkTaxRate_ShouldReturnListOfTaxRateVm() {
        TaxRate taxRate = mockTaxRate();
        when(taxRateRepository.getBatchTaxRates(anyLong(), anyLong(), anyString(), anySet()))
                .thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.getBulkTaxRate(List.of(1L), 1L, 1L, "10000");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }

    @Test
    void getBulkTaxRate_WhenEmpty_ShouldReturnEmptyList() {
        when(taxRateRepository.getBatchTaxRates(anyLong(), anyLong(), anyString(), anySet()))
                .thenReturn(List.of());

        List<TaxRateVm> result = taxRateService.getBulkTaxRate(List.of(1L), 1L, 1L, "10000");

        assertTrue(result.isEmpty());
    }

    // ========== getPageableTaxRates ==========

    @Test
    void getPageableTaxRates_WhenHasData_ShouldReturnTaxRateListGetVm() {
        TaxRate taxRate = mockTaxRate();
        Page<TaxRate> page = new PageImpl<>(List.of(taxRate));
        when(taxRateRepository.findAll(any(Pageable.class))).thenReturn(page);

        StateOrProvinceAndCountryGetNameVm locationVm =
                new StateOrProvinceAndCountryGetNameVm(1L, "HCM", "Vietnam");
        when(locationService.getStateOrProvinceAndCountryNames(anyList()))
                .thenReturn(List.of(locationVm));

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);

        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(1, result.taxRateGetDetailContent().size());
        assertEquals("VAT", result.taxRateGetDetailContent().get(0).taxClassName());
        assertEquals("HCM", result.taxRateGetDetailContent().get(0).stateOrProvinceName());
        assertEquals("Vietnam", result.taxRateGetDetailContent().get(0).countryName());
    }

    @Test
    void getPageableTaxRates_WhenEmpty_ShouldReturnEmptyContent() {
        Page<TaxRate> page = new PageImpl<>(List.of());
        when(taxRateRepository.findAll(any(Pageable.class))).thenReturn(page);

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);

        assertNotNull(result);
        assertTrue(result.taxRateGetDetailContent().isEmpty());
        verify(locationService, never()).getStateOrProvinceAndCountryNames(anyList());
    }
}
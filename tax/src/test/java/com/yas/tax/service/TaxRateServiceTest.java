package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import static org.instancio.Select.field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = TaxRateService.class)
public class TaxRateServiceTest {

    @MockitoBean
    TaxRateRepository taxRateRepository;

    @MockitoBean
    TaxClassRepository taxClassRepository;

    @MockitoBean
    LocationService locationService;

    @Autowired
    TaxRateService taxRateService;

    TaxRate taxRate;
    TaxClass taxClass;

    @BeforeEach
    void setUp() {
        taxClass = Instancio.create(TaxClass.class);
        taxRate = Instancio.of(TaxRate.class)
                .set(field("taxClass"), taxClass)
                .create();
    }

    @Test
    void createTaxRate_whenTaxClassNotFound_shouldThrowNotFoundException() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", 999L, 1L, 1L);
        when(taxClassRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.createTaxRate(postVm));
    }

    @Test
    void createTaxRate_whenTaxClassExists_shouldSaveAndReturn() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", taxClass.getId(), 1L, 1L);
        when(taxClassRepository.existsById(taxClass.getId())).thenReturn(true);
        when(taxClassRepository.getReferenceById(taxClass.getId())).thenReturn(taxClass);
        when(taxRateRepository.save(any(TaxRate.class))).thenReturn(taxRate);

        TaxRate result = taxRateService.createTaxRate(postVm);

        assertThat(result).isNotNull();
        verify(taxRateRepository).save(any(TaxRate.class));
    }

    @Test
    void updateTaxRate_whenTaxRateNotFound_shouldThrowNotFoundException() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", taxClass.getId(), 1L, 1L);
        when(taxRateRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxRateService.updateTaxRate(postVm, 999L));
    }

    @Test
    void updateTaxRate_whenTaxClassNotFound_shouldThrowNotFoundException() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", 999L, 1L, 1L);
        when(taxRateRepository.findById(taxRate.getId())).thenReturn(Optional.of(taxRate));
        when(taxClassRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> taxRateService.updateTaxRate(postVm, taxRate.getId()));
    }

    @Test
    void updateTaxRate_whenValid_shouldUpdateTaxRate() {
        TaxRatePostVm postVm = new TaxRatePostVm(15.0, "54321", taxClass.getId(), 2L, 2L);
        when(taxRateRepository.findById(taxRate.getId())).thenReturn(Optional.of(taxRate));
        when(taxClassRepository.existsById(taxClass.getId())).thenReturn(true);
        when(taxClassRepository.getReferenceById(taxClass.getId())).thenReturn(taxClass);
        when(taxRateRepository.save(any(TaxRate.class))).thenReturn(taxRate);

        taxRateService.updateTaxRate(postVm, taxRate.getId());

        verify(taxRateRepository).save(any(TaxRate.class));
    }

    @Test
    void delete_whenNotFound_shouldThrowNotFoundException() {
        when(taxRateRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.delete(999L));
    }

    @Test
    void delete_whenFound_shouldDeleteTaxRate() {
        when(taxRateRepository.existsById(taxRate.getId())).thenReturn(true);

        taxRateService.delete(taxRate.getId());

        verify(taxRateRepository).deleteById(taxRate.getId());
    }

    @Test
    void findById_whenFound_shouldReturnTaxRateVm() {
        when(taxRateRepository.findById(taxRate.getId())).thenReturn(Optional.of(taxRate));

        TaxRateVm result = taxRateService.findById(taxRate.getId());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(taxRate.getId());
    }

    @Test
    void findById_whenNotFound_shouldThrowNotFoundException() {
        when(taxRateRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxRateService.findById(999L));
    }

    @Test
    void getTaxPercent_whenFound_shouldReturnRate() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "12345", 1L)).thenReturn(10.0);

        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "12345");

        assertThat(result).isEqualTo(10.0);
    }

    @Test
    void getTaxPercent_whenNotFound_shouldReturnZero() {
        when(taxRateRepository.getTaxPercent(any(), any(), any(), any())).thenReturn(null);

        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "99999");

        assertThat(result).isZero();
    }

    @Test
    void getPageableTaxRates_whenEmpty_shouldReturnEmptyResult() {
        Page<TaxRate> emptyPage = Page.empty();
        when(taxRateRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.taxRateGetDetailContent()).isEmpty();
    }

    @Test
    void getBulkTaxRate_shouldReturnTaxRateVmList() {
        when(taxRateRepository.getBatchTaxRates(any(), any(), any(), any()))
                .thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.getBulkTaxRate(
                List.of(taxClass.getId()), 1L, 1L, "12345");

        assertThat(result).hasSize(1);
    }
}

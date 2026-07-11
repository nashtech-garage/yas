package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class TaxServiceTest {
    private TaxRateRepository taxRateRepository;
    private TaxClassRepository taxClassRepository;
    private LocationService locationService;
    private TaxRateService taxRateService;

    @BeforeEach
    void setUp() {
        taxRateRepository = mock(TaxRateRepository.class);
        taxClassRepository = mock(TaxClassRepository.class);
        locationService = mock(LocationService.class);
        taxRateService = new TaxRateService(locationService, taxRateRepository, taxClassRepository);
    }

    @Test
    void findAll_whenCalled_returnList() {
        TaxClass taxClass = TaxClass.builder().id(1L).name("Tax Class").build();
        TaxRate taxRate = TaxRate.builder().id(1L).rate(10.0).taxClass(taxClass).build();
        when(taxRateRepository.findAll()).thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findById_whenNotFound_throwNotFoundException() {
        when(taxRateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxRateService.findById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createTaxRate_whenTaxClassNotFound_throwNotFoundException() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "123", 1L, 1L, 1L);
        when(taxClassRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> taxRateService.createTaxRate(postVm))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createTaxRate_whenValid_saveTaxRate() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "123", 1L, 1L, 1L);
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(new TaxClass());
        when(taxRateRepository.save(any())).thenReturn(new TaxRate());

        taxRateService.createTaxRate(postVm);

        verify(taxRateRepository).save(any());
    }

    @Test
    void updateTaxRate_whenNotFound_throwNotFoundException() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "123", 1L, 1L, 1L);
        when(taxRateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxRateService.updateTaxRate(postVm, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateTaxRate_whenTaxClassNotFound_throwNotFoundException() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "123", 1L, 1L, 1L);
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(new TaxRate()));
        when(taxClassRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> taxRateService.updateTaxRate(postVm, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_whenNotFound_throwNotFoundException() {
        when(taxRateRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> taxRateService.delete(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getPageableTaxRates_whenCalled_returnTaxRateListGetVm() {
        TaxClass taxClass = TaxClass.builder().name("Class").build();
        TaxRate taxRate = TaxRate.builder().id(1L).rate(10.0).taxClass(taxClass).stateOrProvinceId(1L).build();
        Page<TaxRate> page = new PageImpl<>(List.of(taxRate), PageRequest.of(0, 10), 1);
        
        when(taxRateRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(locationService.getStateOrProvinceAndCountryNames(anyList()))
                .thenReturn(List.of(new StateOrProvinceAndCountryGetNameVm(1L, "State", "Country")));

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);

        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.taxRateGetDetailContent().get(0).stateOrProvinceName()).isEqualTo("State");
    }

    @Test
    void getTaxPercent_whenCalled_returnPercent() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "123", 1L)).thenReturn(10.0);

        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "123");

        assertThat(result).isEqualTo(10.0);
    }

    @Test
    void getTaxPercent_whenNull_returnZero() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "123", 1L)).thenReturn(null);

        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "123");

        assertThat(result).isEqualTo(0);
    }

    @Test
    void getBulkTaxRate_whenCalled_returnList() {
        TaxClass taxClass = TaxClass.builder().id(1L).build();
        TaxRate taxRate = TaxRate.builder().taxClass(taxClass).build();
        when(taxRateRepository.getBatchTaxRates(any(), any(), any(), any())).thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.getBulkTaxRate(List.of(1L), 1L, 1L, "123");

        assertThat(result).hasSize(1);
    }
}

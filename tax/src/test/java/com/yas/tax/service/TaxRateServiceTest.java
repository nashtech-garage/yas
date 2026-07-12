package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

class TaxRateServiceTest {

    private TaxRateRepository taxRateRepository;
    private LocationService locationService;
    private TaxClassRepository taxClassRepository;
    private TaxRateService taxRateService;

    @BeforeEach
    void setUp() {
        taxRateRepository = mock(TaxRateRepository.class);
        locationService = mock(LocationService.class);
        taxClassRepository = mock(TaxClassRepository.class);
        taxRateService = new TaxRateService(locationService, taxRateRepository, taxClassRepository);
    }

    @Test
    void findAll_shouldReturnList() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Class");
        TaxRate taxRate = new TaxRate();
        taxRate.setId(1L);
        taxRate.setTaxClass(taxClass);
        when(taxRateRepository.findAll()).thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findById_whenExists_shouldReturnTaxRateVm() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Class");
        TaxRate taxRate = new TaxRate();
        taxRate.setId(1L);
        taxRate.setTaxClass(taxClass);
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));

        TaxRateVm result = taxRateService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void findById_whenNotExists_shouldThrowNotFoundException() {
        when(taxRateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxRateService.findById(1L));
    }

    @Test
    void create_whenTaxClassExists_shouldSaveAndReturn() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Class");
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        
        TaxRate taxRate = new TaxRate();
        taxRate.setId(1L);
        taxRate.setTaxClass(taxClass);
        when(taxRateRepository.save(any(TaxRate.class))).thenReturn(taxRate);

        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "Zip", 1L, 1L, 1L);
        TaxRate result = taxRateService.createTaxRate(postVm);

        assertThat(result).isNotNull();
        verify(taxRateRepository).save(any(TaxRate.class));
    }

    @Test
    void create_whenTaxClassNotExists_shouldThrowNotFoundException() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.empty());
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "Zip", 1L, 1L, 1L);

        assertThrows(NotFoundException.class, () -> taxRateService.createTaxRate(postVm));
    }

    @Test
    void update_whenValid_shouldUpdateAndSave() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Class");
        
        TaxRate taxRate = new TaxRate();
        taxRate.setId(1L);
        taxRate.setTaxClass(taxClass);

        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));
        when(taxClassRepository.existsById(1L)).thenReturn(true);

        TaxRatePostVm postVm = new TaxRatePostVm(15.0, "Zip", 1L, 1L, 1L);
        taxRateService.updateTaxRate(postVm, 1L);

        assertThat(taxRate.getRate()).isEqualTo(15.0);
        verify(taxRateRepository).save(taxRate);
    }

    @Test
    void update_whenTaxRateNotExists_shouldThrowNotFoundException() {
        when(taxRateRepository.findById(1L)).thenReturn(Optional.empty());

        TaxRatePostVm postVm = new TaxRatePostVm(15.0, "Zip", 1L, 1L, 1L);
        assertThrows(NotFoundException.class, () -> taxRateService.updateTaxRate(postVm, 1L));
    }

    @Test
    void delete_whenExists_shouldDelete() {
        when(taxRateRepository.existsById(1L)).thenReturn(true);

        taxRateService.delete(1L);

        verify(taxRateRepository).deleteById(1L);
    }

    @Test
    void delete_whenNotExists_shouldThrowNotFoundException() {
        when(taxRateRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.delete(1L));
    }

    @Test
    void getPageableTaxRates_shouldReturnPage() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Class");
        
        TaxRate taxRate = new TaxRate();
        taxRate.setId(1L);
        taxRate.setStateOrProvinceId(1L);
        taxRate.setTaxClass(taxClass);
        
        Page<TaxRate> page = new PageImpl<>(List.of(taxRate), PageRequest.of(0, 10), 1);
        when(taxRateRepository.findAll(any(Pageable.class))).thenReturn(page);
        
        StateOrProvinceAndCountryGetNameVm locationVm = new StateOrProvinceAndCountryGetNameVm(1L, "State", "Country");
        when(locationService.getStateOrProvinceAndCountryNames(any())).thenReturn(List.of(locationVm));

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);

        assertThat(result.taxRateGetDetailContent()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }
}

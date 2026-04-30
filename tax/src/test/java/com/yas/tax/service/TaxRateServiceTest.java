package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class TaxRateServiceTest {

    private TaxRateRepository taxRateRepository;
    private TaxClassRepository taxClassRepository;
    private LocationService locationService;
    private TaxRateService taxRateService;

    @BeforeEach
    void setUp() {
        taxRateRepository = Mockito.mock(TaxRateRepository.class);
        taxClassRepository = Mockito.mock(TaxClassRepository.class);
        locationService = Mockito.mock(LocationService.class);
        taxRateService = new TaxRateService(locationService, taxRateRepository, taxClassRepository);
    }

    @Test
    void createTaxRate_whenTaxClassNotFound_throwNotFoundException() {
        TaxRatePostVm request = new TaxRatePostVm(10.0, "70000", 1L, 2L, 3L);
        when(taxClassRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.createTaxRate(request));
    }

    @Test
    void createTaxRate_whenValidRequest_saveAndReturnTaxRate() {
        TaxClass taxClassRef = TaxClass.builder().id(1L).name("Standard").build();
        TaxRatePostVm request = new TaxRatePostVm(10.0, "70000", 1L, 2L, 3L);

        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClassRef);
        when(taxRateRepository.save(any(TaxRate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaxRate result = taxRateService.createTaxRate(request);

        assertThat(result.getRate()).isEqualTo(10.0);
        assertThat(result.getZipCode()).isEqualTo("70000");
        assertThat(result.getTaxClass().getId()).isEqualTo(1L);
    }

    @Test
    void updateTaxRate_whenTaxRateNotFound_throwNotFoundException() {
        TaxRatePostVm request = new TaxRatePostVm(8.0, "75000", 1L, 2L, 3L);
        when(taxRateRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxRateService.updateTaxRate(request, 10L));
    }

    @Test
    void updateTaxRate_whenTaxClassNotFound_throwNotFoundException() {
        TaxRate existing = TaxRate.builder().id(10L).build();
        TaxRatePostVm request = new TaxRatePostVm(8.0, "75000", 1L, 2L, 3L);

        when(taxRateRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(taxClassRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.updateTaxRate(request, 10L));
    }

    @Test
    void updateTaxRate_whenValidRequest_updateAndSave() {
        TaxClass taxClassRef = TaxClass.builder().id(1L).name("Standard").build();
        TaxRate existing = TaxRate.builder().id(10L).rate(5.0).zipCode("10000").build();
        TaxRatePostVm request = new TaxRatePostVm(8.0, "75000", 1L, 2L, 3L);

        when(taxRateRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClassRef);

        taxRateService.updateTaxRate(request, 10L);

        assertThat(existing.getRate()).isEqualTo(8.0);
        assertThat(existing.getZipCode()).isEqualTo("75000");
        assertThat(existing.getTaxClass().getId()).isEqualTo(1L);
        verify(taxRateRepository).save(existing);
    }

    @Test
    void delete_whenTaxRateNotFound_throwNotFoundException() {
        when(taxRateRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.delete(99L));
    }

    @Test
    void delete_whenTaxRateExists_deleteById() {
        when(taxRateRepository.existsById(9L)).thenReturn(true);

        taxRateService.delete(9L);

        verify(taxRateRepository).deleteById(9L);
    }

    @Test
    void findById_whenNotFound_throwNotFoundException() {
        when(taxRateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxRateService.findById(1L));
    }

    @Test
    void findById_whenFound_returnTaxRateVm() {
        TaxClass taxClass = TaxClass.builder().id(1L).name("Standard").build();
        TaxRate taxRate = TaxRate.builder()
            .id(1L)
            .rate(10.0)
            .zipCode("70000")
            .taxClass(taxClass)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build();
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));

        TaxRateVm result = taxRateService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.taxClassId()).isEqualTo(1L);
    }

    @Test
    void findAll_whenHasData_returnMappedList() {
        TaxClass taxClass = TaxClass.builder().id(1L).name("Standard").build();
        TaxRate taxRate = TaxRate.builder().id(1L).rate(10.0).zipCode("70000").taxClass(taxClass).countryId(3L).build();
        when(taxRateRepository.findAll()).thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(1L);
    }

    @Test
    void getPageableTaxRates_whenNoTaxRate_returnEmptyContent() {
        Page<TaxRate> page = new PageImpl<>(List.of(), PageRequest.of(0, 5), 0);
        when(taxRateRepository.findAll(PageRequest.of(0, 5))).thenReturn(page);

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 5);

        assertThat(result.taxRateGetDetailContent()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0);
    }

    @Test
    void getPageableTaxRates_whenHasTaxRates_returnDetailedContent() {
        TaxClass taxClass = TaxClass.builder().id(1L).name("Standard").build();
        TaxRate taxRate = TaxRate.builder()
            .id(11L)
            .rate(10.0)
            .zipCode("70000")
            .taxClass(taxClass)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build();
        Page<TaxRate> page = new PageImpl<>(List.of(taxRate), PageRequest.of(0, 5), 1);

        when(taxRateRepository.findAll(PageRequest.of(0, 5))).thenReturn(page);
        when(locationService.getStateOrProvinceAndCountryNames(List.of(2L))).thenReturn(
            List.of(new StateOrProvinceAndCountryGetNameVm(2L, "HCM", "VN"))
        );

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 5);

        assertThat(result.taxRateGetDetailContent()).hasSize(1);
        TaxRateGetDetailVm detail = result.taxRateGetDetailContent().getFirst();
        assertThat(detail.id()).isEqualTo(11L);
        assertThat(detail.taxClassName()).isEqualTo("Standard");
        assertThat(detail.stateOrProvinceName()).isEqualTo("HCM");
        assertThat(detail.countryName()).isEqualTo("VN");
    }

    @Test
    void getTaxPercent_whenRepositoryReturnsValue_returnThatValue() {
        when(taxRateRepository.getTaxPercent(3L, 2L, "70000", 1L)).thenReturn(12.5);

        double result = taxRateService.getTaxPercent(1L, 3L, 2L, "70000");

        assertEquals(12.5, result);
    }

    @Test
    void getTaxPercent_whenRepositoryReturnsNull_returnZero() {
        when(taxRateRepository.getTaxPercent(3L, 2L, "70000", 1L)).thenReturn(null);

        double result = taxRateService.getTaxPercent(1L, 3L, 2L, "70000");

        assertEquals(0.0, result);
    }

    @Test
    void getBulkTaxRate_whenRepositoryReturnsData_mapToVmList() {
        TaxClass taxClass = TaxClass.builder().id(1L).name("Standard").build();
        TaxRate taxRate = TaxRate.builder()
            .id(5L)
            .rate(10.0)
            .zipCode("70000")
            .taxClass(taxClass)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build();

        when(taxRateRepository.getBatchTaxRates(3L, 2L, "70000", Set.of(1L, 2L))).thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.getBulkTaxRate(List.of(1L, 2L), 3L, 2L, "70000");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(5L);
        assertThat(result.getFirst().taxClassId()).isEqualTo(1L);
    }
}

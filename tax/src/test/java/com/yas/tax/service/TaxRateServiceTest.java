package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

    private TaxClass taxClass;
    private TaxRate taxRate1;
    private TaxRate taxRate2;

    @BeforeEach
    void setUp() {
        taxClass = TaxClass.builder().id(1L).name("VAT").build();

        taxRate1 = TaxRate.builder()
            .id(1L)
            .rate(10.0)
            .zipCode("70000")
            .stateOrProvinceId(100L)
            .countryId(1L)
            .taxClass(taxClass)
            .build();

        taxRate2 = TaxRate.builder()
            .id(2L)
            .rate(5.0)
            .zipCode("80000")
            .stateOrProvinceId(200L)
            .countryId(1L)
            .taxClass(taxClass)
            .build();
    }

    @Nested
    class CreateTaxRate {
        @Test
        void createTaxRate_whenTaxClassExists_shouldSaveAndReturn() {
            TaxRatePostVm postVm = new TaxRatePostVm(10.0, "70000", 1L, 100L, 1L);

            when(taxClassRepository.existsById(1L)).thenReturn(true);
            when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);
            when(taxRateRepository.save(any(TaxRate.class))).thenReturn(taxRate1);

            TaxRate result = taxRateService.createTaxRate(postVm);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getRate()).isEqualTo(10.0);
            verify(taxRateRepository).save(any(TaxRate.class));
        }

        @Test
        void createTaxRate_whenTaxClassNotExists_shouldThrowNotFoundException() {
            TaxRatePostVm postVm = new TaxRatePostVm(10.0, "70000", 99L, 100L, 1L);

            when(taxClassRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> taxRateService.createTaxRate(postVm))
                .isInstanceOf(NotFoundException.class);
            verify(taxRateRepository, never()).save(any(TaxRate.class));
        }
    }

    @Nested
    class UpdateTaxRate {
        @Test
        void updateTaxRate_whenBothExist_shouldUpdateSuccessfully() {
            TaxRatePostVm postVm = new TaxRatePostVm(15.0, "71000", 1L, 101L, 2L);

            when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate1));
            when(taxClassRepository.existsById(1L)).thenReturn(true);
            when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);

            taxRateService.updateTaxRate(postVm, 1L);

            assertThat(taxRate1.getRate()).isEqualTo(15.0);
            assertThat(taxRate1.getZipCode()).isEqualTo("71000");
            assertThat(taxRate1.getStateOrProvinceId()).isEqualTo(101L);
            assertThat(taxRate1.getCountryId()).isEqualTo(2L);
            verify(taxRateRepository).save(taxRate1);
        }

        @Test
        void updateTaxRate_whenTaxRateNotExists_shouldThrowNotFoundException() {
            TaxRatePostVm postVm = new TaxRatePostVm(15.0, "71000", 1L, 101L, 2L);

            when(taxRateRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taxRateService.updateTaxRate(postVm, 99L))
                .isInstanceOf(NotFoundException.class);
        }

        @Test
        void updateTaxRate_whenTaxClassNotExists_shouldThrowNotFoundException() {
            TaxRatePostVm postVm = new TaxRatePostVm(15.0, "71000", 99L, 101L, 2L);

            when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate1));
            when(taxClassRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> taxRateService.updateTaxRate(postVm, 1L))
                .isInstanceOf(NotFoundException.class);
            verify(taxRateRepository, never()).save(any(TaxRate.class));
        }
    }

    @Nested
    class DeleteTaxRate {
        @Test
        void delete_whenExists_shouldDeleteSuccessfully() {
            when(taxRateRepository.existsById(1L)).thenReturn(true);

            taxRateService.delete(1L);

            verify(taxRateRepository).deleteById(1L);
        }

        @Test
        void delete_whenNotExists_shouldThrowNotFoundException() {
            when(taxRateRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> taxRateService.delete(99L))
                .isInstanceOf(NotFoundException.class);
            verify(taxRateRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    class FindById {
        @Test
        void findById_whenExists_shouldReturnTaxRateVm() {
            when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate1));

            TaxRateVm result = taxRateService.findById(1L);

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.rate()).isEqualTo(10.0);
            assertThat(result.zipCode()).isEqualTo("70000");
            assertThat(result.taxClassId()).isEqualTo(1L);
        }

        @Test
        void findById_whenNotExists_shouldThrowNotFoundException() {
            when(taxRateRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taxRateService.findById(99L))
                .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    class FindAll {
        @Test
        void findAll_shouldReturnAllTaxRates() {
            when(taxRateRepository.findAll()).thenReturn(List.of(taxRate1, taxRate2));

            List<TaxRateVm> result = taxRateService.findAll();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).id()).isEqualTo(1L);
            assertThat(result.get(1).id()).isEqualTo(2L);
        }

        @Test
        void findAll_whenEmpty_shouldReturnEmptyList() {
            when(taxRateRepository.findAll()).thenReturn(List.of());

            List<TaxRateVm> result = taxRateService.findAll();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class GetPageableTaxRates {
        @Test
        void getPageableTaxRates_withStateOrProvinceIds_shouldReturnDetailedResults() {
            Page<TaxRate> page = new PageImpl<>(
                List.of(taxRate1),
                PageRequest.of(0, 10),
                1
            );
            when(taxRateRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

            StateOrProvinceAndCountryGetNameVm locationVm =
                new StateOrProvinceAndCountryGetNameVm(100L, "Ho Chi Minh", "Vietnam");
            when(locationService.getStateOrProvinceAndCountryNames(List.of(100L)))
                .thenReturn(List.of(locationVm));

            TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);

            assertThat(result.taxRateGetDetailContent()).hasSize(1);
            TaxRateGetDetailVm detail = result.taxRateGetDetailContent().get(0);
            assertThat(detail.id()).isEqualTo(1L);
            assertThat(detail.rate()).isEqualTo(10.0);
            assertThat(detail.stateOrProvinceName()).isEqualTo("Ho Chi Minh");
            assertThat(detail.countryName()).isEqualTo("Vietnam");
            assertThat(result.pageNo()).isEqualTo(0);
            assertThat(result.totalElements()).isEqualTo(1);
            assertThat(result.isLast()).isTrue();
        }

        @Test
        void getPageableTaxRates_whenEmpty_shouldReturnEmptyPage() {
            Page<TaxRate> emptyPage = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 10),
                0
            );
            when(taxRateRepository.findAll(PageRequest.of(0, 10))).thenReturn(emptyPage);

            TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);

            assertThat(result.taxRateGetDetailContent()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
        }
    }

    @Nested
    class GetTaxPercent {
        @Test
        void getTaxPercent_whenRateExists_shouldReturnRate() {
            when(taxRateRepository.getTaxPercent(1L, 100L, "70000", 1L))
                .thenReturn(10.0);

            double result = taxRateService.getTaxPercent(1L, 1L, 100L, "70000");

            assertThat(result).isEqualTo(10.0);
        }

        @Test
        void getTaxPercent_whenRateNotExists_shouldReturnZero() {
            when(taxRateRepository.getTaxPercent(1L, 100L, "99999", 1L))
                .thenReturn(null);

            double result = taxRateService.getTaxPercent(1L, 1L, 100L, "99999");

            assertThat(result).isEqualTo(0.0);
        }
    }

    @Nested
    class GetBulkTaxRate {
        @Test
        void getBulkTaxRate_shouldReturnMatchingRates() {
            List<Long> taxClassIds = List.of(1L, 2L);
            Set<Long> taxClassIdSet = new HashSet<>(taxClassIds);

            when(taxRateRepository.getBatchTaxRates(1L, 100L, "70000", taxClassIdSet))
                .thenReturn(List.of(taxRate1));

            List<TaxRateVm> result = taxRateService.getBulkTaxRate(taxClassIds, 1L, 100L, "70000");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(1L);
        }

        @Test
        void getBulkTaxRate_whenNoMatch_shouldReturnEmptyList() {
            List<Long> taxClassIds = List.of(99L);
            Set<Long> taxClassIdSet = new HashSet<>(taxClassIds);

            when(taxRateRepository.getBatchTaxRates(1L, 100L, "70000", taxClassIdSet))
                .thenReturn(List.of());

            List<TaxRateVm> result = taxRateService.getBulkTaxRate(taxClassIds, 1L, 100L, "70000");

            assertThat(result).isEmpty();
        }
    }
}

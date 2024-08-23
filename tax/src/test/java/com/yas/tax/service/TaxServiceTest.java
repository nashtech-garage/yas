package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.lenient;

import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = TaxRateService.class)
public class TaxServiceTest {
    @MockBean
    TaxRateRepository taxRateRepository;
    @MockBean
    LocationService locationService;
    @MockBean
    TaxClassRepository taxClassRepository;

    @Autowired
    TaxRateService taxRateService;

    TaxRate taxRate;
    @BeforeEach
    void setUp() {
        TaxClass taxClass = Instancio.create(TaxClass.class);
        taxRate = Instancio.of(TaxRate.class)
            .set(field("taxClass"), taxClass)
            .create();
        lenient().when(taxRateRepository.findAll()).thenReturn(List.of(taxRate));
    }

    @Test
    void  testFindAll_shouldReturnAllTaxRate() {
        // run
        List<TaxRateVm> result = taxRateService.findAll();
        // assert
        assertThat(result).hasSize(1).contains(TaxRateVm.fromModel(taxRate));
    }
}

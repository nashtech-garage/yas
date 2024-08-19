package com.yas.tax.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import com.yas.tax.integration.config.IntegrationTestConfiguration;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaxRateServiceIntegrationTest {
    @Autowired
    TaxRateRepository taxRateRepository;

    @Autowired
    TaxClassRepository taxClassRepository;

    @Autowired
    TaxRateService taxRateService;

    TaxRate taxRate;
    @BeforeEach
    void setUp() {
        TaxClass taxClass = taxClassRepository.save(Instancio.create(TaxClass.class));
        taxRate = taxRateRepository.save(Instancio.of(TaxRate.class)
            .set(field("taxClass"), taxClass)
            .create());
    }

    @Test
    void  testFindAll_shouldReturnAllTaxRate() {
        // run
        List<TaxRateVm> result = taxRateService.findAll();
        // assert
        assertThat(result).hasSize(1).contains(TaxRateVm.fromModel(taxRate));
    }
}

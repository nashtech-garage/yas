package com.yas.tax.integration.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

import com.yas.tax.integration.config.IntegrationTestConfiguration;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaxRateRepositoryIntegrationTest {

    @Autowired
    TaxRateRepository taxRateRepository;

    @Autowired
    TaxClassRepository taxClassRepository;

    TaxClass taxClass;

    TaxRate taxRate;

    @BeforeEach
    void setUp(){
        taxClass = taxClassRepository.save(Instancio.of(TaxClass.class)
            .set(field(TaxClass::getId), 1L)
            .create());

        taxRate = taxRateRepository.save(Instancio.of(TaxRate.class)
                .set(field("taxClass"), taxClass)
            .create());
    }

    @AfterEach
    void tearDown(){
        taxRateRepository.deleteAll();
        taxClassRepository.deleteAll();
    }

    @Test
    void testGetTaxPercent_shouldReturnCorrectTaxRate_whenGivenCorrectParams(){
        assertThat(taxRateRepository.getTaxPercent(
            taxRate.getCountryId(),
            taxRate.getStateOrProvinceId(),
            taxRate.getZipCode(),
            taxRate.getTaxClass().getId()))
            .isEqualTo(taxRate.getRate());
    }

    @Test
    void testGetTaxPercent_shouldReturnNull_whenGivenWrongParams() {
        assertThat(taxRateRepository.getTaxPercent(
            Instancio.of(Long.class).create(),
            Instancio.of(Long.class).create(),
            Instancio.of(String.class).create(),
            Instancio.of(Long.class).create()))
            .isNull();
    }
}

package com.yas.tax.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import com.yas.commonlibrary.IntegrationTestConfiguration;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import java.util.Set;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaxRateRepositoryIT {

    @Autowired
    TaxRateRepository taxRateRepository;

    @Autowired
    TaxClassRepository taxClassRepository;

    TaxClass taxClass;

    TaxClass taxClass2;

    TaxRate taxRate;

    TaxRate taxRate2;

    @BeforeEach
    void setUp(){
        taxClass = taxClassRepository.save(Instancio.of(TaxClass.class)
            .set(field(TaxClass::getId), 1L)
            .create());

        taxClass2 = taxClassRepository.save(Instancio.of(TaxClass.class)
            .set(field(TaxClass::getId), 2L)
            .create());

        taxRate = taxRateRepository.save(Instancio.of(TaxRate.class)
            .set(field("taxClass"), taxClass)
            .create());

        taxRate2 = taxRateRepository.save(Instancio.of(TaxRate.class)
            .set(field("taxClass"), taxClass2)
            .set(field("countryId"), taxRate.getCountryId())
            .set(field("stateOrProvinceId"), taxRate.getStateOrProvinceId())
            .set(field("zipCode"), taxRate.getZipCode())
            .create());

        taxRateRepository.save(Instancio.of(TaxRate.class)
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

    @Test
    void testGetBatchTaxPercent_shouldReturnListOfRates_whenGivenCorrectParams() {
        assertThat(taxRateRepository.getBatchTaxRates(
            taxRate.getCountryId(),
            taxRate.getStateOrProvinceId(),
            taxRate.getZipCode(),
            Set.of(taxRate.getTaxClass().getId(), taxRate2.getTaxClass().getId())))
            .anyMatch(t -> t.getRate().equals(taxRate.getRate()))
            .anyMatch(t -> t.getRate().equals(taxRate2.getRate()))
            .hasSize(2);
    }
}

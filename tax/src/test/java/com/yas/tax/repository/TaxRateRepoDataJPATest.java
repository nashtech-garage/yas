package com.yas.tax.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
}, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
    classes = {TaxRateRepository.class, TaxClassRepository.class}))
@ExtendWith(SpringExtension.class)
public class TaxRateRepoDataJPATest {
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

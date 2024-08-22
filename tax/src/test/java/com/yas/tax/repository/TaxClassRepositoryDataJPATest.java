package com.yas.tax.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.yas.tax.model.TaxClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
},includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
    classes = AuditingHandler.class),
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
    classes = TaxRateRepository.class))
@ExtendWith(SpringExtension.class)
public class TaxClassRepositoryDataJPATest {
    @MockBean
    private AuditingHandler auditingHandler;
    @Autowired
    private TaxClassRepository taxClassRepository;

    @BeforeEach
    void insertTestData(){
        taxClassRepository.save(TaxClass.builder().name("test_tax_class").id(1L).build());
        taxClassRepository.save(TaxClass.builder().name("another_tax_class").id(2L).build());
    }

    @AfterEach
    void tearDown(){
        taxClassRepository.deleteAll();
    }

    @Test
    void test_existByName_shouldReturnTrue_whenTaxClassNameExists(){
        assertThat(taxClassRepository.existsByName("test_tax_class")).isTrue();
    }

    @Test
    void test_existByName_shouldReturnFalse_whenTaxClassNameNotExists(){
        assertThat(taxClassRepository.existsByName("dummy_class")).isFalse();
    }

    @Test
    void test_existsByNameNotUpdatingTaxClass_shouldReturnTrue_whenThereIsAClassWithSameNameAndDiffID(){
        assertThat(taxClassRepository.existsByNameNotUpdatingTaxClass("test_tax_class", 2L)).isTrue();
    }

    @Test
    void test_existsByNameNotUpdatingTaxClass_shouldReturnFalse_whenThereIsNoClassWithSameName(){
        assertThat(taxClassRepository.existsByNameNotUpdatingTaxClass("dummy_class", 1L)).isFalse();
    }
}

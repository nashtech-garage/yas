package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = TaxClassService.class)
public class TaxClassServiceTest {

    @MockitoBean
    TaxClassRepository taxClassRepository;

    @Autowired
    TaxClassService taxClassService;

    TaxClass taxClass;

    @BeforeEach
    void setUp() {
        taxClass = Instancio.create(TaxClass.class);
    }

    @Test
    void findAllTaxClasses_shouldReturnAllTaxClasses() {
        when(taxClassRepository.findAll(any(Sort.class))).thenReturn(List.of(taxClass));

        List<TaxClassVm> result = taxClassService.findAllTaxClasses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(taxClass.getId());
    }

    @Test
    void findById_whenFound_shouldReturnTaxClassVm() {
        when(taxClassRepository.findById(taxClass.getId())).thenReturn(Optional.of(taxClass));

        TaxClassVm result = taxClassService.findById(taxClass.getId());

        assertThat(result.id()).isEqualTo(taxClass.getId());
    }

    @Test
    void findById_whenNotFound_shouldThrowNotFoundException() {
        when(taxClassRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxClassService.findById(999L));
    }

    @Test
    void create_whenNameNotExists_shouldSaveAndReturn() {
        TaxClassPostVm postVm = new TaxClassPostVm("id-1", "Standard Tax");
        when(taxClassRepository.existsByName("Standard Tax")).thenReturn(false);
        when(taxClassRepository.save(any(TaxClass.class))).thenReturn(taxClass);

        TaxClass result = taxClassService.create(postVm);

        assertThat(result).isNotNull();
        verify(taxClassRepository).save(any(TaxClass.class));
    }

    @Test
    void create_whenNameAlreadyExists_shouldThrowDuplicatedException() {
        TaxClassPostVm postVm = new TaxClassPostVm("id-1", "Standard Tax");
        when(taxClassRepository.existsByName("Standard Tax")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> taxClassService.create(postVm));
    }

    @Test
    void update_whenNotFound_shouldThrowNotFoundException() {
        TaxClassPostVm postVm = new TaxClassPostVm("id-1", "Updated Tax");
        when(taxClassRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxClassService.update(postVm, 999L));
    }

    @Test
    void update_whenDuplicateName_shouldThrowDuplicatedException() {
        TaxClassPostVm postVm = new TaxClassPostVm("id-1", "Dup Name");
        when(taxClassRepository.findById(taxClass.getId())).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Dup Name", taxClass.getId()))
                .thenReturn(true);

        assertThrows(DuplicatedException.class,
                () -> taxClassService.update(postVm, taxClass.getId()));
    }

    @Test
    void update_whenValid_shouldSaveTaxClass() {
        TaxClassPostVm postVm = new TaxClassPostVm("id-1", "New Name");
        when(taxClassRepository.findById(taxClass.getId())).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("New Name", taxClass.getId()))
                .thenReturn(false);
        when(taxClassRepository.save(any(TaxClass.class))).thenReturn(taxClass);

        taxClassService.update(postVm, taxClass.getId());

        verify(taxClassRepository).save(taxClass);
    }

    @Test
    void delete_whenNotFound_shouldThrowNotFoundException() {
        when(taxClassRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxClassService.delete(999L));
    }

    @Test
    void delete_whenFound_shouldDeleteTaxClass() {
        when(taxClassRepository.existsById(taxClass.getId())).thenReturn(true);

        taxClassService.delete(taxClass.getId());

        verify(taxClassRepository).deleteById(taxClass.getId());
    }

    @Test
    void getPageableTaxClasses_shouldReturnPagedResult() {
        Page<TaxClass> page = new PageImpl<>(List.of(taxClass));
        when(taxClassRepository.findAll(any(Pageable.class))).thenReturn(page);

        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.taxClassContent()).hasSize(1);
    }
}

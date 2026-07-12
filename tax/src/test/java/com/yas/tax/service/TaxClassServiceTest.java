package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class TaxClassServiceTest {

    private TaxClassRepository taxClassRepository;
    private TaxClassService taxClassService;

    @BeforeEach
    void setUp() {
        taxClassRepository = mock(TaxClassRepository.class);
        taxClassService = new TaxClassService(taxClassRepository);
    }

    @Test
    void findAllTaxClasses_shouldReturnList() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Standard");
        when(taxClassRepository.findAll(any(Sort.class))).thenReturn(List.of(taxClass));

        List<TaxClassVm> result = taxClassService.findAllTaxClasses();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Standard");
    }

    @Test
    void findById_whenExists_shouldReturnTaxClassVm() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Standard");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));

        TaxClassVm result = taxClassService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Standard");
    }

    @Test
    void findById_whenNotExists_shouldThrowNotFoundException() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxClassService.findById(1L));
    }

    @Test
    void create_whenNameNotExists_shouldSaveAndReturn() {
        TaxClassPostVm postVm = new TaxClassPostVm("new-class", "New Class");
        when(taxClassRepository.existsByName("New Class")).thenReturn(false);
        
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("New Class");
        when(taxClassRepository.save(any(TaxClass.class))).thenReturn(taxClass);

        TaxClass result = taxClassService.create(postVm);

        assertThat(result.getName()).isEqualTo("New Class");
        verify(taxClassRepository).save(any(TaxClass.class));
    }

    @Test
    void create_whenNameExists_shouldThrowDuplicatedException() {
        TaxClassPostVm postVm = new TaxClassPostVm("existing-class", "Existing Class");
        when(taxClassRepository.existsByName("Existing Class")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> taxClassService.create(postVm));
    }

    @Test
    void update_whenValid_shouldUpdateAndSave() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Old Class");
        
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("New Class", 1L)).thenReturn(false);

        TaxClassPostVm postVm = new TaxClassPostVm("new-class", "New Class");
        taxClassService.update(postVm, 1L);

        assertThat(taxClass.getName()).isEqualTo("New Class");
        verify(taxClassRepository).save(taxClass);
    }

    @Test
    void update_whenNameExistsInAnotherRecord_shouldThrowDuplicatedException() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Old Class");
        
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("New Class", 1L)).thenReturn(true);

        TaxClassPostVm postVm = new TaxClassPostVm("new-class", "New Class");
        assertThrows(DuplicatedException.class, () -> taxClassService.update(postVm, 1L));
    }

    @Test
    void update_whenNotExists_shouldThrowNotFoundException() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.empty());

        TaxClassPostVm postVm = new TaxClassPostVm("new-class", "New Class");
        assertThrows(NotFoundException.class, () -> taxClassService.update(postVm, 1L));
    }

    @Test
    void delete_whenExists_shouldDelete() {
        when(taxClassRepository.existsById(1L)).thenReturn(true);

        taxClassService.delete(1L);

        verify(taxClassRepository).deleteById(1L);
    }

    @Test
    void delete_whenNotExists_shouldThrowNotFoundException() {
        when(taxClassRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxClassService.delete(1L));
    }

    @Test
    void getPageableTaxClasses_shouldReturnPage() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Standard");
        
        Page<TaxClass> page = new PageImpl<>(List.of(taxClass), PageRequest.of(0, 10), 1);
        when(taxClassRepository.findAll(any(Pageable.class))).thenReturn(page);

        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 10);

        assertThat(result.taxClassContent()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
    }
}

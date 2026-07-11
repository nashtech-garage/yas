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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

class TaxClassServiceTest {

    private TaxClassRepository taxClassRepository;
    private TaxClassService taxClassService;

    @BeforeEach
    void setUp() {
        taxClassRepository = Mockito.mock(TaxClassRepository.class);
        taxClassService = new TaxClassService(taxClassRepository);
    }

    @Test
    void findAllTaxClasses_whenHasData_returnMappedList() {
        TaxClass taxClass = TaxClass.builder().id(1L).name("Standard").build();
        when(taxClassRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(List.of(taxClass));

        List<TaxClassVm> result = taxClassService.findAllTaxClasses();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(1L);
        assertThat(result.getFirst().name()).isEqualTo("Standard");
    }

    @Test
    void findById_whenNotFound_throwNotFoundException() {
        when(taxClassRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxClassService.findById(10L));
    }

    @Test
    void findById_whenFound_returnTaxClassVm() {
        TaxClass taxClass = TaxClass.builder().id(1L).name("Reduced").build();
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));

        TaxClassVm result = taxClassService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Reduced");
    }

    @Test
    void create_whenNameExists_throwDuplicatedException() {
        TaxClassPostVm request = new TaxClassPostVm("", "Standard");
        when(taxClassRepository.existsByName("Standard")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> taxClassService.create(request));
    }

    @Test
    void create_whenValidRequest_saveAndReturnTaxClass() {
        TaxClassPostVm request = new TaxClassPostVm("", "Standard");
        TaxClass saved = TaxClass.builder().id(1L).name("Standard").build();

        when(taxClassRepository.existsByName("Standard")).thenReturn(false);
        when(taxClassRepository.save(any(TaxClass.class))).thenReturn(saved);

        TaxClass result = taxClassService.create(request);

        assertThat(result.getId()).isEqualTo(1L);
        ArgumentCaptor<TaxClass> captor = ArgumentCaptor.forClass(TaxClass.class);
        verify(taxClassRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Standard");
    }

    @Test
    void update_whenTaxClassNotFound_throwNotFoundException() {
        TaxClassPostVm request = new TaxClassPostVm("", "New Name");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxClassService.update(request, 1L));
    }

    @Test
    void update_whenNameExistedInAnotherTaxClass_throwDuplicatedException() {
        TaxClass existing = TaxClass.builder().id(1L).name("Old Name").build();
        TaxClassPostVm request = new TaxClassPostVm("", "Duplicated");

        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Duplicated", 1L)).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> taxClassService.update(request, 1L));
    }

    @Test
    void update_whenValidRequest_updateAndSave() {
        TaxClass existing = TaxClass.builder().id(1L).name("Old Name").build();
        TaxClassPostVm request = new TaxClassPostVm("", "New Name");

        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("New Name", 1L)).thenReturn(false);

        taxClassService.update(request, 1L);

        assertThat(existing.getName()).isEqualTo("New Name");
        verify(taxClassRepository).save(existing);
    }

    @Test
    void delete_whenNotFound_throwNotFoundException() {
        when(taxClassRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxClassService.delete(99L));
    }

    @Test
    void delete_whenFound_deleteById() {
        when(taxClassRepository.existsById(1L)).thenReturn(true);

        taxClassService.delete(1L);

        verify(taxClassRepository).deleteById(1L);
    }

    @Test
    void getPageableTaxClasses_whenHasPageData_returnTaxClassListVm() {
        TaxClass taxClass = TaxClass.builder().id(1L).name("Standard").build();
        Page<TaxClass> page = new PageImpl<>(List.of(taxClass), PageRequest.of(0, 5), 1);
        when(taxClassRepository.findAll(PageRequest.of(0, 5))).thenReturn(page);

        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 5);

        assertThat(result.pageNo()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(5);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.taxClassContent()).hasSize(1);
    }
}

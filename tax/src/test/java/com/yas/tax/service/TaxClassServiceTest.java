package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    void findAllTaxClasses_whenCalled_returnList() {
        TaxClass taxClass = TaxClass.builder().id(1L).name("Tax Class").build();
        when(taxClassRepository.findAll(any(Sort.class))).thenReturn(List.of(taxClass));

        List<TaxClassVm> result = taxClassService.findAllTaxClasses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Tax Class");
    }

    @Test
    void findById_whenNotFound_throwNotFoundException() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxClassService.findById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_whenValid_returnVm() {
        TaxClass taxClass = TaxClass.builder().id(1L).name("Tax Class").build();
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));

        TaxClassVm result = taxClassService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void create_whenNameDuplicated_throwDuplicatedException() {
        TaxClassPostVm postVm = new TaxClassPostVm("TC1", "Tax Class");
        when(taxClassRepository.existsByName("Tax Class")).thenReturn(true);

        assertThatThrownBy(() -> taxClassService.create(postVm))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void create_whenValid_saveTaxClass() {
        TaxClassPostVm postVm = new TaxClassPostVm("TC1", "Tax Class");
        when(taxClassRepository.existsByName("Tax Class")).thenReturn(false);
        when(taxClassRepository.save(any())).thenReturn(new TaxClass());

        taxClassService.create(postVm);

        verify(taxClassRepository).save(any());
    }

    @Test
    void update_whenNotFound_throwNotFoundException() {
        TaxClassPostVm postVm = new TaxClassPostVm("TC1", "Updated");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxClassService.update(postVm, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_whenNameDuplicated_throwDuplicatedException() {
        TaxClassPostVm postVm = new TaxClassPostVm("TC1", "Updated");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(new TaxClass()));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Updated", 1L)).thenReturn(true);

        assertThatThrownBy(() -> taxClassService.update(postVm, 1L))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void update_whenValid_saveTaxClass() {
        TaxClassPostVm postVm = new TaxClassPostVm("TC1", "Updated");
        TaxClass taxClass = new TaxClass();
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Updated", 1L)).thenReturn(false);

        taxClassService.update(postVm, 1L);

        assertThat(taxClass.getName()).isEqualTo("Updated");
        verify(taxClassRepository).save(taxClass);
    }

    @Test
    void delete_whenNotFound_throwNotFoundException() {
        when(taxClassRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> taxClassService.delete(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_whenValid_deleteById() {
        when(taxClassRepository.existsById(1L)).thenReturn(true);

        taxClassService.delete(1L);

        verify(taxClassRepository).deleteById(1L);
    }

    @Test
    void getPageableTaxClasses_whenCalled_returnTaxClassListGetVm() {
        Page<TaxClass> page = new PageImpl<>(List.of(new TaxClass()), PageRequest.of(0, 10), 1);
        when(taxClassRepository.findAll(any(Pageable.class))).thenReturn(page);

        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 10);

        assertThat(result.totalElements()).isEqualTo(1);
    }
}

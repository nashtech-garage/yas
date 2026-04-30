package com.yas.tax.service;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaxClassServiceTest {

    @Mock
    private TaxClassRepository taxClassRepository;

    @InjectMocks
    private TaxClassService taxClassService;

    // ========== findById ==========

    @Test
    void findById_WhenExists_ShouldReturnTaxClassVm() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("VAT");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));

        TaxClassVm result = taxClassService.findById(1L);

        assertNotNull(result);
        assertEquals("VAT", result.name());
    }

    @Test
    void findById_WhenNotExists_ShouldThrowNotFoundException() {
        when(taxClassRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxClassService.findById(99L));
    }

    // ========== findAllTaxClasses ==========

    @Test
    void findAllTaxClasses_ShouldReturnList() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("VAT");
        when(taxClassRepository.findAll(any(Sort.class))).thenReturn(List.of(taxClass));

        List<TaxClassVm> result = taxClassService.findAllTaxClasses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("VAT", result.get(0).name());
    }

    @Test
    void findAllTaxClasses_WhenEmpty_ShouldReturnEmptyList() {
        when(taxClassRepository.findAll(any(Sort.class))).thenReturn(List.of());

        List<TaxClassVm> result = taxClassService.findAllTaxClasses();

        assertTrue(result.isEmpty());
    }

    // ========== create ==========

    @Test
    void create_WhenNameNotExists_ShouldReturnSavedTaxClass() {
        TaxClassPostVm postVm = new TaxClassPostVm("1", "VAT");
        TaxClass savedTaxClass = new TaxClass();
        savedTaxClass.setId(1L);
        savedTaxClass.setName("VAT");

        when(taxClassRepository.existsByName("VAT")).thenReturn(false);
        when(taxClassRepository.save(any(TaxClass.class))).thenReturn(savedTaxClass);

        TaxClass result = taxClassService.create(postVm);

        assertNotNull(result);
        assertEquals("VAT", result.getName());
        verify(taxClassRepository).save(any(TaxClass.class));
    }

    @Test
    void create_WhenNameAlreadyExists_ShouldThrowDuplicatedException() {
        TaxClassPostVm postVm = new TaxClassPostVm("1", "VAT");
        when(taxClassRepository.existsByName("VAT")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> taxClassService.create(postVm));
        verify(taxClassRepository, never()).save(any());
    }

    // ========== update ==========

    @Test
    void update_WhenValidData_ShouldUpdateSuccessfully() {
        TaxClassPostVm postVm = new TaxClassPostVm("1", "GST");
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("VAT");

        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("GST", 1L)).thenReturn(false);
        when(taxClassRepository.save(any(TaxClass.class))).thenReturn(taxClass);

        taxClassService.update(postVm, 1L);

        assertEquals("GST", taxClass.getName());
        verify(taxClassRepository).save(taxClass);
    }

    @Test
    void update_WhenTaxClassNotExists_ShouldThrowNotFoundException() {
        TaxClassPostVm postVm = new TaxClassPostVm("1", "GST");
        when(taxClassRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxClassService.update(postVm, 99L));
        verify(taxClassRepository, never()).save(any());
    }

    @Test
    void update_WhenNameAlreadyExists_ShouldThrowDuplicatedException() {
        TaxClassPostVm postVm = new TaxClassPostVm("1", "GST");
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("VAT");

        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("GST", 1L)).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> taxClassService.update(postVm, 1L));
        verify(taxClassRepository, never()).save(any());
    }

    // ========== delete ==========

    @Test
    void delete_WhenExists_ShouldDeleteSuccessfully() {
        when(taxClassRepository.existsById(1L)).thenReturn(true);

        taxClassService.delete(1L);

        verify(taxClassRepository).deleteById(1L);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowNotFoundException() {
        when(taxClassRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxClassService.delete(99L));
        verify(taxClassRepository, never()).deleteById(any());
    }

    // ========== getPageableTaxClasses ==========

    @Test
    void getPageableTaxClasses_WhenHasData_ShouldReturnTaxClassListGetVm() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("VAT");

        Page<TaxClass> page = new PageImpl<>(List.of(taxClass));
        when(taxClassRepository.findAll(any(Pageable.class))).thenReturn(page);

        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 10);

        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(1, result.taxClassContent().size());
        assertEquals("VAT", result.taxClassContent().get(0).name());
    }

    @Test
    void getPageableTaxClasses_WhenEmpty_ShouldReturnEmptyContent() {
        Page<TaxClass> page = new PageImpl<>(List.of());
        when(taxClassRepository.findAll(any(Pageable.class))).thenReturn(page);

        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 10);

        assertNotNull(result);
        assertTrue(result.taxClassContent().isEmpty());
        assertEquals(0, result.totalElements());
    }
}
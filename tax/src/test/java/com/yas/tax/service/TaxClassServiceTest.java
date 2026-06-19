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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TaxClassServiceTest {

    @Mock
    private TaxClassRepository taxClassRepository;

    @InjectMocks
    private TaxClassService taxClassService;

    private TaxClass taxClass;

    @BeforeEach
    void setUp() {
        taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Standard");
    }

    @Test
    void findAllTaxClasses_ShouldReturnList() {
        when(taxClassRepository.findAll(any(Sort.class))).thenReturn(List.of(taxClass));
        List<TaxClassVm> result = taxClassService.findAllTaxClasses();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Standard");
    }

    @Test
    void findById_WhenExist_ShouldReturnVm() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        TaxClassVm result = taxClassService.findById(1L);
        assertThat(result.name()).isEqualTo("Standard");
    }

    @Test
    void findById_WhenNotExist_ShouldThrowException() {
        when(taxClassRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> taxClassService.findById(2L));
    }

    @Test
    void create_WhenNameExists_ShouldThrowException() {
        TaxClassPostVm request = new TaxClassPostVm("1", "Standard");
        when(taxClassRepository.existsByName("Standard")).thenReturn(true);
        assertThrows(DuplicatedException.class, () -> taxClassService.create(request));
    }

    @Test
    void create_ValidRequest_ShouldSaveAndReturn() {
        TaxClassPostVm request = new TaxClassPostVm("1", "Standard");
        when(taxClassRepository.existsByName("Standard")).thenReturn(false);
        when(taxClassRepository.save(any())).thenReturn(taxClass);
        TaxClass result = taxClassService.create(request);
        assertThat(result.getName()).isEqualTo("Standard");
    }

    @Test
    void update_WhenNotFound_ShouldThrowException() {
        TaxClassPostVm request = new TaxClassPostVm("1", "Standard");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> taxClassService.update(request, 1L));
    }

    @Test
    void update_WhenNameExistsForOtherId_ShouldThrowException() {
        TaxClassPostVm request = new TaxClassPostVm("1", "Standard updated");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Standard updated", 1L)).thenReturn(true);
        assertThrows(DuplicatedException.class, () -> taxClassService.update(request, 1L));
    }

    @Test
    void update_ValidRequest_ShouldSave() {
        TaxClassPostVm request = new TaxClassPostVm("1", "Standard updated");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Standard updated", 1L)).thenReturn(false);
        taxClassService.update(request, 1L);
        verify(taxClassRepository).save(taxClass);
        assertThat(taxClass.getName()).isEqualTo("Standard updated");
    }

    @Test
    void delete_WhenNotFound_ShouldThrowException() {
        when(taxClassRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> taxClassService.delete(1L));
    }

    @Test
    void delete_WhenValid_ShouldDelete() {
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        taxClassService.delete(1L);
        verify(taxClassRepository).deleteById(1L);
    }

    @Test
    void getPageableTaxClasses_ShouldReturnPageableList() {
        Page<TaxClass> page = new PageImpl<>(List.of(taxClass), PageRequest.of(0, 10), 1);
        when(taxClassRepository.findAll(any(PageRequest.class))).thenReturn(page);

        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 10);
        assertThat(result.taxClassContent()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }
}

package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TaxClassServiceTest {

    @Mock
    TaxClassRepository taxClassRepository;

    @InjectMocks
    TaxClassService taxClassService;

    @Test
    void findAllTaxClassesShouldReturnSortedClasses() {
        TaxClass standard = taxClass(1L, "Standard");
        when(taxClassRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(List.of(standard));

        List<TaxClassVm> result = taxClassService.findAllTaxClasses();

        assertThat(result).containsExactly(new TaxClassVm(1L, "Standard"));
    }

    @Test
    void findByIdShouldReturnClassWhenFound() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass(1L, "Reduced")));

        TaxClassVm result = taxClassService.findById(1L);

        assertThat(result).isEqualTo(new TaxClassVm(1L, "Reduced"));
    }

    @Test
    void findByIdShouldThrowWhenMissing() {
        when(taxClassRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxClassService.findById(99L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createShouldSaveClassWhenNameIsUnique() {
        when(taxClassRepository.existsByName("Standard")).thenReturn(false);
        when(taxClassRepository.save(any(TaxClass.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaxClass created = taxClassService.create(new TaxClassPostVm(null, "Standard"));

        assertThat(created.getName()).isEqualTo("Standard");
    }

    @Test
    void createShouldThrowWhenNameExists() {
        when(taxClassRepository.existsByName("Standard")).thenReturn(true);

        assertThatThrownBy(() -> taxClassService.create(new TaxClassPostVm(null, "Standard")))
            .isInstanceOf(DuplicatedException.class);
        verify(taxClassRepository, never()).save(any());
    }

    @Test
    void updateShouldPersistChangedName() {
        TaxClass existing = taxClass(1L, "Old");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("New", 1L)).thenReturn(false);

        taxClassService.update(new TaxClassPostVm(null, "New"), 1L);

        ArgumentCaptor<TaxClass> captor = ArgumentCaptor.forClass(TaxClass.class);
        verify(taxClassRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("New");
    }

    @Test
    void updateShouldThrowWhenTargetMissing() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxClassService.update(new TaxClassPostVm(null, "New"), 1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateShouldThrowWhenNewNameAlreadyExists() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass(1L, "Old")));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("New", 1L)).thenReturn(true);

        assertThatThrownBy(() -> taxClassService.update(new TaxClassPostVm(null, "New"), 1L))
            .isInstanceOf(DuplicatedException.class);
        verify(taxClassRepository, never()).save(any());
    }

    @Test
    void deleteShouldRemoveExistingClass() {
        when(taxClassRepository.existsById(1L)).thenReturn(true);

        taxClassService.delete(1L);

        verify(taxClassRepository).deleteById(1L);
    }

    @Test
    void deleteShouldThrowWhenClassMissing() {
        when(taxClassRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> taxClassService.delete(1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getPageableTaxClassesShouldReturnPageMetadata() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        when(taxClassRepository.findAll(pageRequest))
            .thenReturn(new PageImpl<>(List.of(taxClass(1L, "Standard")), pageRequest, 1));

        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 2);

        assertThat(result.taxClassContent()).containsExactly(new TaxClassVm(1L, "Standard"));
        assertThat(result.pageNo()).isZero();
        assertThat(result.pageSize()).isEqualTo(2);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.isLast()).isTrue();
    }

    private static TaxClass taxClass(Long id, String name) {
        return TaxClass.builder().id(id).name(name).build();
    }
}

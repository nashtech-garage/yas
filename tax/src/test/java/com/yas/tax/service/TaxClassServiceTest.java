package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.constants.MessageCode;
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

    TaxClass taxClass;

    @BeforeEach
    void setUp() {
        taxClass = TaxClass.builder().id(1L).name("Standard").build();
    }

    @Test
    void findAllTaxClasses_shouldMapFromRepository() {
        when(taxClassRepository.findAll(Sort.by(Sort.Direction.ASC, "name")))
            .thenReturn(List.of(taxClass));

        List<TaxClassVm> result = taxClassService.findAllTaxClasses();

        assertThat(result).containsExactly(TaxClassVm.fromModel(taxClass));
    }

    @Test
    void findById_whenFound_shouldReturnVm() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));

        TaxClassVm result = taxClassService.findById(1L);

        assertThat(result).isEqualTo(TaxClassVm.fromModel(taxClass));
    }

    @Test
    void findById_whenMissing_shouldThrow() {
        when(taxClassRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxClassService.findById(2L))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(MessageCode.TAX_CLASS_NOT_FOUND);
    }

    @Test
    void create_whenNameExists_shouldThrow() {
        TaxClassPostVm request = new TaxClassPostVm("id", "Standard");
        when(taxClassRepository.existsByName("Standard")).thenReturn(true);

        assertThatThrownBy(() -> taxClassService.create(request))
            .isInstanceOf(DuplicatedException.class)
            .hasMessageContaining(MessageCode.NAME_ALREADY_EXITED);
    }

    @Test
    void create_shouldSaveAndReturnTaxClass() {
        TaxClassPostVm request = new TaxClassPostVm("id", "Premium");
        when(taxClassRepository.existsByName("Premium")).thenReturn(false);
        when(taxClassRepository.save(any(TaxClass.class)))
            .thenAnswer(invocation -> {
                TaxClass saved = invocation.getArgument(0);
                saved.setId(5L);
                return saved;
            });

        TaxClass result = taxClassService.create(request);

        assertAll(
            () -> assertThat(result.getId()).isEqualTo(5L),
            () -> assertThat(result.getName()).isEqualTo("Premium")
        );
    }

    @Test
    void update_whenTaxClassMissing_shouldThrow() {
        when(taxClassRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxClassService.update(new TaxClassPostVm("id", "New"), 3L))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(MessageCode.TAX_CLASS_NOT_FOUND);
    }

    @Test
    void update_whenNameUsedByOther_shouldThrow() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Clash", 1L)).thenReturn(true);

        assertThatThrownBy(() -> taxClassService.update(new TaxClassPostVm("id", "Clash"), 1L))
            .isInstanceOf(DuplicatedException.class)
            .hasMessageContaining(MessageCode.NAME_ALREADY_EXITED);
    }

    @Test
    void update_shouldPersistNewName() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Updated", 1L)).thenReturn(false);

        taxClassService.update(new TaxClassPostVm("id", "Updated"), 1L);

        ArgumentCaptor<TaxClass> captor = ArgumentCaptor.forClass(TaxClass.class);
        verify(taxClassRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Updated");
    }

    @Test
    void delete_whenMissing_shouldThrow() {
        when(taxClassRepository.existsById(7L)).thenReturn(false);

        assertThatThrownBy(() -> taxClassService.delete(7L))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(MessageCode.TAX_CLASS_NOT_FOUND);
    }

    @Test
    void delete_shouldInvokeRepository() {
        when(taxClassRepository.existsById(7L)).thenReturn(true);
        doNothing().when(taxClassRepository).deleteById(7L);

        taxClassService.delete(7L);

        verify(taxClassRepository).deleteById(7L);
    }

    @Test
    void getPageableTaxClasses_shouldReturnPagedResult() {
        TaxClass another = TaxClass.builder().id(2L).name("Reduced").build();
        when(taxClassRepository.findAll(any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(taxClass, another), PageRequest.of(0, 2), 4));

        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 2);

        assertAll(
            () -> assertThat(result.taxClassContent()).containsExactly(
                TaxClassVm.fromModel(taxClass), TaxClassVm.fromModel(another)),
            () -> assertThat(result.totalElements()).isEqualTo(4),
            () -> assertThat(result.totalPages()).isEqualTo(2),
            () -> assertThat(result.isLast()).isFalse()
        );
    }
}

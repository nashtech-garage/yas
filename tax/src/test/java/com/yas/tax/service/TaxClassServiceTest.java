package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

    private TaxClass taxClass1;
    private TaxClass taxClass2;

    @BeforeEach
    void setUp() {
        taxClass1 = TaxClass.builder().id(1L).name("Standard Tax").build();
        taxClass2 = TaxClass.builder().id(2L).name("Reduced Tax").build();
    }

    @Nested
    class FindAllTaxClasses {
        @Test
        void findAllTaxClasses_shouldReturnAllTaxClasses() {
            when(taxClassRepository.findAll(Sort.by(Sort.Direction.ASC, "name")))
                .thenReturn(List.of(taxClass1, taxClass2));

            List<TaxClassVm> result = taxClassService.findAllTaxClasses();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).id()).isEqualTo(1L);
            assertThat(result.get(0).name()).isEqualTo("Standard Tax");
            assertThat(result.get(1).id()).isEqualTo(2L);
            assertThat(result.get(1).name()).isEqualTo("Reduced Tax");
        }

        @Test
        void findAllTaxClasses_whenEmpty_shouldReturnEmptyList() {
            when(taxClassRepository.findAll(Sort.by(Sort.Direction.ASC, "name")))
                .thenReturn(List.of());

            List<TaxClassVm> result = taxClassService.findAllTaxClasses();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class FindById {
        @Test
        void findById_whenExists_shouldReturnTaxClassVm() {
            when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass1));

            TaxClassVm result = taxClassService.findById(1L);

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Standard Tax");
        }

        @Test
        void findById_whenNotExists_shouldThrowNotFoundException() {
            when(taxClassRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taxClassService.findById(99L))
                .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    class Create {
        @Test
        void create_whenNameNotDuplicated_shouldSaveAndReturn() {
            TaxClassPostVm postVm = new TaxClassPostVm("id1", "New Tax Class");
            TaxClass savedTaxClass = TaxClass.builder().id(3L).name("New Tax Class").build();

            when(taxClassRepository.existsByName("New Tax Class")).thenReturn(false);
            when(taxClassRepository.save(any(TaxClass.class))).thenReturn(savedTaxClass);

            TaxClass result = taxClassService.create(postVm);

            assertThat(result.getId()).isEqualTo(3L);
            assertThat(result.getName()).isEqualTo("New Tax Class");
            verify(taxClassRepository).save(any(TaxClass.class));
        }

        @Test
        void create_whenNameDuplicated_shouldThrowDuplicatedException() {
            TaxClassPostVm postVm = new TaxClassPostVm("id1", "Standard Tax");

            when(taxClassRepository.existsByName("Standard Tax")).thenReturn(true);

            assertThatThrownBy(() -> taxClassService.create(postVm))
                .isInstanceOf(DuplicatedException.class);
            verify(taxClassRepository, never()).save(any(TaxClass.class));
        }
    }

    @Nested
    class Update {
        @Test
        void update_whenExistsAndNameNotDuplicated_shouldUpdate() {
            TaxClassPostVm postVm = new TaxClassPostVm("id1", "Updated Tax");

            when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass1));
            when(taxClassRepository.existsByNameNotUpdatingTaxClass("Updated Tax", 1L))
                .thenReturn(false);

            taxClassService.update(postVm, 1L);

            assertThat(taxClass1.getName()).isEqualTo("Updated Tax");
            verify(taxClassRepository).save(taxClass1);
        }

        @Test
        void update_whenNotExists_shouldThrowNotFoundException() {
            TaxClassPostVm postVm = new TaxClassPostVm("id1", "Updated Tax");

            when(taxClassRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taxClassService.update(postVm, 99L))
                .isInstanceOf(NotFoundException.class);
        }

        @Test
        void update_whenNameDuplicatedOnAnotherRecord_shouldThrowDuplicatedException() {
            TaxClassPostVm postVm = new TaxClassPostVm("id1", "Reduced Tax");

            when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass1));
            when(taxClassRepository.existsByNameNotUpdatingTaxClass("Reduced Tax", 1L))
                .thenReturn(true);

            assertThatThrownBy(() -> taxClassService.update(postVm, 1L))
                .isInstanceOf(DuplicatedException.class);
            verify(taxClassRepository, never()).save(any(TaxClass.class));
        }
    }

    @Nested
    class Delete {
        @Test
        void delete_whenExists_shouldDeleteSuccessfully() {
            when(taxClassRepository.existsById(1L)).thenReturn(true);

            taxClassService.delete(1L);

            verify(taxClassRepository).deleteById(1L);
        }

        @Test
        void delete_whenNotExists_shouldThrowNotFoundException() {
            when(taxClassRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> taxClassService.delete(99L))
                .isInstanceOf(NotFoundException.class);
            verify(taxClassRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    class GetPageableTaxClasses {
        @Test
        void getPageableTaxClasses_shouldReturnPagedResult() {
            Page<TaxClass> page = new PageImpl<>(
                List.of(taxClass1, taxClass2),
                PageRequest.of(0, 10),
                2
            );
            when(taxClassRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

            TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 10);

            assertThat(result.taxClassContent()).hasSize(2);
            assertThat(result.pageNo()).isEqualTo(0);
            assertThat(result.pageSize()).isEqualTo(10);
            assertThat(result.totalElements()).isEqualTo(2);
            assertThat(result.totalPages()).isEqualTo(1);
            assertThat(result.isLast()).isTrue();
        }

        @Test
        void getPageableTaxClasses_whenEmpty_shouldReturnEmptyPage() {
            Page<TaxClass> emptyPage = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 10),
                0
            );
            when(taxClassRepository.findAll(PageRequest.of(0, 10))).thenReturn(emptyPage);

            TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 10);

            assertThat(result.taxClassContent()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
        }
    }
}

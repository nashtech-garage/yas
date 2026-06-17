package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.repository.BrandRepository;
import com.yas.product.viewmodel.brand.BrandListGetVm;
import com.yas.product.viewmodel.brand.BrandPostVm;
import com.yas.product.viewmodel.brand.BrandVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Nested
    class CreateBrandTest {

        @Test
        void create_validVm_savesAndReturnsBrand() {
            BrandPostVm vm = new BrandPostVm("BrandX", "brandx", true);
            Brand saved = vm.toModel();
            saved.setId(1L);

            when(brandRepository.findExistedName("BrandX", null)).thenReturn(null);
            when(brandRepository.save(any(Brand.class))).thenReturn(saved);

            Brand result = brandService.create(vm);

            assertNotNull(result);
            assertEquals("BrandX", result.getName());
            assertEquals("brandx", result.getSlug());
            verify(brandRepository).save(any(Brand.class));
        }

        @Test
        void create_duplicateName_throwsDuplicatedException() {
            BrandPostVm vm = new BrandPostVm("ExistingBrand", "existing-brand", true);
            when(brandRepository.findExistedName("ExistingBrand", null)).thenReturn(new Brand());

            assertThrows(DuplicatedException.class, () -> brandService.create(vm));
            verify(brandRepository, never()).save(any());
        }
    }

    @Nested
    class UpdateBrandTest {

        @Test
        void update_validVm_updatesAndReturnsBrand() {
            BrandPostVm vm = new BrandPostVm("UpdatedBrand", "updated-brand", true);
            Brand existing = new Brand();
            existing.setId(1L);
            existing.setName("OldBrand");

            when(brandRepository.findExistedName("UpdatedBrand", 1L)).thenReturn(null);
            when(brandRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(brandRepository.save(any(Brand.class))).thenReturn(existing);

            Brand result = brandService.update(vm, 1L);

            assertNotNull(result);
            verify(brandRepository).save(any(Brand.class));
        }

        @Test
        void update_notFound_throwsNotFoundException() {
            BrandPostVm vm = new BrandPostVm("UpdatedBrand", "updated-brand", true);

            when(brandRepository.findExistedName("UpdatedBrand", 99L)).thenReturn(null);
            when(brandRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> brandService.update(vm, 99L));
        }

        @Test
        void update_duplicateName_throwsDuplicatedException() {
            BrandPostVm vm = new BrandPostVm("TakenName", "taken-name", true);
            when(brandRepository.findExistedName("TakenName", 1L)).thenReturn(new Brand());

            assertThrows(DuplicatedException.class, () -> brandService.update(vm, 1L));
        }
    }

    @Nested
    class GetBrandsTest {

        @Test
        void getBrands_returnsPagedResult() {
            Brand brand = new Brand();
            brand.setId(1L);
            brand.setName("BrandA");
            brand.setSlug("brand-a");

            when(brandRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(brand)));

            BrandListGetVm result = brandService.getBrands(0, 10);

            assertNotNull(result);
            assertEquals(1, result.brandContent().size());
        }

        @Test
        void getBrands_emptyPage_returnsEmptyList() {
            when(brandRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

            BrandListGetVm result = brandService.getBrands(0, 10);

            assertNotNull(result);
            assertTrue(result.brandContent().isEmpty());
        }
    }

    @Nested
    class GetBrandsByIdsTest {

        @Test
        void getBrandsByIds_returnsVmList() {
            Brand brand = new Brand();
            brand.setId(1L);
            brand.setName("BrandA");
            brand.setSlug("brand-a");

            when(brandRepository.findAllById(List.of(1L))).thenReturn(List.of(brand));

            List<BrandVm> result = brandService.getBrandsByIds(List.of(1L));

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("BrandA", result.get(0).name());
        }
    }

    @Nested
    class DeleteBrandTest {

        @Test
        void delete_brandWithProducts_throwsBadRequestException() {
            Brand brand = new Brand();
            brand.setId(1L);
            brand.setProducts(List.of(new Product()));

            when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

            assertThrows(BadRequestException.class, () -> brandService.delete(1L));
            verify(brandRepository, never()).deleteById(any());
        }

        @Test
        void delete_cleanBrand_deletesSuccessfully() {
            Brand brand = new Brand();
            brand.setId(1L);
            brand.setProducts(List.of());

            when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

            brandService.delete(1L);

            verify(brandRepository).deleteById(1L);
        }

        @Test
        void delete_brandNotFound_throwsNotFoundException() {
            when(brandRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> brandService.delete(99L));
        }
    }
}

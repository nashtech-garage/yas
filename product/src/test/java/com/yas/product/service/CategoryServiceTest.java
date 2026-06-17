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
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.category.CategoryGetDetailVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryListGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;
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
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryPostVm buildVm(String name, Long parentId) {
        return new CategoryPostVm(name, "slug-" + name.toLowerCase().replace(" ", "-"),
            "desc", parentId, "keywords", "meta", (short) 1, true, null);
    }

    @Nested
    class CreateCategoryTest {

        @Test
        void create_validVm_savesAndReturnsCategory() {
            CategoryPostVm vm = buildVm("Electronics", null);
            Category saved = new Category();
            saved.setId(1L);
            saved.setName("Electronics");

            when(categoryRepository.findExistedName("Electronics", null)).thenReturn(null);
            when(categoryRepository.save(any(Category.class))).thenReturn(saved);

            Category result = categoryService.create(vm);

            assertNotNull(result);
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        void create_duplicateName_throwsDuplicatedException() {
            CategoryPostVm vm = buildVm("Electronics", null);
            Category existing = new Category();
            existing.setId(99L);

            when(categoryRepository.findExistedName("Electronics", null)).thenReturn(existing);

            assertThrows(DuplicatedException.class, () -> categoryService.create(vm));
            verify(categoryRepository, never()).save(any());
        }

        @Test
        void create_withParentId_setsParentCategory() {
            CategoryPostVm vm = buildVm("Laptops", 1L);
            Category parent = new Category();
            parent.setId(1L);
            parent.setName("Electronics");

            Category saved = new Category();
            saved.setId(2L);
            saved.setName("Laptops");
            saved.setParent(parent);

            when(categoryRepository.findExistedName("Laptops", null)).thenReturn(null);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(parent));
            when(categoryRepository.save(any(Category.class))).thenReturn(saved);

            Category result = categoryService.create(vm);

            assertNotNull(result);
            verify(categoryRepository).findById(1L);
        }

        @Test
        void create_withParentIdNotFound_throwsBadRequestException() {
            CategoryPostVm vm = buildVm("Laptops", 99L);

            when(categoryRepository.findExistedName("Laptops", null)).thenReturn(null);
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(BadRequestException.class, () -> categoryService.create(vm));
        }
    }

    @Nested
    class UpdateCategoryTest {

        @Test
        void update_notFound_throwsNotFoundException() {
            CategoryPostVm vm = buildVm("Updated", null);

            when(categoryRepository.findExistedName("Updated", 99L)).thenReturn(null);
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> categoryService.update(vm, 99L));
        }

        @Test
        void update_validVm_updatesCategory() {
            CategoryPostVm vm = buildVm("Updated", null);
            Category existing = new Category();
            existing.setId(1L);
            existing.setName("Old");

            when(categoryRepository.findExistedName("Updated", 1L)).thenReturn(null);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));

            categoryService.update(vm, 1L);

            verify(categoryRepository).findById(1L);
        }

        @Test
        void update_withParentId_setsParent() {
            CategoryPostVm vm = buildVm("Updated", 5L);
            Category existing = new Category();
            existing.setId(1L);
            existing.setName("Old");

            Category parent = new Category();
            parent.setId(5L);
            parent.setName("Parent");

            when(categoryRepository.findExistedName("Updated", 1L)).thenReturn(null);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(categoryRepository.findById(5L)).thenReturn(Optional.of(parent));

            categoryService.update(vm, 1L);

            verify(categoryRepository).findById(5L);
        }

        @Test
        void update_withSelfAsParent_throwsBadRequestException() {
            CategoryPostVm vm = buildVm("Updated", 1L);
            Category existing = new Category();
            existing.setId(1L);
            existing.setName("Old");

            // parent is the category itself
            Category selfAsParent = new Category();
            selfAsParent.setId(1L);
            selfAsParent.setName("Old");

            when(categoryRepository.findExistedName("Updated", 1L)).thenReturn(null);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));

            assertThrows(BadRequestException.class, () -> categoryService.update(vm, 1L));
        }
    }

    @Nested
    class GetPageableCategoriesTest {

        @Test
        void getPageableCategories_returnsPagedResult() {
            Category cat = new Category();
            cat.setId(1L);
            cat.setName("Electronics");
            cat.setSlug("electronics");

            when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(cat)));

            CategoryListGetVm result = categoryService.getPageableCategories(0, 10);

            assertNotNull(result);
            assertEquals(1, result.categoryContent().size());
        }

        @Test
        void getPageableCategories_empty_returnsEmpty() {
            when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

            CategoryListGetVm result = categoryService.getPageableCategories(0, 10);

            assertNotNull(result);
            assertTrue(result.categoryContent().isEmpty());
        }
    }

    @Nested
    class GetCategoryByIdTest {

        @Test
        void getCategoryById_found_returnsDetailVm() {
            Category cat = new Category();
            cat.setId(1L);
            cat.setName("Electronics");
            cat.setSlug("electronics");
            cat.setDisplayOrder((short) 0);

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));

            CategoryGetDetailVm result = categoryService.getCategoryById(1L);

            assertNotNull(result);
            assertEquals("Electronics", result.name());
        }

        @Test
        void getCategoryById_withImageId_callsMediaService() {
            Category cat = new Category();
            cat.setId(1L);
            cat.setName("Electronics");
            cat.setSlug("electronics");
            cat.setDisplayOrder((short) 0);
            cat.setImageId(5L);

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
            when(mediaService.getMedia(5L)).thenReturn(
                new NoFileMediaVm(5L, "", "", "", "http://img.jpg"));

            CategoryGetDetailVm result = categoryService.getCategoryById(1L);

            assertNotNull(result);
            verify(mediaService).getMedia(5L);
        }

        @Test
        void getCategoryById_withParent_returnsParentId() {
            Category parent = new Category();
            parent.setId(10L);
            parent.setName("ParentCat");
            parent.setDisplayOrder((short) 0);

            Category cat = new Category();
            cat.setId(1L);
            cat.setName("Child");
            cat.setSlug("child");
            cat.setDisplayOrder((short) 0);
            cat.setParent(parent);

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));

            CategoryGetDetailVm result = categoryService.getCategoryById(1L);

            assertNotNull(result);
            assertEquals(10L, result.parentId());
        }

        @Test
        void getCategoryById_notFound_throwsNotFoundException() {
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(99L));
        }
    }

    @Nested
    class GetCategoriesTest {

        @Test
        void getCategories_returnsMatchingList() {
            Category cat = new Category();
            cat.setId(1L);
            cat.setName("Electronics");
            cat.setSlug("electronics");

            when(categoryRepository.findByNameContainingIgnoreCase("Elec")).thenReturn(List.of(cat));

            List<CategoryGetVm> result = categoryService.getCategories("Elec");

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void getCategories_withImageId_callsMediaService() {
            Category cat = new Category();
            cat.setId(1L);
            cat.setName("Electronics");
            cat.setSlug("electronics");
            cat.setImageId(3L);

            when(categoryRepository.findByNameContainingIgnoreCase("Elec")).thenReturn(List.of(cat));
            when(mediaService.getMedia(3L)).thenReturn(
                new NoFileMediaVm(3L, "", "", "", "http://img.jpg"));

            List<CategoryGetVm> result = categoryService.getCategories("Elec");

            assertNotNull(result);
            verify(mediaService).getMedia(3L);
        }

        @Test
        void getCategories_withParent_setsParentId() {
            Category parent = new Category();
            parent.setId(5L);

            Category cat = new Category();
            cat.setId(1L);
            cat.setName("Child");
            cat.setSlug("child");
            cat.setParent(parent);

            when(categoryRepository.findByNameContainingIgnoreCase("Child")).thenReturn(List.of(cat));

            List<CategoryGetVm> result = categoryService.getCategories("Child");

            assertNotNull(result);
            assertEquals(5L, result.get(0).parentId());
        }
    }

    @Nested
    class GetCategoryByIdsTest {

        @Test
        void getCategoryByIds_returnsVmList() {
            Category cat = new Category();
            cat.setId(1L);
            cat.setName("Electronics");
            cat.setSlug("electronics");

            when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(cat));

            List<CategoryGetVm> result = categoryService.getCategoryByIds(List.of(1L));

            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Nested
    class GetTopNthCategoriesTest {

        @Test
        void getTopNthCategories_returnsLimitedList() {
            when(categoryRepository.findCategoriesOrderedByProductCount(any(Pageable.class)))
                .thenReturn(List.of("Electronics", "Clothing"));

            List<String> result = categoryService.getTopNthCategories(2);

            assertNotNull(result);
            assertEquals(2, result.size());
        }
    }
}

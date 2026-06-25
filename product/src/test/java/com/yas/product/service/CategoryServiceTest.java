/**
 * Unit tests for {@link CategoryService}.
 * Coverage target: >= 70% line coverage.
 * Tests: GetPageableCategoriesTest, CreateTest, UpdateTest, GetCategoryByIdTest,
 *        GetCategoriesTest, GetCategoryByIdsTest, GetTopNthCategoriesTest
 */
package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private static final Long CATEGORY_ID = 1L;
    private static final Long PARENT_ID = 2L;
    private static final String CATEGORY_NAME = "Electronics";
    private static final String SLUG = "electronics";
    private static final String DESCRIPTION = "Electronic items";
    private static final Long IMAGE_ID = 100L;

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private MediaService mediaService;

    @InjectMocks
    private CategoryService categoryService;

    private Category buildCategory() {
        Category c = new Category();
        c.setId(CATEGORY_ID);
        c.setName(CATEGORY_NAME);
        c.setSlug(SLUG);
        c.setDescription(DESCRIPTION);
        c.setDisplayOrder((short) 1);
        c.setIsPublished(true);
        c.setImageId(IMAGE_ID);
        c.setCategories(new ArrayList<>());
        c.setProductCategories(new ArrayList<>());
        return c;
    }

    @Nested
    class GetPageableCategoriesTest {
        @Test
        void testGetPageableCategories_whenCategoriesExist_shouldReturnList() {
            Category cat = buildCategory();
            Page<Category> page = new PageImpl<>(List.of(cat), PageRequest.of(0, 10), 1);
            when(categoryRepository.findAll(any(Pageable.class))).thenReturn(page);

            CategoryListGetVm result = categoryService.getPageableCategories(0, 10);

            assertThat(result.categoryContent()).hasSize(1);
            assertThat(result.pageNo()).isZero();
            assertThat(result.totalElements()).isEqualTo(1);
        }

        @Test
        void testGetPageableCategories_whenEmpty_shouldReturnEmpty() {
            Page<Category> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
            when(categoryRepository.findAll(any(Pageable.class))).thenReturn(page);

            CategoryListGetVm result = categoryService.getPageableCategories(0, 10);

            assertThat(result.categoryContent()).isEmpty();
        }
    }

    @Nested
    class CreateTest {
        @Test
        void testCreate_whenValid_shouldSaveAndReturn() {
            CategoryPostVm vm = new CategoryPostVm(CATEGORY_NAME, SLUG, DESCRIPTION,
                    null, "kw", "meta", (short) 1, true, IMAGE_ID);
            when(categoryRepository.findExistedName(CATEGORY_NAME, null)).thenReturn(null);
            Category saved = buildCategory();
            when(categoryRepository.save(any(Category.class))).thenReturn(saved);

            Category result = categoryService.create(vm);

            assertThat(result.getName()).isEqualTo(CATEGORY_NAME);
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        void testCreate_whenDuplicateName_shouldThrowDuplicatedException() {
            CategoryPostVm vm = new CategoryPostVm(CATEGORY_NAME, SLUG, DESCRIPTION,
                    null, "kw", "meta", (short) 1, true, IMAGE_ID);
            when(categoryRepository.findExistedName(CATEGORY_NAME, null)).thenReturn(new Category());

            assertThrows(DuplicatedException.class, () -> categoryService.create(vm));
        }

        @Test
        void testCreate_whenParentExists_shouldSetParent() {
            Category parent = buildCategory();
            parent.setId(PARENT_ID);
            CategoryPostVm vm = new CategoryPostVm(CATEGORY_NAME, SLUG, DESCRIPTION,
                    PARENT_ID, "kw", "meta", (short) 1, true, IMAGE_ID);
            when(categoryRepository.findExistedName(CATEGORY_NAME, null)).thenReturn(null);
            when(categoryRepository.findById(PARENT_ID)).thenReturn(Optional.of(parent));
            when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

            Category result = categoryService.create(vm);

            assertThat(result.getParent()).isNotNull();
            assertThat(result.getParent().getId()).isEqualTo(PARENT_ID);
        }

        @Test
        void testCreate_whenParentNotFound_shouldThrowBadRequestException() {
            CategoryPostVm vm = new CategoryPostVm(CATEGORY_NAME, SLUG, DESCRIPTION,
                    PARENT_ID, "kw", "meta", (short) 1, true, IMAGE_ID);
            when(categoryRepository.findExistedName(CATEGORY_NAME, null)).thenReturn(null);
            when(categoryRepository.findById(PARENT_ID)).thenReturn(Optional.empty());

            assertThrows(BadRequestException.class, () -> categoryService.create(vm));
        }
    }

    @Nested
    class UpdateTest {
        @Test
        void testUpdate_whenValid_shouldUpdateFields() {
            Category existing = buildCategory();
            CategoryPostVm vm = new CategoryPostVm("Updated", "updated-slug", "desc",
                    null, "kw", "meta", (short) 2, false, null);
            when(categoryRepository.findExistedName("Updated", CATEGORY_ID)).thenReturn(null);
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(existing));

            categoryService.update(vm, CATEGORY_ID);

            assertThat(existing.getName()).isEqualTo("Updated");
            assertThat(existing.getSlug()).isEqualTo("updated-slug");
            assertThat(existing.getParent()).isNull();
        }

        @Test
        void testUpdate_whenNotFound_shouldThrowNotFoundException() {
            CategoryPostVm vm = new CategoryPostVm("Updated", "slug", "d",
                    null, "kw", "meta", (short) 1, true, null);
            when(categoryRepository.findExistedName("Updated", CATEGORY_ID)).thenReturn(null);
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> categoryService.update(vm, CATEGORY_ID));
        }

        @Test
        void testUpdate_whenDuplicateName_shouldThrowDuplicatedException() {
            CategoryPostVm vm = new CategoryPostVm(CATEGORY_NAME, SLUG, DESCRIPTION,
                    null, "kw", "meta", (short) 1, true, IMAGE_ID);
            when(categoryRepository.findExistedName(CATEGORY_NAME, CATEGORY_ID)).thenReturn(new Category());

            assertThrows(DuplicatedException.class, () -> categoryService.update(vm, CATEGORY_ID));
        }

        @Test
        void testUpdate_whenParentIsItself_shouldThrowBadRequestException() {
            Category existing = buildCategory();
            CategoryPostVm vm = new CategoryPostVm("Updated", "slug", "d",
                    CATEGORY_ID, "kw", "meta", (short) 1, true, null);
            when(categoryRepository.findExistedName("Updated", CATEGORY_ID)).thenReturn(null);
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(existing));

            assertThrows(BadRequestException.class, () -> categoryService.update(vm, CATEGORY_ID));
        }
    }

    @Nested
    class GetCategoryByIdTest {
        @Test
        void testGetCategoryById_whenExists_shouldReturnDetailVm() {
            Category cat = buildCategory();
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(cat));
            when(mediaService.getMedia(IMAGE_ID))
                    .thenReturn(new NoFileMediaVm(IMAGE_ID, "", "", "", "http://img.jpg"));

            CategoryGetDetailVm result = categoryService.getCategoryById(CATEGORY_ID);

            assertThat(result.id()).isEqualTo(CATEGORY_ID);
            assertThat(result.name()).isEqualTo(CATEGORY_NAME);
            assertThat(result.categoryImage()).isNotNull();
        }

        @Test
        void testGetCategoryById_whenNotFound_shouldThrowNotFoundException() {
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(CATEGORY_ID));
        }

        @Test
        void testGetCategoryById_whenNoImage_shouldReturnNullImage() {
            Category cat = buildCategory();
            cat.setImageId(null);
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(cat));

            CategoryGetDetailVm result = categoryService.getCategoryById(CATEGORY_ID);

            assertThat(result.categoryImage()).isNull();
        }

        @Test
        void testGetCategoryById_whenHasParent_shouldReturnParentId() {
            Category parent = buildCategory();
            parent.setId(PARENT_ID);
            Category cat = buildCategory();
            cat.setParent(parent);
            cat.setImageId(null);
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(cat));

            CategoryGetDetailVm result = categoryService.getCategoryById(CATEGORY_ID);

            assertThat(result.parentId()).isEqualTo(PARENT_ID);
        }
    }

    @Nested
    class GetCategoriesTest {
        @Test
        void testGetCategories_whenFound_shouldReturnList() {
            Category cat = buildCategory();
            when(categoryRepository.findByNameContainingIgnoreCase("Elec")).thenReturn(List.of(cat));
            when(mediaService.getMedia(IMAGE_ID))
                    .thenReturn(new NoFileMediaVm(IMAGE_ID, "", "", "", "http://img.jpg"));

            List<CategoryGetVm> result = categoryService.getCategories("Elec");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo(CATEGORY_NAME);
        }

        @Test
        void testGetCategories_whenEmpty_shouldReturnEmpty() {
            when(categoryRepository.findByNameContainingIgnoreCase("xyz")).thenReturn(List.of());

            List<CategoryGetVm> result = categoryService.getCategories("xyz");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class GetCategoryByIdsTest {
        @Test
        void testGetCategoryByIds_whenFound_shouldReturnList() {
            Category cat = buildCategory();
            when(categoryRepository.findAllById(List.of(CATEGORY_ID))).thenReturn(List.of(cat));

            List<CategoryGetVm> result = categoryService.getCategoryByIds(List.of(CATEGORY_ID));

            assertThat(result).hasSize(1);
        }

        @Test
        void testGetCategoryByIds_whenEmpty_shouldReturnEmpty() {
            when(categoryRepository.findAllById(List.of())).thenReturn(List.of());

            List<CategoryGetVm> result = categoryService.getCategoryByIds(List.of());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class GetTopNthCategoriesTest {
        @Test
        void testGetTopNthCategories_shouldReturnCategoryNames() {
            when(categoryRepository.findCategoriesOrderedByProductCount(any(Pageable.class)))
                    .thenReturn(List.of("Electronics", "Clothing"));

            List<String> result = categoryService.getTopNthCategories(2);

            assertThat(result).hasSize(2);
            assertThat(result.get(0)).isEqualTo("Electronics");
        }
    }
}
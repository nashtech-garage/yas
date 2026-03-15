package com.yas.product.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private Category parentCategory;

    @BeforeEach
    void setUp() {
        // Khởi tạo Category mẫu với đầy đủ field để tránh NPE khi unboxing
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setSlug("electronics");
        category.setDescription("Description");
        category.setImageId(10L);
        category.setDisplayOrder((short) 1); // Fix lỗi ép kiểu int sang Short
        category.setIsPublished(true);
        category.setMetaKeyword("key");
        category.setMetaDescription("desc");

        parentCategory = new Category();
        parentCategory.setId(2L);
        parentCategory.setName("Parent Category");
        parentCategory.setDisplayOrder((short) 0);
    }

    @Test
    @DisplayName("Lấy danh sách phân trang - Thành công")
    void getPageableCategories_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(List.of(category));
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        CategoryListGetVm result = categoryService.getPageableCategories(0, 10);

        assertThat(result.categoryContent()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Tạo Category - Thành công")
    void create_Success() {
        CategoryPostVm postVm = mock(CategoryPostVm.class);
        when(postVm.name()).thenReturn("New Category");
        when(postVm.parentId()).thenReturn(null);
        
        when(categoryRepository.findExistedName(anyString(), any())).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.create(postVm);

        assertThat(result).isNotNull();
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Cập nhật Category - Thành công (Dirty Checking)")
    void update_Success() {
        // Arrange
        CategoryPostVm postVm = mock(CategoryPostVm.class);
        when(postVm.name()).thenReturn("Updated Name");
        when(postVm.slug()).thenReturn("updated-slug");
        when(postVm.parentId()).thenReturn(null);
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        // Code gọi validateDuplicateName(name, id) -> repository.findExistedName(name, id)
        when(categoryRepository.findExistedName("Updated Name", 1L)).thenReturn(null);

        // Act
        categoryService.update(postVm, 1L);

        // Assert
        // Vì code dùng @Transactional, ta kiểm tra giá trị của object đã thay đổi
        assertThat(category.getName()).isEqualTo("Updated Name");
        assertThat(category.getSlug()).isEqualTo("updated-slug");
        
        // Kiểm tra tương tác thay vì lệnh save() nếu code không gọi save() tường minh
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).findExistedName("Updated Name", 1L);
    }

    @Test
    @DisplayName("Cập nhật Category - Lỗi trùng tên")
    void update_DuplicateName_ThrowsException() {
        CategoryPostVm postVm = mock(CategoryPostVm.class);
        when(postVm.name()).thenReturn("Existing Name");
        
        when(categoryRepository.findExistedName("Existing Name", 1L)).thenReturn(new Category());

        assertThatThrownBy(() -> categoryService.update(postVm, 1L))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    @DisplayName("Cập nhật Category - Lỗi chọn chính mình làm cha")
    void update_CircularReference_ThrowsBadRequest() {
        CategoryPostVm postVm = mock(CategoryPostVm.class);
        when(postVm.parentId()).thenReturn(1L); 
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> categoryService.update(postVm, 1L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("Lấy chi tiết Category - Thành công")
    void getCategoryById_Success() {
        category.setParent(parentCategory);
        NoFileMediaVm mockMedia = mock(NoFileMediaVm.class);
        when(mockMedia.url()).thenReturn("http://image.url");
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(mediaService.getMedia(10L)).thenReturn(mockMedia);

        CategoryGetDetailVm result = categoryService.getCategoryById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.parentId()).isEqualTo(2L);
        assertThat(result.displayOrder()).isEqualTo((short) 1);
        assertThat(result.categoryImage().url()).isEqualTo("http://image.url");
    }

    @Test
    @DisplayName("Tìm kiếm theo tên - Thành công")
    void getCategories_ByName_Success() {
        when(categoryRepository.findByNameContainingIgnoreCase("Elec")).thenReturn(List.of(category));
        NoFileMediaVm mockMedia = mock(NoFileMediaVm.class);
        when(mockMedia.url()).thenReturn("url");
        when(mediaService.getMedia(anyLong())).thenReturn(mockMedia);

        List<CategoryGetVm> result = categoryService.getCategories("Elec");

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).name()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("Lấy danh sách theo IDs - Thành công")
    void getCategoryByIds_Success() {
        when(categoryRepository.findAllById(anyList())).thenReturn(List.of(category));

        List<CategoryGetVm> result = categoryService.getCategoryByIds(List.of(1L));

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Lấy Top Category - Thành công")
    void getTopNthCategories_Success() {
        Pageable pageable = PageRequest.of(0, 5);
        when(categoryRepository.findCategoriesOrderedByProductCount(pageable))
                .thenReturn(List.of("Cat1", "Cat2"));

        List<String> result = categoryService.getTopNthCategories(5);

        assertThat(result).hasSize(2);
        verify(categoryRepository).findCategoriesOrderedByProductCount(pageable);
    }
    @Test
    @DisplayName("Tạo Category - Trùng tên")
    void create_DuplicateName_ThrowsException() {
        CategoryPostVm postVm = mock(CategoryPostVm.class);
        when(postVm.name()).thenReturn("Electronics");

        when(categoryRepository.findExistedName("Electronics", null))
                .thenReturn(new Category());

        assertThatThrownBy(() -> categoryService.create(postVm))
                .isInstanceOf(DuplicatedException.class);
    }
    @Test
    @DisplayName("Tạo Category - Có parent")
    void create_WithParent_Success() {
        CategoryPostVm postVm = mock(CategoryPostVm.class);

        when(postVm.name()).thenReturn("Child Category");
        when(postVm.parentId()).thenReturn(2L);

        when(categoryRepository.findExistedName(anyString(), any()))
                .thenReturn(null);

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(parentCategory));

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);

        Category result = categoryService.create(postVm);

        assertThat(result).isNotNull();
        verify(categoryRepository).findById(2L);
    }
    @Test
    @DisplayName("Tạo Category - Parent không tồn tại")
    void create_ParentNotFound() {
        CategoryPostVm postVm = mock(CategoryPostVm.class);

        when(postVm.name()).thenReturn("Child Category");
        when(postVm.parentId()).thenReturn(99L);

        when(categoryRepository.findExistedName(anyString(), any()))
                .thenReturn(null);

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.create(postVm))
                .isInstanceOf(BadRequestException.class);
    }
    @Test
    @DisplayName("Update Category - Không tìm thấy")
    void update_CategoryNotFound() {
        CategoryPostVm postVm = mock(CategoryPostVm.class);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(postVm, 1L))
                .isInstanceOf(NotFoundException.class);
    }
    @Test
    @DisplayName("Lấy chi tiết Category - Không tồn tại")
    void getCategoryById_NotFound() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(1L))
                .isInstanceOf(NotFoundException.class);
    }
    @Test
    @DisplayName("Lấy Category theo IDs - List rỗng")
    void getCategoryByIds_EmptyList() {

        when(categoryRepository.findAllById(anyList()))
                .thenReturn(List.of());

        List<CategoryGetVm> result = categoryService.getCategoryByIds(List.of());

        assertThat(result).isEmpty();
    }
}

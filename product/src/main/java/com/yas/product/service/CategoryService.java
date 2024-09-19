package com.yas.product.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.ImageVm;
import com.yas.product.viewmodel.category.CategoryGetDetailVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryListGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    private final MediaService mediaService;

    public CategoryListGetVm getPageableCategories(int pageNo, int pageSize) {
        List<CategoryGetVm> categoryGetVms = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<Category> categories = categoryPage.getContent();
        for (Category category : categories) {
            categoryGetVms.add(CategoryGetVm.fromModel(category));
        }

        return new CategoryListGetVm(
                categoryGetVms,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                (int) categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.isLast()
        );
    }

    public Category create(CategoryPostVm categoryPostVm) {
        validateDuplicateName(categoryPostVm.name(), null);
        Category category = new Category();
        category.setName(categoryPostVm.name());
        category.setSlug(categoryPostVm.slug());
        category.setDescription(categoryPostVm.description());
        category.setDisplayOrder(categoryPostVm.displayOrder());
        category.setMetaDescription(categoryPostVm.metaDescription());
        category.setMetaKeyword(categoryPostVm.metaKeywords());
        category.setIsPublished(categoryPostVm.isPublish());
        category.setImageId(categoryPostVm.imageId());
        if (categoryPostVm.parentId() != null) {
            Category parentCategory = categoryRepository
                    .findById(categoryPostVm.parentId())
                    .orElseThrow(() -> new BadRequestException(
                        Constants.ErrorCode.PARENT_CATEGORY_NOT_FOUND, categoryPostVm.parentId()));
            category.setParent(parentCategory);
        }

        return categoryRepository.save(category);
    }

    public void update(CategoryPostVm categoryPostVm, Long id) {
        validateDuplicateName(categoryPostVm.name(), id);
        Category category = categoryRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.CATEGORY_NOT_FOUND, id));
        category.setName(categoryPostVm.name());
        category.setSlug(categoryPostVm.slug());
        category.setDescription(categoryPostVm.description());
        category.setDisplayOrder(categoryPostVm.displayOrder());
        category.setMetaDescription(categoryPostVm.metaDescription());
        category.setMetaKeyword(categoryPostVm.metaKeywords());
        category.setIsPublished(categoryPostVm.isPublish());
        category.setImageId(categoryPostVm.imageId());
        if (categoryPostVm.parentId() == null) {
            category.setParent(null);
        } else {
            Category parentCategory = categoryRepository
                    .findById(categoryPostVm.parentId())
                    .orElseThrow(() -> new BadRequestException(
                        Constants.ErrorCode.PARENT_CATEGORY_NOT_FOUND, categoryPostVm.parentId()));

            if (!checkParent(category.getId(), parentCategory)) {
                throw new BadRequestException(Constants.ErrorCode.PARENT_CATEGORY_CANNOT_BE_ITSELF);
            }
            category.setParent(parentCategory);
        }
    }

    public CategoryGetDetailVm getCategoryById(Long id) {
        Category category = categoryRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.CATEGORY_NOT_FOUND, id));
        ImageVm categoryImage = null;
        if (category.getImageId() != null) {
            categoryImage = new ImageVm(category.getImageId(), mediaService.getMedia(category.getImageId()).url());
        }
        Category parentCategory = category.getParent();
        Long parentId = 0L;
        if (parentCategory != null) {
            parentId = parentCategory.getId();
        }
        return new CategoryGetDetailVm(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                parentId,
                category.getMetaKeyword(),
                category.getMetaDescription(),
                category.getDisplayOrder(),
                category.getIsPublished(),
                categoryImage
        );
    }

    public List<CategoryGetVm> getCategories(String categoryName) {
        List<Category> category = categoryRepository.findByNameContainingIgnoreCase(categoryName);
        List<CategoryGetVm> categoryGetVms = new ArrayList<>();
        category.forEach(cate -> {
            ImageVm categoryImage = null;
            if (cate.getImageId() != null) {
                categoryImage = new ImageVm(cate.getImageId(), mediaService.getMedia(cate.getImageId()).url());
            }
            Category parent = cate.getParent();
            long parentId = parent == null ? -1 : parent.getId();
            CategoryGetVm categoryGetVm = new CategoryGetVm(
                    cate.getId(),
                    cate.getName(),
                    cate.getSlug(),
                    parentId,
                    categoryImage
            );
            categoryGetVms.add(categoryGetVm);
        });
        return categoryGetVms;
    }

    private boolean checkExistedName(String name, Long id) {
        return categoryRepository.findExistedName(name, id) != null;
    }

    private void validateDuplicateName(String name, Long id) {
        if (checkExistedName(name, id)) {
            throw new DuplicatedException(Constants.ErrorCode.NAME_ALREADY_EXITED, name);
        }
    }

    private boolean checkParent(Long id, Category category) {
        if (id.equals(category.getId())) {
            return false;
        }
        if (category.getParent() != null) {
            return checkParent(id, category.getParent());
        } else {
            return true;
        }
    }

    public List<CategoryGetVm> getCategoryByIds(List<Long> ids) {
        return categoryRepository.findAllById(ids).stream().map(CategoryGetVm::fromModel).toList();
    }
}

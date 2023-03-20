package com.yas.product.service;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.DuplicatedNameException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryListGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

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
        if(categoryPostVm.parentId() != null){
            Category parentCategory = categoryRepository
                    .findById(categoryPostVm.parentId())
                    .orElseThrow(() -> new BadRequestException(Constants.ERROR_CODE.PARENT_CATEGORY_NOT_FOUND, categoryPostVm.parentId()));
            category.setParent(parentCategory);
        }

        return categoryRepository.saveAndFlush(category);
    }

    public void update(CategoryPostVm categoryPostVm, Long id) {
        validateDuplicateName(categoryPostVm.name(), id);
        Category category = categoryRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, id));
        category.setName(categoryPostVm.name());
        category.setSlug(categoryPostVm.slug());
        category.setDescription(categoryPostVm.description());
        category.setDisplayOrder(categoryPostVm.displayOrder());
        category.setMetaDescription(categoryPostVm.metaDescription());
        category.setMetaKeyword(categoryPostVm.metaKeywords());
        category.setIsPublished(categoryPostVm.isPublish());
        if(categoryPostVm.parentId() == null){
            category.setParent(null);
        } else {
            Category parentCategory = categoryRepository
                    .findById(categoryPostVm.parentId())
                    .orElseThrow(() -> new BadRequestException(Constants.ERROR_CODE.PARENT_CATEGORY_NOT_FOUND, categoryPostVm.parentId()));

            if(!checkParent(category.getId(), parentCategory)){
                throw new BadRequestException(Constants.ERROR_CODE.PARENT_CATEGORY_CANNOT_BE_ITSELF);
            }
            category.setParent(parentCategory);
        }
    }

    private boolean checkExistedName(String name, Long id) {
        return categoryRepository.findExistedName(name, id) != null;
    }

    private void validateDuplicateName(String name, Long id) {
        if (checkExistedName(name, id)) {
            throw new DuplicatedNameException(Constants.ERROR_CODE.NAME_ALREADY_EXITED, name);
        }
    }

    private boolean checkParent(Long id, Category category){
        if(id.equals(category.getId())) return false;
        if(category.getParent()!=null)
            return checkParent(id, category.getParent());
        else return true;
    }
}

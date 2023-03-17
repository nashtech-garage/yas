package com.yas.product.service;

import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.model.Category;
import com.yas.product.viewmodel.category.CategoryListGetVm;
import com.yas.product.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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

}

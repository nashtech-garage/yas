package com.yas.product.controller;

import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.viewModel.CategoryGetDetailVm;
import com.yas.product.viewModel.CategoryGetVm;
import com.yas.product.viewModel.CategoryPostVm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CategoryController {
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("categories")
    public List<CategoryGetVm> list(){
        return categoryRepository.findAll().stream()
                .map(item -> new CategoryGetVm(item.getId(), item.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("categories/{id}")
    public ResponseEntity<CategoryGetDetailVm> get(@PathVariable Long id){
        Category category = categoryRepository.getReferenceById(id);
        if(category == null){
            return ResponseEntity.notFound().build();
        }
        CategoryGetDetailVm categoryGetDetailVm = new CategoryGetDetailVm(
                category.getId(),
                category.getName(),
                category.getDescription()
        );

        return  ResponseEntity.ok(categoryGetDetailVm);
    }

    @PostMapping("categories")
    public ResponseEntity<CategoryGetDetailVm> create(@RequestBody final CategoryPostVm categoryPostVm){
        Category category = new Category();
        category.setName(categoryPostVm.name());
        category.setDescription(categoryPostVm.description());
        if(categoryPostVm.parentId() != null){
            Category parentCategory = categoryRepository.getReferenceById(categoryPostVm.parentId());
            if(parentCategory == null){
                return ResponseEntity.badRequest().build();
            }
            category.setParent(parentCategory);
        }
        categoryRepository.saveAndFlush(category);

        CategoryGetDetailVm categoryGetDetailVm = new CategoryGetDetailVm(
                category.getId(),
                category.getName(),
                category.getDescription()
        );

        return  ResponseEntity.ok(categoryGetDetailVm);
    }
}

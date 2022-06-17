package com.yas.product.controller;

import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.viewModel.CategoryGetDetailVm;
import com.yas.product.viewModel.CategoryGetVm;
import com.yas.product.viewModel.CategoryPostVm;
import com.yas.product.viewModel.ErrorVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
        Category category = categoryRepository.findById(id).orElse(null);
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
    @ApiResponses(value = {
            @ApiResponse(responseCode  = "200",  description  = "Ok", content = @Content(schema = @Schema(implementation = CategoryGetDetailVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Object> create(@RequestBody final CategoryPostVm categoryPostVm){
        Category category = new Category();
        category.setName(categoryPostVm.name());
        category.setDescription(categoryPostVm.description());

        if(categoryPostVm.parentId() != null){
            Category parentCategory = categoryRepository.findById(categoryPostVm.parentId()).orElse(null);
            if(parentCategory == null){
                return ResponseEntity.badRequest().body(new ErrorVm("400", "Bad Request", "parent category not exist"));
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

    @PutMapping("categories/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody final CategoryPostVm categoryPostVm){
        Category category = categoryRepository.findById(id).orElse(null);
        if(category == null){
            return  ResponseEntity.notFound().build();
        }
        category.setName(categoryPostVm.name());
        category.setDescription(categoryPostVm.description());
        if(categoryPostVm.parentId() == null){
            category.setParent(null);
        } else {
            Category parentCategory = categoryRepository.findById(categoryPostVm.parentId()).orElse(null);
            if(parentCategory == null){
                return ResponseEntity.badRequest().body(new ErrorVm("400", "Bad Request", "parent category not exist"));
            }
            category.setParent(parentCategory);
        }

        categoryRepository.saveAndFlush(category);
        return ResponseEntity.noContent().build();
    }
}

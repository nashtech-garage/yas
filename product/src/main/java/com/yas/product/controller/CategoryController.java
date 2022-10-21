package com.yas.product.controller;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.CategoryGetDetailVm;
import com.yas.product.viewmodel.CategoryGetVm;
import com.yas.product.viewmodel.CategoryPostVm;
import com.yas.product.viewmodel.ErrorVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
public class CategoryController {
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/backoffice/categories")
    public ResponseEntity<List<CategoryGetVm>> listCategories(){
        List<CategoryGetVm> categoryGetVms = categoryRepository.findAll().stream()
                .map(CategoryGetVm::fromModel)
                .toList();
        return  ResponseEntity.ok(categoryGetVms);
    }

    @GetMapping("/backoffice/categories/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = CategoryGetDetailVm.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CategoryGetDetailVm> getCategory(@PathVariable Long id){
        Category category = categoryRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, id));

        CategoryGetDetailVm categoryGetDetailVm = CategoryGetDetailVm.fromModel(category);
        return  ResponseEntity.ok(categoryGetDetailVm);
    }

    @PostMapping("/backoffice/categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = CategoryGetDetailVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CategoryGetDetailVm> createCategory(@Valid @RequestBody CategoryPostVm categoryPostVm, UriComponentsBuilder uriComponentsBuilder, Principal principal){
        Category category = new Category();
        category.setName(categoryPostVm.name());
        category.setSlug(categoryPostVm.slug());
        category.setDescription(categoryPostVm.description());
        category.setCreatedBy(principal.getName());
        category.setLastModifiedBy(principal.getName());
        category.setDisplayOrder(categoryPostVm.displayOrder());
        category.setMetaDescription(categoryPostVm.metaDescription());
        category.setMetaKeyword(categoryPostVm.metaKeywords());
        if(categoryPostVm.parentId() != null){
            Category parentCategory = categoryRepository
                    .findById(categoryPostVm.parentId())
                    .orElseThrow(() -> new BadRequestException(Constants.ERROR_CODE.PARENT_CATEGORY_NOT_FOUND, categoryPostVm.parentId()));
            category.setParent(parentCategory);
        }
        Category savedCategory = categoryRepository.saveAndFlush(category);

        CategoryGetDetailVm categoryGetDetailVm = CategoryGetDetailVm.fromModel(savedCategory);
        return  ResponseEntity.created(uriComponentsBuilder.replacePath("/categories/{id}").buildAndExpand(savedCategory.getId()).toUri())
                .body(categoryGetDetailVm);
    }

    @PutMapping("/backoffice/categories/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateCategory(@PathVariable Long id, @RequestBody @Valid final CategoryPostVm categoryPostVm, Principal principal){
        Category category = categoryRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, id));
        category.setName(categoryPostVm.name());
        category.setSlug(categoryPostVm.slug());
        category.setDescription(categoryPostVm.description());
        category.setLastModifiedBy(principal.getName());
        category.setLastModifiedOn(ZonedDateTime.now());
        category.setDisplayOrder(categoryPostVm.displayOrder());
        category.setMetaDescription(categoryPostVm.metaDescription());
        category.setMetaKeyword(categoryPostVm.metaKeywords());
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
        categoryRepository.saveAndFlush(category);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/backoffice/categories/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, id));
        if(category.getCategories().size()>0){
            throw new BadRequestException(Constants.ERROR_CODE.MAKE_SURE_CATEGORY_DO_NOT_CONTAIN_CHILDREN);
        }
        if(category.getProductCategories().size()>0){
            throw new BadRequestException(Constants.ERROR_CODE.MAKE_SURE_CATEGORY_DO_NOT_CONTAIN_PRODUCT);
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    boolean checkParent(Long id, Category category){
        if(id == category.getId()) return false;
        if(category.getParent()!=null)
            return checkParent(id, category.getParent());
        else return true;
    }
}

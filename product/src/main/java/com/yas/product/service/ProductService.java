package com.yas.product.service;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.ProductGetDetailVm;
import com.yas.product.viewmodel.ProductPostVm;
import com.yas.product.viewmodel.ProductThumbnailVm;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final MediaService mediaService;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public ProductService(ProductRepository productRepository, MediaService mediaService, BrandRepository brandRepository,
                          ProductCategoryRepository productCategoryRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.mediaService = mediaService;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    public ProductGetDetailVm createProduct(ProductPostVm productPostVm) {
        Product product = new Product();
        List<ProductCategory> productCategoryList = new ArrayList<>();

        if (productPostVm.brandId() != null) {
            Brand brand = brandRepository.findById(productPostVm.brandId()).
                    orElseThrow(() -> new NotFoundException(String.format("Brand %s is not found", productPostVm.brandId())));
            product.setBrand(brand);
        }

        if (CollectionUtils.isNotEmpty(productPostVm.categoryIds())) {
            List<Category> categoryList = categoryRepository.findAllById(productPostVm.categoryIds());
            if (categoryList.isEmpty()) {
                throw new BadRequestException(String.format("Not found categories %s", productPostVm.categoryIds()));
            } else if (categoryList.size() < productPostVm.categoryIds().size()) {
                List<Long> categoryIdsNotFound = productPostVm.categoryIds();
                categoryIdsNotFound.removeAll(categoryList.stream().map(Category::getId).toList());
                throw new BadRequestException(String.format("Not found categories %s", categoryIdsNotFound));
            } else {
                for (Category category : categoryList) {
                    ProductCategory productCategory = new ProductCategory();
                    productCategory.setProduct(product);
                    productCategory.setCategory(category);
                    productCategoryList.add(productCategory);
                }
            }
        }

        product.setName(productPostVm.name());
        product.setSlug(productPostVm.slug());
        product.setDescription(productPostVm.description());
        product.setShortDescription(productPostVm.shortDescription());
        product.setSpecification(productPostVm.specification());
        product.setSku(productPostVm.sku());
        product.setGtin(productPostVm.gtin());
        product.setMetaKeyword(productPostVm.metaKeyword());
        product.setMetaDescription(productPostVm.metaDescription());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        product.setCreatedBy(auth.getName());
        product.setLastModifiedBy(auth.getName());

        NoFileMediaVm noFileMediaVm = mediaService.SaveFile(productPostVm.thumbnail(), "", "");
        product.setThumbnailMediaId(noFileMediaVm.id());

        productRepository.saveAndFlush(product);
        productCategoryRepository.saveAllAndFlush(productCategoryList);
        return ProductGetDetailVm.fromModel(product);
    }

    public List<ProductThumbnailVm> getFeaturedProducts() {
        List<ProductThumbnailVm> productThumbnailVms = new ArrayList<>();
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            productThumbnailVms.add(new ProductThumbnailVm(
                    product.getId(),
                    product.getName(),
                    product.getSlug(),
                    mediaService.getMedia(product.getThumbnailMediaId()).url()
            ));
        }
        return productThumbnailVms;
    }

    public List<ProductThumbnailVm> getProductsByBrand(String brandSlug) {
        List<ProductThumbnailVm> productThumbnailVms = new ArrayList<>();
        Brand brand = brandRepository
                .findBySlug(brandSlug)
                .orElseThrow(() -> new NotFoundException(String.format("Brand %s is not found", brandSlug)));
        List<Product> products = productRepository.findAllByBrand(brand);
        for (Product product : products) {
            productThumbnailVms.add(new ProductThumbnailVm(
                    product.getId(),
                    product.getName(),
                    product.getSlug(),
                    mediaService.getMedia(product.getThumbnailMediaId()).url()
            ));
        }
        return productThumbnailVms;
    }
}

package com.yas.product.service;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final MediaService mediaService;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductImageRepository productImageRepository;

    public ProductService(ProductRepository productRepository, MediaService mediaService,
            BrandRepository brandRepository,
            ProductCategoryRepository productCategoryRepository, CategoryRepository categoryRepository,
            ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.mediaService = mediaService;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productImageRepository = productImageRepository;
    }

    public List<ProductListVm> getProducts() {
        return productRepository.findAll().stream()
                .map(ProductListVm::fromModel)
                .toList();
    }

    public ProductGetDetailVm createProduct(ProductPostVm productPostVm, List<MultipartFile> files) {
        Product product = new Product();
        List<ProductCategory> productCategoryList = new ArrayList<>();
        List<ProductImage> productImageList = new ArrayList<>();

        if (productPostVm.brandId() != null) {
            Brand brand = brandRepository.findById(productPostVm.brandId()).orElseThrow(
                    () -> new NotFoundException(String.format("Brand %s is not found", productPostVm.brandId())));
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

        for (int i = 1; i < files.size(); i++) {
            NoFileMediaVm noFileMediaVm = mediaService.saveFile(files.get(i), "", "");
            ProductImage productImage = new ProductImage();
            productImage.setImageId(noFileMediaVm.id());
            productImage.setProduct(product);
            productImageList.add(productImage);
        }

        product.setName(productPostVm.name());
        product.setSlug(productPostVm.slug());
        product.setDescription(productPostVm.description());
        product.setShortDescription(productPostVm.shortDescription());
        product.setSpecification(productPostVm.specification());
        product.setSku(productPostVm.sku());
        product.setGtin(productPostVm.gtin());
        product.setPrice(productPostVm.price());
        product.setIsAllowedToOrder(productPostVm.isAllowedToOrder());
        product.setIsFeatured(productPostVm.isFeatured());
        product.setIsPublished(productPostVm.isPublished());
        product.setMetaKeyword(productPostVm.metaKeyword());
        product.setMetaDescription(productPostVm.metaDescription());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        product.setCreatedBy(auth.getName());
        product.setLastModifiedBy(auth.getName());

        NoFileMediaVm noFileMediaVm = mediaService.saveFile(files.get(0), "", "");
        product.setThumbnailMediaId(noFileMediaVm.id());

        productRepository.saveAndFlush(product);
        productCategoryRepository.saveAllAndFlush(productCategoryList);
        productImageRepository.saveAllAndFlush(productImageList);

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
                    mediaService.getMedia(product.getThumbnailMediaId()).url()));
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
                    mediaService.getMedia(product.getThumbnailMediaId()).url()));
        }
        return productThumbnailVms;
    }

    public List<ProductThumbnailVm> getProductsByCategory(String categorySlug) {
        List<ProductThumbnailVm> productThumbnailVms = new ArrayList<>();
        Category category = categoryRepository
                .findBySlug(categorySlug)
                .orElseThrow(() -> new NotFoundException(String.format("Category %s is not found", categorySlug)));
        List<Product> products = productCategoryRepository.findAllByCategory(category).stream()
                .map(ProductCategory::getProduct).toList();
        for (Product product : products) {
            productThumbnailVms.add(new ProductThumbnailVm(
                    product.getId(),
                    product.getName(),
                    product.getSlug(),
                    mediaService.getMedia(product.getThumbnailMediaId()).url()));
        }
        return productThumbnailVms;
    }
}

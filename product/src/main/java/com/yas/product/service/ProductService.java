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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
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

    public ProductListGetVm getProductsWithFilter(int pageNo, int pageSize, String productName, String brandName) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> productPage;
        if(brandName.isBlank() && productName.isBlank()) {
            productPage= productRepository.findAll(pageable);
        }
        else if(brandName.isBlank() && !productName.isBlank()) {
            productPage= productRepository.findByName(pageable, productName.trim());
        }
        else if(!brandName.isBlank() && !productName.isBlank()){
            productPage = productRepository.getProductsWithFilter( productName.trim(), brandName.trim(), pageable);
        }
        else {
            productPage = productRepository.findByBrandName(pageable, brandName.trim());
        }
        List<Product> productList = productPage.getContent();
        List<ProductListVm> productListVmList = productList.stream()
                .map(ProductListVm::fromModel)
                .toList();

        return new ProductListGetVm(
                productListVmList,
                productPage.getNumber(),
                productPage.getSize(),
                (int) productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    public ProductDetailVm getProduct(String slug) {
        Product product = productRepository
                .findBySlug(slug)
                .orElseThrow(() -> new NotFoundException(String.format("Product %s is not found", slug)));
        List<String> productImageMediaUrls = new ArrayList<>();
        if(null != product.getProductImages() && product.getProductImages().size() > 0){
            for (ProductImage image: product.getProductImages()){
                productImageMediaUrls.add(mediaService.getMedia(image.getImageId()).url());
            }
        }
        List<Category> categories = new ArrayList<>();
        if(null != product.getProductCategories()){
            for (ProductCategory category: product.getProductCategories()){
                categories.add(category.getCategory());
            }
        }
        return new ProductDetailVm(product.getId(),
                product.getName(),
                product.getShortDescription(),
                product.getDescription(),
                product.getSpecification(),
                product.getSku(),
                product.getGtin(),
                product.getSlug(),
                product.getIsAllowedToOrder(),
                product.getIsPublished(),
                product.getIsFeatured(),
                product.getPrice(),
                product.getBrand().getId(),
                categories,
                product.getMetaKeyword(),
                product.getMetaDescription(),
                mediaService.getMedia(product.getThumbnailMediaId()).url(),
                productImageMediaUrls
        );
    }

    public ProductGetDetailVm createProduct(ProductPostVm productPostVm, List<MultipartFile> files) {
        Product product = new Product();
        List<ProductCategory> productCategoryList = new ArrayList<>();
        List<ProductImage> productImages = new ArrayList<>();

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

        for (int index = 1; index < files.size(); index++) {
            ProductImage productImage = new ProductImage();
            NoFileMediaVm noFileMediaVm = mediaService.saveFile(files.get(index), "", "");
            productImage.setImageId(noFileMediaVm.id());
            productImage.setProduct(product);
            productImages.add(productImage);
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

        Product savedProduct = productRepository.saveAndFlush(product);
        productCategoryRepository.saveAllAndFlush(productCategoryList);
        productImageRepository.saveAllAndFlush(productImages);
        return ProductGetDetailVm.fromModel(savedProduct);
    }
    public ProductGetDetailVm updateProduct(long productId, ProductPutVm productPutVm) {
        Product product = productRepository.findById(productId).orElseThrow(()->new NotFoundException(String.format("Product %s is not found", productId)));
        List<ProductCategory> productCategoryList = new ArrayList<>();
        List<ProductImage> productImages = new ArrayList<>();
        if(!productPutVm.slug().equals(product.getSlug()) && productRepository.findBySlug(productPutVm.slug()).isPresent()){
            throw new BadRequestException(String.format("Slug %s is duplicated", productPutVm.slug()));
        }

        if (productPutVm.brandId() != null) {
            Brand brand = brandRepository.findById(productPutVm.brandId()).
                    orElseThrow(() -> new NotFoundException(String.format("Brand %s is not found", productPutVm.brandId())));
            product.setBrand(brand);
        }
        
        if (CollectionUtils.isNotEmpty(productPutVm.categoryIds())) {
            productCategoryRepository.deleteAll(product.getProductCategories());
            product.setProductCategories(null);
            List<Category> categoryList = categoryRepository.findAllById(productPutVm.categoryIds());
            if (categoryList.isEmpty()) {
                throw new BadRequestException(String.format("Not found categories %s", productPutVm.categoryIds()));
            } else if (categoryList.size() < productPutVm.categoryIds().size()) {
                List<Long> categoryIdsNotFound = productPutVm.categoryIds();
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
        product.setIsAllowedToOrder(productPutVm.isAllowedToOrder());
        product.setIsPublished(productPutVm.isPublished());
        product.setIsFeatured(productPutVm.isFeatured());
        product.setPrice(productPutVm.price());
        product.setName(productPutVm.name());
        product.setSlug(productPutVm.slug());
        product.setDescription(productPutVm.description());
        product.setShortDescription(productPutVm.shortDescription());
        product.setSpecification(productPutVm.specification());
        product.setSku(productPutVm.sku());
        product.setGtin(productPutVm.gtin());
        product.setMetaKeyword(productPutVm.metaKeyword());
        product.setMetaDescription(productPutVm.metaDescription());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        product.setLastModifiedBy(auth.getName());
        product.setLastModifiedOn(ZonedDateTime.now());

        if(null != productPutVm.thumbnailMediaId()){
            mediaService.removeMedia(product.getThumbnailMediaId());
            product.setThumbnailMediaId(productPutVm.thumbnailMediaId());
        }

        if(null != productPutVm.productImageIds() && !productPutVm.productImageIds().isEmpty()){
            productImageRepository.deleteAll(product.getProductImages());
            for(int i  = 0; i < product.getProductImages().size(); i++){
                mediaService.removeMedia(product.getProductImages().get(i).getImageId());
            }
            product.setProductImages(null);
            for (int i = 0; i < productPutVm.productImageIds().size(); i++) {
                ProductImage productImage = new ProductImage();
                productImage.setImageId(productPutVm.productImageIds().get(i));
                productImage.setProduct(product);
                productImages.add(productImage);
            }
        }

        productRepository.saveAndFlush(product);
        productCategoryRepository.saveAllAndFlush(productCategoryList);
        productImageRepository.saveAllAndFlush(productImages);
        return ProductGetDetailVm.fromModel(product);
    }
    public ProductDetailVm getProductById(long productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(()->
                        new NotFoundException(String.format("Product %s is not found", productId))
                );
        List<String> productImageMediaUrls = new ArrayList<>();
        if(null != product.getProductImages()){
            for (ProductImage image: product.getProductImages()){
                productImageMediaUrls.add(mediaService.getMedia(image.getImageId()).url());
            }
        }
        List<Category> categories = new ArrayList<>();
        if(null != product.getProductCategories()){
            for (ProductCategory category: product.getProductCategories()){
                categories.add(category.getCategory());
            }
        }
        return new ProductDetailVm(product.getId(),
                product.getName(),
                product.getShortDescription(),
                product.getDescription(),
                product.getSpecification(),
                product.getSku(),
                product.getGtin(),
                product.getSlug(),
                product.getIsAllowedToOrder(),
                product.getIsPublished(),
                product.getIsFeatured(),
                product.getPrice(),
                product.getBrand().getId(),
                categories,
                product.getMetaKeyword(),
                product.getMetaDescription(),
                mediaService.getMedia(product.getThumbnailMediaId()).url(),
                productImageMediaUrls
                );
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

    public ProductThumbnailVm getFeaturedProductsById(Long productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format("Product %s is not found", productId)));
        ProductThumbnailVm productThumbnailVm = new ProductThumbnailVm(
                product.getId(),
                product.getName(),
                product.getSlug(),
                mediaService.getMedia(product.getThumbnailMediaId()).url());
        return productThumbnailVm;
    }
}

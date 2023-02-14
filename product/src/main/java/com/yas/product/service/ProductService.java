package com.yas.product.service;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
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
        if(brandName.isBlank() && productName.isBlank()){
            productPage= productRepository.findAll(pageable);
        } else{
            productPage = productRepository.getProductsWithFilter(productName.trim().toLowerCase(),
                    brandName.trim(), pageable);
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
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, slug));
        List<String> productImageMediaUrls = new ArrayList<>();
        if(null != product.getProductImages() && !product.getProductImages().isEmpty()){
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
                    () -> new NotFoundException(Constants.ERROR_CODE.BRAND_NOT_FOUND, productPostVm.brandId()));
            product.setBrand(brand);
        }

        if (CollectionUtils.isNotEmpty(productPostVm.categoryIds())) {
            List<Category> categoryList = categoryRepository.findAllById(productPostVm.categoryIds());
            if (categoryList.isEmpty()) {
                throw new BadRequestException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, productPostVm.categoryIds());
            } else if (categoryList.size() < productPostVm.categoryIds().size()) {
                List<Long> categoryIdsNotFound = productPostVm.categoryIds();
                categoryIdsNotFound.removeAll(categoryList.stream().map(Category::getId).toList());
                throw new BadRequestException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, categoryIdsNotFound);
            } else {
                for (Category category : categoryList) {
                    ProductCategory productCategory = new ProductCategory();
                    productCategory.setProduct(product);
                    productCategory.setCategory(category);
                    productCategoryList.add(productCategory);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(files)) {
            for (int index = 1; index < files.size(); index++) {
                ProductImage productImage = new ProductImage();
                NoFileMediaVm noFileMediaVm = mediaService.saveFile(files.get(index), "", "");
                productImage.setImageId(noFileMediaVm.id());
                productImage.setProduct(product);
                productImages.add(productImage);
            }

            NoFileMediaVm noFileMediaVm = mediaService.saveFile(files.get(0), "", "");
            product.setThumbnailMediaId(noFileMediaVm.id());
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
        product.setMetaTitle(productPostVm.metaTitle());
        product.setMetaKeyword(productPostVm.metaKeyword());
        product.setMetaDescription(productPostVm.metaDescription());
        product.setIsVisibleIndividually(productPostVm.isVisibleIndividually());
        
        if(productPostVm.parentId() != null){
            Product parentProduct = productRepository.findById(productPostVm.parentId()).orElseThrow(
                    () -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, productPostVm.parentId()));
            product.setParent(parentProduct);
        }else{
            product.setParent(product);
        }
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        product.setCreatedBy(auth.getName());
        product.setLastModifiedBy(auth.getName());

        product.setProductCategories(productCategoryList);
        product.setProductImages(productImages);
        Product savedProduct = productRepository.saveAndFlush(product);
        return ProductGetDetailVm.fromModel(savedProduct);
    }
    public ProductGetDetailVm updateProduct(long productId, ProductPutVm productPutVm) {
        Product product = productRepository.findById(productId).orElseThrow(()
                ->new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, productId));
        List<ProductCategory> productCategoryList = new ArrayList<>();
        List<ProductImage> productImages = new ArrayList<>();
        if(!productPutVm.slug().equals(product.getSlug()) && productRepository.findBySlug(productPutVm.slug()).isPresent()){
            throw new BadRequestException(String.format("Slug %s is duplicated", productPutVm.slug()));
        }

        if (productPutVm.brandId() != null && (product.getBrand() == null || !(productPutVm.brandId().equals(product.getBrand().getId()) ))) {
            Brand brand = brandRepository.findById(productPutVm.brandId()).
                    orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.BRAND_NOT_FOUND, productPutVm.brandId()));
            product.setBrand(brand);
        }
        
        if (CollectionUtils.isNotEmpty(productPutVm.categoryIds())) {
            List<Category> categories =product.getProductCategories().stream().map(ProductCategory::getCategory).toList();
            List<Long> categoryIds =categories.stream().map(Category::getId).toList();
            if(!categoryIds.equals(productPutVm.categoryIds().stream().sorted().toList())) {
                List<Category> categoryList = categoryRepository.findAllById(productPutVm.categoryIds());
                productCategoryRepository.deleteAll(product.getProductCategories());
                product.setProductCategories(null);
                if (categoryList.isEmpty()) {
                    throw new BadRequestException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, productPutVm.categoryIds());
                } else if (categoryList.size() < productPutVm.categoryIds().size()) {
                    List<Long> categoryIdsNotFound = productPutVm.categoryIds();
                    categoryIdsNotFound.removeAll(categoryList.stream().map(Category::getId).toList());
                    throw new BadRequestException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, categoryIdsNotFound);
                } else {
                    for (Category category : categoryList) {
                        ProductCategory productCategory = new ProductCategory();
                        productCategory.setProduct(product);
                        productCategory.setCategory(category);
                        productCategoryList.add(productCategory);
                    }
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
            if(null != product.getThumbnailMediaId()){
                mediaService.removeMedia(product.getThumbnailMediaId());
            }            
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
                        new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, productId)
                );
        List<String> productImageMediaUrls = new ArrayList<>();
        if(null != product.getProductImages()){
            for (ProductImage image: product.getProductImages()){
                productImageMediaUrls.add(mediaService.getMedia(image.getImageId()).url());
            }
        }
        String thumbnailMediaId = "";
        if(null != product.getThumbnailMediaId()) {
            thumbnailMediaId = mediaService.getMedia(product.getThumbnailMediaId()).url();
        }
        List<Category> categories = new ArrayList<>();
        if(null != product.getProductCategories()){
            for (ProductCategory category: product.getProductCategories()){
                categories.add(category.getCategory());
            }
        }
        Long brandId = null;
        if(null != product.getBrand()) {
            brandId = product.getBrand().getId();
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
                brandId,
                categories,
                product.getMetaKeyword(),
                product.getMetaDescription(),
                thumbnailMediaId,
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
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.BRAND_NOT_FOUND, brandSlug));
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

    public ProductListGetFromCategoryVm getProductsFromCategory(int pageNo, int pageSize, String categorySlug) {
        List<ProductThumbnailVm> productThumbnailVms = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ProductCategory> productCategoryPage;
        Category category = categoryRepository
                .findBySlug(categorySlug)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, categorySlug));
        productCategoryPage = productCategoryRepository.findAllByCategory(pageable,category);
        List<ProductCategory> productList = productCategoryPage.getContent();
        List<Product> products = productList.stream()
                .map(ProductCategory::getProduct).toList();
        for (Product product : products) {
            productThumbnailVms.add(new ProductThumbnailVm(
                    product.getId(),
                    product.getName(),
                    product.getSlug(),
                    mediaService.getMedia(product.getThumbnailMediaId()).url()));
        }
        return new ProductListGetFromCategoryVm(
                productThumbnailVms,
                productCategoryPage.getNumber(),
                productCategoryPage.getSize(),
                (int) productCategoryPage.getTotalElements(),
                productCategoryPage.getTotalPages(),
                productCategoryPage.isLast()
        );
    }

    public ProductThumbnailVm getFeaturedProductsById(Long productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, productId));
        return new ProductThumbnailVm(
                product.getId(),
                product.getName(),
                product.getSlug(),
                mediaService.getMedia(product.getThumbnailMediaId()).url());
    }

    public ProductFeatureGetVm getListFeaturedProducts(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<ProductThumbnailGetVm> productThumbnailVms = new ArrayList<>();
        Page<Product> productPage = productRepository.getFeaturedProduct(pageable);
        List<Product> products = productPage.getContent();
        for (Product product : products) {
            productThumbnailVms.add(new ProductThumbnailGetVm(
                    product.getId(),
                    product.getName(),
                    product.getSlug(),
                    mediaService.getMedia(product.getThumbnailMediaId()).url(),
                    product.getPrice()));
        }
        return new ProductFeatureGetVm(productThumbnailVms, productPage.getTotalPages());
    }


    public ProductDetailGetVm getProductDetail(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, slug));

        Long productThumbnailMediaId = product.getThumbnailMediaId();
        String productThumbnailurl = "";
        if (productThumbnailMediaId != null) {
            productThumbnailurl = mediaService.getMedia(productThumbnailMediaId).url();
        }

        List<String> productImageMediaUrls = new ArrayList<>();
        if(null != product.getProductImages() && !product.getProductImages().isEmpty()){
            for (ProductImage image: product.getProductImages()){
                productImageMediaUrls.add(mediaService.getMedia(image.getImageId()).url());
            }
        }

        List<ProductAttributeGroupGetVm> productAttributeGroupsVm = new ArrayList<>();
        List<ProductAttributeValue> productAttributeValues = product.getAttributeValues();
        if (!productAttributeValues.isEmpty()) {
            List<ProductAttributeGroup> productAttributeGroups = productAttributeValues.stream()
                    .map(productAttributeValue -> productAttributeValue.getProductAttribute().getProductAttributeGroup())
                    .distinct()
                    .toList();

            productAttributeGroups.forEach(productAttributeGroup -> {
                List<ProductAttributeValueVm> productAttributeValueVms = new ArrayList<>();
                if (!productAttributeValues.isEmpty()) {
                    productAttributeValues.forEach(productAttributeValue -> {
                        if (productAttributeValue.getProductAttribute().getProductAttributeGroup().equals(productAttributeGroup)) {
                            ProductAttributeValueVm productAttributeValueVm = new ProductAttributeValueVm(
                                    productAttributeValue.getProductAttribute().getName(),
                                    productAttributeValue.getValue());
                            productAttributeValueVms.add(productAttributeValueVm);
                        }
                    });
                }
                ProductAttributeGroupGetVm productAttributeGroupVm = new ProductAttributeGroupGetVm(
                        productAttributeGroup.getName(),
                        productAttributeValueVms);
                productAttributeGroupsVm.add(productAttributeGroupVm);
            });
        }


        return new ProductDetailGetVm(
                product.getId(),
                product.getName(),
                product.getBrand() == null ? null : product.getBrand().getName(),
                product.getProductCategories().stream().map(category -> category.getCategory().getName()).toList(),
                productAttributeGroupsVm,
                product.getShortDescription(),
                product.getDescription(),
                product.getSpecification(),
                product.getIsAllowedToOrder(),
                product.getIsPublished(),
                product.getIsFeatured(),
                product.getPrice(),
                productThumbnailurl,
                productImageMediaUrls
        );
    }
}

package com.yas.product.service;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.DuplicatedException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.*;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.repository.*;
import com.yas.product.utils.Constants;
import com.yas.product.utils.StringUtils;
import com.yas.product.viewmodel.ImageVm;
import com.yas.product.viewmodel.product.*;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeValueVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final MediaService mediaService;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductOptionCombinationRepository productOptionCombinationRepository;

    private static final String NONE_GROUP = "None group";

    public ProductService(ProductRepository productRepository,
                          MediaService mediaService,
                          BrandRepository brandRepository,
                          ProductCategoryRepository productCategoryRepository,
                          CategoryRepository categoryRepository,
                          ProductImageRepository productImageRepository,
                          ProductOptionRepository productOptionRepository,
                          ProductOptionValueRepository productOptionValueRepository,
                          ProductOptionCombinationRepository productOptionCombinationRepository) {
        this.productRepository = productRepository;
        this.mediaService = mediaService;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productImageRepository = productImageRepository;
        this.productOptionRepository = productOptionRepository;
        this.productOptionValueRepository = productOptionValueRepository;
        this.productOptionCombinationRepository = productOptionCombinationRepository;
    }

    public ProductListGetVm getProductsWithFilter(int pageNo, int pageSize, String productName, String brandName) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> productPage;

        productPage = productRepository.getProductsWithFilter(productName.trim().toLowerCase(),
                brandName.trim(), pageable);

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

    private boolean isProductWithSlugAvailable(String slug) {
        return productRepository.findBySlugAndIsActiveTrue(slug).isPresent();
    }

    private boolean isProductWithSkuAvailable(String sku) {
        return productRepository.findBySkuAndIsActiveTrue(sku).isPresent();
    }

    private boolean isProductWithGtinAvailable(String gtin) {
        return productRepository.findByGtinAndIsActiveTrue(gtin).isPresent();
    }

    private void validateIfProductWithSkuOrGtinOrSlugExist(String slug,
                                                           String gtin,
                                                           String sku) {
        if (isProductWithSlugAvailable(slug))
            throw new DuplicatedException(Constants.ERROR_CODE.SLUG_ALREADY_EXISTED, slug);

        if (isProductWithGtinAvailable(gtin))
            throw new DuplicatedException(Constants.ERROR_CODE.GTIN_ALREADY_EXISTED, gtin);

        if (isProductWithSkuAvailable(sku))
            throw new DuplicatedException(Constants.ERROR_CODE.SKU_ALREADY_EXISTED, sku);
    }

    public ProductGetDetailVm createProduct(ProductPostVm productPostVm) {
        validateIfProductWithSkuOrGtinOrSlugExist(
                productPostVm.slug(),
                productPostVm.gtin(),
                productPostVm.sku()
        );

        Product mainProduct = Product.builder()
                .name(productPostVm.name())
                .thumbnailMediaId(productPostVm.thumbnailMediaId())
                .slug(productPostVm.slug())
                .description(productPostVm.shortDescription())
                .shortDescription(productPostVm.description())
                .specification(productPostVm.specification())
                .sku(productPostVm.sku())
                .gtin(productPostVm.gtin())
                .price(productPostVm.price())
                .isAllowedToOrder(productPostVm.isAllowedToOrder())
                .isPublished(productPostVm.isPublished())
                .isFeatured(productPostVm.isFeatured())
                .isVisibleIndividually(productPostVm.isVisibleIndividually())
                .stockTrackingEnabled(productPostVm.stockTrackingEnabled())
                .metaTitle(productPostVm.metaTitle())
                .metaKeyword(productPostVm.metaKeyword())
                .metaDescription(productPostVm.description())
                .hasOptions(CollectionUtils.isNotEmpty(productPostVm.variations())
                        && CollectionUtils.isNotEmpty(productPostVm.productOptionValues()))
                .isActive(true).build();

        setProductBrand(productPostVm.brandId(), mainProduct);

        List<ProductCategory> productCategoryList = setProductCategories(productPostVm.categoryIds(), mainProduct);

        List<ProductImage> productImageList = setProductImages(productPostVm.productImageIds(), mainProduct);

        Product mainSavedProduct = productRepository.saveAndFlush(mainProduct);
        productImageRepository.saveAllAndFlush(productImageList);
        productCategoryRepository.saveAllAndFlush(productCategoryList);

        // Save product variations, product option values, and product option combinations
        if (CollectionUtils.isNotEmpty(productPostVm.variations()) && CollectionUtils.isNotEmpty(productPostVm.productOptionValues())) {
            List<ProductImage> allProductVariantImageList = new ArrayList<>();
            List<Product> productVariants = productPostVm.variations().stream()
                    .map(variation -> {
                        validateIfProductWithSkuOrGtinOrSlugExist(
                                variation.slug(),
                                variation.gtin(),
                                variation.sku()
                        );

                        Product productVariant = Product.builder()
                                .name(variation.name())
                                .thumbnailMediaId(variation.thumbnailMediaId())
                                .slug(variation.slug().toLowerCase())
                                .sku(variation.sku())
                                .gtin(variation.gtin())
                                .price(variation.price())
                                .parent(mainProduct).build();
                        List<ProductImage> productVariantImageList = setProductImages(variation.productImageIds(), productVariant);
                        allProductVariantImageList.addAll(productVariantImageList);
                        return productVariant;
                    })
                    .toList();

            List<Product> productsVariantsSaved = productRepository.saveAllAndFlush(productVariants);
            productImageRepository.saveAllAndFlush(allProductVariantImageList);

            List<Long> productOptionIds = productPostVm.productOptionValues().stream().map(ProductOptionValuePostVm::productOptionId).toList();
            List<ProductOption> productOptions = productOptionRepository.findAllByIdIn(productOptionIds);
            Map<Long, ProductOption> productOptionMap = productOptions.stream().collect(Collectors.toMap(ProductOption::getId, Function.identity()));
            List<ProductOptionValue> productOptionValues = new ArrayList<>();
            List<ProductOptionCombination> productOptionCombinations = new ArrayList<>();

            productPostVm.productOptionValues().forEach(optionValue -> optionValue.value().forEach(value -> {
                ProductOptionValue productOptionValue = ProductOptionValue.builder()
                        .product(mainSavedProduct)
                        .displayOrder(optionValue.displayOrder())
                        .displayType(optionValue.displayType())
                        .productOption(productOptionMap.get(optionValue.productOptionId()))
                        .value(value)
                        .build();
                List<ProductOptionCombination> productOptionCombinationList =
                        productsVariantsSaved.stream()
                                .filter(product -> product.getSlug().contains(StringUtils.toSlug(value)))
                                .map(product -> ProductOptionCombination.builder()
                                        .product(product)
                                        .productOption(productOptionMap.get(optionValue.productOptionId()))
                                        .value(value)
                                        .displayOrder(optionValue.displayOrder())
                                        .build()).toList();
                productOptionValues.add(productOptionValue);
                productOptionCombinations.addAll(productOptionCombinationList);
            }));

            productOptionValueRepository.saveAllAndFlush(productOptionValues);
            productOptionCombinationRepository.saveAllAndFlush(productOptionCombinations);
        }

        return ProductGetDetailVm.fromModel(mainSavedProduct);
    }

    public void updateProduct(long productId, ProductPutVm productPutVm) {
        Product product = productRepository.findById(productId).orElseThrow(()
                -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, productId));

        setProductBrand(productPutVm.brandId(), product);

        List<ProductCategory> productCategoryList = setProductCategories(productPutVm.categoryIds(), product);

        List<ProductImage> productImageList = setProductImages(productPutVm.productImageIds(), product);

        product.setName(productPutVm.name());
        product.setSlug(productPutVm.slug());
        product.setThumbnailMediaId(productPutVm.thumbnailMediaId());
        product.setDescription(productPutVm.description());
        product.setShortDescription(productPutVm.shortDescription());
        product.setSpecification(productPutVm.specification());
        product.setSku(productPutVm.sku());
        product.setGtin(productPutVm.gtin());
        product.setPrice(productPutVm.price());
        product.setIsAllowedToOrder(productPutVm.isAllowedToOrder());
        product.setIsFeatured(productPutVm.isFeatured());
        product.setIsPublished(productPutVm.isPublished());
        product.setIsVisibleIndividually(productPutVm.isVisibleIndividually());
        product.setStockTrackingEnabled(productPutVm.stockTrackingEnabled());
        product.setMetaTitle(productPutVm.metaTitle());
        product.setMetaKeyword(productPutVm.metaKeyword());
        product.setMetaDescription(productPutVm.metaDescription());

        productRepository.saveAndFlush(product);
        productImageRepository.saveAllAndFlush(productImageList);
        productCategoryRepository.saveAllAndFlush(productCategoryList);
    }

    public List<ProductImage> setProductImages(List<Long> imageMediaIds, Product product) {
        List<ProductImage> productImages = new ArrayList<>();
        if (CollectionUtils.isEmpty(imageMediaIds)) {
            return productImages;
        }
        if (product.getProductImages() == null) {
            productImages = imageMediaIds.stream()
                    .map(id -> ProductImage.builder().imageId(id).product(product).build()).toList();
        } else {
            List<Long> productImageIds = product.getProductImages().stream().map(ProductImage::getImageId).toList();
            List<Long> newImageIds = imageMediaIds.stream().filter(id -> !productImageIds.contains(id)).toList();
            List<Long> deletedImageIds = productImageIds.stream().filter(id -> !imageMediaIds.contains(id)).toList();
            if (CollectionUtils.isNotEmpty(newImageIds)) {
                productImages = newImageIds.stream()
                        .map(id -> ProductImage.builder().imageId(id).product(product).build()).toList();
            }
            if (CollectionUtils.isNotEmpty(deletedImageIds)) {
                productImageRepository.deleteByImageIdInAndProductId(deletedImageIds, product.getId());
            }
        }
        return productImages;
    }

    private void setProductBrand(Long brandId, Product product) {
        if (brandId != null && (product.getBrand() == null || !(brandId.equals(product.getBrand().getId())))) {
            Brand brand = brandRepository.findById(brandId).orElseThrow(()
                    -> new NotFoundException(Constants.ERROR_CODE.BRAND_NOT_FOUND, brandId));
            product.setBrand(brand);
        }
    }

    private List<ProductCategory> setProductCategories(List<Long> vmCategoryIds, Product product) {
        List<ProductCategory> productCategoryList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(vmCategoryIds)) {
            List<Long> categoryIds = product.getProductCategories().stream().map(productCategory -> productCategory.getCategory().getId()).sorted().toList();
            if (!CollectionUtils.isEqualCollection(categoryIds, vmCategoryIds.stream().sorted().toList())) {
                List<Category> categoryList = categoryRepository.findAllById(vmCategoryIds);
                if (categoryList.isEmpty()) {
                    throw new BadRequestException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, vmCategoryIds);
                } else if (categoryList.size() < vmCategoryIds.size()) {
                    vmCategoryIds.removeAll(categoryList.stream().map(Category::getId).toList());
                    throw new BadRequestException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, vmCategoryIds);
                } else {
                    for (Category category : categoryList) {
                        productCategoryList.add(ProductCategory.builder()
                                .product(product)
                                .category(category).build());
                    }
                }
            }
        }
        return productCategoryList;
    }

    public ProductDetailVm getProductById(long productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, productId)
                );
        List<ImageVm> productImageMedias = new ArrayList<>();
        if (null != product.getProductImages()) {
            for (ProductImage image : product.getProductImages()) {
                productImageMedias.add(new ImageVm(image.getImageId(), mediaService.getMedia(image.getImageId()).url()));
            }
        }
        ImageVm thumbnailMedia = null;
        if (null != product.getThumbnailMediaId()) {
            thumbnailMedia = new ImageVm(product.getThumbnailMediaId(), mediaService.getMedia(product.getThumbnailMediaId()).url());
        }
        List<Category> categories = new ArrayList<>();
        if (null != product.getProductCategories()) {
            for (ProductCategory category : product.getProductCategories()) {
                categories.add(category.getCategory());
            }
        }
        Long brandId = null;
        if (null != product.getBrand()) {
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
                product.getIsVisibleIndividually() != null || product.getIsVisibleIndividually(),
                product.getStockTrackingEnabled(),
                product.getPrice(),
                brandId,
                categories,
                product.getMetaTitle(),
                product.getMetaKeyword(),
                product.getMetaDescription(),
                thumbnailMedia,
                productImageMedias
        );
    }

    public List<ProductThumbnailVm> getProductsByBrand(String brandSlug) {
        List<ProductThumbnailVm> productThumbnailVms = new ArrayList<>();
        Brand brand = brandRepository
                .findBySlug(brandSlug)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.BRAND_NOT_FOUND, brandSlug));
        List<Product> products = productRepository.findAllByBrandAndIsActiveTrue(brand);
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
        productCategoryPage = productCategoryRepository.findAllByCategory(pageable, category);
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

    public List<ProductThumbnailGetVm> getFeaturedProductsById(List<Long> productIds) {
        List<Product> products = productRepository.findAllByIdIn(productIds);
        return products.stream().map(product -> new ProductThumbnailGetVm(
                product.getId(),
                product.getName(),
                product.getSlug(),
                mediaService.getMedia(product.getThumbnailMediaId()).url(),
                product.getPrice())).toList();
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
        Product product = productRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, slug));

        Long productThumbnailMediaId = product.getThumbnailMediaId();
        String productThumbnailUrl = mediaService.getMedia(productThumbnailMediaId).url();

        List<String> productImageMediaUrls = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(product.getProductImages())) {
            for (ProductImage image : product.getProductImages()) {
                productImageMediaUrls.add(mediaService.getMedia(image.getImageId()).url());
            }
        }

        List<ProductAttributeGroupGetVm> productAttributeGroupsVm = new ArrayList<>();
        List<ProductAttributeValue> productAttributeValues = product.getAttributeValues();
        if (CollectionUtils.isNotEmpty(productAttributeValues)) {
            List<ProductAttributeGroup> productAttributeGroups = productAttributeValues.stream()
                    .map(productAttributeValue -> productAttributeValue.getProductAttribute().getProductAttributeGroup())
                    .distinct()
                    .toList();

            productAttributeGroups.forEach(productAttributeGroup -> {
                List<ProductAttributeValueVm> productAttributeValueVms = new ArrayList<>();
                productAttributeValues.forEach(productAttributeValue -> {
                    ProductAttributeGroup group = productAttributeValue.getProductAttribute().getProductAttributeGroup();
                    if ((group != null && group.equals(productAttributeGroup))
                            || (group == null && productAttributeGroup == null)) {
                        ProductAttributeValueVm productAttributeValueVm = new ProductAttributeValueVm(
                                productAttributeValue.getProductAttribute().getName(),
                                productAttributeValue.getValue());
                        productAttributeValueVms.add(productAttributeValueVm);
                    }
                });
                String productAttributeGroupName = productAttributeGroup == null ? NONE_GROUP : productAttributeGroup.getName();
                ProductAttributeGroupGetVm productAttributeGroupVm = new ProductAttributeGroupGetVm(
                        productAttributeGroupName,
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
                product.getHasOptions(),
                product.getPrice(),
                productThumbnailUrl,
                productImageMediaUrls
        );
    }

    public void deleteProduct(Long id) {
        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, id));
        product.setIsActive(false);
        productRepository.save(product);
    }

    public ProductsGetVm getProductsByMultiQuery(int pageNo, int pageSize, String productName, String categorySlug, Double startPrice, Double endPrice) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Product> productPage;
        productPage = productRepository.findByProductNameAndCategorySlugAndPriceBetween(
                productName.trim().toLowerCase(),
                categorySlug.trim(), startPrice, endPrice, pageable);

        List<ProductThumbnailGetVm> productThumbnailVms = new ArrayList<>();
        List<Product> products = productPage.getContent();
        for (Product product : products) {
            productThumbnailVms.add(new ProductThumbnailGetVm(
                    product.getId(),
                    product.getName(),
                    product.getSlug(),
                    mediaService.getMedia(product.getThumbnailMediaId()).url(),
                    product.getPrice()));
        }

        return new ProductsGetVm(
                productThumbnailVms,
                productPage.getNumber(),
                productPage.getSize(),
                (int) productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    public List<ProductVariationGetVm> getProductVariationsByParentId(Long id) {
        Product parentProduct = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, id));
        if (Boolean.TRUE.equals(parentProduct.getHasOptions())) {
            List<Product> productVariations = parentProduct.getProducts();
            return productVariations.stream().map(product -> {
                List<ProductOptionCombination> productOptionCombinations =
                        productOptionCombinationRepository.findAllByProduct(product);
                Map<String, String> options = productOptionCombinations.stream().collect(Collectors.toMap(
                        productOptionCombination -> productOptionCombination.getProductOption().getName(),
                        ProductOptionCombination::getValue
                ));
                return new ProductVariationGetVm(
                        product.getId(),
                        product.getName(),
                        product.getSlug(),
                        product.getSku(),
                        product.getGtin(),
                        product.getPrice(),
                        new ImageVm(product.getThumbnailMediaId(), mediaService.getMedia(product.getThumbnailMediaId()).url()),
                        product.getProductImages().stream()
                                .map(productImage -> new ImageVm(productImage.getImageId(), mediaService.getMedia(productImage.getImageId()).url())).toList(),
                        options
                );
            }).toList();
        } else {
            throw new BadRequestException(Constants.ERROR_CODE.PRODUCT_NOT_HAVE_VARIATION, id);
        }
    }

    public List<ProductExportingDetailVm> exportProducts(String productName, String brandName) {
        List<Product> productList = productRepository.getExportingProducts(productName.trim().toLowerCase(),
                brandName.trim());

        return productList.stream()
                .map(product -> new ProductExportingDetailVm(product.getId(),
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
                        product.getIsVisibleIndividually(),
                        product.getStockTrackingEnabled(),
                        product.getPrice(),
                        product.getBrand().getId(),
                        product.getBrand().getName(),
                        product.getMetaTitle(),
                        product.getMetaKeyword(),
                        product.getMetaDescription()
                ))
                .toList();
    }

    public ProductSlugGetVm getProductSlug(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, id));
        Product parent = product.getParent();
        if (parent != null) {
            return new ProductSlugGetVm(parent.getSlug(), id);
        }
        return new ProductSlugGetVm(product.getSlug(), null);
    }
}

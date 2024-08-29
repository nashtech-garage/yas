package com.yas.product.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.InternalServerErrorException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.model.ProductOptionValueSaveVm;
import com.yas.product.model.ProductRelated;
import com.yas.product.model.ProductVariationSaveVm;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.model.enumeration.FilterExistInWhSelection;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.ImageVm;
import com.yas.product.viewmodel.product.ProductDetailGetVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductEsDetailVm;
import com.yas.product.viewmodel.product.ProductExportingDetailVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductInfoVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductProperties;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductSaveVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductVariationGetVm;
import com.yas.product.viewmodel.product.ProductVariationPostVm;
import com.yas.product.viewmodel.product.ProductVariationPutVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeValueVm;
import io.micrometer.common.util.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class ProductService {
    private static final String NONE_GROUP = "None group";
    private final ProductRepository productRepository;
    private final MediaService mediaService;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductOptionCombinationRepository productOptionCombinationRepository;
    private final ProductRelatedRepository productRelatedRepository;

    public ProductService(ProductRepository productRepository,
                          MediaService mediaService,
                          BrandRepository brandRepository,
                          ProductCategoryRepository productCategoryRepository,
                          CategoryRepository categoryRepository,
                          ProductImageRepository productImageRepository,
                          ProductOptionRepository productOptionRepository,
                          ProductOptionValueRepository productOptionValueRepository,
                          ProductOptionCombinationRepository productOptionCombinationRepository,
                          ProductRelatedRepository productRelatedRepository) {
        this.productRepository = productRepository;
        this.mediaService = mediaService;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productImageRepository = productImageRepository;
        this.productOptionRepository = productOptionRepository;
        this.productOptionValueRepository = productOptionValueRepository;
        this.productOptionCombinationRepository = productOptionCombinationRepository;
        this.productRelatedRepository = productRelatedRepository;
    }

    public ProductGetDetailVm createProduct(ProductPostVm productPostVm) {
        validateProductVm(productPostVm);

        Product mainProduct = buildMainProductFromVm(productPostVm);

        setProductBrand(productPostVm.brandId(), mainProduct);

        Product savedMainProduct = productRepository.save(mainProduct);

        List<ProductCategory> productCategories = setProductCategories(productPostVm.categoryIds(), savedMainProduct);
        List<ProductImage> productImages = setProductImages(productPostVm.productImageIds(), savedMainProduct);
        productImageRepository.saveAll(productImages);
        productCategoryRepository.saveAll(productCategories);

        createProductRelations(productPostVm, savedMainProduct);

        if (CollectionUtils.isEmpty(productPostVm.variations())
            || CollectionUtils.isEmpty(productPostVm.productOptionValues())) {
            return ProductGetDetailVm.fromModel(savedMainProduct);
        }

        List<Product> savedVariations = createProductVariationsFromPostVm(productPostVm.variations(), savedMainProduct);

        Map<Long, ProductOption> optionsById = getProductOptionByIdMap(productPostVm.productOptionValues());
        List<ProductOptionValue> savedOptionValues =
            createProductOptionValues(productPostVm, savedMainProduct, optionsById);

        savedMainProduct.setHasOptions((CollectionUtils.isNotEmpty(savedVariations))
            && CollectionUtils.isNotEmpty(savedOptionValues));
        savedMainProduct = productRepository.save(savedMainProduct);

        createOptionCombinations(productPostVm.variations(), savedVariations, optionsById, savedOptionValues);

        return ProductGetDetailVm.fromModel(savedMainProduct);
    }

    private <T extends ProductVariationSaveVm> void validateProductVm(ProductSaveVm<T> productSaveVm) {
        validateProductVm(productSaveVm, null);
    }

    private <T extends ProductVariationSaveVm> void validateProductVm(ProductSaveVm<T> productSaveVm,
                                                                      Product existingProduct) {
        validateExistingProductProperties(productSaveVm, existingProduct);

        validateProductVariationDuplicates(productSaveVm);

        // validate whether the variations contain properties that already exist
        List<Long> variationIds = productSaveVm.variations().stream()
            .map(ProductProperties::id)
            .filter(Objects::nonNull).toList();

        Map<Long, Product> existingVariationsById = productRepository.findAllById(variationIds).stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (ProductProperties variation : productSaveVm.variations()) {
            Product existingVariation = existingVariationsById.get(variation.id());
            validateExistingProductProperties(variation, existingVariation);
        }
    }

    private void validateExistingProductProperties(ProductProperties productProperties, Product existingProduct) {
        checkPropertyExists(productProperties.slug().toLowerCase(), existingProduct,
            productRepository::findBySlugAndIsPublishedTrue, Constants.ErrorCode.SLUG_ALREADY_EXISTED_OR_DUPLICATED);
        // only check gtin when it's not empty
        if (StringUtils.isNotEmpty(productProperties.gtin())) {
            checkPropertyExists(productProperties.gtin(), existingProduct,
                productRepository::findByGtinAndIsPublishedTrue,
                Constants.ErrorCode.GTIN_ALREADY_EXISTED_OR_DUPLICATED);
        }
        checkPropertyExists(productProperties.sku(), existingProduct,
            productRepository::findBySkuAndIsPublishedTrue, Constants.ErrorCode.SKU_ALREADY_EXISTED_OR_DUPLICATED);
    }

    private void checkPropertyExists(String propertyValue, Product existingProduct, Function<String,
        Optional<Product>> finder, String errorCode) {
        finder.apply(propertyValue).ifPresent(foundProduct -> {
            if (existingProduct == null || !foundProduct.getId().equals(existingProduct.getId())) {
                throw new DuplicatedException(errorCode, propertyValue);
            }
        });
    }

    private <T extends ProductVariationSaveVm> void validateProductVariationDuplicates(ProductSaveVm<T> productSaveVm) {
        Set<String> seenSlugs = new HashSet<>(Collections.singletonList(productSaveVm.slug().toLowerCase()));
        Set<String> seenSkus = new HashSet<>(Collections.singletonList(productSaveVm.sku()));
        Set<String> seenGtins = new HashSet<>(Collections.singletonList(productSaveVm.gtin()));
        for (ProductProperties variation : productSaveVm.variations()) {
            if (!seenSlugs.add(variation.slug().toLowerCase())) {
                throw new DuplicatedException(Constants.ErrorCode.SLUG_ALREADY_EXISTED_OR_DUPLICATED, variation.slug());
            }
            // only check gtin when it's not empty
            if (StringUtils.isNotEmpty(variation.gtin()) && !seenGtins.add(variation.gtin())) {
                throw new DuplicatedException(Constants.ErrorCode.GTIN_ALREADY_EXISTED_OR_DUPLICATED, variation.gtin());
            }
            if (!seenSkus.add(variation.sku())) {
                throw new DuplicatedException(Constants.ErrorCode.SKU_ALREADY_EXISTED_OR_DUPLICATED, variation.sku());
            }
        }
    }

    private Product buildMainProductFromVm(ProductPostVm productPostVm) {
        return Product.builder()
            .name(productPostVm.name())
            .thumbnailMediaId(productPostVm.thumbnailMediaId())
            .slug(productPostVm.slug().toLowerCase())
            .description(productPostVm.shortDescription())
            .shortDescription(productPostVm.description())
            .specification(productPostVm.specification())
            .sku(productPostVm.sku())
            .gtin(productPostVm.gtin())
            .price(productPostVm.price())
            .hasOptions(false)
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
            .productCategories(List.of())
            .taxClassId(productPostVm.taxClassId())
            .build();
    }

    private List<Product> createProductVariationsFromPostVm(List<ProductVariationPostVm> newVariationVms,
                                                            Product mainProduct) {
        List<ProductImage> allVariationImages = new ArrayList<>();
        return performCreateVariations(newVariationVms, mainProduct, allVariationImages);
    }

    private List<Product> createProductVariationsFromPutVm(List<ProductVariationPutVm> newVariationVms,
                                                           Product mainProduct,
                                                           List<ProductImage> existingVariationImages) {
        List<ProductImage> allVariationImages = new ArrayList<>(existingVariationImages);
        return performCreateVariations(newVariationVms, mainProduct, allVariationImages);
    }

    private List<Product> performCreateVariations(List<? extends ProductVariationSaveVm> newVariationVms,
                                                  Product mainProduct,
                                                  List<ProductImage> allVariationImages) {
        List<Product> productVariations = newVariationVms.stream()
            .map(variation -> {
                Product productVariation = buildProductVariationFromVm(variation, mainProduct);
                List<ProductImage> variationImages
                    = setProductImages(variation.productImageIds(), productVariation);
                allVariationImages.addAll(variationImages);
                return productVariation;
            })
            .toList();

        List<Product> savedVariations = productRepository.saveAll(productVariations);
        productImageRepository.saveAll(allVariationImages);
        return savedVariations;
    }

    private Product buildProductVariationFromVm(ProductVariationSaveVm variationVm, Product mainProduct) {
        return Product.builder()
            .name(variationVm.name())
            .thumbnailMediaId(variationVm.thumbnailMediaId())
            .slug(variationVm.slug().toLowerCase())
            .sku(variationVm.sku())
            .gtin(variationVm.gtin())
            .price(variationVm.price())
            .isPublished(mainProduct.isPublished())
            .parent(mainProduct)
            .build();
    }

    private List<ProductOptionValue> createProductOptionValues(ProductPostVm productPostVm, Product savedMainProduct,
                                                               Map<Long, ProductOption> optionsById) {
        List<ProductOptionValue> optionValues = new ArrayList<>();
        productPostVm.productOptionValues().forEach(optionValueVm -> optionValueVm.value().forEach(value -> {
            ProductOptionValue optionValue = ProductOptionValue.builder()
                .product(savedMainProduct)
                .displayOrder(optionValueVm.displayOrder())
                .displayType(optionValueVm.displayType())
                .productOption(optionsById.get(optionValueVm.productOptionId()))
                .value(value)
                .build();
            optionValues.add(optionValue);
        }));
        return productOptionValueRepository.saveAll(optionValues);
    }

    private void createOptionCombinations(
        List<? extends ProductVariationSaveVm> variationVms,
        List<Product> savedVariations,
        Map<Long, ProductOption> optionsById,
        List<ProductOptionValue> optionValues) {
        List<ProductOptionCombination> optionCombinations = new ArrayList<>();
        Map<String, Product> variationsBySlug = savedVariations.stream()
            .collect(Collectors.toMap(Product::getSlug, Function.identity()));

        for (ProductVariationSaveVm variationVm : variationVms) {
            String variationSlug = variationVm.slug().toLowerCase();
            Product savedVariation = variationsBySlug.get(variationSlug);
            if (savedVariation == null) {
                log.error(
                    "Failed to create combinations: variation with slug '{}' not found in the saved variations map",
                    variationSlug);
                throw new InternalServerErrorException(Constants.ErrorCode.PRODUCT_COMBINATION_PROCESSING_FAILED);
            }

            variationVm.optionValuesByOptionId().forEach((optionId, optionValue) -> {
                ProductOption productOption = optionsById.get(optionId);
                ProductOptionValue foundOptionValue = optionValues.stream()
                    .filter(
                        pov -> pov.getProductOption().getId().equals(optionId) && pov.getValue().equals(optionValue))
                    .findFirst()
                    .orElseThrow(()
                        -> new BadRequestException(Constants.ErrorCode.PRODUCT_OPTION_VALUE_IS_NOT_FOUND, optionValue));

                ProductOptionCombination optionCombination = ProductOptionCombination.builder()
                    .product(savedVariation)
                    .productOption(productOption)
                    .value(foundOptionValue.getValue())
                    .displayOrder(foundOptionValue.getDisplayOrder())
                    .build();
                optionCombinations.add(optionCombination);
            });
        }
        productOptionCombinationRepository.saveAll(optionCombinations);
    }

    private void createProductRelations(ProductPostVm productPostVm, Product savedMainProduct) {
        if (CollectionUtils.isEmpty(productPostVm.relatedProductIds())) {
            return;
        }
        List<Product> relatedProducts = productRepository.findAllById(productPostVm.relatedProductIds());
        List<ProductRelated> productRelations = relatedProducts.stream()
            .map(relatedProduct -> ProductRelated.builder()
                .product(savedMainProduct)
                .relatedProduct(relatedProduct)
                .build())
            .toList();
        productRelatedRepository.saveAll(productRelations);
    }

    public void updateProduct(long productId, ProductPutVm productPutVm) {
        Product product = productRepository.findById(productId).orElseThrow(()
                -> new NotFoundException(Constants.ErrorCode.PRODUCT_NOT_FOUND, productId));

        validateProductVm(productPutVm, product);

        setProductBrand(productPutVm.brandId(), product);

        updateMainProductFromVm(productPutVm, product);

        updateProductCategories(productPutVm, product);

        List<ProductImage> productImages = setProductImages(productPutVm.productImageIds(), product);
        productImageRepository.saveAll(productImages);

        updateProductRelations(productPutVm, product);

        List<ProductImage> allVariationImages = new ArrayList<>();
        List<Product> existingVariations = product.getProducts();
        updateExistingVariants(productPutVm, allVariationImages, existingVariations);
        productRepository.saveAll(existingVariations);

        if (CollectionUtils.isEmpty(productPutVm.productOptionValues())) {
            return;
        }

        List<ProductVariationPutVm> newVariationVms = productPutVm.variations().stream()
            .filter(variant -> variant.id() == null).toList();
        if (CollectionUtils.isEmpty(newVariationVms)) {
            return;
        }

        List<Product> newSavedVariations =
            createProductVariationsFromPutVm(newVariationVms, product, allVariationImages);

        Map<Long, ProductOption> optionsById = getProductOptionByIdMap(productPutVm.productOptionValues());

        List<ProductOptionValue> productOptionValues = updateProductOptionValues(productPutVm, product, optionsById);

        product.setHasOptions((CollectionUtils.isNotEmpty(newSavedVariations)
            || CollectionUtils.isNotEmpty(existingVariations))
            && CollectionUtils.isNotEmpty(productOptionValues));
        productRepository.save(product);

        createOptionCombinations(newVariationVms, newSavedVariations, optionsById, productOptionValues);

    }

    private Map<Long, ProductOption> getProductOptionByIdMap(List<? extends ProductOptionValueSaveVm> optionValueVms) {
        List<Long> productOptionIds = optionValueVms.stream()
            .map(ProductOptionValueSaveVm::productOptionId).toList();
        List<ProductOption> productOptions = productOptionRepository.findAllByIdIn(productOptionIds);
        if (CollectionUtils.isEmpty(productOptions)) {
            throw new BadRequestException(Constants.ErrorCode.NO_MATCHING_PRODUCT_OPTIONS);
        }
        return productOptions.stream()
            .collect(Collectors.toMap(ProductOption::getId, Function.identity()));
    }

    public void updateMainProductFromVm(ProductPutVm productPutVm, Product product) {
        product.setName(productPutVm.name());
        product.setSlug(productPutVm.slug().toLowerCase());
        product.setThumbnailMediaId(productPutVm.thumbnailMediaId());
        product.setDescription(productPutVm.description());
        product.setShortDescription(productPutVm.shortDescription());
        product.setSpecification(productPutVm.specification());
        product.setSku(productPutVm.sku());
        product.setGtin(productPutVm.gtin());
        product.setPrice(productPutVm.price());
        product.setAllowedToOrder(productPutVm.isAllowedToOrder());
        product.setFeatured(productPutVm.isFeatured());
        product.setPublished(productPutVm.isPublished());
        product.setVisibleIndividually(productPutVm.isVisibleIndividually());
        product.setStockTrackingEnabled(productPutVm.stockTrackingEnabled());
        product.setMetaTitle(productPutVm.metaTitle());
        product.setMetaKeyword(productPutVm.metaKeyword());
        product.setMetaDescription(productPutVm.metaDescription());
        product.setTaxClassId(productPutVm.taxClassId());
    }

    private void updateProductCategories(ProductPutVm productPutVm, Product product) {
        List<ProductCategory> newProductCategories = setProductCategories(productPutVm.categoryIds(), product);
        List<ProductCategory> oldProductCategories = productCategoryRepository.findAllByProductId(product.getId());
        productCategoryRepository.deleteAllInBatch(oldProductCategories);
        productCategoryRepository.saveAll(newProductCategories);
    }

    private void updateProductRelations(ProductPutVm productPutVm, Product product) {
        List<ProductRelated> newProductRelations;
        List<ProductRelated> oldProductRelations;
        List<Long> requestedProductIds = productPutVm.relatedProductIds();
        List<ProductRelated> currentProductRelations = product.getRelatedProducts();

        Set<Long> currentRelatedProductIds = currentProductRelations.stream()
                .map(currentRelation -> currentRelation.getRelatedProduct().getId())
                .collect(Collectors.toSet());

        Set<Long> oldRelatedProductIds = currentRelatedProductIds.stream()
                .filter(id -> !requestedProductIds.contains(id))
                .collect(Collectors.toSet());
        oldProductRelations = currentProductRelations.stream()
            .filter(currentRelation
                -> oldRelatedProductIds.contains(currentRelation.getRelatedProduct().getId()))
            .toList();

        Set<Long> newRelatedProductIds = requestedProductIds.stream()
                .filter(id -> !currentRelatedProductIds.contains(id))
                .collect(Collectors.toSet());
        List<Product> newRelatedProducts = productRepository.findAllById(newRelatedProductIds);
        newProductRelations = newRelatedProducts.stream()
                .map(newRelatedProduct -> ProductRelated.builder()
                        .product(product)
                        .relatedProduct(newRelatedProduct)
                        .build())
                .toList();

        productRelatedRepository.deleteAll(oldProductRelations);
        productRelatedRepository.saveAll(newProductRelations);
    }

    private List<ProductOptionValue> updateProductOptionValues(ProductPutVm productPutVm, Product product,
                                                            Map<Long, ProductOption> optionsById) {
        List<ProductOptionValue> productOptionValues = new ArrayList<>();
        productPutVm.productOptionValues().forEach(optionValueVm -> optionValueVm.value().forEach(value -> {
            ProductOptionValue optionValue = ProductOptionValue.builder()
                .product(product)
                .displayOrder(optionValueVm.displayOrder())
                .displayType(optionValueVm.displayType())
                .productOption(optionsById.get(optionValueVm.productOptionId()))
                .value(value)
                .build();
            productOptionValues.add(optionValue);
        }));
        productOptionValueRepository.saveAll(productOptionValues);
        return productOptionValues;
    }

    private void updateExistingVariants(
            ProductPutVm productPutVm,
            List<ProductImage> newProductImages,
            List<Product> existingVariants
    ) {
        if (CollectionUtils.isNotEmpty(productPutVm.variations())) {
            productPutVm.variations().forEach(variant -> {
                if (variant.id() != null) {
                    Product variantInDb = existingVariants.stream().filter(productVariant
                                    -> variant.id().equals(productVariant.getId()))
                            .findFirst().orElse(null);
                    setValuesForVariantExisting(newProductImages, variant, variantInDb);
                }
            });

        }
    }

    private void setValuesForVariantExisting(
        List<ProductImage> newProductImages,
        ProductVariationPutVm variant,
        Product variantInDb
    ) {
        if (variantInDb != null) {
            variantInDb.setName(variant.name());
            variantInDb.setThumbnailMediaId(variant.thumbnailMediaId());
            variantInDb.setSlug(variant.slug().toLowerCase());
            variantInDb.setSku(variant.sku());
            variantInDb.setGtin(variant.gtin());
            variantInDb.setPrice(variant.price());
            productImageRepository.deleteByProductId(variant.id());
            if (CollectionUtils.isNotEmpty(variant.productImageIds())) {
                variant.productImageIds().forEach(imageId -> {
                    newProductImages.add(ProductImage.builder()
                        .imageId(imageId).product(variantInDb).build());
                });
            }
            productImageRepository.saveAll(newProductImages);
        }
    }

    public List<ProductImage> setProductImages(List<Long> imageMediaIds, Product product) {
        List<ProductImage> productImages = new ArrayList<>();
        if (CollectionUtils.isEmpty(imageMediaIds)) {
            productImageRepository.deleteByProductId(product.getId());
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
                    -> new NotFoundException(Constants.ErrorCode.BRAND_NOT_FOUND, brandId));
            product.setBrand(brand);
        }
    }

    private List<ProductCategory> setProductCategories(List<Long> vmCategoryIds, Product product) {
        List<ProductCategory> productCategoryList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(vmCategoryIds)) {
            List<Long> categoryIds
                = product.getProductCategories().stream().map(productCategory
                    -> productCategory.getCategory().getId()).sorted().toList();
            if (!CollectionUtils.isEqualCollection(categoryIds, vmCategoryIds.stream().sorted().toList())) {
                List<Category> categoryList = categoryRepository.findAllById(vmCategoryIds);
                if (categoryList.isEmpty()) {
                    throw new BadRequestException(Constants.ErrorCode.CATEGORY_NOT_FOUND, vmCategoryIds);
                } else if (categoryList.size() < vmCategoryIds.size()) {
                    vmCategoryIds.removeAll(categoryList.stream().map(Category::getId).toList());
                    throw new BadRequestException(Constants.ErrorCode.CATEGORY_NOT_FOUND, vmCategoryIds);
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

    public ProductDetailVm getProductById(long productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new NotFoundException(Constants.ErrorCode.PRODUCT_NOT_FOUND, productId)
                );
        List<ImageVm> productImageMedias = new ArrayList<>();
        if (null != product.getProductImages()) {
            for (ProductImage image : product.getProductImages()) {
                productImageMedias.add(new ImageVm(image.getImageId(),
                    mediaService.getMedia(image.getImageId()).url()));
            }
        }
        ImageVm thumbnailMedia = null;
        if (null != product.getThumbnailMediaId()) {
            thumbnailMedia = new ImageVm(product.getThumbnailMediaId(),
                mediaService.getMedia(product.getThumbnailMediaId()).url());
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
                product.isAllowedToOrder(),
                product.isPublished(),
                product.isFeatured(),
                product.isVisibleIndividually(),
                product.isStockTrackingEnabled(),
                product.getPrice(),
                brandId,
                categories,
                product.getMetaTitle(),
                product.getMetaKeyword(),
                product.getMetaDescription(),
                thumbnailMedia,
                productImageMedias,
                product.getTaxClassId()
        );
    }

    public List<ProductThumbnailVm> getProductsByBrand(String brandSlug) {
        List<ProductThumbnailVm> productThumbnailVms = new ArrayList<>();
        Brand brand = brandRepository
                .findBySlug(brandSlug)
                .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.BRAND_NOT_FOUND, brandSlug));
        List<Product> products = productRepository.findAllByBrandAndIsPublishedTrue(brand);
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
                .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.CATEGORY_NOT_FOUND, categorySlug));
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
        return products.stream().map(product -> {

            String thumbnailUrl = mediaService.getMedia(product.getThumbnailMediaId()).url();
            if (StringUtils.isNotEmpty(thumbnailUrl) || Objects.isNull(product.getParent())) {
                return new ProductThumbnailGetVm(
                        product.getId(),
                        product.getName(),
                        product.getSlug(),
                        thumbnailUrl,
                        product.getPrice());
            }

            Optional<Product> parentProduct = productRepository.findById(product.getParent().getId());

            return new ProductThumbnailGetVm(
                    product.getId(),
                    product.getName(),
                    product.getSlug(),
                    parentProduct.map(pr -> mediaService.getMedia(pr.getThumbnailMediaId()).url())
                            .orElse(""),
                    product.getPrice());
        }).toList();
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
        Product product = productRepository.findBySlugAndIsPublishedTrue(slug)
                .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.PRODUCT_NOT_FOUND, slug));

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
                    .map(productAttributeValue
                            -> productAttributeValue.getProductAttribute().getProductAttributeGroup())
                    .distinct()
                    .toList();

            productAttributeGroups.forEach(productAttributeGroup -> {
                List<ProductAttributeValueVm> productAttributeValueVms = new ArrayList<>();
                productAttributeValues.forEach(productAttributeValue -> {
                    ProductAttributeGroup group
                        = productAttributeValue.getProductAttribute().getProductAttributeGroup();
                    if ((group != null && group.equals(productAttributeGroup))
                            || (group == null && productAttributeGroup == null)) {
                        ProductAttributeValueVm productAttributeValueVm = new ProductAttributeValueVm(
                                productAttributeValue.getProductAttribute().getName(),
                                productAttributeValue.getValue());
                        productAttributeValueVms.add(productAttributeValueVm);
                    }
                });
                String productAttributeGroupName
                    = productAttributeGroup == null ? NONE_GROUP : productAttributeGroup.getName();
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
                product.isAllowedToOrder(),
                product.isPublished(),
                product.isFeatured(),
                product.isHasOptions(),
                product.getPrice(),
                productThumbnailUrl,
                productImageMediaUrls
        );
    }

    public void deleteProduct(Long id) {
        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.PRODUCT_NOT_FOUND, id));
        product.setPublished(false);
        productRepository.save(product);
    }

    public ProductsGetVm getProductsByMultiQuery(
            int pageNo,
            int pageSize,
            String productName,
            String categorySlug,
            Double startPrice,
            Double endPrice
    ) {
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
                .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.PRODUCT_NOT_FOUND, id));
        if (Boolean.TRUE.equals(parentProduct.isHasOptions())) {
            List<Product> productVariations
                = parentProduct.getProducts().stream().filter(Product::isPublished).toList();

            return productVariations.stream().map(product -> {
                List<ProductOptionCombination> productOptionCombinations =
                        productOptionCombinationRepository.findAllByProduct(product);
                Map<Long, String> options = productOptionCombinations.stream().collect(Collectors.toMap(
                        productOptionCombination -> productOptionCombination.getProductOption().getId(),
                        ProductOptionCombination::getValue
                ));
                ImageVm image = null;
                if (product.getThumbnailMediaId() != null) {
                    image = new ImageVm(product.getThumbnailMediaId(),
                        mediaService.getMedia(product.getThumbnailMediaId()).url());
                }
                return new ProductVariationGetVm(
                        product.getId(),
                        product.getName(),
                        product.getSlug(),
                        product.getSku(),
                        product.getGtin(),
                        product.getPrice(),
                        image,
                        product.getProductImages().stream()
                            .map(productImage -> new ImageVm(productImage.getImageId(),
                                mediaService.getMedia(productImage.getImageId()).url())).toList(),
                        options
                );
            }).toList();
        }
        return Collections.emptyList();
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
                        product.isAllowedToOrder(),
                        product.isPublished(),
                        product.isFeatured(),
                        product.isVisibleIndividually(),
                        product.isStockTrackingEnabled(),
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
                .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.PRODUCT_NOT_FOUND, id));
        Product parent = product.getParent();
        if (parent != null) {
            return new ProductSlugGetVm(parent.getSlug(), id);
        }
        return new ProductSlugGetVm(product.getSlug(), null);
    }

    public ProductEsDetailVm getProductEsDetailById(long productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new NotFoundException(Constants.ErrorCode.PRODUCT_NOT_FOUND, productId)
                );

        Long thumbnailMediaId = null;
        if (null != product.getThumbnailMediaId()) {
            thumbnailMediaId = product.getThumbnailMediaId();
        }
        List<String> categoryNames = product.getProductCategories().stream().map(productCategory
            -> productCategory.getCategory().getName()).toList();
        List<String> attributeNames = product.getAttributeValues().stream().map(attributeValue
            -> attributeValue.getProductAttribute().getName()).toList();

        String brandName = null;
        if (null != product.getBrand()) {
            brandName = product.getBrand().getName();
        }

        return new ProductEsDetailVm(product.getId(),
                product.getName(),
                product.getSlug(),
                product.getPrice(),
                product.isPublished(),
                product.isVisibleIndividually(),
                product.isAllowedToOrder(),
                product.isFeatured(),
                thumbnailMediaId,
                brandName,
                categoryNames,
                attributeNames
        );
    }

    public List<ProductListVm> getRelatedProductsBackoffice(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.PRODUCT_NOT_FOUND, id));
        List<ProductRelated> relatedProducts = product.getRelatedProducts();
        return relatedProducts.stream()
                .map(productRelated ->
                        new ProductListVm(
                                productRelated.getRelatedProduct().getId(),
                                productRelated.getRelatedProduct().getName(),
                                productRelated.getRelatedProduct().getSlug(),
                                productRelated.getRelatedProduct().isAllowedToOrder(),
                                productRelated.getRelatedProduct().isPublished(),
                                productRelated.getRelatedProduct().isFeatured(),
                                productRelated.getRelatedProduct().isVisibleIndividually(),
                                productRelated.getRelatedProduct().getCreatedOn(),
                                productRelated.getRelatedProduct().getTaxClassId()
                        )
                ).toList();
    }

    public ProductsGetVm getRelatedProductsStorefront(Long id, int pageNo, int pageSize) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.PRODUCT_NOT_FOUND, id));
        Page<ProductRelated> relatedProductsPage
            = productRelatedRepository.findAllByProduct(product, PageRequest.of(pageNo, pageSize));
        List<ProductThumbnailGetVm> productThumbnailVms = relatedProductsPage.stream()
                .filter(productRelated -> productRelated.getRelatedProduct().isPublished())
                .map(productRelated -> {
                    Product relatedProduct = productRelated.getRelatedProduct();
                    return new ProductThumbnailGetVm(
                            relatedProduct.getId(),
                            relatedProduct.getName(),
                            relatedProduct.getSlug(),
                            mediaService.getMedia(relatedProduct.getThumbnailMediaId()).url(),
                            relatedProduct.getPrice());
                })
                .toList();
        return new ProductsGetVm(
                productThumbnailVms,
                relatedProductsPage.getNumber(),
                relatedProductsPage.getSize(),
                (int) relatedProductsPage.getTotalElements(),
                relatedProductsPage.getTotalPages(),
                relatedProductsPage.isLast()
        );
    }

    public List<ProductInfoVm> getProductsForWarehouse(
            String name, String sku, List<Long> productIds, FilterExistInWhSelection selection) {
        return productRepository.findProductForWarehouse(name, sku, productIds, selection.name())
                .stream().map(ProductInfoVm::fromProduct).toList();
    }

    public void updateProductQuantity(List<ProductQuantityPostVm> productQuantityPostVms) {

        List<Long> productIds = productQuantityPostVms.stream().map(ProductQuantityPostVm::productId).toList();
        List<Product> products = productRepository.findAllByIdIn(productIds);
        products.parallelStream().forEach(product -> {
            Optional<ProductQuantityPostVm> productQuantityPostVmOptional = productQuantityPostVms.parallelStream()
                .filter(productPostVm -> product.getId().equals(productPostVm.productId())).findFirst();
            productQuantityPostVmOptional.ifPresent(productQuantityPostVm
                -> product.setStockQuantity(productQuantityPostVm.stockQuantity()));
        });

        productRepository.saveAll(products);
    }

    private void partitionUpdateStockQuantityByCalculation(List<ProductQuantityPutVm> productQuantityItems,
                                                           BiFunction<Long, Long, Long> calculation) {
        var productIds = productQuantityItems.stream()
                .map(ProductQuantityPutVm::productId)
                .toList();

        var productQuantityItemMap = productQuantityItems.stream()
                .collect(Collectors.toMap(
                        ProductQuantityPutVm::productId,
                        Function.identity(),
                        this::mergeProductQuantityItem
                ));


        List<Product> products = this.productRepository.findAllByIdIn(productIds);
        products.forEach(product -> {
            if (product.isStockTrackingEnabled()) {
                long amount = getRemainAmountOfStockQuantity(productQuantityItemMap, product, calculation);
                product.setStockQuantity(amount);
            }
        });
        this.productRepository.saveAll(products);
    }

    public void subtractStockQuantity(List<ProductQuantityPutVm> productQuantityItems) {
        ListUtils.partition(productQuantityItems, 5)
                .forEach(it -> partitionUpdateStockQuantityByCalculation(it, this.subtractStockQuantity()));
    }

    private BiFunction<Long, Long, Long> subtractStockQuantity() {
        return (totalQuantity, amount) -> {
            long result = totalQuantity - amount;
            return result < 0 ? 0 : result;
        };
    }

    public List<ProductListVm> getProductByIds(List<Long> productIds) {
        return this.productRepository.findAllByIdIn(productIds).stream().map(ProductListVm::fromModel).toList();
    }

    public void restoreStockQuantity(List<ProductQuantityPutVm> productQuantityItems) {
        ListUtils.partition(productQuantityItems, 5)
                .forEach(it -> partitionUpdateStockQuantityByCalculation(it, this.restoreStockQuantity()));
    }

    private BiFunction<Long, Long, Long> restoreStockQuantity() {
        return Long::sum;
    }

    private Long getRemainAmountOfStockQuantity(Map<Long, ProductQuantityPutVm> productQuantityItemMap,
                                                Product product, BiFunction<Long, Long, Long> calculation) {
        Long stockQuantity = product.getStockQuantity();
        var productItem = productQuantityItemMap.get(product.getId());
        Long quantity = productItem.quantity();
        return calculation.apply(stockQuantity, quantity);
    }

    private ProductQuantityPutVm mergeProductQuantityItem(ProductQuantityPutVm p1, ProductQuantityPutVm p2) {
        var q1 = p1.quantity();
        var q2 = p2.quantity();
        return new ProductQuantityPutVm(p1.productId(), q1 + q2);
    }
}

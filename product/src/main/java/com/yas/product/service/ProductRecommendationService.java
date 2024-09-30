package com.yas.product.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.ImageVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import com.yas.product.viewmodel.product.ProductVariationGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeValueGetVm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for managing product recommendations.
 */

@Service
@Transactional
@Slf4j
public class ProductRecommendationService {
    private final ProductRepository productRepository;
    private final MediaService mediaService;
    private final ProductOptionCombinationRepository productOptionCombinationRepository;

    /**
     * Constructor for {@code ProductRecommendationService} that initializes the service with necessary dependencies.
     *
     * @param productRepository                  the repository responsible for handling product data
     * @param mediaService                       the service responsible for managing media assets associated
     *                                           with products
     * @param productOptionCombinationRepository the repository for managing product option combinations
     */
    public ProductRecommendationService(ProductRepository productRepository, MediaService mediaService,
                                        ProductOptionCombinationRepository productOptionCombinationRepository) {
        this.productRepository = productRepository;
        this.mediaService = mediaService;
        this.productOptionCombinationRepository = productOptionCombinationRepository;
    }

    /**
     * Retrieves detailed information about a product by its unique identifier.
     *
     * @param productId the unique identifier of the product to be retrieved
     * @return a {@link ProductDetailInfoVm} containing detailed information about the product
     * @throws NotFoundException if no product is found with the given {@code productId}
     */
    public ProductDetailInfoVm getProductDetailById(long productId) {
        List<ProductVariationGetVm> variations = new ArrayList<>();
        Product product = productRepository
                .findById(productId)
                .filter(Product::isPublished)
                .orElseThrow(() ->
                        new NotFoundException(Constants.ErrorCode.PRODUCT_NOT_FOUND, productId)
                );

        List<Category> categories = Optional.ofNullable(product.getProductCategories())
                .orElse(Collections.emptyList())  // Handle null case
                .stream()
                .map(ProductCategory::getCategory)
                .toList();

        Long brandId = Optional.ofNullable(product.getBrand())
                .map(Brand::getId)
                .orElse(null);
        String brandName = Optional.ofNullable(product.getBrand())
                .map(Brand::getName)
                .orElse(null);

        List<ProductAttributeValueGetVm> productAttributes = product.getAttributeValues()
                .stream()
                .map(ProductAttributeValueGetVm::fromModel)
                .toList();

        if (Boolean.TRUE.equals(product.isHasOptions())) {
            List<Product> productVariations = product.getProducts()
                    .stream()
                    .toList();
            variations = productVariations.stream()
                    .filter(Product::isPublished)
                    .map(pro -> {
                        List<ProductOptionCombination> productOptionCombinations =
                                productOptionCombinationRepository.findAllByProduct(pro);
                        Map<Long, String> options = productOptionCombinations.stream().collect(Collectors.toMap(
                                productOptionCombination -> productOptionCombination.getProductOption().getId(),
                                ProductOptionCombination::getValue
                        ));

                        return new ProductVariationGetVm(
                                pro.getId(),
                                pro.getName(),
                                pro.getSlug(),
                                pro.getSku(),
                                pro.getGtin(),
                                pro.getPrice(),
                                getThumbnailFromProduct(pro),
                                getImagesFromProduct(pro),
                                options
                        );
                    }).toList();
        }
        return new ProductDetailInfoVm(product.getId(),
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
                product.getTaxClassId(),
                brandName,
                productAttributes,
                variations,
                getThumbnailFromProduct(product),
                getImagesFromProduct(product)
        );
    }

    private ImageVm getThumbnailFromProduct(Product product) {
        return Optional.ofNullable(product.getThumbnailMediaId())
                .map(thumbnailId -> new ImageVm(thumbnailId, mediaService.getMedia(thumbnailId).url()))
                .orElse(null);

    }

    private List<ImageVm> getImagesFromProduct(Product product) {
        return Optional.ofNullable(product.getProductImages())
                .orElse(Collections.emptyList())
                .stream()
                .map(image -> new ImageVm(image.getImageId(), mediaService.getMedia(image.getImageId()).url()))
                .toList();
    }
}

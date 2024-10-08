package com.yas.recommendation.vector.product.service;

import com.yas.recommendation.vector.product.store.ProductVectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for synchronizing product vector data.
 * Provides methods to create, update, and delete product vectors
 * in response to changes in product data.
 */
@Service
@RequiredArgsConstructor
public class ProductVectorSyncService {
    private final ProductVectorRepository productVectorRepository;

    /**
     * Creates a product vector if the product is published.
     *
     * @param productId The unique identifier of the product to be synchronized.
     * @param isPublished Indicates if the product is published.
     *                    The product vector is created only if this value is true.
     */
    public void createProductVector(Long productId, boolean isPublished) {
        if (isPublished) {
            productVectorRepository.add(productId);
        }
    }

    /**
     * Updates a product vector if the product is published; deletes it otherwise.
     *
     * @param productId The unique identifier of the product to be updated.
     * @param isPublished Indicates if the product is published.
     *                    The product vector is updated if true, or deleted if false.
     */
    public void updateProductVector(Long productId, boolean isPublished) {
        if (!isPublished) {
            productVectorRepository.delete(productId);
            return;
        }
        productVectorRepository.update(productId);
    }

    /**
     * Deletes the product vector for the specified product.
     *
     * @param productId The unique identifier of the product whose vector is to be deleted.
     */
    public void deleteProductVector(Long productId) {
        productVectorRepository.delete(productId);
    }


}

package com.yas.recommendation.vector.product.service;

import com.yas.commonlibrary.kafka.cdc.message.Product;
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
     * @param product {@link Product} the product to be synchronized.
     */
    public void createProductVector(Product product) {
        if (product.isPublished()) {
            productVectorRepository.add(product.getId());
        }
    }

    /**
     * Updates a product vector if the product is published; deletes it otherwise.
     *
     * @param product {@link Product} the product to be synchronized.
     */
    public void updateProductVector(Product product) {
        if (product.isPublished()) {
            productVectorRepository.update(product.getId());
        } else {
            productVectorRepository.delete(product.getId());
        }
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

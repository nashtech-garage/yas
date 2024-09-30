package com.yas.recommendation.vector.product.service;

import com.yas.recommendation.vector.product.store.ProductVectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductVectorSyncService {
    private final ProductVectorRepository productVectorRepository;

    public void createProductVector(Long productId, boolean isPublished) {
        if (isPublished) {
            productVectorRepository.add(productId);
        }
    }

    public void updateProductVector(Long productId, boolean isPublished) {
        if (!isPublished) {
            productVectorRepository.delete(productId);
            return;
        }
        productVectorRepository.update(productId);
    }

    public void deleteProductVector(Long productId) {
        productVectorRepository.delete(productId);
    }


}

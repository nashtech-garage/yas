package com.yas.recommendation.vector.product.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.recommendation.vector.product.store.ProductVectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductVectorSyncServiceTest {

    private ProductVectorRepository productVectorRepository;
    private ProductVectorSyncService productVectorSyncService;

    @BeforeEach
    void setUp() {
        productVectorRepository = mock(ProductVectorRepository.class);
        productVectorSyncService = new ProductVectorSyncService(productVectorRepository);
    }

    @Test
    void createProductVector_whenProductIsPublished_shouldCallRepositoryAdd() {
        Product product = new Product();
        product.setId(1L);
        product.setPublished(true);

        productVectorSyncService.createProductVector(product);

        verify(productVectorRepository).add(1L);
    }

    @Test
    void createProductVector_whenProductIsNotPublished_shouldDoNothing() {
        Product product = new Product();
        product.setId(1L);
        product.setPublished(false);

        productVectorSyncService.createProductVector(product);

        verifyNoInteractions(productVectorRepository);
    }

    @Test
    void updateProductVector_whenProductIsPublished_shouldCallRepositoryUpdate() {
        Product product = new Product();
        product.setId(1L);
        product.setPublished(true);

        productVectorSyncService.updateProductVector(product);

        verify(productVectorRepository).update(1L);
    }

    @Test
    void updateProductVector_whenProductIsNotPublished_shouldCallRepositoryDelete() {
        Product product = new Product();
        product.setId(1L);
        product.setPublished(false);

        productVectorSyncService.updateProductVector(product);

        verify(productVectorRepository).delete(1L);
    }

    @Test
    void deleteProductVector_shouldCallRepositoryDelete() {
        productVectorSyncService.deleteProductVector(1L);

        verify(productVectorRepository).delete(1L);
    }
}

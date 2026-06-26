package com.yas.recommendation.vector.product.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.recommendation.vector.product.store.ProductVectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ProductVectorSyncServiceTest {

    private ProductVectorRepository productVectorRepository;
    private ProductVectorSyncService productVectorSyncService;

    @BeforeEach
    void setUp() {
        productVectorRepository = Mockito.mock(ProductVectorRepository.class);
        productVectorSyncService = new ProductVectorSyncService(productVectorRepository);
    }

    @Test
    void createProductVector_whenProductIsPublished_shouldAddVector() {
        Product product = Product.builder().id(10L).isPublished(true).build();

        productVectorSyncService.createProductVector(product);

        verify(productVectorRepository).add(10L);
        verify(productVectorRepository, never()).delete(10L);
    }

    @Test
    void createProductVector_whenProductIsNotPublished_shouldNotAddVector() {
        Product product = Product.builder().id(11L).isPublished(false).build();

        productVectorSyncService.createProductVector(product);

        verify(productVectorRepository, never()).add(11L);
    }

    @Test
    void updateProductVector_whenProductIsPublished_shouldUpdateVector() {
        Product product = Product.builder().id(12L).isPublished(true).build();

        productVectorSyncService.updateProductVector(product);

        verify(productVectorRepository).update(12L);
        verify(productVectorRepository, never()).delete(12L);
    }

    @Test
    void updateProductVector_whenProductIsNotPublished_shouldDeleteVector() {
        Product product = Product.builder().id(13L).isPublished(false).build();

        productVectorSyncService.updateProductVector(product);

        verify(productVectorRepository).delete(13L);
        verify(productVectorRepository, never()).update(13L);
    }

    @Test
    void deleteProductVector_shouldDeleteVectorById() {
        productVectorSyncService.deleteProductVector(14L);

        verify(productVectorRepository).delete(14L);
    }
}

package com.yas.recommendation.vector.product.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.recommendation.vector.product.store.ProductVectorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductVectorSyncServiceTest {

    @Mock
    private ProductVectorRepository productVectorRepository;

    @InjectMocks
    private ProductVectorSyncService productVectorSyncService;

    @Test
    void createProductVector_whenPublished_shouldAdd() {
        Product product = Product.builder().id(1L).isPublished(true).build();
        productVectorSyncService.createProductVector(product);
        verify(productVectorRepository).add(1L);
    }

    @Test
    void createProductVector_whenNotPublished_shouldNotAdd() {
        Product product = Product.builder().id(1L).isPublished(false).build();
        productVectorSyncService.createProductVector(product);
        verifyNoInteractions(productVectorRepository);
    }

    @Test
    void updateProductVector_whenPublished_shouldUpdate() {
        Product product = Product.builder().id(1L).isPublished(true).build();
        productVectorSyncService.updateProductVector(product);
        verify(productVectorRepository).update(1L);
    }

    @Test
    void updateProductVector_whenNotPublished_shouldDelete() {
        Product product = Product.builder().id(1L).isPublished(false).build();
        productVectorSyncService.updateProductVector(product);
        verify(productVectorRepository).delete(1L);
    }

    @Test
    void deleteProductVector_shouldDelete() {
        productVectorSyncService.deleteProductVector(1L);
        verify(productVectorRepository).delete(1L);
    }
}

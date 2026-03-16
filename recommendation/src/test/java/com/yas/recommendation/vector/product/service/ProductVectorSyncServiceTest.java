package com.yas.recommendation.vector.product.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
    void shouldCreateVectorWhenProductIsPublished() {
        Product product = Product.builder().id(10L).isPublished(true).build();

        productVectorSyncService.createProductVector(product);

        verify(productVectorRepository).add(10L);
    }

    @Test
    void shouldSkipCreateWhenProductIsUnpublished() {
        Product product = Product.builder().id(10L).isPublished(false).build();

        productVectorSyncService.createProductVector(product);

        verify(productVectorRepository, never()).add(10L);
    }

    @Test
    void shouldUpdateVectorWhenProductIsPublished() {
        Product product = Product.builder().id(20L).isPublished(true).build();

        productVectorSyncService.updateProductVector(product);

        verify(productVectorRepository).update(20L);
        verify(productVectorRepository, never()).delete(20L);
    }

    @Test
    void shouldDeleteVectorWhenUpdatedProductIsUnpublished() {
        Product product = Product.builder().id(20L).isPublished(false).build();

        productVectorSyncService.updateProductVector(product);

        verify(productVectorRepository).delete(20L);
        verify(productVectorRepository, never()).update(20L);
    }

    @Test
    void shouldDeleteVectorByProductId() {
        productVectorSyncService.deleteProductVector(30L);

        verify(productVectorRepository).delete(30L);
    }
}

package com.yas.recommendation.vector.product.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void createProductVector_whenPublished_shouldAdd() {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(1L);
        when(product.isPublished()).thenReturn(true);

        productVectorSyncService.createProductVector(product);

        verify(productVectorRepository, times(1)).add(1L);
    }

    @Test
    void createProductVector_whenNotPublished_shouldNotAdd() {
        Product product = mock(Product.class);
        when(product.isPublished()).thenReturn(false);

        productVectorSyncService.createProductVector(product);

        verify(productVectorRepository, never()).add(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void updateProductVector_whenPublished_shouldUpdate() {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(1L);
        when(product.isPublished()).thenReturn(true);

        productVectorSyncService.updateProductVector(product);

        verify(productVectorRepository, times(1)).update(1L);
    }

    @Test
    void updateProductVector_whenNotPublished_shouldDelete() {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(1L);
        when(product.isPublished()).thenReturn(false);

        productVectorSyncService.updateProductVector(product);

        verify(productVectorRepository, times(1)).delete(1L);
    }

    @Test
    void deleteProductVector_whenCalled_shouldDelete() {
        productVectorSyncService.deleteProductVector(1L);

        verify(productVectorRepository, times(1)).delete(1L);
    }
}

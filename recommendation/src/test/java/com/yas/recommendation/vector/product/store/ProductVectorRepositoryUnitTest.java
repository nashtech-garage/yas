package com.yas.recommendation.vector.product.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.yas.recommendation.service.ProductService;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;

class ProductVectorRepositoryUnitTest {

    private ProductVectorRepository repository;
    private ProductService productService;
    private VectorStore vectorStore;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        vectorStore = mock(VectorStore.class);
        repository = new ProductVectorRepository(vectorStore, productService);
    }

    @Test
    void getEntity_whenCalled_shouldCallProductService() {
        ProductDetailVm vm = new ProductDetailVm(1L, "Name", null, null, null, null, null, "slug",
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        org.mockito.Mockito.when(productService.getProductDetail(1L)).thenReturn(vm);

        ProductDetailVm result = repository.getEntity(1L);

        assertEquals(vm, result);
    }
}

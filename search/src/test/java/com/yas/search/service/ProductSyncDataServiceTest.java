package com.yas.search.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.search.config.ServiceUrlConfig;
import com.yas.search.model.Product;
import com.yas.search.repository.ProductRepository;
import com.yas.search.viewmodel.ProductEsDetailVm;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

class ProductSyncDataServiceTest {
    private static final String PRODUCT_URL = "http://api.yas.local/product";

    private ProductRepository productRepository;

    private RestClient restClient;

    private ServiceUrlConfig serviceUrlConfig;

    RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    RestClient.ResponseSpec responseSpec;

    private ProductSyncDataService productSyncDataService;

    private static final Long ID = 1L;

    @BeforeEach
    void setUp() {

        productRepository = mock(ProductRepository.class);
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        productSyncDataService = new ProductSyncDataService(restClient, serviceUrlConfig, productRepository);
        requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);

    }

    private void mockProductThumbnailVmsByUri() {

        final URI url = UriComponentsBuilder.fromHttpUrl(PRODUCT_URL)
            .path("/storefront/products-es/{id}").buildAndExpand(ID).toUri();

        when(serviceUrlConfig.product()).thenReturn(PRODUCT_URL);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(ProductEsDetailVm.class))
            .thenReturn(getProductThumbnailVms());
    }

    private ProductEsDetailVm getProductThumbnailVms() {
        return new ProductEsDetailVm(
            ID,
            "Smartphone XYZ",
            "smartphone-xyz",
            299.99,
            true,
            true,
            true,
            false,
            456L,
            "BrandName",
            List.of("Electronics", "Mobile Phones"),
            List.of("Color: Black", "Storage: 128GB", "RAM: 6GB")
        );
    }

    @Test
    void testGetProductEsDetailById_whenNormalCase_returnProductEsDetailVm() {

        mockProductThumbnailVmsByUri();
        ProductEsDetailVm productEsDetailVm = productSyncDataService.getProductEsDetailById(ID);
        assertThat(productEsDetailVm.id()).isEqualTo(1);
        assertThat(productEsDetailVm.name()).isEqualTo("Smartphone XYZ");
        assertThat(productEsDetailVm.slug()).isEqualTo("smartphone-xyz");
        assertThat(productEsDetailVm.price()).isEqualTo(299.99);
        assertThat(productEsDetailVm.isPublished()).isTrue();
        assertThat(productEsDetailVm.isVisibleIndividually()).isTrue();
        assertThat(productEsDetailVm.isAllowedToOrder()).isTrue();
        assertThat(productEsDetailVm.isFeatured()).isFalse();
        assertThat(productEsDetailVm.thumbnailMediaId()).isEqualTo(456L);
        assertThat(productEsDetailVm.brand()).isEqualTo("BrandName");
        assertThat(productEsDetailVm.categories().getFirst()).isEqualTo("Electronics");
        assertThat(productEsDetailVm.categories().getLast()).isEqualTo("Mobile Phones");
        assertThat(productEsDetailVm.attributes().getFirst()).isEqualTo("Color: Black");
        assertThat(productEsDetailVm.attributes().get(1)).isEqualTo("Storage: 128GB");
        assertThat(productEsDetailVm.attributes().getLast()).isEqualTo("RAM: 6GB");
    }

    @Test
    void updateProduct_whenProductExists_updatesProductAndSaves() {

        mockProductThumbnailVmsByUri();
        Product existingProduct = new Product();
        existingProduct.setId(ID);

        ProductEsDetailVm productEsDetailVm = getProductThumbnailVms();

        when(productRepository.findById(ID)).thenReturn(Optional.of(existingProduct));

        productSyncDataService.updateProduct(ID);

        assertThat(existingProduct.getName()).isEqualTo(productEsDetailVm.name());
        assertThat(existingProduct.getSlug()).isEqualTo(productEsDetailVm.slug());
        assertThat(existingProduct.getPrice()).isEqualTo(productEsDetailVm.price());
        assertThat(existingProduct.getIsPublished()).isEqualTo(productEsDetailVm.isPublished());
        assertThat(existingProduct.getIsVisibleIndividually()).isEqualTo(productEsDetailVm.isVisibleIndividually());
        assertThat(existingProduct.getIsAllowedToOrder()).isEqualTo(productEsDetailVm.isAllowedToOrder());
        assertThat(existingProduct.getIsFeatured()).isEqualTo(productEsDetailVm.isFeatured());
        assertThat(existingProduct.getThumbnailMediaId()).isEqualTo(productEsDetailVm.thumbnailMediaId());
        assertThat(existingProduct.getBrand()).isEqualTo(productEsDetailVm.brand());
        assertThat(existingProduct.getCategories()).isEqualTo(productEsDetailVm.categories());
        assertThat(existingProduct.getAttributes()).isEqualTo(productEsDetailVm.attributes());

        verify(productRepository).save(existingProduct);
    }

    @Test
    void testUpdateProduct_whenProductDoesNotExist_throwsNotFoundException() {

        mockProductThumbnailVmsByUri();
        when(productRepository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productSyncDataService.updateProduct(ID))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("The product 1 is not found");
    }


    @Test
    void testCreateProduct_whenNormalCase_createsAndSavesProduct() {

        mockProductThumbnailVmsByUri();
        ProductEsDetailVm productEsDetailVm = getProductThumbnailVms();

        when(productSyncDataService.getProductEsDetailById(ID)).thenReturn(productEsDetailVm);

        productSyncDataService.createProduct(ID);

        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(argumentCaptor.capture());
        Product actual = argumentCaptor.getValue();
        assertThat(actual.getId()).isEqualTo(productEsDetailVm.id());
        assertThat(actual.getName()).isEqualTo(productEsDetailVm.name());
        assertThat(actual.getSlug()).isEqualTo(productEsDetailVm.slug());
        assertThat(actual.getPrice()).isEqualTo(productEsDetailVm.price());
        assertThat(actual.getIsPublished()).isEqualTo(productEsDetailVm.isPublished());
        assertThat(actual.getIsVisibleIndividually()).isEqualTo(productEsDetailVm.isVisibleIndividually());
        assertThat(actual.getIsAllowedToOrder()).isEqualTo(productEsDetailVm.isAllowedToOrder());
        assertThat(actual.getIsFeatured()).isEqualTo(productEsDetailVm.isFeatured());
        assertThat(actual.getThumbnailMediaId()).isEqualTo(productEsDetailVm.thumbnailMediaId());
        assertThat(actual.getBrand()).isEqualTo(productEsDetailVm.brand());
        assertThat(actual.getCategories()).isEqualTo(productEsDetailVm.categories());
        assertThat(actual.getAttributes()).isEqualTo(productEsDetailVm.attributes());
    }


    @Test
    void testDeleteProduct_whenProductExists_deletesProduct() {
        Long id = 1L;

        when(productRepository.existsById(id)).thenReturn(true);

        productSyncDataService.deleteProduct(id);

        verify(productRepository).deleteById(id);
    }

    @Test
    void testDeleteProduct_whenProductDoesNotExist_throwsNotFoundException() {
        Long id = 1L;

        when(productRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> productSyncDataService.deleteProduct(id))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("The product 1 is not found");

        verify(productRepository, never()).deleteById(id);
    }
}
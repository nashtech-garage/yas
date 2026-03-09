package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.ImageVm;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductExportingDetailVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductSaveVm;
import com.yas.product.viewmodel.product.ProductThumbnailGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.model.enumeration.DimensionUnit;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import com.yas.product.viewmodel.product.ProductInfoVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MediaService mediaService;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductCategoryRepository productCategoryRepository;
    @Mock
    private ProductImageRepository productImageRepository;
    @Mock
    private ProductOptionRepository productOptionRepository;
    @Mock
    private ProductOptionValueRepository productOptionValueRepository;
    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;
    @Mock
    private ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductsWithFilter_whenValidParams_thenReturnProductList() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setSlug("product-1");
        
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.getProductsWithFilter(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(productPage);

        ProductListGetVm result = productService.getProductsWithFilter(0, 10, "Product", "Brand");

        assertNotNull(result);
        assertEquals(1, result.productContent().size());
        assertEquals("Product 1", result.productContent().get(0).name());
        assertEquals(1, result.totalElements());
    }

    @Test
    void getProductById_whenProductFound_thenReturnProductDetail() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setThumbnailMediaId(10L);
        product.setBrand(new Brand());
        
        ProductImage productImage = new ProductImage();
        productImage.setImageId(11L);
        product.setProductImages(List.of(productImage));
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumbnail.url"));
        when(mediaService.getMedia(11L)).thenReturn(new NoFileMediaVm(11L, "", "", "", "http://image.url"));

        ProductDetailVm result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Product 1", result.name());
        assertNotNull(result.thumbnailMedia());
        assertEquals("http://thumbnail.url", result.thumbnailMedia().url());
        assertEquals(1, result.productImageMedias().size());
    }

    @Test
    void getProductById_whenProductNotFound_thenThrowNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getLatestProducts_whenValidCount_thenReturnProducts() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");

        when(productRepository.getLatestProducts(any(Pageable.class))).thenReturn(List.of(product));

        List<ProductListVm> result = productService.getLatestProducts(1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getLatestProducts_whenCountIsZero_thenReturnEmptyList() {
        List<ProductListVm> result = productService.getLatestProducts(0);
        assertEquals(0, result.size());
    }

    @Test
    void getProductsByBrand_whenBrandFound_thenReturnProducts() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setSlug("brand");
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setThumbnailMediaId(10L);

        when(brandRepository.findBySlug("brand")).thenReturn(Optional.of(brand));
        when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand)).thenReturn(List.of(product));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumbnail.url"));

        List<ProductThumbnailVm> result = productService.getProductsByBrand("brand");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("http://thumbnail.url", result.get(0).thumbnailUrl());
    }

    @Test
    void getProductsByBrand_whenBrandNotFound_thenThrowNotFoundException() {
        when(brandRepository.findBySlug("brand")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductsByBrand("brand"));
    }

    @Test
    void getProductsFromCategory_whenCategoryFound_thenReturnProducts() {
        Category category = new Category();
        category.setId(1L);
        Product product = new Product();
        product.setId(1L);
        product.setThumbnailMediaId(10L);
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(category);
        productCategory.setProduct(product);

        Page<ProductCategory> categoryPage = new PageImpl<>(List.of(productCategory), PageRequest.of(0, 10), 1);
        
        when(categoryRepository.findBySlug("category-slug")).thenReturn(Optional.of(category));
        when(productCategoryRepository.findAllByCategory(any(Pageable.class), any(Category.class))).thenReturn(categoryPage);
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumbnail.url"));

        ProductListGetFromCategoryVm result = productService.getProductsFromCategory(0, 10, "category-slug");

        assertNotNull(result);
        assertEquals(1, result.productContent().size());
    }

    @Test
    void getProductsFromCategory_whenCategoryNotFound_thenThrowNotFoundException() {
        when(categoryRepository.findBySlug("category-slug")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductsFromCategory(0, 10, "category-slug"));
    }

    @Test
    void getFeaturedProductsById_whenProductsFound_thenReturnProducts() {
        Product product = new Product();
        product.setId(1L);
        product.setThumbnailMediaId(10L);
        product.setPrice(100.0);

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumbnail.url"));

        List<ProductThumbnailGetVm> result = productService.getFeaturedProductsById(List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("http://thumbnail.url", result.get(0).thumbnailUrl());
        assertEquals(100.0, result.get(0).price());
    }

    @Test
    void getListFeaturedProducts_whenValidParams_thenReturnProducts() {
        Product product = new Product();
        product.setId(1L);
        product.setThumbnailMediaId(10L);
        
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(productPage);
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumbnail.url"));

        ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 10);

        assertNotNull(result);
        assertEquals(1, result.productList().size());
        assertEquals(1, result.totalPage());
    }

    @Test
    void getProductByIds_whenValidIds_thenReturnProducts() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));

        List<ProductListVm> result = productService.getProductByIds(List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getProductByCategoryIds_whenValidIds_thenReturnProducts() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");

        when(productRepository.findByCategoryIdsIn(List.of(1L))).thenReturn(List.of(product));

        List<ProductListVm> result = productService.getProductByCategoryIds(List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getProductByBrandIds_whenValidIds_thenReturnProducts() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");

        when(productRepository.findByBrandIdsIn(List.of(1L))).thenReturn(List.of(product));

        List<ProductListVm> result = productService.getProductByBrandIds(List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void exportProducts_whenValidParams_thenReturnExportingDetails() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Brand 1");
        product.setBrand(brand);

        when(productRepository.getExportingProducts(anyString(), anyString())).thenReturn(List.of(product));

        List<ProductExportingDetailVm> result = productService.exportProducts("Product", "Brand");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Product 1", result.get(0).name());
        assertEquals("Brand 1", result.get(0).brandName());
    }

    @Test
    void updateProductQuantity_whenValidParams_thenUpdateSuccessfully() {
        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(10L);

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));

        productService.updateProductQuantity(List.of(new ProductQuantityPostVm(1L, 20L)));

        assertEquals(20L, product.getStockQuantity());
    }

    @Test
    void createProduct_whenValidInput_thenReturnProductGetDetailVm() {
        ProductPostVm postVm = new ProductPostVm("Name", "slug", 1L, List.of(1L), "Short", "Desc", "Spec", "sku", "gtin", 
            1.0, DimensionUnit.CM, 2.0, 1.0, 1.0, 10.0, true, true, true, true, true, 
            "MetaTitle", "MetaKey", "MetaDesc", 1L, List.of(1L), List.of(), List.of(), List.of(), List.of(1L), 1L);
        
        Brand brand = new Brand();
        brand.setId(1L);
        
        Category category = new Category();
        category.setId(1L);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Name");
        savedProduct.setSlug("slug");

        when(productRepository.findBySlugAndIsPublishedTrue("slug")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("sku")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("gtin")).thenReturn(Optional.empty());
        
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));
        
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductGetDetailVm result = productService.createProduct(postVm);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Name", result.name());
    }

    @Test
    void updateProduct_whenValidInput_thenUpdateSuccessfully() {
        ProductPutVm putVm = new ProductPutVm("Name", "slug", 1.0, true, true, true, true, true, 
            1L, List.of(1L), "Short", "Desc", "Spec", "sku", "gtin", 
            1.0, DimensionUnit.CM, 1.0, 1.0, 1.0, "title", "keyword", "meta", 
            1L, List.of(1L), List.of(), List.of(), List.of(), List.of(1L), 1L);
        
        Product product = new Product();
        product.setId(1L);
        product.setName("OldName");
        product.setSlug("oldslug");

        Brand brand = new Brand();
        brand.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findBySlugAndIsPublishedTrue("slug")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("sku")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("gtin")).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        
        Category category = new Category();
        category.setId(1L);
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));

        ProductOption option = new ProductOption();
        option.setId(1L);
        when(productOptionRepository.findAllByIdIn(any())).thenReturn(List.of(option));

        productService.updateProduct(1L, putVm);

        assertEquals("Name", product.getName());
        assertEquals("slug", product.getSlug());
    }

    @Test
    void testSubtractStockQuantity() {
        ProductQuantityPutVm qVm = new ProductQuantityPutVm(1L, 5L);
        Product product = new Product();
        product.setId(1L);
        product.setStockTrackingEnabled(true);
        product.setStockQuantity(10L);
        
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        
        productService.subtractStockQuantity(List.of(qVm));
        assertEquals(5L, product.getStockQuantity());
    }

    @Test
    void testRestoreStockQuantity() {
        ProductQuantityPutVm qVm = new ProductQuantityPutVm(1L, 5L);
        Product product = new Product();
        product.setId(1L);
        product.setStockTrackingEnabled(true);
        product.setStockQuantity(10L);
        
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        
        productService.restoreStockQuantity(List.of(qVm));
        assertEquals(15L, product.getStockQuantity());
    }
}

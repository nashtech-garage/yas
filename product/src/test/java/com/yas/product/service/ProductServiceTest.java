package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.model.ProductRelated;
import com.yas.product.repository.*;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductOptionValueDisplay;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductPutVm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductVariationPutVm;

import org.springframework.data.domain.Page;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private MediaService mediaService;
    @Mock private BrandRepository brandRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductCategoryRepository productCategoryRepository;
    @Mock private ProductImageRepository productImageRepository;
    @Mock private ProductOptionRepository productOptionRepository;
    @Mock private ProductOptionValueRepository productOptionValueRepository;
    @Mock private ProductOptionCombinationRepository productOptionCombinationRepository;
    @Mock private ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Iphone 15 Pro Max");
        product.setSlug("iphone-15-pro-max");
        product.setPrice(1000.0);
        product.setThumbnailMediaId(10L);
        product.setPublished(true);
        product.setHasOptions(false);

        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Apple");
        product.setBrand(brand);
    }

    @Test
    void getProductById_WhenValidId_ShouldReturnProductDetailVm() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        NoFileMediaVm mediaVm = mock(NoFileMediaVm.class);
        when(mediaVm.url()).thenReturn("https://image.url");
        when(mediaService.getMedia(anyLong())).thenReturn(mediaVm);

        ProductDetailVm result = productService.getProductById(1L);
        assertNotNull(result);
    }

    @Test
    void getProductById_WhenProductNotFound_ShouldThrowNotFoundException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductById(999L));
    }

    @Test
    void createProduct_WhenValidData_ShouldReturnProductGetDetailVm() {
        ProductPostVm postVm = mock(ProductPostVm.class);
        when(postVm.name()).thenReturn("New Phone");
        when(postVm.slug()).thenReturn("new-phone");
        when(postVm.length()).thenReturn(10.0);
        when(postVm.width()).thenReturn(5.0);
        when(postVm.brandId()).thenReturn(1L);
        when(postVm.variations()).thenReturn(List.of());
        when(postVm.productOptionValues()).thenReturn(List.of());

        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductGetDetailVm result = productService.createProduct(postVm);
        assertNotNull(result);
    }

    @Test
    void createProduct_WhenLengthLessThanWidth_ShouldThrowBadRequestException() {
        ProductPostVm postVm = mock(ProductPostVm.class);
        when(postVm.length()).thenReturn(5.0);
        when(postVm.width()).thenReturn(10.0);

        assertThrows(BadRequestException.class, () -> productService.createProduct(postVm));
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void updateProduct_WhenValidData_ShouldUpdateSuccessfully() {
        ProductPutVm putVm = mock(ProductPutVm.class);
        when(putVm.name()).thenReturn("Updated Phone");
        when(putVm.slug()).thenReturn("updated-phone");
        when(putVm.price()).thenReturn(1000.0);
        when(putVm.length()).thenReturn(10.0);
        when(putVm.width()).thenReturn(5.0);
        when(putVm.brandId()).thenReturn(1L);
        when(putVm.isPublished()).thenReturn(true);
        when(putVm.isAllowedToOrder()).thenReturn(true);
        when(putVm.isFeatured()).thenReturn(true);

        when(putVm.categoryIds()).thenReturn(List.of());
        when(putVm.relatedProductIds()).thenReturn(List.of());
        when(putVm.variations()).thenReturn(List.of());
        when(putVm.productImageIds()).thenReturn(List.of());
        when(putVm.productOptionValueDisplays()).thenReturn(List.of());

        com.yas.product.model.ProductOptionValueSaveVm optionValueVm = mock(com.yas.product.model.ProductOptionValueSaveVm.class);
        when(optionValueVm.productOptionId()).thenReturn(1L);
        when(putVm.productOptionValues()).thenReturn((List) List.of(optionValueVm));

        com.yas.product.model.ProductOption option = new com.yas.product.model.ProductOption();
        option.setId(1L);
        when(productOptionRepository.findAllByIdIn(anyList())).thenReturn(List.of(option));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        
        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());

        when(productRepository.save(any(Product.class))).thenReturn(product);

        assertDoesNotThrow(() -> productService.updateProduct(1L, putVm));
    }

    @Test
    void updateProduct_WhenProductNotFound_ShouldThrowNotFoundException() {
        ProductPutVm putVm = mock(ProductPutVm.class);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.updateProduct(999L, putVm));
    }

    @Test
    void deleteProduct_WhenValidId_ShouldDeleteSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        assertDoesNotThrow(() -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void deleteProduct_WhenProductNotFound_ShouldThrowNotFoundException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.deleteProduct(999L));
    }

    @Test
    void getProductByBrandIds_ShouldReturnList() {
        when(productRepository.findByBrandIdsIn(anyList())).thenReturn(List.of(product));
        var result = productService.getProductByBrandIds(List.of(1L));
        assertFalse(result.isEmpty());
    }

    @Test
    void getListFeaturedProducts_ShouldReturnPage() {
        org.springframework.data.domain.Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(page);
        
        NoFileMediaVm mediaVm = mock(NoFileMediaVm.class);
        when(mediaVm.url()).thenReturn("url");
        when(mediaService.getMedia(anyLong())).thenReturn(mediaVm);

        var result = productService.getListFeaturedProducts(0, 10);
        assertNotNull(result);
    }

    @Test
    void getLatestProducts_ShouldReturnProductList() {
        when(productRepository.getLatestProducts(any(Pageable.class))).thenReturn(List.of(product));
        
        NoFileMediaVm mediaVm = mock(NoFileMediaVm.class);
        when(mediaVm.url()).thenReturn("url");
        when(mediaService.getMedia(anyLong())).thenReturn(mediaVm);

        var result = productService.getLatestProducts(10);
        assertFalse(result.isEmpty());
    }

    @Test
    void getProductCheckoutList_ShouldReturnList() {
        org.springframework.data.domain.Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findAllPublishedProductsByIds(anyList(), any(Pageable.class))).thenReturn(page);
        
        NoFileMediaVm mediaVm = mock(NoFileMediaVm.class);
        when(mediaVm.url()).thenReturn("thumb-url");
        when(mediaService.getMedia(anyLong())).thenReturn(mediaVm);

        var result = productService.getProductCheckoutList(0, 10, List.of(1L));
        assertNotNull(result);
    }

    @Test
    void getProductSlug_ShouldReturnSlug() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductSlug(1L);
        assertNotNull(result);
    }

    @Test
    void getProductEsDetailById_ShouldReturnData() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductEsDetailById(1L);
        assertNotNull(result);
    }

    @Test
    void getRelatedProductsStorefront_ShouldReturnList() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        // FIX 2: Mock hàm findAllByProduct để tránh lỗi NullPointerException
        when(productRelatedRepository.findAllByProduct(any(Product.class), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
            
        var result = productService.getRelatedProductsStorefront(1L, 0, 10);
        assertNotNull(result);
    }
    @Test
    void deleteProduct_WhenHasParent_ShouldDeleteCombinations() {
        // Test nhánh: Nếu product có parent, nó sẽ xóa cả ProductOptionCombination
        Product parent = new Product();
        parent.setId(2L);
        product.setParent(parent);
        
        com.yas.product.model.ProductOptionCombination combo = new com.yas.product.model.ProductOptionCombination();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(product)).thenReturn(List.of(combo));
        
        assertDoesNotThrow(() -> productService.deleteProduct(1L));
        
        // Đảm bảo lệnh xóa Combinations đã được gọi
        verify(productOptionCombinationRepository, times(1)).deleteAll(anyList());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void getRelatedProductsBackoffice_ShouldReturnList() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getRelatedProductsBackoffice(1L);
        assertNotNull(result);
    }

    @Test
    void getProductsWithFilter_ShouldReturnPage() {
        org.springframework.data.domain.Page<Product> page = new org.springframework.data.domain.PageImpl<>(List.of(product));
        when(productRepository.getProductsWithFilter(anyString(), anyString(), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(page);
        
        var result = productService.getProductsWithFilter(0, 10, "iphone", "apple");
        assertNotNull(result);
    }

    @Test
    void getProductsFromCategory_ShouldReturnList() {
        com.yas.product.model.Category category = new com.yas.product.model.Category();
        category.setId(1L);
        when(categoryRepository.findBySlug(anyString())).thenReturn(Optional.of(category));

        com.yas.product.model.ProductCategory pc = new com.yas.product.model.ProductCategory();
        pc.setProduct(product);
        org.springframework.data.domain.Page<com.yas.product.model.ProductCategory> pcPage = new org.springframework.data.domain.PageImpl<>(List.of(pc));
        when(productCategoryRepository.findAllByCategory(any(org.springframework.data.domain.Pageable.class), any(com.yas.product.model.Category.class)))
            .thenReturn(pcPage);

        NoFileMediaVm mediaVm = mock(NoFileMediaVm.class);
        when(mediaVm.url()).thenReturn("url");
        when(mediaService.getMedia(anyLong())).thenReturn(mediaVm);

        var result = productService.getProductsFromCategory(0, 10, "smartphones");
        assertNotNull(result);
    }

    @Test
    void getProductDetail_BySlug_ShouldReturnData() {
        when(productRepository.findBySlugAndIsPublishedTrue("iphone-15-pro-max")).thenReturn(Optional.of(product));
        NoFileMediaVm mediaVm = mock(NoFileMediaVm.class);
        when(mediaVm.url()).thenReturn("url");
        when(mediaService.getMedia(anyLong())).thenReturn(mediaVm);

        var result = productService.getProductDetail("iphone-15-pro-max");
        assertNotNull(result);
    }

    @Test
    void getProductsByMultiQuery_ShouldReturnPage() {
        org.springframework.data.domain.Page<Product> page = new org.springframework.data.domain.PageImpl<>(List.of(product));
        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(anyString(), anyString(), anyDouble(), anyDouble(), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(page);
            
        NoFileMediaVm mediaVm = mock(NoFileMediaVm.class);
        when(mediaVm.url()).thenReturn("url");
        when(mediaService.getMedia(anyLong())).thenReturn(mediaVm);

        var result = productService.getProductsByMultiQuery(0, 10, "iphone", "phone", 0.0, 1000.0);
        assertNotNull(result);
    }

    @Test
    void getProductVariationsByParentId_ShouldReturnList() {
        product.setHasOptions(true); // Cờ báo hiệu có variation
        Product variant = new Product();
        variant.setId(2L);
        variant.setPublished(true);
        product.setProducts(List.of(variant)); 

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(any(Product.class))).thenReturn(List.of());

        var result = productService.getProductVariationsByParentId(1L);
        assertFalse(result.isEmpty());
    }

    @Test
    void exportProducts_ShouldReturnList() {
        when(productRepository.getExportingProducts(anyString(), anyString())).thenReturn(List.of(product));
        var result = productService.exportProducts("iphone", "apple");
        assertFalse(result.isEmpty());
    }

    @Test
    void updateProductQuantity_ShouldUpdate() {
        com.yas.product.viewmodel.product.ProductQuantityPostVm pq = mock(com.yas.product.viewmodel.product.ProductQuantityPostVm.class);
        when(pq.productId()).thenReturn(1L);
        when(pq.stockQuantity()).thenReturn(100L);

        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
        
        productService.updateProductQuantity(List.of(pq));

        verify(productRepository, times(1)).saveAll(anyList());
        assertEquals(100L, product.getStockQuantity());
    }

    @Test
    void subtractStockQuantity_ShouldSubtract() {
        com.yas.product.viewmodel.product.ProductQuantityPutVm pq = mock(com.yas.product.viewmodel.product.ProductQuantityPutVm.class);
        when(pq.productId()).thenReturn(1L);
        when(pq.quantity()).thenReturn(2L); // Giảm đi 2

        product.setStockTrackingEnabled(true);
        product.setStockQuantity(10L); // Có sẵn 10

        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

        productService.subtractStockQuantity(List.of(pq));

        verify(productRepository, times(1)).saveAll(anyList());
        assertEquals(8L, product.getStockQuantity());
    }

    @Test
    void restoreStockQuantity_ShouldRestore() {
        com.yas.product.viewmodel.product.ProductQuantityPutVm pq = mock(com.yas.product.viewmodel.product.ProductQuantityPutVm.class);
        when(pq.productId()).thenReturn(1L);
        when(pq.quantity()).thenReturn(3L); // Hồi phục 3

        product.setStockTrackingEnabled(true);
        product.setStockQuantity(10L); // Có sẵn 10

        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

        productService.restoreStockQuantity(List.of(pq));

        verify(productRepository, times(1)).saveAll(anyList());
        assertEquals(13L, product.getStockQuantity());
    }

@Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void createProduct_WithFullData_ShouldCoverAllSubMethods() {
        ProductPostVm postVm = mock(ProductPostVm.class);
        when(postVm.name()).thenReturn("Full Product");
        when(postVm.slug()).thenReturn("full-product");
        when(postVm.sku()).thenReturn("main-sku");
        when(postVm.length()).thenReturn(10.0);
        when(postVm.width()).thenReturn(5.0);
        when(postVm.brandId()).thenReturn(1L);

        // Mock Categories
        when(postVm.categoryIds()).thenReturn(List.of(1L));
        com.yas.product.model.Category category = new com.yas.product.model.Category(); 
        category.setId(1L);
        when(categoryRepository.findAllById(anyList())).thenReturn(List.of(category));

        // Mock Brand
        when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));

        // Mock Options (Để cover được nhánh check hasOptions)
        com.yas.product.model.ProductOptionValueSaveVm optionValue = mock(com.yas.product.model.ProductOptionValueSaveVm.class);
        when(optionValue.productOptionId()).thenReturn(1L);
        when(postVm.productOptionValues()).thenReturn((List) List.of(optionValue));
        when(postVm.productOptionValueDisplays()).thenReturn(List.of());

        // Mock Variations rỗng để tránh lỗi xử lý Combinations phức tạp ở bước này
        when(postVm.variations()).thenReturn(List.of());

        com.yas.product.model.ProductOption option = new com.yas.product.model.ProductOption(); 
        option.setId(1L);
        when(productOptionRepository.findAllByIdIn(anyList())).thenReturn(List.of(option));
        
        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(product);

        var result = productService.createProduct(postVm);
        assertNotNull(result);
    }

@Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void updateProduct_WithFullData_ShouldCoverAllSubMethods() {
        ProductPutVm putVm = mock(ProductPutVm.class);
        when(putVm.name()).thenReturn("Full Update");
        when(putVm.slug()).thenReturn("full-update");
        when(putVm.sku()).thenReturn("main-sku");
        when(putVm.length()).thenReturn(10.0);
        when(putVm.width()).thenReturn(5.0);
        when(putVm.brandId()).thenReturn(1L);

        // Mock Categories
        when(putVm.categoryIds()).thenReturn(List.of(1L));
        com.yas.product.model.Category category = new com.yas.product.model.Category(); 
        category.setId(1L);
        when(categoryRepository.findAllById(anyList())).thenReturn(List.of(category));

        // Mock Options (QUAN TRỌNG: Không được để rỗng ở hàm Update)
        com.yas.product.model.ProductOptionValueSaveVm optionValue = mock(com.yas.product.model.ProductOptionValueSaveVm.class);
        when(optionValue.productOptionId()).thenReturn(1L);
        when(putVm.productOptionValues()).thenReturn((List) List.of(optionValue));
        when(putVm.productOptionValueDisplays()).thenReturn(List.of());

        com.yas.product.model.ProductOption option = new com.yas.product.model.ProductOption(); 
        option.setId(1L);
        // Fix lỗi BadRequest: Trả về option khi hàm getProductOptionByIdMap gọi Repo
        when(productOptionRepository.findAllByIdIn(anyList())).thenReturn(List.of(option));

        // Các thành phần khác
        when(putVm.productImageIds()).thenReturn(List.of());
        when(putVm.relatedProductIds()).thenReturn(List.of());
        when(putVm.variations()).thenReturn(List.of());

        when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(product);

        assertDoesNotThrow(() -> productService.updateProduct(1L, putVm));
    }

@Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void createProduct_WhenVariationsAreDuplicated_ShouldThrowBadRequestException() {
        ProductPostVm postVm = mock(ProductPostVm.class);
        when(postVm.name()).thenReturn("Dup");
        when(postVm.slug()).thenReturn("dup");
        when(postVm.length()).thenReturn(10.0);
        when(postVm.width()).thenReturn(5.0);

        // Tạo 2 variation giống hệt nhau về Slug để ép văng lỗi Duplicate
        com.yas.product.model.ProductVariationSaveVm var1 = mock(com.yas.product.model.ProductVariationSaveVm.class);
        when(var1.slug()).thenReturn("same-slug");

        com.yas.product.model.ProductVariationSaveVm var2 = mock(com.yas.product.model.ProductVariationSaveVm.class);
        when(var2.slug()).thenReturn("same-slug"); // Bị trùng ở đây

        when(postVm.variations()).thenReturn((List) List.of(var1, var2));

        assertThrows(DuplicatedException.class, () -> productService.createProduct(postVm));
    }

    @Test
    void getProductDetail_WithFullRelationships_ShouldReturnData() {
        // Test hàm lấy chi tiết khi sản phẩm có kèm cả Category, Image, Attribute, và Variation
        product.setHasOptions(true);
        Product var1 = new Product(); 
        var1.setId(2L); 
        var1.setPublished(true);
        product.setProducts(List.of(var1));

        com.yas.product.model.ProductCategory pc = new com.yas.product.model.ProductCategory();
        com.yas.product.model.Category cat = new com.yas.product.model.Category(); 
        cat.setName("Cat1"); 
        pc.setCategory(cat);
        product.setProductCategories(List.of(pc));

        com.yas.product.model.ProductImage pi = new com.yas.product.model.ProductImage();
        pi.setImageId(10L);
        product.setProductImages(List.of(pi));

        com.yas.product.model.attribute.ProductAttributeValue pav = new com.yas.product.model.attribute.ProductAttributeValue();
        com.yas.product.model.attribute.ProductAttribute pa = new com.yas.product.model.attribute.ProductAttribute();
        com.yas.product.model.attribute.ProductAttributeGroup pag = new com.yas.product.model.attribute.ProductAttributeGroup();
        pag.setName("Group1"); 
        pa.setProductAttributeGroup(pag); 
        pa.setName("Attr1");
        pav.setProductAttribute(pa); 
        pav.setValue("Val1");
        product.setAttributeValues(List.of(pav));

        when(productRepository.findBySlugAndIsPublishedTrue("full-slug")).thenReturn(Optional.of(product));
        
        NoFileMediaVm mediaVm = mock(NoFileMediaVm.class);
        when(mediaVm.url()).thenReturn("url");
        when(mediaService.getMedia(anyLong())).thenReturn(mediaVm);

        var result = productService.getProductDetail("full-slug");
        assertNotNull(result);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void createProduct_WhenGtinsAreDuplicated_ShouldThrowDuplicatedException() {
        ProductPostVm postVm = mock(ProductPostVm.class);
        when(postVm.name()).thenReturn("Dup GTIN");
        when(postVm.slug()).thenReturn("dup-gtin");
        when(postVm.sku()).thenReturn("main-sku");
        when(postVm.length()).thenReturn(10.0);
        when(postVm.width()).thenReturn(5.0);

        com.yas.product.model.ProductVariationSaveVm var1 = mock(com.yas.product.model.ProductVariationSaveVm.class);
        when(var1.slug()).thenReturn("var1");
        when(var1.sku()).thenReturn("sku1");
        when(var1.gtin()).thenReturn("same-gtin"); // GTIN trùng lặp

        com.yas.product.model.ProductVariationSaveVm var2 = mock(com.yas.product.model.ProductVariationSaveVm.class);
        when(var2.slug()).thenReturn("var2");
        when(var2.sku()).thenReturn("sku2");
        when(var2.gtin()).thenReturn("same-gtin"); // GTIN trùng lặp

        when(postVm.variations()).thenReturn((List) List.of(var1, var2));

        assertThrows(DuplicatedException.class, () -> productService.createProduct(postVm));
    }

    @Test
    void setProductImages_WithExistingImages_ShouldAddAndRemoveCorrectly() {
        // Test nhánh logic phức tạp khi cập nhật danh sách hình ảnh (giữ cũ, xóa cũ, thêm mới)
        Product p = new Product();
        p.setId(1L);
        com.yas.product.model.ProductImage pi1 = new com.yas.product.model.ProductImage();
        pi1.setImageId(10L);
        com.yas.product.model.ProductImage pi2 = new com.yas.product.model.ProductImage();
        pi2.setImageId(20L);
        p.setProductImages(List.of(pi1, pi2)); // Đang có sẵn ảnh 10 và 20

        List<Long> newImageIds = List.of(20L, 30L); // Yêu cầu: Giữ ảnh 20, Xóa ảnh 10, Thêm ảnh 30

        var result = productService.setProductImages(newImageIds, p);

        assertEquals(1, result.size()); // Chỉ trả về 1 ảnh mới (30L) để lưu vào DB
        assertEquals(30L, result.get(0).getImageId());
        
        // Xác nhận đã gọi lệnh xóa ảnh 10L
        verify(productImageRepository, times(1)).deleteByImageIdInAndProductId(List.of(10L), 1L);
    }

@Test
    void getFeaturedProductsById_WithParentThumbnail() {
        Product parent = new Product();
        parent.setId(100L);
        parent.setThumbnailMediaId(99L); // Sản phẩm cha có ảnh 99L

        Product child = new Product();
        child.setId(1L);
        child.setThumbnailMediaId(null); // Sản phẩm con không có ảnh (null)
        child.setParent(parent);

        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(child));
        when(productRepository.findById(100L)).thenReturn(Optional.of(parent));

        NoFileMediaVm mediaVm = mock(NoFileMediaVm.class);
        when(mediaVm.url()).thenReturn("parent-thumbnail-url");
        
        // FIX: Dùng any() để Mockito tự tin mock cả trường hợp input bị null
        when(mediaService.getMedia(any())).thenReturn(mediaVm);

        var result = productService.getFeaturedProductsById(List.of(1L));
        assertEquals(1, result.size());
        assertEquals("parent-thumbnail-url", result.get(0).thumbnailUrl()); 
    }

    @Test
    void getProductDetail_WithNullAttributeGroup() {
        // Test nhánh: Lấy chi tiết sản phẩm mà Thuộc tính (Attribute) KHÔNG thuộc Nhóm (Group) nào
        Product p = new Product();
        p.setId(1L);
        p.setPublished(true);

        com.yas.product.model.attribute.ProductAttributeValue pav = new com.yas.product.model.attribute.ProductAttributeValue();
        com.yas.product.model.attribute.ProductAttribute pa = new com.yas.product.model.attribute.ProductAttribute();
        pa.setName("AttrNoGroup");
        pa.setProductAttributeGroup(null); // Không có Group
        pav.setProductAttribute(pa);
        pav.setValue("Val1");
        p.setAttributeValues(List.of(pav));

        when(productRepository.findBySlugAndIsPublishedTrue("slug-no-group")).thenReturn(Optional.of(p));

        NoFileMediaVm mediaVm = mock(NoFileMediaVm.class);
        when(mediaVm.url()).thenReturn("url");
        when(mediaService.getMedia(any())).thenReturn(mediaVm);

        var result = productService.getProductDetail("slug-no-group");
        
        assertNotNull(result);
        assertEquals(1, result.productAttributeGroups().size());
        assertEquals("None group", result.productAttributeGroups().get(0).name()); // Tự động gán là "None group"
    }

    @Test
    void subtractStockQuantity_WhenStockTrackingDisabled_ShouldNotUpdate() {
        // Test nhánh: Nếu StockTracking = false, thì hệ thống KHÔNG ĐƯỢC trừ tồn kho
        com.yas.product.viewmodel.product.ProductQuantityPutVm pq = mock(com.yas.product.viewmodel.product.ProductQuantityPutVm.class);
        when(pq.productId()).thenReturn(1L);
        when(pq.quantity()).thenReturn(2L); // Yêu cầu trừ 2

        product.setStockTrackingEnabled(false); // Đã TẮT tính năng quản lý tồn kho
        product.setStockQuantity(10L);

        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

        productService.subtractStockQuantity(List.of(pq));

        // Tồn kho phải giữ nguyên là 10, không bị trừ thành 8
        assertEquals(10L, product.getStockQuantity());
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void createProduct_WithCombinations_ShouldThrowInternalServerErrorAndCoverCatchBlock() {
        ProductPostVm postVm = mock(ProductPostVm.class);
        when(postVm.name()).thenReturn("Combo");
        when(postVm.slug()).thenReturn("combo");
        when(postVm.sku()).thenReturn("combo-sku");
        when(postVm.length()).thenReturn(10.0);
        when(postVm.width()).thenReturn(5.0);
        when(postVm.brandId()).thenReturn(1L);

        // Kích hoạt lỗi Combination bằng cách truyền mảng Value có dữ liệu nhưng Display bị rỗng
        com.yas.product.model.ProductOptionValueSaveVm optionValue = mock(com.yas.product.model.ProductOptionValueSaveVm.class);
        when(optionValue.productOptionId()).thenReturn(1L);
        when(postVm.productOptionValues()).thenReturn((List) List.of(optionValue));
        when(postVm.productOptionValueDisplays()).thenReturn(List.of()); // Rỗng -> Xảy ra lỗi Map

        com.yas.product.model.ProductVariationSaveVm variation = mock(com.yas.product.model.ProductVariationSaveVm.class);
        when(variation.name()).thenReturn("Red Variation");
        when(variation.sku()).thenReturn("var-sku");
        when(variation.slug()).thenReturn("var-slug");
        when(variation.optionValuesByOptionId()).thenReturn((java.util.Map) java.util.Map.of(1L, "Red"));
        when(postVm.variations()).thenReturn((List) List.of(variation));

        com.yas.product.model.ProductOption option = new com.yas.product.model.ProductOption(); 
        option.setId(1L);
        when(productOptionRepository.findAllByIdIn(anyList())).thenReturn(List.of(option));

        when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // CHỜ ĐỢI ĐƯỢC BẮT LỖI -> Phủ xanh khối CATCH trong source code của bạn
        assertThrows(com.yas.commonlibrary.exception.InternalServerErrorException.class, () -> productService.createProduct(postVm));
    }

    @Test
    void getProductEsDetailById_WithFullData_ShouldReturnData() {
        product.setHasOptions(true);
        Product var = new Product();
        var.setId(2L);
        product.setProducts(List.of(var));

        com.yas.product.model.attribute.ProductAttributeValue pav = new com.yas.product.model.attribute.ProductAttributeValue();
        com.yas.product.model.attribute.ProductAttribute pa = new com.yas.product.model.attribute.ProductAttribute();
        pa.setName("Attr1");
        pav.setProductAttribute(pa);
        pav.setValue("Val1");
        product.setAttributeValues(List.of(pav));

        com.yas.product.model.ProductCategory pc = new com.yas.product.model.ProductCategory();
        com.yas.product.model.Category cat = new com.yas.product.model.Category();
        cat.setName("Cat1");
        pc.setCategory(cat);
        product.setProductCategories(List.of(pc));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductEsDetailById(1L);
        assertNotNull(result);
    }



    @Test
    void getProductByIds_ShouldReturnList() {
        when(productRepository.findAllById(anyList())).thenReturn(List.of(new Product()));
        var result = productService.getProductByIds(List.of(1L));
        assertNotNull(result);
    }

    @Test
    void getProductByCategoryIds_ShouldReturnList() {
        when(productRepository.findByCategoryIdsIn(anyList())).thenReturn(List.of(new Product()));
        var result = productService.getProductByCategoryIds(List.of(1L));
        assertNotNull(result);
    }



// Cập nhật hàm helper để nhận thêm SKU
private com.yas.product.viewmodel.product.ProductVariationPutVm mockProductVariationPutVm(Long id, String name, String sku) {
    var vMock = mock(com.yas.product.viewmodel.product.ProductVariationPutVm.class);
    when(vMock.id()).thenReturn(id);
    when(vMock.name()).thenReturn(name);
    when(vMock.sku()).thenReturn(sku); // Thêm SKU ở đây
    when(vMock.slug()).thenReturn(name.toLowerCase().replace(" ", "-"));
    when(vMock.optionValuesByOptionId()).thenReturn(java.util.Map.of());
    return vMock;
}

    @Test
void getRelatedProductsBackoffice_ShouldCoverLambda() {
    ProductRelated related = new ProductRelated();
    Product relatedProd = new Product();
    relatedProd.setId(2L);
    relatedProd.setName("Related");
    related.setRelatedProduct(relatedProd);
    
    product.setRelatedProducts(List.of(related));
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    var result = productService.getRelatedProductsBackoffice(1L);
    assertFalse(result.isEmpty());
    assertEquals("Related", result.get(0).name());
}



    @Test
    void createProduct_WithDuplicateSlug_ShouldThrowException() {
        ProductPostVm postVm = mock(ProductPostVm.class);
        when(postVm.slug()).thenReturn("iphone-15");
        
        // Giả lập đã tồn tại 1 sản phẩm khác có slug này
        when(productRepository.findBySlugAndIsPublishedTrue("iphone-15"))
            .thenReturn(Optional.of(new Product()));

        assertThrows(com.yas.commonlibrary.exception.DuplicatedException.class, 
            () -> productService.createProduct(postVm));
    }

    @Test
    void subtractStockQuantity_WithMissingProduct_ShouldCoverLambda() {
        // Đổi ProductQuantityPostVm -> ProductQuantityPutVm
        ProductQuantityPutVm qty = new ProductQuantityPutVm(1L, 5L); 
        
        Product p = new Product(); 
        p.setId(1L); 
        p.setStockQuantity(10L);
        
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(p));
        
        productService.subtractStockQuantity(List.of(qty));
        verify(productRepository).saveAll(anyList());
    }

    @Test
    void getRelatedProductsStorefront_ShouldCoverLambda() {
        // 1. Tạo sản phẩm chính
        Product product = new Product();
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // 2. Tạo sản phẩm liên quan (Phải setPublished(true) để vượt qua filter ở dòng 612 của Service)
        Product relatedProduct = new Product();
        relatedProduct.setId(2L);
        relatedProduct.setName("Related Product");
        relatedProduct.setSlug("related-slug");
        relatedProduct.setPublished(true); 
        relatedProduct.setThumbnailMediaId(100L);

        // 3. Tạo đối tượng ProductRelated
        ProductRelated productRelated = new ProductRelated();
        productRelated.setProduct(product);
        productRelated.setRelatedProduct(relatedProduct);

        // 4. Mock Repository dùng đúng hàm findAllByProduct
        // Lưu ý: Import org.springframework.data.domain.Page và PageImpl
        Page<ProductRelated> page = new PageImpl<>(List.of(productRelated));
        
        // SỬA TẠI ĐÂY: Dùng findAllByProduct thay vì hàm lạ kia
        when(productRelatedRepository.findAllByProduct(eq(product), any(Pageable.class)))
            .thenReturn(page);

        // 5. Mock MediaService để phủ tiếp lambda mapping URL
        NoFileMediaVm mediaVm = mock(NoFileMediaVm.class);
        when(mediaVm.url()).thenReturn("http://image.url");
        when(mediaService.getMedia(any())).thenReturn(mediaVm);

        // 6. Thực thi
        var result = productService.getRelatedProductsStorefront(1L, 0, 5);

        // 7. Kiểm chứng
        assertNotNull(result);
        assertFalse(result.productContent().isEmpty());
        assertEquals("Related Product", result.productContent().get(0).name());
    }    
}
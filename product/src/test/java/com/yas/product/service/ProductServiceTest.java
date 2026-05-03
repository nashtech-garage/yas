package com.yas.product.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.model.ProductRelated;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.model.enumeration.FilterExistInWhSelection;
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
import com.yas.product.viewmodel.product.ProductDetailGetVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductEsDetailVm;
import com.yas.product.viewmodel.product.ProductExportingDetailVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductProperties;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductSaveVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductVariationPostVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    MediaService mediaService;
    @Mock
    BrandRepository brandRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    ProductCategoryRepository productCategoryRepository;
    @Mock
    ProductImageRepository productImageRepository;
    @Mock
    ProductOptionRepository productOptionRepository;
    @Mock
    ProductOptionValueRepository productOptionValueRepository;
    @Mock
    ProductOptionCombinationRepository productOptionCombinationRepository;
    @Mock
    ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    ProductService productService;

    Product product;
    Brand brand;
    Category category;

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(1L);
        brand.setName("Test Brand");

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setSlug("test-product");
        product.setPrice(10.0);
        product.setBrand(brand);
        product.setThumbnailMediaId(1L);
        product.setPublished(true);
        product.setLength(10.0);
        product.setWidth(5.0);
    }

    // --- Test Get Product By Id ---
    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void testGetProductById_Success() {
        // Setup Media mock
        NoFileMediaVm mockMedia = mock(NoFileMediaVm.class);
        when(mockMedia.url()).thenReturn("http://test-url.com/image.jpg");
        when(mediaService.getMedia(anyLong())).thenReturn(mockMedia);
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(category);
        product.setProductCategories(List.of(productCategory));

        ProductImage productImage = new ProductImage();
        productImage.setImageId(2L);
        product.setProductImages(List.of(productImage));

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        ProductDetailVm result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(product.getId(), result.id());
        assertEquals(product.getName(), result.name());
        assertEquals(brand.getId(), result.brandId());
        assertEquals(1, result.categories().size());
        assertEquals(1, result.productImageMedias().size());
    }

    // --- Test Delete Product ---
    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void testDeleteProduct_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.deleteProduct(1L);

        // Verify product is set to un-published
        assertFalse(product.isPublished());
        verify(productRepository).save(product);
    }
    
    @Test
    void testDeleteProduct_WithParentAndCombinations() {
        Product parent = new Product();
        parent.setId(2L);
        product.setParent(parent);
        
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        
        ProductOptionCombination combination = new ProductOptionCombination();
        when(productOptionCombinationRepository.findAllByProduct(product)).thenReturn(List.of(combination));

        productService.deleteProduct(1L);

        assertFalse(product.isPublished());
        verify(productOptionCombinationRepository).deleteAll(any());
        verify(productRepository).save(product);
    }

    // --- Test Validate Product Vm (Length < Width) ---
    @Test
    void testValidateProductVm_LengthLessThanWidth() {
        ProductPostVm postVm = mock(ProductPostVm.class);
        when(postVm.length()).thenReturn(4.0);
        when(postVm.width()).thenReturn(5.0); // Width > Length

        assertThrows(BadRequestException.class, () -> productService.createProduct(postVm));
    }

    // --- Test Get Latest Products ---
    @Test
    void testGetLatestProducts_CountZero() {
        List<ProductListVm> result = productService.getLatestProducts(0);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLatestProducts_Success() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.getLatestProducts(any(Pageable.class))).thenReturn(List.of(product));
        
        List<ProductListVm> result = productService.getLatestProducts(5);
        
        assertEquals(1, result.size());
        assertEquals(product.getId(), result.get(0).id());
    }

    // --- Test Get Products By Brand ---
    @Test
    void testGetProductsByBrand_NotFound() {
         when(brandRepository.findBySlug(anyString())).thenReturn(Optional.empty());
         assertThrows(NotFoundException.class, () -> productService.getProductsByBrand("test-slug"));
    }

    @Test
    void testGetProductsWithFilter() {
        Page<Product> page = new PageImpl<>(List.of(product));
        // Giả lập Repository trả về 1 trang chứa product
        when(productRepository.getProductsWithFilter(anyString(), anyString(), any(Pageable.class))).thenReturn(page);
        
        ProductListGetVm result = productService.getProductsWithFilter(0, 10, "product", "brand");
        
        assertNotNull(result);
        assertEquals(1, result.productContent().size());
        assertEquals(product.getName(), result.productContent().get(0).name());
    }

    @Test
    void testGetProductSlug_NoParent() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        ProductSlugGetVm result = productService.getProductSlug(1L);
        assertEquals(product.getSlug(), result.slug());
    }

    @Test
    void testGetProductSlug_WithParent() {
        Product parent = new Product();
        parent.setSlug("parent-slug");
        product.setParent(parent);
        
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        ProductSlugGetVm result = productService.getProductSlug(1L);
        assertEquals("parent-slug", result.slug());
    }

    @Test
    void testGetProductByIds() {
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        List<ProductListVm> result = productService.getProductByIds(List.of(1L, 2L));
        assertEquals(1, result.size());
    }

    @Test
    void testGetProductByCategoryIds() {
        when(productRepository.findByCategoryIdsIn(any())).thenReturn(List.of(product));
        List<ProductListVm> result = productService.getProductByCategoryIds(List.of(1L));
        assertEquals(1, result.size());
    }

    @Test
    void testGetProductByBrandIds() {
        when(productRepository.findByBrandIdsIn(any())).thenReturn(List.of(product));
        List<ProductListVm> result = productService.getProductByBrandIds(List.of(1L));
        assertEquals(1, result.size());
    }

    @Test
    void testExportProducts() {
        when(productRepository.getExportingProducts(anyString(), anyString())).thenReturn(List.of(product));
        
        List<ProductExportingDetailVm> result = productService.exportProducts("name", "brand");
        
        assertEquals(1, result.size());
        assertEquals(product.getName(), result.get(0).name());
    }
    
    @Test
    void testGetProductsFromCategory_Success() {
        when(categoryRepository.findBySlug(anyString())).thenReturn(Optional.of(category));
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setProduct(product);
        Page<ProductCategory> page = new PageImpl<>(List.of(productCategory));
        when(productCategoryRepository.findAllByCategory(any(Pageable.class), any(Category.class))).thenReturn(page);
        
        // Mock media service y như cách lúc nãy đã fix
        NoFileMediaVm mockMedia = mock(NoFileMediaVm.class);
        when(mockMedia.url()).thenReturn("http://test-url.com/image.jpg");
        when(mediaService.getMedia(anyLong())).thenReturn(mockMedia);

        ProductListGetFromCategoryVm result = productService.getProductsFromCategory(0, 10, "category-slug");
        
        assertNotNull(result);
        assertEquals(1, result.productContent().size());
    }
    
    @Test
    void testGetListFeaturedProducts() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(page);
        
        NoFileMediaVm mockMedia = mock(NoFileMediaVm.class);
        when(mockMedia.url()).thenReturn("http://test-url.com/image.jpg");
        when(mediaService.getMedia(anyLong())).thenReturn(mockMedia);

        ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 10);
        
        assertNotNull(result);
        assertEquals(1, result.productList().size());
    }

    @Test
    void testGetRelatedProductsBackoffice() {
        Product related = new Product();
        related.setId(2L);
        related.setName("Related");
        
        ProductRelated pr = new ProductRelated();
        pr.setRelatedProduct(related);
        product.setRelatedProducts(List.of(pr));
        
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        
        List<ProductListVm> result = productService.getRelatedProductsBackoffice(1L);
        
        assertEquals(1, result.size());
        assertEquals("Related", result.get(0).name());
    }
    
    @Test
    void testGetProductDetail_Success() {
        // Setup dữ liệu giả cho Thuộc tính sản phẩm (Attributes)
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setName("Group 1");
        ProductAttribute attr = new ProductAttribute();
        attr.setName("Attr 1");
        attr.setProductAttributeGroup(group);
        ProductAttributeValue attrValue = new ProductAttributeValue();
        attrValue.setProductAttribute(attr);
        attrValue.setValue("Value 1");
        product.setAttributeValues(List.of(attrValue));
        
        // Setup danh mục và hình ảnh
        ProductCategory pc = new ProductCategory();
        pc.setCategory(category);
        product.setProductCategories(List.of(pc));
        ProductImage pi = new ProductImage();
        pi.setImageId(2L);
        product.setProductImages(List.of(pi));

        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.of(product));
        
        NoFileMediaVm mockMedia = mock(NoFileMediaVm.class);
        when(mockMedia.url()).thenReturn("http://test.url/img.jpg");
        when(mediaService.getMedia(anyLong())).thenReturn(mockMedia);

        ProductDetailGetVm result = productService.getProductDetail("test-product");
        
        assertNotNull(result);
        assertEquals(product.getName(), result.name());
        assertFalse(result.productAttributeGroups().isEmpty());
    }

    @Test
    void testGetProductEsDetailById_Success() {
        ProductCategory pc = new ProductCategory();
        pc.setCategory(category);
        product.setProductCategories(List.of(pc));
        
        ProductAttribute attr = new ProductAttribute();
        attr.setName("Attr 1");
        ProductAttributeValue attrValue = new ProductAttributeValue();
        attrValue.setProductAttribute(attr);
        product.setAttributeValues(List.of(attrValue));

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        
        ProductEsDetailVm result = productService.getProductEsDetailById(1L);
        
        assertNotNull(result);
        assertEquals(product.getName(), result.name());
    }

    @Test
    void testGetProductsForWarehouse() {
        when(productRepository.findProductForWarehouse(any(), any(), any(), anyString()))
            .thenReturn(List.of(product));
            
        var result = productService.getProductsForWarehouse("name", "sku", List.of(1L), FilterExistInWhSelection.ALL);
        
        assertEquals(1, result.size());
    }

    @Test
    void testUpdateProductQuantity() {
        ProductQuantityPostVm vm = mock(ProductQuantityPostVm.class);
        when(vm.productId()).thenReturn(1L);
        when(vm.stockQuantity()).thenReturn(100L);
        
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        
        productService.updateProductQuantity(List.of(vm));
        
        assertEquals(100L, product.getStockQuantity());
        verify(productRepository).saveAll(any());
    }

    @Test
    void testCreateProduct_WithVariations_Success() {
        // 1. Giả lập Dữ liệu đầu vào (ProductPostVm) - Dùng lenient() để né strict stubbing
        ProductPostVm postVm = org.mockito.Mockito.mock(ProductPostVm.class);
        org.mockito.Mockito.lenient().when(postVm.name()).thenReturn("iPhone 15");
        org.mockito.Mockito.lenient().when(postVm.slug()).thenReturn("iphone-15");
        org.mockito.Mockito.lenient().when(postVm.sku()).thenReturn("IP15-01");
        org.mockito.Mockito.lenient().when(postVm.length()).thenReturn(10.0);
        org.mockito.Mockito.lenient().when(postVm.width()).thenReturn(5.0); // Đảm bảo length > width
        org.mockito.Mockito.lenient().when(postVm.brandId()).thenReturn(1L);
        org.mockito.Mockito.lenient().when(postVm.categoryIds()).thenReturn(List.of(1L));
        org.mockito.Mockito.lenient().when(postVm.productImageIds()).thenReturn(List.of(1L));
        
        // 2. Giả lập Biến thể (Variations)
        ProductVariationPostVm variationVm = org.mockito.Mockito.mock(ProductVariationPostVm.class);
        org.mockito.Mockito.lenient().when(variationVm.slug()).thenReturn("iphone-15-pro");
        org.mockito.Mockito.lenient().when(variationVm.sku()).thenReturn("IP15-PRO");
        org.mockito.Mockito.lenient().when(variationVm.optionValuesByOptionId()).thenReturn(java.util.Map.of(1L, "Titanium"));
        org.mockito.Mockito.lenient().when(postVm.variations()).thenReturn(List.of(variationVm));

        // 3. Giả lập Option Values
        com.yas.product.viewmodel.product.ProductOptionValueDisplay optionValueDisplay = 
                org.mockito.Mockito.mock(com.yas.product.viewmodel.product.ProductOptionValueDisplay.class);
        org.mockito.Mockito.lenient().when(optionValueDisplay.productOptionId()).thenReturn(1L);
        org.mockito.Mockito.lenient().when(optionValueDisplay.value()).thenReturn("Titanium");
        org.mockito.Mockito.lenient().when(optionValueDisplay.displayOrder()).thenReturn(1);

        // Dùng class ProductOptionValuePostVm thay vì SaveVm
        ProductOptionValuePostVm optionValuePostVm = org.mockito.Mockito.mock(ProductOptionValuePostVm.class);
        org.mockito.Mockito.lenient().when(optionValuePostVm.productOptionId()).thenReturn(1L);
        
        // Gán các list mock vào postVm
        org.mockito.Mockito.lenient().when(postVm.productOptionValues()).thenReturn(List.of(optionValuePostVm));
        org.mockito.Mockito.lenient().when(postVm.productOptionValueDisplays()).thenReturn(List.of(optionValueDisplay));

        // 4. Vượt qua các chốt chặn Validation (Duplicate Check)
        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());

        // 5. Giả lập các thao tác lưu Database
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(categoryRepository.findAllById(any())).thenReturn(List.of(category));
        
        // Mock lưu sản phẩm chính
        Product savedMainProduct = new Product();
        savedMainProduct.setId(10L);
        savedMainProduct.setSlug("iphone-15");
        when(productRepository.save(any(Product.class))).thenReturn(savedMainProduct);
        
        // Tạo mock cho Product biến thể với đúng slug
        Product savedVariationProduct = new Product();
        savedVariationProduct.setId(11L);
        savedVariationProduct.setSlug("iphone-15-pro"); // Phải GIỐNG HỆT slug của variationVm đã mock ở trên
        // Mock saveAll trả về danh sách chứa biến thể
        when(productRepository.saveAll(any())).thenReturn(List.of(savedVariationProduct));

        ProductOption option = new ProductOption();
        option.setId(1L);
        when(productOptionRepository.findAllByIdIn(any())).thenReturn(List.of(option));
        
        ProductOptionValue pov = new ProductOptionValue();
        pov.setProductOption(option);
        when(productOptionValueRepository.saveAll(any())).thenReturn(List.of(pov));

        // 6. THỰC THI HÀM (Đoạn này bị thiếu trong file của bạn)
        com.yas.product.viewmodel.product.ProductGetDetailVm result = productService.createProduct(postVm);

        // 7. KIỂM TRA KẾT QUẢ
        assertNotNull(result);
        
        verify(productImageRepository, org.mockito.Mockito.times(2)).saveAll(any());
        
        verify(productCategoryRepository).saveAll(any());
        verify(productOptionCombinationRepository).saveAll(any());
    }
}
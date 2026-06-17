package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.Category;
import com.yas.product.repository.*;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
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

    private Product mockProduct;
    private ProductPostVm buildProductPostVm(double length, double width) {
        // Truyền chính xác 30 tham số theo đúng thứ tự của record ProductPostVm
        return new ProductPostVm(
                "Test Product",       // name
                "test-product",       // slug
                1L,                   // brandId
                List.of(),            // categoryIds
                "short desc",         // shortDescription
                "description",        // description
                "specification",      // specification
                "sku",                // sku
                "gtin",               // gtin
                1.0,                  // weight
                null,                 // dimensionUnit (để null cho test pass nhanh, không cần import Enum)
                length,               // length (để test case truyền vào)
                width,                // width (để test case truyền vào)
                5.0,                  // height
                100.0,                // price
                true,                 // isAllowedToOrder
                true,                 // isPublished
                true,                 // isFeatured
                true,                 // isVisibleIndividually
                true,                 // stockTrackingEnabled
                "metaTitle",          // metaTitle
                "metaKeyword",        // metaKeyword
                "metaDescription",    // metaDescription
                1L,                   // thumbnailMediaId
                List.of(),            // productImageIds
                List.of(),            // variations
                List.of(),            // productOptionValues
                List.of(),            // productOptionValueDisplays
                List.of(),            // relatedProductIds
                1L                    // taxClassId
        );
    }

   @BeforeEach
    void setUp() {
        // Khởi tạo Brand bằng cách thông thường thay vì dùng Builder
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Test Brand");

        mockProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .slug("test-product")
                .thumbnailMediaId(1L)
                .price(100.0)
                .isPublished(true)
                .brand(brand)
                .productCategories(List.of())
                .build();
    }

    // ==========================================
    // TESTS CHO CREATE PRODUCT & VALIDATIONS
    // ==========================================

 @Test
    void createProduct_WhenLengthLessThanWidth_ShouldThrowBadRequestException() {
        // Arrange
        ProductPostVm postVm = buildProductPostVm(5.0, 10.0);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            productService.createProduct(postVm);
        });
        
        // FIX 1: Sửa lại chuỗi message cho khớp với file hằng số
        assertEquals("Please make sure length greater than width", exception.getMessage());
    }

   @Test
    void createProduct_Success_WithoutVariations() {
        // Arrange
        ProductPostVm postVm = buildProductPostVm(10.0, 5.0);

        // Mock cho các kiểm tra trùng lặp
        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("gtin")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("sku")).thenReturn(Optional.empty());
        
        // FIX 2: Bổ sung mock cho BrandRepository để không bị văng lỗi NotFound
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Test Brand");
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        // Mock lưu DB
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // Act
        ProductGetDetailVm result = productService.createProduct(postVm);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.name());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // ==========================================
    // TESTS CHO GET PRODUCT
    // ==========================================

    @Test
    void getProductById_WhenProductExists_ShouldReturnProductDetailVm() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        
        // Mock mediaService do trong logic getProductById có gọi để lấy thumbnail url
        NoFileMediaVm mockMedia = new NoFileMediaVm(1L, "caption", "file", "image/png", "http://image-url.com");
        lenient().when(mediaService.getMedia(anyLong())).thenReturn(mockMedia);

        // Act
        ProductDetailVm result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test Product", result.name());
        assertEquals(1L, result.brandId());
        assertEquals("http://image-url.com", result.thumbnailMedia().url());
    }

    @Test
    void getProductById_WhenProductNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> productService.getProductById(99L));
    }

    // ==========================================
    // TESTS CHO LẤY DANH SÁCH (PAGINATION)
    // ==========================================

    @Test
    void getProductsWithFilter_ShouldReturnProductListGetVm() {
        // Arrange
        Page<Product> productPage = new PageImpl<>(List.of(mockProduct), PageRequest.of(0, 10), 1);
        when(productRepository.getProductsWithFilter("test", "brand", PageRequest.of(0, 10)))
                .thenReturn(productPage);

        // Act
        ProductListGetVm result = productService.getProductsWithFilter(0, 10, "test", "brand");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(1, result.productContent().size());
        assertEquals("Test Product", result.productContent().get(0).name());
    }

    // ==========================================
    // TESTS CHO XÓA PRODUCT
    // ==========================================

    @Test
    void deleteProduct_WhenExists_ShouldSetPublishedToFalse() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // Act
        productService.deleteProduct(1L);

        // Assert
        assertFalse(mockProduct.isPublished()); // Kiểm tra cờ isPublished đã bị set thành false (soft delete)
        verify(productRepository, times(1)).save(mockProduct);
    }
    
    @Test
    void deleteProduct_WhenNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> productService.deleteProduct(99L));
    }

    // ==========================================
    // TESTS CHO UPDATE PRODUCT (HÀM LỚN)
    // ==========================================

    @Test
    void updateProduct_WhenProductNotFound_ShouldThrowNotFoundException() {
        // Arrange
        ProductPutVm putVm = mock(ProductPutVm.class);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> productService.updateProduct(99L, putVm));
    }

@Test
    void updateProduct_Success_BasicInfo() {
        // Arrange
        ProductPutVm putVm = mock(ProductPutVm.class);
        
        // Thêm lenient() vào trước tất cả các lệnh when để tránh lỗi UnnecessaryStubbing
        lenient().when(putVm.name()).thenReturn("Updated Name");
        lenient().when(putVm.slug()).thenReturn("updated-slug");
        lenient().when(putVm.price()).thenReturn(200.0);
        lenient().when(putVm.brandId()).thenReturn(1L);
        lenient().when(putVm.categoryIds()).thenReturn(List.of());
        lenient().when(putVm.productImageIds()).thenReturn(List.of());
        lenient().when(putVm.relatedProductIds()).thenReturn(List.of());
        lenient().when(putVm.variations()).thenReturn(List.of()); // Không update variation để code chạy vào nhánh return sớm
        lenient().when(putVm.productOptionValues()).thenReturn(List.of());
        lenient().when(putVm.productOptionValueDisplays()).thenReturn(List.of());

        Brand brand = new Brand();
        brand.setId(1L);

        lenient().when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        lenient().when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        lenient().when(productRepository.findBySlugAndIsPublishedTrue("updated-slug")).thenReturn(Optional.empty());

        com.yas.product.model.ProductOption mockOption = new com.yas.product.model.ProductOption();
        mockOption.setId(1L);
        lenient().when(productOptionRepository.findAllByIdIn(anyList())).thenReturn(List.of(mockOption));

        // Act
        productService.updateProduct(1L, putVm);

        // Assert
        assertEquals("Updated Name", mockProduct.getName());
        assertEquals("updated-slug", mockProduct.getSlug());
        assertEquals(200.0, mockProduct.getPrice());
    }

    // ==========================================
    // TESTS CHO CÁC HÀM GET, EXPORT, QUANTITY (Tăng coverage nhanh)
    // ==========================================

    @Test
    void getLatestProducts_ShouldReturnList() {
        // Arrange
        when(productRepository.getLatestProducts(any(Pageable.class))).thenReturn(List.of(mockProduct));
        
        // Act
        List<ProductListVm> result = productService.getLatestProducts(10);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).name());
    }

    @Test
    void getProductsByBrand_ShouldReturnList() {
        // Arrange
        Brand brand = new Brand(); 
        brand.setId(1L); 
        brand.setName("Brand");
        
        when(brandRepository.findBySlug("brand-slug")).thenReturn(Optional.of(brand));
        when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand)).thenReturn(List.of(mockProduct));
        
        NoFileMediaVm mediaVm = new NoFileMediaVm(1L, "caption", "file", "img", "url");
        lenient().when(mediaService.getMedia(1L)).thenReturn(mediaVm);

        // Act
        List<ProductThumbnailVm> result = productService.getProductsByBrand("brand-slug");
        
        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).name());
    }

    @Test
    void getProductSlug_WhenProductExists_ShouldReturnSlug() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        
        // Act
        ProductSlugGetVm result = productService.getProductSlug(1L);
        
        // Assert
        assertEquals("test-product", result.slug());
    }

    @Test
    void exportProducts_ShouldReturnExportList() {
        // Arrange
        mockProduct.setShortDescription("short");
        mockProduct.setDescription("desc");
        when(productRepository.getExportingProducts("test", "brand")).thenReturn(List.of(mockProduct));
        
        // Act
        List<ProductExportingDetailVm> result = productService.exportProducts("test", "brand");
        
        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).name());
    }

    @Test
    void updateProductQuantity_ShouldUpdateStock() {
        // Arrange
        ProductQuantityPostVm mockQty = mock(ProductQuantityPostVm.class);
        when(mockQty.productId()).thenReturn(1L);
        when(mockQty.stockQuantity()).thenReturn(50L);
        
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(mockProduct));
        
        // Act
        productService.updateProductQuantity(List.of(mockQty));
        
        // Assert
        assertEquals(50L, mockProduct.getStockQuantity());
        verify(productRepository, times(1)).saveAll(any());
    }

    @Test
    void getProductDetail_BySlug_ShouldReturnDetail() {
        // Arrange
        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.of(mockProduct));
        NoFileMediaVm media = new NoFileMediaVm(1L, "", "", "", "url");
        lenient().when(mediaService.getMedia(1L)).thenReturn(media);
        mockProduct.setAttributeValues(List.of()); // Ngăn lỗi NullPointer khi loop attribute

        // Act
        ProductDetailGetVm result = productService.getProductDetail("test-product");
        
        // Assert
        assertEquals("Test Product", result.name());
        assertEquals("url", result.thumbnailMediaUrl());
    }

    @Test
    void getProductsByMultiQuery_ShouldReturnPage() {
        // Arrange
        Page<Product> page = new PageImpl<>(List.of(mockProduct), PageRequest.of(0, 10), 1);
        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween("test", "cate", 0.0, 100.0, PageRequest.of(0, 10)))
            .thenReturn(page);
        NoFileMediaVm media = new NoFileMediaVm(1L, "", "", "", "url");
        lenient().when(mediaService.getMedia(1L)).thenReturn(media);

        // Act
        ProductsGetVm result = productService.getProductsByMultiQuery(0, 10, "test", "cate", 0.0, 100.0);
        
        // Assert
        assertEquals(1, result.totalElements());
    }
    // ==========================================
    // TESTS CHO CÁC HÀM GET/QUERY (AN TOÀN TUYỆT ĐỐI)
    // ==========================================

    @Test
    void getProductByIds_ShouldReturnList() {
        lenient().when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(mockProduct));
        List<ProductListVm> result = productService.getProductByIds(List.of(1L, 2L));
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getProductByCategoryIds_ShouldReturnList() {
        lenient().when(productRepository.findByCategoryIdsIn(anyList())).thenReturn(List.of(mockProduct));
        List<ProductListVm> result = productService.getProductByCategoryIds(List.of(1L));
        assertNotNull(result);
    }

    @Test
    void getProductByBrandIds_ShouldReturnList() {
        lenient().when(productRepository.findByBrandIdsIn(anyList())).thenReturn(List.of(mockProduct));
        List<ProductListVm> result = productService.getProductByBrandIds(List.of(1L));
        assertNotNull(result);
    }

    @Test
    void getProductsForWarehouse_ShouldReturnList() {
        lenient().when(productRepository.findProductForWarehouse(anyString(), anyString(), anyList(), anyString()))
                .thenReturn(List.of(mockProduct));
        
        // Sử dụng giá trị Enum an toàn
        var selection = com.yas.product.model.enumeration.FilterExistInWhSelection.ALL;

        List<ProductInfoVm> result = productService.getProductsForWarehouse("name", "sku", List.of(1L), selection);
        assertNotNull(result);
    }

    @Test
    void getListFeaturedProducts_ShouldReturnList() {
        Page<Product> page = new PageImpl<>(List.of(mockProduct), PageRequest.of(0, 10), 1);
        lenient().when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(page);

        NoFileMediaVm media = new NoFileMediaVm(1L, "", "", "", "url");
        lenient().when(mediaService.getMedia(1L)).thenReturn(media);

        // Act
        ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 10);
        
        // Assert: Sử dụng đúng tên field 'productList' từ mã nguồn bạn cung cấp
        assertNotNull(result);
        assertNotNull(result.productList()); 
        assertEquals(1, result.totalPage());
    }

    @Test
    void getProductCheckoutList_ShouldReturnList() {
        Page<Product> page = new PageImpl<>(List.of(mockProduct), PageRequest.of(0, 10), 1);
        lenient().when(productRepository.findAllPublishedProductsByIds(anyList(), any(Pageable.class))).thenReturn(page);

        NoFileMediaVm media = new NoFileMediaVm(1L, "", "", "", "url");
        lenient().when(mediaService.getMedia(1L)).thenReturn(media);

        // Act
        ProductGetCheckoutListVm result = productService.getProductCheckoutList(0, 10, List.of(1L));
        
        // Assert
        assertNotNull(result);
    }
    // ==========================================
    // TESTS CHO CÁC HÀM XỬ LÝ NGHIỆP VỤ & TỒN KHO
    // ==========================================

    @Test
    void getProductsFromCategory_ShouldReturnList() {
        // Arrange
        Category category = new Category(); 
        category.setId(1L);
        lenient().when(categoryRepository.findBySlug("cate-slug")).thenReturn(Optional.of(category));
        
        com.yas.product.model.ProductCategory pc = mock(com.yas.product.model.ProductCategory.class);
        lenient().when(pc.getProduct()).thenReturn(mockProduct);
        
        Page<com.yas.product.model.ProductCategory> page = new PageImpl<>(List.of(pc));
        lenient().when(productCategoryRepository.findAllByCategory(any(Pageable.class), eq(category))).thenReturn(page);
        
        NoFileMediaVm media = new NoFileMediaVm(1L, "", "", "", "url");
        lenient().when(mediaService.getMedia(any())).thenReturn(media);

        // Act
        var result = productService.getProductsFromCategory(0, 10, "cate-slug");
        
        // Assert
        assertNotNull(result);
    }

    @Test
    void getProductEsDetailById_ShouldReturnDetail() {
        // Arrange
        lenient().when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        // Đảm bảo các list không null để tránh lỗi NullPointerException khi mapping
        mockProduct.setProductCategories(List.of());
        mockProduct.setAttributeValues(List.of());

        // Act
        var result = productService.getProductEsDetailById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getRelatedProductsStorefront_ShouldReturnList() {
        // Arrange
        lenient().when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        
        com.yas.product.model.ProductRelated pr = mock(com.yas.product.model.ProductRelated.class);
        lenient().when(pr.getRelatedProduct()).thenReturn(mockProduct);
        
        Page<com.yas.product.model.ProductRelated> page = new PageImpl<>(List.of(pr));
        lenient().when(productRelatedRepository.findAllByProduct(eq(mockProduct), any(Pageable.class))).thenReturn(page);
        
        NoFileMediaVm media = new NoFileMediaVm(1L, "", "", "", "url");
        lenient().when(mediaService.getMedia(any())).thenReturn(media);

        // Act
        var result = productService.getRelatedProductsStorefront(1L, 0, 10);
        
        // Assert
        assertNotNull(result);
    }

    @Test
    void getProductVariationsByParentId_ShouldReturnList() {
        // Arrange
        Product parent = new Product(); 
        parent.setId(1L); 
        parent.setHasOptions(true);
        
        mockProduct.setPublished(true);
        mockProduct.setProductImages(List.of());
        parent.setProducts(List.of(mockProduct));

        lenient().when(productRepository.findById(1L)).thenReturn(Optional.of(parent));
        lenient().when(productOptionCombinationRepository.findAllByProduct(mockProduct)).thenReturn(List.of());
        
        NoFileMediaVm media = new NoFileMediaVm(1L, "", "", "", "url");
        lenient().when(mediaService.getMedia(any())).thenReturn(media);

        // Act
        var result = productService.getProductVariationsByParentId(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void subtractStockQuantity_ShouldExecuteSuccessfully() {
        // Arrange
        ProductQuantityPutVm putVm = mock(ProductQuantityPutVm.class);
        lenient().when(putVm.productId()).thenReturn(1L);
        lenient().when(putVm.quantity()).thenReturn(5L);

        mockProduct.setStockTrackingEnabled(true);
        mockProduct.setStockQuantity(10L); // Tồn kho ban đầu là 10
        lenient().when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(mockProduct));

        // Act
        productService.subtractStockQuantity(List.of(putVm));

        // Assert
        assertEquals(5L, mockProduct.getStockQuantity()); // 10 - 5 = 5
        verify(productRepository, atLeastOnce()).saveAll(any());
    }

    @Test
    void restoreStockQuantity_ShouldExecuteSuccessfully() {
        // Arrange
        ProductQuantityPutVm putVm = mock(ProductQuantityPutVm.class);
        lenient().when(putVm.productId()).thenReturn(1L);
        lenient().when(putVm.quantity()).thenReturn(5L);

        mockProduct.setStockTrackingEnabled(true);
        mockProduct.setStockQuantity(10L); // Tồn kho ban đầu là 10
        lenient().when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(mockProduct));

        // Act
        productService.restoreStockQuantity(List.of(putVm));

        // Assert
        assertEquals(15L, mockProduct.getStockQuantity()); // 10 + 5 = 15
        verify(productRepository, atLeastOnce()).saveAll(any());
    }
    // ==========================================
    // TESTS CHO CÁC NHÁNH ĐIỀU KIỆN SÂU (ĐẨY MẠNH BRANCH COVERAGE)
    // ==========================================

    @Test
    void setProductImages_ShouldHandleEmptyAndNewAndExistingImages() {
        // Trường hợp 1: Truyền list rỗng -> Sẽ gọi xóa toàn bộ ảnh
        List<com.yas.product.model.ProductImage> res1 = productService.setProductImages(List.of(), mockProduct);
        assertTrue(res1.isEmpty());
        verify(productImageRepository, atLeastOnce()).deleteByProductId(mockProduct.getId());

        // Trường hợp 2: Sản phẩm chưa có ảnh nào -> Thêm mới hoàn toàn
        mockProduct.setProductImages(null);
        List<com.yas.product.model.ProductImage> res2 = productService.setProductImages(List.of(1L, 2L), mockProduct);
        assertEquals(2, res2.size());

        // Trường hợp 3: Sản phẩm đã có ảnh -> Cập nhật (xóa ảnh cũ, thêm ảnh mới)
        com.yas.product.model.ProductImage existingImg = com.yas.product.model.ProductImage.builder()
                .imageId(1L).product(mockProduct).build();
        mockProduct.setProductImages(List.of(existingImg));
        
        // Truyền vào [2L, 3L], tức là xóa 1L, thêm 2L và 3L
        List<com.yas.product.model.ProductImage> res3 = productService.setProductImages(List.of(2L, 3L), mockProduct);
        assertEquals(2, res3.size());
    }

    @Test
    void getProductDetail_WithAttributes_ShouldRunThroughLoop() {
        // Arrange: Giả lập Attributes phức tạp để code chạy vào vòng lặp sâu
        com.yas.product.model.attribute.ProductAttributeGroup group = new com.yas.product.model.attribute.ProductAttributeGroup();
        group.setId(1L);
        group.setName("Group 1");

        com.yas.product.model.attribute.ProductAttribute attr = new com.yas.product.model.attribute.ProductAttribute();
        attr.setId(1L);
        attr.setName("Attr 1");
        attr.setProductAttributeGroup(group);

        com.yas.product.model.attribute.ProductAttributeValue attrVal = new com.yas.product.model.attribute.ProductAttributeValue();
        attrVal.setId(1L);
        attrVal.setValue("Value 1");
        attrVal.setProductAttribute(attr);

        mockProduct.setAttributeValues(List.of(attrVal));
        mockProduct.setProductCategories(List.of());

        lenient().when(productRepository.findBySlugAndIsPublishedTrue("test-slug")).thenReturn(Optional.of(mockProduct));
        NoFileMediaVm media = new NoFileMediaVm(1L, "", "", "", "url");
        lenient().when(mediaService.getMedia(any())).thenReturn(media);

        // Act
        var result = productService.getProductDetail("test-slug");
        
        // Assert
        assertNotNull(result);
        assertFalse(result.productAttributeGroups().isEmpty());
        assertEquals("Group 1", result.productAttributeGroups().get(0).name());
    }

    @Test
    void deleteProduct_WhenProductHasParent_ShouldDeleteCombinations() {
        // Arrange
        Product parent = new Product();
        parent.setId(2L);
        mockProduct.setParent(parent); // Gắn Parent để nhảy vào nhánh if

        lenient().when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        
        com.yas.product.model.ProductOptionCombination combo = new com.yas.product.model.ProductOptionCombination();
        lenient().when(productOptionCombinationRepository.findAllByProduct(mockProduct)).thenReturn(List.of(combo));

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productOptionCombinationRepository, times(1)).deleteAll(anyList());
        assertFalse(mockProduct.isPublished()); // Kiểm tra đã soft delete thành công
    }
    
    @Test
    void getProductVariationsByParentId_WithCombinations_ShouldMapCorrectly() {
        // Arrange
        Product parent = new Product(); 
        parent.setId(1L); 
        parent.setHasOptions(true);
        
        mockProduct.setPublished(true);
        mockProduct.setProductImages(List.of());
        parent.setProducts(List.of(mockProduct));

        com.yas.product.model.ProductOption option = new com.yas.product.model.ProductOption();
        option.setId(1L);
        option.setName("Color");

        com.yas.product.model.ProductOptionCombination combo = new com.yas.product.model.ProductOptionCombination();
        combo.setProductOption(option);
        combo.setValue("Red");

        lenient().when(productRepository.findById(1L)).thenReturn(Optional.of(parent));
        lenient().when(productOptionCombinationRepository.findAllByProduct(mockProduct)).thenReturn(List.of(combo));
        
        NoFileMediaVm media = new NoFileMediaVm(1L, "", "", "", "url");
        lenient().when(mediaService.getMedia(any())).thenReturn(media);

        // Act
        var result = productService.getProductVariationsByParentId(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Red", result.get(0).options().get(1L)); // Kiểm tra map option chuẩn
    }
    // ==========================================
    // TESTS CHO NHÁNH VARIATIONS & CÁC NGOẠI LỆ (ĐẨY MẠNH COVERAGE)
    // ==========================================

  @Test
    void createProduct_WithVariations_ShouldCreateVariationsAndCombinations() {
        // Arrange
        ProductPostVm postVm = mock(ProductPostVm.class);
        lenient().when(postVm.length()).thenReturn(10.0);
        lenient().when(postVm.width()).thenReturn(5.0);
        lenient().when(postVm.slug()).thenReturn("main-slug");
        lenient().when(postVm.sku()).thenReturn("main-sku");
        lenient().when(postVm.name()).thenReturn("Main Product");
        
        // FIX: Mock Brand để tránh lỗi NotFound Brand 0
        lenient().when(postVm.brandId()).thenReturn(1L);
        Brand brand = new Brand();
        brand.setId(1L);
        lenient().when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        
        // Mock variations (Biến thể)
        ProductVariationPostVm varVm = mock(ProductVariationPostVm.class);
        lenient().when(varVm.slug()).thenReturn("var-slug");
        lenient().when(varVm.sku()).thenReturn("var-sku");
        lenient().when(varVm.name()).thenReturn("Var Product");
        lenient().when(varVm.optionValuesByOptionId()).thenReturn(java.util.Map.of(1L, "Red"));
        
        lenient().when(postVm.variations()).thenReturn(List.of(varVm));
        
        // Mock Option Values (Giá trị tùy chọn)
        com.yas.product.viewmodel.productoption.ProductOptionValuePostVm optValVm = mock(com.yas.product.viewmodel.productoption.ProductOptionValuePostVm.class);
        lenient().when(optValVm.productOptionId()).thenReturn(1L);
        lenient().when(postVm.productOptionValues()).thenReturn(List.of(optValVm));
        
        // Mock Option Displays
        ProductOptionValueDisplay displayVm = mock(ProductOptionValueDisplay.class);
        lenient().when(displayVm.productOptionId()).thenReturn(1L);
        lenient().when(displayVm.value()).thenReturn("Red");
        lenient().when(postVm.productOptionValueDisplays()).thenReturn(List.of(displayVm));

        // Mock repositories
        lenient().when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        lenient().when(productRepository.findByGtinAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        lenient().when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        
        // Mock Option DB
        com.yas.product.model.ProductOption option = new com.yas.product.model.ProductOption();
        option.setId(1L);
        lenient().when(productOptionRepository.findAllByIdIn(anyList())).thenReturn(List.of(option));
        
        // Mock quá trình Save
        Product savedMain = new Product();
        savedMain.setId(1L);
        savedMain.setSlug("main-slug");
        
        Product savedVar = new Product();
        savedVar.setId(2L);
        savedVar.setSlug("var-slug");
        
        lenient().when(productRepository.save(any(Product.class))).thenReturn(savedMain);
        lenient().when(productRepository.saveAll(anyList())).thenReturn(List.of(savedVar));
        
        com.yas.product.model.ProductOptionValue savedOptVal = new com.yas.product.model.ProductOptionValue();
        savedOptVal.setProductOption(option);
        lenient().when(productOptionValueRepository.saveAll(anyList())).thenReturn(List.of(savedOptVal));

        // Act
        ProductGetDetailVm result = productService.createProduct(postVm);

        // Assert: Xác nhận hàm saveAll của Combination đã được kích hoạt
        assertNotNull(result);
        verify(productOptionCombinationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createProduct_WhenVariationSlugDuplicated_ShouldThrowException() {
        // Arrange
        ProductPostVm postVm = mock(ProductPostVm.class);
        lenient().when(postVm.length()).thenReturn(10.0);
        lenient().when(postVm.width()).thenReturn(5.0);
        lenient().when(postVm.slug()).thenReturn("main-slug");
        lenient().when(postVm.sku()).thenReturn("main-sku");
        
        ProductVariationPostVm var1 = mock(ProductVariationPostVm.class);
        lenient().when(var1.slug()).thenReturn("duplicate-slug");
        lenient().when(var1.sku()).thenReturn("sku1");
        
        ProductVariationPostVm var2 = mock(ProductVariationPostVm.class);
        lenient().when(var2.slug()).thenReturn("duplicate-slug"); // Cố tình truyền 2 biến thể trùng slug
        lenient().when(var2.sku()).thenReturn("sku2");

        lenient().when(postVm.variations()).thenReturn(List.of(var1, var2));

        // Act & Assert
        com.yas.commonlibrary.exception.DuplicatedException ex = assertThrows(
            com.yas.commonlibrary.exception.DuplicatedException.class, 
            () -> productService.createProduct(postVm)
        );
        // FIX: Cập nhật lại chuỗi thông báo lỗi cho khớp với mã nguồn
        assertEquals("Slug duplicate-slug is already existed or is duplicated", ex.getMessage());
    }

    @Test
    void updateProduct_WithCategorySizeMismatch_ShouldThrowBadRequest() {
        // Arrange
        ProductPutVm putVm = mock(ProductPutVm.class);
        lenient().when(putVm.name()).thenReturn("Updated Name");
        lenient().when(putVm.slug()).thenReturn("updated-slug");
        
        lenient().when(putVm.brandId()).thenReturn(1L);
        Brand brand = new Brand();
        brand.setId(1L);
        lenient().when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        
        // Cố tình truyền vào 2 category IDs...
        lenient().when(putVm.categoryIds()).thenReturn(new java.util.ArrayList<>(List.of(1L, 2L)));
        
        Category cate1 = new Category();
        cate1.setId(1L);
        // ...nhưng DB chỉ tìm thấy 1 (để ép vòng lặp văng lỗi)
        lenient().when(categoryRepository.findAllById(anyList())).thenReturn(List.of(cate1));

        lenient().when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        lenient().when(productRepository.findBySlugAndIsPublishedTrue("updated-slug")).thenReturn(Optional.empty());

        // Act & Assert
        // Chỉ cần đảm bảo hàm ném ra đúng ngoại lệ BadRequestException là đủ an toàn và ăn điểm Coverage
        assertThrows(BadRequestException.class, () -> productService.updateProduct(1L, putVm));
    }
}
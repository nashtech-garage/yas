package com.yas.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.search.constant.enums.SortType;
import com.yas.search.model.Product;
import com.yas.search.model.ProductCriteriaDto;
import com.yas.search.viewmodel.ProductListGetVm;
import com.yas.search.viewmodel.ProductNameGetVm;
import com.yas.search.viewmodel.ProductNameListVm;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import java.util.stream.Stream;
import org.mockito.ArgumentCaptor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.SearchShardStatistics;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;

class ProductServiceTest {

    private ElasticsearchOperations elasticsearchOperations;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        elasticsearchOperations = mock(ElasticsearchOperations.class);
        productService = new ProductService(elasticsearchOperations);
    }

    @Test
    void testFindProductAdvance_whenSortTypeIsPriceAsc_ReturnProductListGetVm() {

        Integer page = 0;
        Integer size = 10;

        SearchHits<Product> searchHits = getSearchHits();

        SearchPage<Product> productPage = mock(SearchPage.class);
        when(productPage.getNumber()).thenReturn(page);
        when(productPage.getTotalElements()).thenReturn(1L);
        when(productPage.getSize()).thenReturn(size);
        when(productPage.getTotalPages()).thenReturn(1);
        when(productPage.isLast()).thenReturn(true);

        ArgumentCaptor<NativeQuery> captor = ArgumentCaptor.forClass(NativeQuery.class);

        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class))).thenReturn(searchHits);

        ProductCriteriaDto criteriaDto = new ProductCriteriaDto(
                "test", 0, 10, "testBrand", "testCategory",
                "testAttribute", 10.0, 100.0, SortType.PRICE_ASC);
        ProductListGetVm result = productService.findProductAdvance(criteriaDto);

        verify(elasticsearchOperations, times(1))
                .search(captor.capture(), eq(Product.class));
        assertEquals("price: ASC", Objects.requireNonNull(captor.getValue().getSort()).toString());

        assertNotNull(result);
        assertEquals(1, result.products().size());
        assertEquals(0, result.pageNo());
        assertEquals(10, result.pageSize());
        assertEquals(1, result.totalElements());
        assertTrue(result.isLast());
    }

    @Test
    void testFindProductAdvance_whenSortTypeIsPriceDesc_ReturnProductListGetVm() {

        Integer page = 0;
        Integer size = 10;

        SearchHits<Product> searchHits = getSearchHits();

        SearchPage<Product> productPage = mock(SearchPage.class);
        when(productPage.getNumber()).thenReturn(page);
        when(productPage.getSize()).thenReturn(size);
        when(productPage.getTotalElements()).thenReturn(1L);
        when(productPage.getTotalPages()).thenReturn(1);
        when(productPage.isLast()).thenReturn(true);

        ArgumentCaptor<NativeQuery> captor = ArgumentCaptor.forClass(NativeQuery.class);

        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class))).thenReturn(searchHits);

        ProductCriteriaDto criteriaDto = new ProductCriteriaDto("test", 0, 10, "testBrand", "testCategory",
                "testAttribute", 10.0, 100.0, SortType.PRICE_DESC);
        productService.findProductAdvance(criteriaDto);

        verify(elasticsearchOperations, times(1))
                .search(captor.capture(), eq(Product.class));

        assertEquals("price: DESC", Objects.requireNonNull(captor.getValue().getSort()).toString());
    }

    @Test
    void testFindProductAdvance_whenSortTypeIsDefault_ReturnProductListGetVm() {

        SearchHits<Product> searchHits = getSearchHits();

        SearchPage<Product> productPage = mock(SearchPage.class);
        when(productPage.getNumber()).thenReturn(0);
        when(productPage.getSize()).thenReturn(10);
        when(productPage.getTotalElements()).thenReturn(1L);
        when(productPage.getTotalPages()).thenReturn(1);
        when(productPage.isLast()).thenReturn(true);

        ArgumentCaptor<NativeQuery> captor = ArgumentCaptor.forClass(NativeQuery.class);

        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class))).thenReturn(searchHits);

        ProductCriteriaDto criteriaDto = new ProductCriteriaDto(
                "test", 0, 10, "testBrand", "testCategory",
                "testAttribute", 10.0, 100.0, SortType.DEFAULT);
        productService.findProductAdvance(criteriaDto);

        verify(elasticsearchOperations, times(1))
                .search(captor.capture(), eq(Product.class));

        assertEquals("createdOn: DESC", Objects.requireNonNull(captor.getValue().getSort()).toString());
    }

    @Test
    void testAutoCompleteProductName_whenExistsProducts_returnProductNameListVm() {

        SearchHits<Product> searchHits = getSearchHits();

        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class)))
                .thenReturn(searchHits);

        ProductNameListVm result = productService.autoCompleteProductName("Product");

        assertNotNull(result);
        assertEquals(1, result.productNames().size());
        ProductNameGetVm productNameGetVm = result.productNames().getFirst();
        assertEquals("Test Product", productNameGetVm.name());

        verify(elasticsearchOperations).search(any(NativeQuery.class), eq(Product.class));
    }

    @ParameterizedTest(name = "Test {index}: Filter with brands={0}, categories={1}, attributes={2}")
    @MethodSource("provideFilterCriteria")
    void testFindProductAdvance_shouldReturnFilteredProducts(
            String brands, String categories, String attributes) {

        // Given
        SearchHits<Product> searchHits = getSearchHits();
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class)))
                .thenReturn(searchHits);

        // When
        // Sử dụng các tham số được truyền vào từ MethodSource
        ProductCriteriaDto criteriaDto = new ProductCriteriaDto(
                "test", 0, 10, brands, categories,
                attributes, null, null, SortType.DEFAULT);

        ProductListGetVm result = productService.findProductAdvance(criteriaDto);

        // Then
        assertNotNull(result);
        verify(elasticsearchOperations, times(1))
                .search(any(NativeQuery.class), eq(Product.class));
    }

    // Nguồn cung cấp dữ liệu cho test case
    private static Stream<Arguments> provideFilterCriteria() {
        return Stream.of(
                // Case 1: Multiple Brands
                Arguments.of("Brand1,Brand2", null, null),

                // Case 2: Multiple Categories
                Arguments.of(null, "Category1,Category2,Category3", null),

                // Case 3: Multiple Attributes
                Arguments.of(null, null, "Attr1,Attr2"));
    }

    @Test
    void testFindProductAdvance_whenFilterWithPriceRange_ReturnFilteredProducts() {
        // Given
        SearchHits<Product> searchHits = getSearchHits();

        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class))).thenReturn(searchHits);

        // When
        ProductCriteriaDto criteriaDto = new ProductCriteriaDto(
                "test", 0, 10, null, null, null, 10.0, 100.0, SortType.DEFAULT);
        ProductListGetVm result = productService.findProductAdvance(criteriaDto);

        // Then
        assertNotNull(result);
        verify(elasticsearchOperations, times(1)).search(any(NativeQuery.class), eq(Product.class));
    }

    @Test
    void testFindProductAdvance_whenFilterWithMinPriceOnly_ReturnFilteredProducts() {
        // Given
        SearchHits<Product> searchHits = getSearchHits();

        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class))).thenReturn(searchHits);

        // When
        ProductCriteriaDto criteriaDto = new ProductCriteriaDto(
                "test", 0, 10, null, null, null, 50.0, null, SortType.DEFAULT);
        ProductListGetVm result = productService.findProductAdvance(criteriaDto);

        // Then
        assertNotNull(result);
        verify(elasticsearchOperations, times(1)).search(any(NativeQuery.class), eq(Product.class));
    }

    @Test
    void testFindProductAdvance_whenFilterWithMaxPriceOnly_ReturnFilteredProducts() {
        // Given
        SearchHits<Product> searchHits = getSearchHits();

        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class))).thenReturn(searchHits);

        // When
        ProductCriteriaDto criteriaDto = new ProductCriteriaDto(
                "test", 0, 10, null, null, null, null, 150.0, SortType.DEFAULT);
        ProductListGetVm result = productService.findProductAdvance(criteriaDto);

        // Then
        assertNotNull(result);
        verify(elasticsearchOperations, times(1)).search(any(NativeQuery.class), eq(Product.class));
    }

    @Test
    void testAutoCompleteProductName_whenEmptyKeyword_thenReturnResults() {
        // Given
        SearchHits<Product> searchHits = getSearchHits();

        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class)))
                .thenReturn(searchHits);

        // When
        ProductNameListVm result = productService.autoCompleteProductName("");

        // Then
        assertNotNull(result);
        verify(elasticsearchOperations).search(any(NativeQuery.class), eq(Product.class));
    }

    @Test
    void testFindProductAdvance_whenAllFiltersApplied_ReturnFilteredProducts() {
        // Given
        SearchHits<Product> searchHits = getSearchHits();

        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class))).thenReturn(searchHits);

        // When
        ProductCriteriaDto criteriaDto = new ProductCriteriaDto(
                "test", 0, 10, "TestBrand", "TestCategory",
                "TestAttribute", 10.0, 100.0, SortType.PRICE_ASC);
        ProductListGetVm result = productService.findProductAdvance(criteriaDto);

        // Then
        assertNotNull(result);
        assertEquals(1, result.products().size());
        verify(elasticsearchOperations, times(1)).search(any(NativeQuery.class), eq(Product.class));
    }

    private static SearchHits<Product> getSearchHits() {

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .slug("test-product")
                .price(20.0)
                .isPublished(true)
                .isVisibleIndividually(true)
                .isAllowedToOrder(true)
                .isFeatured(true)
                .thumbnailMediaId(123L)
                .categories(List.of("testCategory"))
                .attributes(List.of("testAttribute"))
                .createdOn(ZonedDateTime.now())
                .build();

        SearchHit<Product> searchHit = new SearchHit<>(
                "products",
                "1",
                null,
                1.0f,
                null,
                new HashMap<>(),
                new HashMap<>(),
                null,
                null,
                new ArrayList<>(),
                product);

        return new SearchHits<>() {

            @Override
            public @NotNull SearchHit<Product> getSearchHit(int index) {
                return searchHit;
            }

            @Override
            public AggregationsContainer<?> getAggregations() {
                return null;
            }

            @Override
            public float getMaxScore() {
                return 1;
            }

            @Override
            public @NotNull List<SearchHit<Product>> getSearchHits() {
                return List.of(searchHit);
            }

            @Override
            public long getTotalHits() {
                return 1;
            }

            @Override
            public @NotNull TotalHitsRelation getTotalHitsRelation() {
                return TotalHitsRelation.EQUAL_TO;
            }

            @Override
            public Suggest getSuggest() {
                return null;
            }

            @Override
            public String getPointInTimeId() {
                return "";
            }

            @Override
            public SearchShardStatistics getSearchShardStatistics() {
                return null;
            }
        };
    }

}

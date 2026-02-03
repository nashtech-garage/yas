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
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

        SearchHits<Product> searchHits =
            getSearchHits();

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

        SearchHits<Product> searchHits =
            getSearchHits();

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

        SearchHits<Product> searchHits =
            getSearchHits();

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

        SearchHits<Product> searchHits =
            getSearchHits();

        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class)))
            .thenReturn(searchHits);

        ProductNameListVm result = productService.autoCompleteProductName("Product");

        assertNotNull(result);
        assertEquals(1, result.productNames().size());
        ProductNameGetVm productNameGetVm = result.productNames().getFirst();
        assertEquals("Test Product", productNameGetVm.name());

        verify(elasticsearchOperations).search(any(NativeQuery.class), eq(Product.class));
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
            product
        );

        return new SearchHits<>(
        ) {

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
            public Duration getExecutionDuration() {
                return Duration.ZERO;
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
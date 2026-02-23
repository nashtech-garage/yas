package com.yas.search.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.search.ElasticsearchApplication;
import com.yas.search.model.ProductCriteriaDto;
import com.yas.search.service.ProductService;
import com.yas.search.viewmodel.ProductGetVm;
import com.yas.search.viewmodel.ProductListGetVm;
import com.yas.search.viewmodel.ProductNameGetVm;
import com.yas.search.viewmodel.ProductNameListVm;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductController.class)
@ContextConfiguration(classes = ElasticsearchApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testFindProductAdvance_whenProductListIsExists_thenReturnProductListGetVm() throws Exception {

        ProductGetVm productGetVm = new ProductGetVm(
                1L,
                "Sample Product",
                "sample-product",
                123L,
                29.99,
                true,
                true,
                false,
                true,
                ZonedDateTime.now());

        ProductListGetVm mockResponse = new ProductListGetVm(
                List.of(productGetVm), 0, 1, 1, 1, true, Map.of());

        when(productService.findProductAdvance(any(ProductCriteriaDto.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/storefront/catalog-search")
                .param("keyword", "test")
                .param("page", "0")
                .param("size", "12")
                .param("sortType", "DEFAULT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.products[0].id").value(productGetVm.id()))
                .andExpect(jsonPath("$.products[0].name").value(productGetVm.name()))
                .andExpect(jsonPath("$.products[0].slug").value(productGetVm.slug()));
    }

    @Test
    void testProductSearchAutoComplete_whenProductNameList_thenReturnProductNameListVm() throws Exception {

        ProductNameListVm mockResponse = new ProductNameListVm(
                List.of(new ProductNameGetVm("Product1")));
        when(productService.autoCompleteProductName(anyString())).thenReturn(mockResponse);

        mockMvc.perform(get("/storefront/search_suggest")
                .param("keyword", "test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productNames[0].name").value("Product1"));
    }

    @Test
    void testFindProductAdvance_whenEmptyResult_thenReturnEmptyList() throws Exception {
        // Given
        ProductListGetVm mockResponse = new ProductListGetVm(
                List.of(), 0, 12, 0, 0, true, Map.of());

        when(productService.findProductAdvance(any(ProductCriteriaDto.class)))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/storefront/catalog-search")
                .param("keyword", "nonexistent")
                .param("page", "0")
                .param("size", "12")
                .param("sortType", "DEFAULT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.products").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void testFindProductAdvance_whenWithFilters_thenReturnFilteredProducts() throws Exception {
        // Given
        ProductGetVm productGetVm = new ProductGetVm(
                1L, "Filtered Product", "filtered-product", 123L, 49.99,
                true, true, false, true, ZonedDateTime.now());

        ProductListGetVm mockResponse = new ProductListGetVm(
                List.of(productGetVm), 0, 1, 1, 1, true, Map.of());

        when(productService.findProductAdvance(any(ProductCriteriaDto.class)))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/storefront/catalog-search")
                .param("keyword", "test")
                .param("page", "0")
                .param("size", "12")
                .param("brand", "TestBrand")
                .param("category", "TestCategory")
                .param("attribute", "TestAttribute")
                .param("minPrice", "10.0")
                .param("maxPrice", "100.0")
                .param("sortType", "PRICE_ASC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.products[0].name").value("Filtered Product"));
    }

    @Test
    void testFindProductAdvance_whenSortByPriceDesc_thenReturnSortedProducts() throws Exception {
        // Given
        ProductListGetVm mockResponse = new ProductListGetVm(
                List.of(), 0, 12, 0, 0, true, Map.of());

        when(productService.findProductAdvance(any(ProductCriteriaDto.class)))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/storefront/catalog-search")
                .param("keyword", "test")
                .param("page", "0")
                .param("size", "12")
                .param("sortType", "PRICE_DESC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testProductSearchAutoComplete_whenEmptyKeyword_thenReturnResults() throws Exception {
        // Given
        ProductNameListVm mockResponse = new ProductNameListVm(List.of());
        when(productService.autoCompleteProductName(anyString())).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/storefront/search_suggest")
                .param("keyword", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productNames").isEmpty());
    }

    @Test
    void testProductSearchAutoComplete_whenMultipleResults_thenReturnAllResults() throws Exception {
        // Given
        ProductNameListVm mockResponse = new ProductNameListVm(
                List.of(
                        new ProductNameGetVm("Product1"),
                        new ProductNameGetVm("Product2"),
                        new ProductNameGetVm("Product3")));
        when(productService.autoCompleteProductName(anyString())).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/storefront/search_suggest")
                .param("keyword", "prod")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productNames.length()").value(3))
                .andExpect(jsonPath("$.productNames[0].name").value("Product1"))
                .andExpect(jsonPath("$.productNames[1].name").value("Product2"))
                .andExpect(jsonPath("$.productNames[2].name").value("Product3"));
    }

}
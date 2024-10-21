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

        ProductGetVm productGetVm = ProductGetVm.builder()
            .id(1L)
            .name("Sample Product")
            .slug("sample-product")
            .thumbnailId(123L)
            .price(29.99)
            .isAllowedToOrder(true)
            .isPublished(true)
            .isFeatured(false)
            .isVisibleIndividually(true)
            .createdOn(ZonedDateTime.now())
            .build();

        ProductListGetVm mockResponse = new ProductListGetVm(
            List.of(productGetVm), 0, 1, 1, 1, true, Map.of()
        );



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
            List.of(new ProductNameGetVm("Product1"))
        );
        when(productService.autoCompleteProductName(anyString())).thenReturn(mockResponse);

        mockMvc.perform(get("/storefront/search_suggest")
                .param("keyword", "test")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.productNames[0].name").value("Product1"));
    }

}
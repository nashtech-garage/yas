package com.yas.search.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.search.model.ProductCriteriaDto;
import com.yas.search.repository.ProductRepository;
import com.yas.search.service.ProductService;
import com.yas.search.viewmodel.ProductGetVm;
import com.yas.search.viewmodel.ProductListGetVm;
import com.yas.search.viewmodel.ProductNameGetVm;
import com.yas.search.viewmodel.ProductNameListVm;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProductController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductRepository productRepository; // Adding this unused mockBean to avoid error
                                                // due to making network call to ES during repository bean initialization.
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
            ZonedDateTime.now()
        );

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
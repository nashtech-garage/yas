package com.yas.product.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.product.model.Product;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProductOptionValueController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductOptionValueControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductOptionValueRepository productOptionValueRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @Test
    void testListProductOptionValues() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");

        ProductOption productOption = new ProductOption();
        productOption.setId(1L);
        productOption.setName("RAM");

        ProductOptionValue mockProductOptionValue
                = new ProductOptionValue();
        mockProductOptionValue.setId(1L);
        mockProductOptionValue.setValue("1GB");
        mockProductOptionValue.setProductOption(productOption);
        mockProductOptionValue.setProduct(product);

        when(productOptionValueRepository.findAll()).thenReturn(List.of(mockProductOptionValue));

        mockMvc.perform(get("/backoffice/product-option-values"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testListProductOptionValueOfProduct() throws Exception {
        Long productId = 1L;
        Product mockProduct = new Product();

        ProductOption productOption = new ProductOption();
        productOption.setId(1L);
        productOption.setName("RAM");

        ProductOptionValue mockProductOptionValue
                = new ProductOptionValue();
        mockProductOptionValue.setId(1L);
        mockProductOptionValue.setValue("1GB");
        mockProductOptionValue.setProductOption(productOption);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(productOptionValueRepository.findAllByProduct(mockProduct)).thenReturn(List.of(mockProductOptionValue));

        mockMvc.perform(get("/storefront/product-option-values/{productId}", productId))
                .andExpect(status().isOk());

        // Test for Not Found scenario
        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/storefront/product-option-values/2"))
                .andExpect(status().isNotFound());
    }
}
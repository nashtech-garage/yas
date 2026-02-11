package com.yas.product.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.product.model.Product;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.repository.ProductOptionCombinationRepository;
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

@WebMvcTest(controllers = ProductOptionCombinationController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductOptionCombinationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @Test
    void listProductOptionValueOfProduct() throws Exception {

        ProductOptionCombination mockProductOptionCombination
            = new ProductOptionCombination();
        mockProductOptionCombination.setId(1L);
        mockProductOptionCombination.setValue("1GB");
        ProductOption productOption = new ProductOption();
        productOption.setId(1L);
        productOption.setName("RAM");
        mockProductOptionCombination.setProductOption(productOption);

        ProductOptionCombination mockProductOptionCombination2
            = new ProductOptionCombination();
        mockProductOptionCombination2.setId(2L);
        mockProductOptionCombination2.setValue("RED");
        ProductOption productOption2 = new ProductOption();
        productOption2.setId(2L);
        productOption2.setName("COLOR");
        mockProductOptionCombination2.setProductOption(productOption2);
        Long productId = 1L;

        Product mockProduct = new Product();
        mockProduct.setId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(productOptionCombinationRepository.findAllByParentProductId(productId))
            .thenReturn(List.of(mockProductOptionCombination,
                mockProductOptionCombination,
                mockProductOptionCombination2));

        mockMvc.perform(get("/storefront/product-option-combinations/{productId}/values", productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));

        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/storefront/product-option-combinations/2/values"))
            .andExpect(status().isNotFound());
    }
}
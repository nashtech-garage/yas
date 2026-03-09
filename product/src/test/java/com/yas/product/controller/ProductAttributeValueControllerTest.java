package com.yas.product.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.product.model.Product;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.repository.ProductAttributeValueRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.productattribute.ProductAttributeValuePostVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ProductAttributeValueController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductAttributeValueControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductAttributeValueRepository productAttributeValueRepository;

    @MockitoBean
    private ProductAttributeRepository productAttributeRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @Test
    void testListProductAttributeValuesByProductId() throws Exception {
        Long productId = 1L;

        Product product = new Product();
        product.setId(productId);
        product.setName("Product name");

        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setId(1L);
        productAttribute.setName("Color");


        ProductAttributeValue productAttributeValue = new ProductAttributeValue();
        productAttributeValue.setId(1L);
        productAttributeValue.setValue("Red");
        productAttributeValue.setProduct(product);
        productAttributeValue.setProductAttribute(productAttribute);

        List<ProductAttributeValue> productAttributeValues =
                List.of(productAttributeValue);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productAttributeValueRepository.findAllByProduct(product))
                .thenReturn(productAttributeValues);

        mockMvc.perform(get("/backoffice/product-attribute-value/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(productAttributeValues.size())));
    }

    @Test
    void testUpdateProductAttributeValue() throws Exception {
        Long id = 1L;
        ProductAttributeValuePostVm productAttributeValuePostVm
                = new ProductAttributeValuePostVm(1L, 1L , "Red");

        when(productAttributeValueRepository.findById(id)).thenReturn(Optional.of(new ProductAttributeValue()));

        mockMvc.perform(put("/backoffice/product-attribute-value/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productAttributeValuePostVm)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCreateProductAttributeValue() throws Exception {
        ProductAttributeValuePostVm productAttributeValuePostVm
                = new ProductAttributeValuePostVm(1L, 1L, "Red");

        Product product = new Product();
        product.setId(1L);
        product.setName("Product name");

        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setId(1L);
        productAttribute.setName("Color");


        ProductAttributeValue productAttributeValue = new ProductAttributeValue();
        productAttributeValue.setId(1L);
        productAttributeValue.setValue("Red");
        productAttributeValue.setProduct(product);
        productAttributeValue.setProductAttribute(productAttribute);

        when(productRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Product()));
        when(productAttributeRepository.findById(anyLong())).thenReturn(Optional.of(new ProductAttribute()));
        when(productAttributeValueRepository.save(any(ProductAttributeValue.class))).thenReturn(productAttributeValue);

        mockMvc.perform(post("/backoffice/product-attribute-value")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productAttributeValuePostVm)))
                .andExpect(status().isCreated());
    }

    @Test
    void testDeleteProductAttributeValueById() throws Exception {
        Long id = 1L;

        when(productAttributeValueRepository.findById(id)).thenReturn(Optional.of(new ProductAttributeValue()));

        mockMvc.perform(delete("/backoffice/product-attribute-value/{id}", id))
                .andExpect(status().isNoContent());
    }
}
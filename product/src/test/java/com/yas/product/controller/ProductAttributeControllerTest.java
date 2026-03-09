package com.yas.product.controller;

import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.service.ProductAttributeService;
import com.yas.product.viewmodel.productattribute.ProductAttributeGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeListGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributePostVm;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductAttributeController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductAttributeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductAttributeRepository productAttributeRepository;

    @MockitoBean
    private ProductAttributeService productAttributeService;

    @InjectMocks
    private ProductAttributeController productAttributeController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testListProductAttributes() throws Exception {
        when(productAttributeRepository.findAll())
                .thenReturn(Arrays.asList(
                        createProductAttribute(1L, "Red", "Color"),
                        createProductAttribute(2L, "11 inch", "Size")
                ));

        mockMvc.perform(get("/backoffice/product-attribute"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Red"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("11 inch"));
    }

    @Test
    void testGetPageableProductAttributes() throws Exception {
        List<ProductAttributeGetVm> content = List.of(ProductAttributeGetVm.fromModel(
                createProductAttribute(1L, "Red", "Color")));
        ProductAttributeListGetVm pageableResponse = new ProductAttributeListGetVm(
                content, 0, 10, 1, 1, true);

        when(productAttributeService.getPageableProductAttributes(anyInt(), anyInt()))
                .thenReturn(pageableResponse);

        mockMvc.perform(get("/backoffice/product-attribute/paging?pageNo=0&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testGetProductAttribute() throws Exception {
        ProductAttribute productAttribute = createProductAttribute(1L, "Red", "Color");
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));

        mockMvc.perform(get("/backoffice/product-attribute/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Red"));
    }

    @Test
    void testGetProductAttributeNotFound() throws Exception {
        when(productAttributeRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/backoffice/product-attribute/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProductAttribute() throws Exception {
        ProductAttribute productAttribute = createProductAttribute(1L, "Red", "Color");
        ProductAttributePostVm postVm = new ProductAttributePostVm("Material", 2L);

        when(productAttributeService.save(any(ProductAttributePostVm.class))).thenReturn(productAttribute);

        mockMvc.perform(post("/backoffice/product-attribute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Red"));
    }

    @Test
    void testUpdateProductAttribute() throws Exception {
        ProductAttributePostVm postVm = new ProductAttributePostVm("Red", 2L);

        when(productAttributeService.update(any(ProductAttributePostVm.class), anyLong()))
                .thenReturn(new ProductAttribute());

        mockMvc.perform(put("/backoffice/product-attribute/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProductAttribute() throws Exception {
        ProductAttribute productAttribute = createProductAttribute(1L, "Red","Color");
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));

        doNothing().when(productAttributeRepository).deleteById(1L);

        mockMvc.perform(delete("/backoffice/product-attribute/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProductAttributeWithAttributeValues() throws Exception {
        ProductAttribute productAttribute = createProductAttribute(1L, "Red", "Color");
        productAttribute.setAttributeValues(Collections.singletonList(new ProductAttributeValue()));
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));

        mockMvc.perform(delete("/backoffice/product-attribute/1"))
                .andExpect(status().isBadRequest());
    }

    private ProductAttribute createProductAttribute(Long id, String name, String groupName) {
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setId(id);
        productAttribute.setName(name);
        productAttribute.setProductAttributeGroup(new ProductAttributeGroup());
        productAttribute.getProductAttributeGroup().setName(groupName);
        return productAttribute;
    }
}
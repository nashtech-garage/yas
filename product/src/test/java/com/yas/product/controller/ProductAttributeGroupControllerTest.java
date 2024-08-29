package com.yas.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.product.ProductApplication;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.service.ProductAttributeGroupService;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupListGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupPostVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductAttributeGroupController.class)
@ContextConfiguration(classes = ProductApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductAttributeGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductAttributeGroupService productAttributeGroupService;

    @MockBean
    private ProductAttributeGroupRepository productAttributeGroupRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testListProductAttributeGroups() throws Exception {
        List<ProductAttributeGroup> productAttributeGroups = List.of(
                createProductAttributeGroup(1L, "Color"),
                createProductAttributeGroup(2L, "Size"));

        Mockito.when(productAttributeGroupRepository.findAll()).thenReturn(productAttributeGroups);

        mockMvc.perform(get("/backoffice/product-attribute-groups")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[1].id").exists());
    }

    @Test
    void testGetPageableProductAttributeGroups() throws Exception {
        List<ProductAttributeGroupVm> productAttributeGroupVms = List.of(new ProductAttributeGroupVm(1L, "Color"));

        Mockito.when(productAttributeGroupService.getPageableProductAttributeGroups(1, 10))
                .thenReturn(new ProductAttributeGroupListGetVm(productAttributeGroupVms, 1, 10, 1, 1, true));

        mockMvc.perform(get("/backoffice/product-attribute-groups/paging?pageNo=1&pageSize=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProductAttributeGroup() throws Exception {
        ProductAttributeGroup productAttributeGroup = new ProductAttributeGroup();
        productAttributeGroup.setId(1L);
        productAttributeGroup.setName("Size");

        Mockito.when(productAttributeGroupRepository.findById(1L)).thenReturn(Optional.of(productAttributeGroup));

        mockMvc.perform(get("/backoffice/product-attribute-groups/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Size"));
    }

    @Test
    void testCreateProductAttributeGroup() throws Exception {
        ProductAttributeGroupPostVm productAttributeGroupPostVm = new ProductAttributeGroupPostVm("Color");

        ProductAttributeGroup productAttributeGroup = new ProductAttributeGroup();
        productAttributeGroup.setId(1L);
        productAttributeGroup.setName("Color");

        Mockito.doNothing().when(productAttributeGroupService).save(Mockito.any(ProductAttributeGroup.class));

        mockMvc.perform(post("/backoffice/product-attribute-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productAttributeGroupPostVm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Color"));
    }

    @Test
    void testUpdateProductAttributeGroup() throws Exception {
        ProductAttributeGroupPostVm productAttributeGroupPostVm = new ProductAttributeGroupPostVm("Color");

        ProductAttributeGroup productAttributeGroup = new ProductAttributeGroup();
        productAttributeGroup.setId(1L);
        productAttributeGroup.setName("Color");

        Mockito.when(productAttributeGroupRepository.findById(1L)).thenReturn(Optional.of(productAttributeGroup));
        Mockito.doNothing().when(productAttributeGroupService).save(Mockito.any(ProductAttributeGroup.class));

        mockMvc.perform(put("/backoffice/product-attribute-groups/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productAttributeGroupPostVm)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProductAttributeGroup() throws Exception {
        mockMvc.perform(delete("/backoffice/product-attribute-groups/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    private ProductAttributeGroup createProductAttributeGroup(Long id, String name) {
        ProductAttributeGroup productAttributeGroup = new ProductAttributeGroup();
        productAttributeGroup.setId(id);
        productAttributeGroup.setName(name);
        return productAttributeGroup;
    }
}
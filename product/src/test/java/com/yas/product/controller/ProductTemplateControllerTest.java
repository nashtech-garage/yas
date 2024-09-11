package com.yas.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.ProductApplication;
import com.yas.product.model.attribute.ProductTemplate;
import com.yas.product.repository.ProductTemplateRepository;
import com.yas.product.service.ProductTemplateService;
import com.yas.product.viewmodel.productattribute.ProductAttributeVm;
import com.yas.product.viewmodel.producttemplate.ProductAttributeTemplateGetVm;
import com.yas.product.viewmodel.producttemplate.ProductAttributeTemplatePostVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplateGetVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplateListGetVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplatePostVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplateVm;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductTemplateController.class)
@ContextConfiguration(classes = ProductApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductTemplateControllerTest {

    @MockBean
    private ProductTemplateService productTemplateService;

    @MockBean
    private ProductTemplateRepository productTemplateRepository;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testListProductTemplate() throws Exception {
        ProductTemplate template1 = ProductTemplate.builder()
                .id(1L)
                .name("Laptop template")
                .build();

        ProductTemplate template2 = ProductTemplate.builder()
                .id(2L)
                .name("Phone template")
                .build();

        when(productTemplateRepository.findAll()).thenReturn(List.of(
                template1, template2
        ));

        mockMvc.perform(get("/backoffice/product-template"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Laptop template"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Phone template"));
    }

    @Test
    void testGetPageableProductTemplates() throws Exception {
        ProductTemplateGetVm laptopTemplate = new ProductTemplateGetVm(1L, "Laptop Template");
        ProductTemplateGetVm phoneTemplate = new ProductTemplateGetVm(2L, "Phone Template");
        ProductTemplateListGetVm pageableResponse =
                new ProductTemplateListGetVm(
                        List.of(laptopTemplate, phoneTemplate), 0, 10, 2, 1, true
                );

        when(productTemplateService.getPageableProductTemplate(anyInt(), anyInt()))
                .thenReturn(pageableResponse);

        mockMvc.perform(get("/backoffice/product-template/paging?pageNo=0&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testGetProductTemplate() throws Exception {
        ProductAttributeVm productAttributeVm = new ProductAttributeVm(1L, "Color");
        ProductAttributeTemplateGetVm productAttributeTemplateGetVm = new ProductAttributeTemplateGetVm(productAttributeVm, 5);

        ProductTemplateVm template = new ProductTemplateVm(
                1L, "Laptop Template", List.of(productAttributeTemplateGetVm)
        );

        when(productTemplateService.getProductTemplate(1L)).thenReturn(template);

        mockMvc.perform(get("/backoffice/product-template/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop Template"));
    }

    @Test
    void testGetProductTemplateNotFound() throws Exception {
        when(productTemplateService.getProductTemplate(anyLong())).thenThrow(new NotFoundException("Template not found"));

        mockMvc.perform(get("/backoffice/product-template/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProductTemplate() throws Exception {
        ProductAttributeTemplatePostVm productAttributeTemplate =
                new ProductAttributeTemplatePostVm(123L,5);
        ProductAttributeVm productAttributeVm = new ProductAttributeVm(1L, "Color");
        ProductTemplatePostVm postVm = new ProductTemplatePostVm("Laptop Template", List.of(productAttributeTemplate));
        ProductAttributeTemplateGetVm productAttributeTemplateGetVm = new ProductAttributeTemplateGetVm(productAttributeVm, 5);

        ProductTemplateVm createdVm = new ProductTemplateVm(
                1L, "Laptop Template", List.of(productAttributeTemplateGetVm)
        );

        when(productTemplateService.saveProductTemplate(any(ProductTemplatePostVm.class))).thenReturn(createdVm);

        mockMvc.perform(post("/backoffice/product-template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop Template"));
    }

    @Test
    void testUpdateProductTemplate() throws Exception {
        ProductAttributeTemplatePostVm productAttributeTemplate =
                new ProductAttributeTemplatePostVm(123L,5);
        ProductTemplatePostVm postVm = new ProductTemplatePostVm("Laptop Template", List.of(productAttributeTemplate));

        doNothing().when(productTemplateService).updateProductTemplate(anyLong(), any(ProductTemplatePostVm.class));

        mockMvc.perform(put("/backoffice/product-template/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isNoContent());
    }
}

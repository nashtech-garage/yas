package com.yas.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.product.model.ProductOption;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.service.ProductOptionService;
import com.yas.product.viewmodel.productoption.ProductOptionGetVm;
import com.yas.product.viewmodel.productoption.ProductOptionListGetVm;
import com.yas.product.viewmodel.productoption.ProductOptionPostVm;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ProductOptionController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductOptionControllerTest {

    @MockitoBean
    private ProductOptionService productOptionService;

    @MockitoBean
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testListProductOption() throws Exception {
        ProductOption colorOption = createProductOption(1L, "color");
        ProductOption sizeOption = createProductOption(2L, "size");

        when(productOptionRepository.findAll()).thenReturn(List.of(
                colorOption, sizeOption
        ));

        mockMvc.perform(get("/backoffice/product-options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("color"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("size"));
    }

    @Test
    void testGetPageableProductOptions() throws Exception {
        ProductOptionGetVm productOptionGetVm = new ProductOptionGetVm(1L, "Color");
        ProductOptionListGetVm pageableResponse = new ProductOptionListGetVm(
                List.of(productOptionGetVm), 1, 10, 100,  1, true
        );

        when(productOptionService.getPageableProductOptions(anyInt(), anyInt()))
                .thenReturn(pageableResponse);

        mockMvc.perform(get("/backoffice/product-options/paging?pageNo=0&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testGetProductOption() throws Exception {
        ProductOption colorOption = new ProductOption();
        colorOption.setId(1L);
        colorOption.setName("color");

        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(colorOption));

        mockMvc.perform(get("/backoffice/product-options/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("color"));
    }

    @Test
    void testGetProductOptionNotFound() throws Exception {
        when(productOptionRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/backoffice/product-options/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProductOption() throws Exception {
        ProductOptionPostVm postVm = new ProductOptionPostVm("Color");
        ProductOption colorOption = createProductOption(1L, "Color");

        when(productOptionService.create(any(ProductOptionPostVm.class))).thenReturn(colorOption);

        mockMvc.perform(post("/backoffice/product-options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Color"));
    }

    @Test
    void testUpdateProductOption() throws Exception {
        ProductOptionPostVm postVm = new ProductOptionPostVm("Color");

        when(productOptionService.update(any(ProductOptionPostVm.class), anyLong())).thenReturn(createProductOption(1L, "Color"));

        mockMvc.perform(put("/backoffice/product-options/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProductOption() throws Exception {
        ProductOption colorOption = createProductOption(1L, "Color");
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(colorOption));
        doNothing().when(productOptionRepository).deleteById(1L);

        mockMvc.perform(delete("/backoffice/product-options/1"))
                .andExpect(status().isNoContent());

        verify(productOptionRepository, times(1)).deleteById(1L);
    }

    private static @NotNull ProductOption createProductOption(Long id, String optionName) {
        ProductOption colorOption = new ProductOption();
        colorOption.setId(id);
        colorOption.setName(optionName);
        return colorOption;
    }

}
package com.yas.inventory.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import com.yas.inventory.service.StockService;
import com.yas.inventory.viewmodel.stock.StockPostVm;
import com.yas.inventory.viewmodel.stock.StockQuantityUpdateVm;
import com.yas.inventory.viewmodel.stock.StockVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = StockController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class StockControllerTest {

    @MockitoBean
    private StockService stockService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testAddProductIntoWarehouse_whenRequestIsValid_thenReturnNoContent() throws Exception {

        StockPostVm stockPostVm1 = new StockPostVm(1L, 10L);
        StockPostVm stockPostVm2 = new StockPostVm(2L, 20L);
        List<StockPostVm> stockPostVms = List.of(stockPostVm1, stockPostVm2);

        mockMvc.perform(post("/backoffice/stocks")
                .contentType("application/json")
                .content(objectWriter.writeValueAsString(stockPostVms)))
            .andExpect(status().isNoContent());
    }

    @Test
    void testGetStocksByWarehouseIdAndProductNameAndSku_whenParametersAreProvided_thenReturnStocks() throws Exception {

        String productName = "Test Product";
        String productSku = "SKU123";

        StockVm stockVm1 = new StockVm(
            1L,
            1L,
            productName,
            productSku,
            100L,
            10L,
            1L
        );
        StockVm stockVm2 = new StockVm(
            2L,
            2L,
            productName,
            productSku,
            100L,
            10L,
            1L
        );
        List<StockVm> stocks = List.of(stockVm1, stockVm2);

        given(stockService.getStocksByWarehouseIdAndProductNameAndSku(1L, productName, productSku))
            .willReturn(stocks);

        this.mockMvc.perform(get("/backoffice/stocks")
                .param("warehouseId", String.valueOf(1L))
                .param("productName", productName)
                .param("productSku", productSku)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(stockVm1.id()))
            .andExpect(jsonPath("$[0].warehouseId").value(stockVm1.warehouseId()))
            .andExpect(jsonPath("$[0].productName").value(stockVm1.productName()))
            .andExpect(jsonPath("$[0].productSku").value(stockVm1.productSku()))
            .andExpect(jsonPath("$[0].quantity").value(stockVm1.quantity()))
            .andExpect(jsonPath("$[1].id").value(stockVm2.id()))
            .andExpect(jsonPath("$[1].warehouseId").value(stockVm2.warehouseId()))
            .andExpect(jsonPath("$[1].productName").value(stockVm2.productName()))
            .andExpect(jsonPath("$[1].productSku").value(stockVm2.productSku()))
            .andExpect(jsonPath("$[1].quantity").value(stockVm2.quantity()));
    }

    @Test
    void testUpdateProductQuantityInStock_whenRequestIsValid_thenReturnOk() throws Exception {

        StockQuantityUpdateVm stockQuantityUpdateVm = new StockQuantityUpdateVm(List.of());

        mockMvc.perform(MockMvcRequestBuilders.put("/backoffice/stocks")
                .contentType("application/json")
                .content(objectWriter.writeValueAsString(stockQuantityUpdateVm)))
            .andExpect(MockMvcResultMatchers.status().isOk());

    }

}
package com.yas.inventory.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.service.WarehouseService;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseDetailVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseListGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehousePostVm;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = WarehouseController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class WarehouseControllerTest {

    @MockitoBean
    private WarehouseService warehouseService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testGetProductByWarehouse_whenValidRequest_thenReturnProducts() throws Exception {
        Long warehouseId = 1L;
        String productName = "Sample Product";
        String productSku = "SKU123";
        FilterExistInWhSelection existStatus = FilterExistInWhSelection.YES;

        List<ProductInfoVm> products = Arrays.asList(
            new ProductInfoVm(1L, "Product1", "SKU1", true),
            new ProductInfoVm(2L, "Product2", "SKU2", true)
        );

        given(warehouseService.getProductWarehouse(warehouseId, productName, productSku, existStatus))
            .willReturn(products);

        this.mockMvc.perform(get("/backoffice/warehouses/{warehouseId}/products",
                    warehouseId)
                .param("productName", productName)
                .param("productSku", productSku)
                .param("existStatus", existStatus.name())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Product1"))
            .andExpect(jsonPath("$[0].sku").value("SKU1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Product2"))
            .andExpect(jsonPath("$[1].sku").value("SKU2"));
    }

    @Test
    void testGetPageableWarehouses_whenValidRequest_thenReturnPagedWarehouses() throws Exception {

        int pageNo = 1;
        int pageSize = 10;
        int totalElements = 12;

        List<WarehouseGetVm> warehouseContent
            = List.of(new WarehouseGetVm(1L, "A1"), new WarehouseGetVm(2L, "A2"));
        WarehouseListGetVm warehouseListGetVm
            = new WarehouseListGetVm(warehouseContent, pageNo, pageSize, totalElements, 2, true);

        given(warehouseService.getPageableWarehouses(pageNo, pageSize)).willReturn(warehouseListGetVm);

        this.mockMvc.perform(get("/backoffice/warehouses/paging")
                .param("pageNo", String.valueOf(pageNo))
                .param("pageSize", String.valueOf(pageSize))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.warehouseContent[0].id").value(1))
            .andExpect(jsonPath("$.warehouseContent[0].name").value("A1"))
            .andExpect(jsonPath("$.warehouseContent[1].id").value(2))
            .andExpect(jsonPath("$.warehouseContent[1].name").value("A2"))
            .andExpect(jsonPath("$.totalElements").value(12))
            .andExpect(jsonPath("$.pageNo").value(pageNo))
            .andExpect(jsonPath("$.pageSize").value(pageSize))
            .andExpect(jsonPath("$.totalPages").value(2));

    }

    @Test
    void testListWarehouses_whenWarehousesExist_thenReturnListOfWarehouses() throws Exception {

        given(warehouseService.findAllWarehouses()).willReturn(
            Arrays.asList(
                WarehouseGetVm.fromModel(Warehouse.builder().id(1L).name("Warehouse1").addressId(1L).build()),
                WarehouseGetVm.fromModel(Warehouse.builder().id(2L).name("Warehouse2").addressId(2L).build())
            )
        );

        this.mockMvc.perform(get("/backoffice/warehouses")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Warehouse1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Warehouse2"));
    }

    @Test
    void testGetWarehouse_whenWarehouseExists_thenReturnWarehouseDetails() throws Exception {

        Long warehouseId = 1L;
        WarehouseDetailVm warehouseDetailVm = new WarehouseDetailVm(
            warehouseId,
            "Main Warehouse",
            "John Doe",
            "123-456-7890",
            "123 Main St",
            "Suite 100",
            "Springfield",
            "12345",
            10L,
            5L,
            2L
        );

        given(warehouseService.findById(warehouseId)).willReturn(warehouseDetailVm);

        this.mockMvc.perform(get("/backoffice/warehouses/{id}", warehouseId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(warehouseId))
            .andExpect(jsonPath("$.name").value("Main Warehouse"))
            .andExpect(jsonPath("$.contactName").value("John Doe"));
    }

    @Test
    void testCreateWarehouse_whenRequestIsValid_thenReturn201() throws Exception {
        WarehousePostVm warehousePostVm = WarehousePostVm.builder()
            .name("name")
            .contactName("contactName")
            .phone("12345678")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .city("city")
            .zipCode("zipCode")
            .districtId(1L)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(warehousePostVm);
        given(warehouseService.create(warehousePostVm)).willReturn(new Warehouse());

        this.mockMvc.perform(post("/backoffice/warehouses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated());
    }

    @Test
    void testCreateWarehouse_whenPhoneIsOverMaxLength_thenReturnBadRequest() throws Exception {
        WarehousePostVm warehousePostVm = WarehousePostVm.builder()
            .name("name")
            .contactName("contactName")
            .phone("12345678912345678912345678")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .city("city")
            .zipCode("zipCode")
            .districtId(1L)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(warehousePostVm);

        this.mockMvc.perform(post("/backoffice/warehouses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateWarehouse_whenDistrictIsNull_thenReturnBadRequest() throws Exception {
        WarehousePostVm warehousePostVm = WarehousePostVm.builder()
            .name("name")
            .contactName("contactName")
            .phone("12345678")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .city("city")
            .zipCode("zipCode")
            .districtId(null)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(warehousePostVm);

        this.mockMvc.perform(post("/backoffice/warehouses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateWarehouse_whenContactNameIsBlank_thenReturnBadRequest() throws Exception {
        WarehousePostVm warehousePostVm = WarehousePostVm.builder()
            .name("")
            .contactName("contactName")
            .phone("12345678")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .city("city")
            .zipCode("zipCode")
            .districtId(1L)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(warehousePostVm);

        this.mockMvc.perform(post("/backoffice/warehouses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateWarehouse_whenRequestIsValid_thenReturn204() throws Exception {
        WarehousePostVm warehousePostVm = WarehousePostVm.builder()
            .name("name")
            .contactName("contactName")
            .phone("12345678")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .city("city")
            .zipCode("zipCode")
            .districtId(1L)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(warehousePostVm);

        this.mockMvc.perform(put("/backoffice/warehouses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateWarehouse_whenPhoneIsOverMaxLength_thenReturnBadRequest() throws Exception {
        WarehousePostVm warehousePostVm = WarehousePostVm.builder()
            .name("name")
            .contactName("contactName")
            .phone("12345678912345678912345678")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .city("city")
            .zipCode("zipCode")
            .districtId(1L)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(warehousePostVm);

        this.mockMvc.perform(put("/backoffice/warehouses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateWarehouse_whenDistrictIsNull_thenReturnBadRequest() throws Exception {
        WarehousePostVm warehousePostVm = WarehousePostVm.builder()
            .name("name")
            .contactName("contactName")
            .phone("12345678")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .city("city")
            .zipCode("zipCode")
            .districtId(null)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(warehousePostVm);

        this.mockMvc.perform(put("/backoffice/warehouses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateWarehouse_whenContactNameIsBlank_thenReturnBadRequest() throws Exception {
        WarehousePostVm warehousePostVm = WarehousePostVm.builder()
            .name("")
            .contactName("contactName")
            .phone("12345678")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .city("city")
            .zipCode("zipCode")
            .districtId(1L)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(warehousePostVm);

        this.mockMvc.perform(put("/backoffice/warehouses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

}
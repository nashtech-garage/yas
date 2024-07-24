package controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.inventory.InventoryApplication;
import com.yas.inventory.controller.WarehouseController;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.service.WarehouseService;
import com.yas.inventory.viewmodel.warehouse.WarehousePostVm;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = WarehouseController.class)
@ContextConfiguration(classes = InventoryApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class WarehouseControllerTest {

  @MockBean
  private WarehouseService warehouseService;

  @Autowired
  private MockMvc mockMvc;

  private ObjectWriter objectWriter;

  @BeforeEach
  void setUp() {
    objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
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

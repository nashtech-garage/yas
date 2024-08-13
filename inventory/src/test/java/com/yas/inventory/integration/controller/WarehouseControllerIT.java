package com.yas.inventory.integration.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.yas.inventory.integration.config.IntegrationTestConfiguration;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.service.LocationService;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressVm;
import com.yas.inventory.viewmodel.warehouse.WarehousePostVm;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WarehouseControllerIT extends AbstractControllerIT {

    @MockBean
    protected LocationService locationService;
    @Autowired
    private WarehouseRepository warehouseRepository;
    private Warehouse warehouse;

    @BeforeEach
    public void insertTestData() {
        warehouse = warehouseRepository.save(
            Instancio.create(Warehouse.class)
        );
    }

    @AfterEach
    public void clearTestData() {
        warehouseRepository.deleteAll();
    }

    @Test
    void test_listWarehouses_shouldReturn401NotAuthenticated_ifNotProvidedAccessToken() {
        given(getRequestSpecification())
            .when()
            .get("/v1/backoffice/warehouses")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .log().ifValidationFails();
    }

    @Test
    void test_listWarehouses_shouldReturnData_ifProvidedAccessToken() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .when()
            .get("/v1/backoffice/warehouses")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_getWarehouse_shouldReturnData_ifProvidedAccessToken() {
        AddressDetailVm addressDetailVm = Instancio.create(AddressDetailVm.class);
        when(locationService.getAddressById(anyLong())).thenReturn(addressDetailVm);

        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .pathParam("id", warehouse.getId())
            .when()
            .get("/v1/backoffice/warehouses/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("city", equalTo(addressDetailVm.city()))
            .log().ifValidationFails();
    }

    @Test
    void test_getWarehouse_shouldReturn404_ifProvidedAccessTokenAndWarehouseDetailIsNotFound() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .pathParam("id", 1000L)
            .when()
            .get("/v1/backoffice/warehouses/{id}")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getPage_shouldReturnData_ifProvidedAccessToken() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .param("pageNo", 0)
            .param("pageSize", 100)
            .when()
            .get("/v1/backoffice/warehouses/paging")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("totalElements", equalTo(1))
            .log().ifValidationFails();
    }

    @Test
    void test_create_shouldCreateResource_ifProvidedAccessToken() {
        WarehousePostVm body = Instancio.create(WarehousePostVm.class);
        AddressVm addressVm = Instancio.create(AddressVm.class);
        when(locationService.createAddress(any())).thenReturn(addressVm);

        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(body)
            .when()
            .post("/v1/backoffice/warehouses")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .log().ifValidationFails();
    }
}
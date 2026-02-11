package com.yas.rating.controller;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.anyLong;

import com.yas.commonlibrary.AbstractControllerIT;
import com.yas.commonlibrary.IntegrationTestConfiguration;
import com.yas.rating.model.Rating;
import com.yas.rating.repository.RatingRepository;
import com.yas.rating.service.CustomerService;
import com.yas.rating.service.OrderService;
import com.yas.rating.viewmodel.CustomerVm;
import com.yas.rating.viewmodel.OrderExistsByProductAndUserGetVm;
import com.yas.rating.viewmodel.RatingPostVm;
import java.util.HashSet;
import java.util.Set;
import org.hamcrest.Matchers;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@Import(IntegrationTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RatingControllerIT extends AbstractControllerIT {

    final String storeFrontUrl = "/v1/storefront/ratings";
    final String backOfficeUrl = "/v1/backoffice/ratings";

    @MockitoBean
    protected OrderService orderService;

    @MockitoBean
    protected CustomerService customerService;

    @Autowired
    private RatingRepository ratingRepository;

    Rating rating;

    @BeforeEach
    public void setUp() {
        rating = ratingRepository.save(Instancio.of(Rating.class)
                .ignore(Select.field((Rating::getId)))
                .generate(Select.field((Rating::getRatingStar)), gen -> gen.ints().min(0).max(5))
                .create());
        Set<Rating> set = new HashSet();
        for (int i = 0; i <= 5; i++) {
            set.add(Instancio.of(Rating.class)
                    .ignore(Select.field((Rating::getId)))
                    .generate(
                            Select.field((Rating::getRatingStar)),
                            gen -> gen.ints().min(0).max(5)
                    )
                    .create()
            );
        }
        ratingRepository.saveAll(set);
    }

    @AfterEach
    public void tearDown() {
        ratingRepository.deleteAll();
    }

    @Test
    void getRatingListWithFilter_whenNotProvidedAccessToken_shouldThrowNotAuthenticatedException() {
        given(getRequestSpecification())
                .when()
                .get(backOfficeUrl)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().ifValidationFails();
    }

    @Test
    void getRatingListWithFilter_whenProvidedAccessToken_shouldReturnData() {
        given(getRequestSpecification())
                .auth().oauth2(getAccessToken("admin", "admin"))
                .when()
                .get(backOfficeUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().ifValidationFails();
    }

    @Test
    void testDeleteRating_whenNotProvidedAccessToken_shouldThrowNotAuthenticatedException() throws Exception {
        given(getRequestSpecification())
                .when()
                .delete(backOfficeUrl + "/" + rating.getId())
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().ifValidationFails();
    }

    @Test
    void testDeleteRating_whenValidId_shouldReturn200() throws Exception {
        given(getRequestSpecification())
                .auth().oauth2(getAccessToken("admin", "admin"))
                .when()
                .delete(backOfficeUrl + "/" + rating.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().ifValidationFails();
    }

    @Test
    void testDeleteRating_whenInvalidId_shouldReturnNotFoundException() throws Exception {
        given(getRequestSpecification())
                .auth().oauth2(getAccessToken("admin", "admin"))
                .when()
                .delete(backOfficeUrl + "/0")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().ifValidationFails();
    }

    @Test
    void testStorefrontGetRatingList_whenNotProvideAccessToken_shouldThrowNotAuthenticatedException() throws Exception {
        given(getRequestSpecification())
                .when()
                .get(storeFrontUrl + "/products/" + rating.getProductId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().ifValidationFails();
    }

    @Test
    void testStorefrontGetRatingList_ShouldReturnList() throws Exception {
        given(getRequestSpecification())
                .auth().oauth2(getAccessToken("admin", "admin"))
                .when()
                .get(storeFrontUrl + "/products/" + rating.getProductId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().ifValidationFails();
    }

    @Test
    void TestCreateRating_WhenValidRequest_ShouldSuccess() throws Exception {
        CustomerVm customer = Instancio.create(CustomerVm.class);
        RatingPostVm vm = new RatingPostVm("rating content", 5, rating.getProductId(), rating.getProductName());
        when(orderService.checkOrderExistsByProductAndUserWithStatus(anyLong())).thenReturn(new OrderExistsByProductAndUserGetVm(true));
        when(customerService.getCustomer()).thenReturn(customer);
        given(getRequestSpecification())
                .auth().oauth2(getAccessToken("admin", "admin"))
                .when()
                .body(vm)
                .post(storeFrontUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().ifValidationFails();
    }

    @Test
    void TestCreateRating_WhenRatingExisted_ShouldThrowException() throws Exception {
        RatingPostVm vm = new RatingPostVm("rating content", 5, rating.getProductId(), rating.getProductName());
        when(orderService.checkOrderExistsByProductAndUserWithStatus(anyLong())).thenReturn(new OrderExistsByProductAndUserGetVm(false));
        given(getRequestSpecification())
                .auth().oauth2(getAccessToken("admin", "admin"))
                .when()
                .body(vm)
                .post(storeFrontUrl)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().ifValidationFails();
    }

    @Test
    void TestCreateRating_WhenInvalidUser_ShouldThrowNotFoundException() throws Exception {
        RatingPostVm vm = new RatingPostVm("rating content", 5, rating.getProductId(), rating.getProductName());
        when(orderService.checkOrderExistsByProductAndUserWithStatus(anyLong())).thenReturn(new OrderExistsByProductAndUserGetVm(true));
        when(customerService.getCustomer()).thenReturn(null);
        given(getRequestSpecification())
                .auth().oauth2(getAccessToken("admin", "admin"))
                .when()
                .body(vm)
                .post(storeFrontUrl)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().ifValidationFails();
    }

    @Test
    void TestGetAverageStarOfProduct_ShouldReturnSuccess() throws Exception {
        given(getRequestSpecification())
                .auth().oauth2(getAccessToken("admin", "admin"))
                .when()
                .get(storeFrontUrl + "/product/{productId}/average-star", rating.getProductId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().ifValidationFails();
    }

    @Test
    void testGetAverageStarOfProduct_whenProductNotExisted_shouldReturnZero() throws Exception {
        given(getRequestSpecification())
                .auth().oauth2(getAccessToken("admin", "admin"))
                .when()
                .get(storeFrontUrl + "/product/{productId}/average-star", 0)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("", Matchers.equalTo(Float.valueOf(0)))
                .log().ifValidationFails();
    }
}
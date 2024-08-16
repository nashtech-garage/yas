package com.yas.rating.service;

import com.yas.rating.RatingApplication;
import com.yas.rating.exception.AccessDeniedException;
import com.yas.rating.exception.NotFoundException;
import com.yas.rating.exception.ResourceExistedException;
import com.yas.rating.model.Rating;
import com.yas.rating.repository.RatingRepository;
import com.yas.rating.utils.AuthenticationUtils;
import com.yas.rating.viewmodel.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = RatingApplication.class)
class RatingServiceTest {

    private final String userId = "user1";
    @Autowired
    private RatingRepository ratingRepository;
    @MockBean
    private CustomerService customerService;
    @MockBean
    private OrderService orderService;
    @Autowired
    private RatingService ratingService;
    private List<Rating> ratingList;

    @BeforeEach
    void setUp() {
        ratingList = List.of(
                Rating.builder()
                        .content("comment 1")
                        .ratingStar(4)
                        .productId(1L)
                        .productName("product1")
                        .firstName("Duy")
                        .lastName("Nguyen")
                        .build(),
                Rating.builder()
                        .content("comment 2")
                        .ratingStar(2)
                        .productId(1L)
                        .productName("product1")
                        .firstName("Hai")
                        .lastName("Le")
                        .build(),
                Rating.builder()
                        .content("comment 3")
                        .ratingStar(3)
                        .productId(2L)
                        .productName("product2")
                        .firstName("Cuong")
                        .lastName("Tran")
                        .build()
        );
        ratingRepository.saveAll(ratingList);
    }

    @AfterEach
    void tearDown() {
        ratingRepository.deleteAll();
        SecurityContextHolder.clearContext();
    }

    @Test
    void getRatingList_ValidProductId_ShouldSuccess() {
        int totalPage = 1;
        int pageNo = 0;
        int pageSize = 10;

        RatingListVm actualResponse = ratingService.getRatingListByProductId(1L, pageNo, pageSize);
        assertEquals(totalPage, actualResponse.totalPages());
        assertEquals(2, actualResponse.totalElements());
        assertEquals(2, actualResponse.ratingList().size());
    }

    @Test
    void getRatingList_NotExistedProductId_ShouldReturnEmptyList() {
        int pageNo = 0;
        int pageSize = 10;

        RatingListVm actualResponse = ratingService.getRatingListByProductId(0L, pageNo, pageSize);
        assertEquals(0, actualResponse.ratingList().size());
        assertEquals(0, actualResponse.totalPages());
        assertEquals(0, actualResponse.totalElements());
    }

    @Test
    void getRatingListWithFilter_ValidFilterData_ShouldReturnSuccess() {
        String proName = "product2";
        String firstName = "Cuong";
        String lastName = "Tran";
        String cusName = firstName + " " + lastName;
        String message = "comment 3";
        ZonedDateTime createdFrom = ZonedDateTime.now().minusDays(30);
        ZonedDateTime createdTo = ZonedDateTime.now().plusDays(30);
        int totalPage = 1;
        int pageNo = 0;
        int pageSize = 10;
        RatingListVm actualResponse = ratingService.getRatingListWithFilter(proName, cusName, message, createdFrom, createdTo, pageNo, pageSize);
        assertEquals(totalPage, actualResponse.totalPages());
        assertEquals(1, actualResponse.totalElements());
        assertEquals(proName, actualResponse.ratingList().getFirst().productName());
        assertEquals(message, actualResponse.ratingList().getFirst().content());
        assertEquals(firstName, actualResponse.ratingList().getFirst().firstName());
        assertEquals(lastName, actualResponse.ratingList().getFirst().lastName());
    }

    @Test
    void createRating_ValidRatingData_ShouldSuccess() {
        Jwt jwt = mock(Jwt.class);
        JwtAuthenticationToken authentication = mock(JwtAuthenticationToken.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getToken()).thenReturn(jwt);
        when(authentication.getName()).thenReturn(userId);
        when(jwt.getSubject()).thenReturn(userId);
        when(orderService.checkOrderExistsByProductAndUserWithStatus(anyLong())).
                thenReturn(new OrderExistsByProductAndUserGetVm(true));
        when(customerService.getCustomer()).thenReturn(new CustomerVm(userId, null, "Cuong", "Tran"));

        RatingPostVm ratingPostVm = RatingPostVm.builder().content("comment 4").productName("product3").star(4).productId(3L).build();
        RatingVm ratingVm = ratingService.createRating(ratingPostVm);
        assertEquals(ratingPostVm.productName(), ratingVm.productName());
        assertEquals(ratingPostVm.content(), ratingVm.content());
        assertEquals(ratingPostVm.star(), ratingVm.star());
    }

    @Test
    void createRating_InvalidAuthorization_ShouldThrowAccessDeniedException() {
        RatingPostVm ratingPostVm = RatingPostVm.builder().content("comment 4").productName("product3").star(4).productId(3L).build();

        Jwt jwt = mock(Jwt.class);
        JwtAuthenticationToken authentication = mock(JwtAuthenticationToken.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getToken()).thenReturn(jwt);
        when(authentication.getName()).thenReturn(userId);
        when(jwt.getSubject()).thenReturn(userId);
        when(orderService.checkOrderExistsByProductAndUserWithStatus(anyLong())).thenReturn(new OrderExistsByProductAndUserGetVm(false));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> ratingService.createRating(ratingPostVm));

        assertEquals("ACCESS_DENIED", exception.getMessage());
    }

    @Test
    void createRating_ExistedRating_ShouldThrowResourceExistedException() {
        RatingPostVm ratingPostVm = RatingPostVm.builder().productId(1L).content("comment 4").productName("product3").star(4).build();

        Jwt jwt = mock(Jwt.class);
        JwtAuthenticationToken authentication = mock(JwtAuthenticationToken.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getToken()).thenReturn(jwt);
        when(authentication.getName()).thenReturn("");
        when(jwt.getSubject()).thenReturn("");

        when(orderService.checkOrderExistsByProductAndUserWithStatus(anyLong())).thenReturn(new OrderExistsByProductAndUserGetVm(true));

        ResourceExistedException exception = assertThrows(ResourceExistedException.class,
                () -> ratingService.createRating(ratingPostVm));

        assertEquals("Resource already existed", exception.getMessage());
    }

    @Test
    void deleteRating_ValidRatingId_ShouldSuccess() {
        Long id = ratingRepository.findAll().getFirst().getId();
        ratingService.deleteRating(id);
        Optional<Rating> rating = ratingRepository.findById(id);
        assertFalse(rating.isPresent());
    }

    @Test
    void deleteRating_InvalidRatingId_ShouldThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> ratingService.deleteRating(0L));
        assertEquals("RATING 0 is not found", exception.getMessage());
    }

    @Test
    void calculateAverageStar_ValidProductId_ShouldSuccess() {
        Double averageStar = ratingService.calculateAverageStar(1L);
        assertEquals(3, averageStar);
    }

    @Test
    void calculateAverageStar_InvalidProductId_ShouldReturnZero() {
        Double averageStar = ratingService.calculateAverageStar(0L);
        assertEquals(0, averageStar);
    }
}

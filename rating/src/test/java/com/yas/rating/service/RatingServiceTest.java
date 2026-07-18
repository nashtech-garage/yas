package com.yas.rating.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.ResourceExistedException;
import com.yas.rating.model.Rating;
import com.yas.rating.repository.RatingRepository;
import com.yas.rating.viewmodel.CustomerVm;
import com.yas.rating.viewmodel.OrderExistsByProductAndUserGetVm;
import com.yas.rating.viewmodel.RatingListVm;
import com.yas.rating.viewmodel.RatingPostVm;
import com.yas.rating.viewmodel.RatingVm;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    private final String userId = "user1";
    
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private CustomerService customerService;
    @Mock
    private OrderService orderService;
    
    @InjectMocks
    private RatingService ratingService;

    private List<Rating> ratingList;

    @BeforeEach
    void setUp() {
        ratingList = List.of(
            Rating.builder()
                .id(1L)
                .content("comment 1")
                .ratingStar(4)
                .productId(1L)
                .productName("product1")
                .firstName("Duy")
                .lastName("Nguyen")
                .build(),
            Rating.builder()
                .id(2L)
                .content("comment 2")
                .ratingStar(2)
                .productId(1L)
                .productName("product1")
                .firstName("Hai")
                .lastName("Le")
                .build(),
            Rating.builder()
                .id(3L)
                .content("comment 3")
                .ratingStar(3)
                .productId(2L)
                .productName("product2")
                .firstName("Cuong")
                .lastName("Tran")
                .build()
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getRatingList_ValidProductId_ShouldSuccess() {
        int pageNo = 0;
        int pageSize = 10;
        Page<Rating> page = new PageImpl<>(ratingList.subList(0, 2));
        
        when(ratingRepository.findByProductId(anyLong(), any(Pageable.class))).thenReturn(page);

        RatingListVm actualResponse = ratingService.getRatingListByProductId(1L, pageNo, pageSize);
        
        assertEquals(1, actualResponse.totalPages());
        assertEquals(2, actualResponse.totalElements());
        assertEquals(2, actualResponse.ratingList().size());
    }

    @Test
    void getRatingList_NotExistedProductId_ShouldReturnEmptyList() {
        int pageNo = 0;
        int pageSize = 10;
        Page<Rating> page = new PageImpl<>(List.of());
        
        when(ratingRepository.findByProductId(anyLong(), any(Pageable.class))).thenReturn(page);

        RatingListVm actualResponse = ratingService.getRatingListByProductId(0L, pageNo, pageSize);
        
        assertEquals(0, actualResponse.ratingList().size());
        assertEquals(1, actualResponse.totalPages());
        assertEquals(0, actualResponse.totalElements());
    }

    @Test
    void getRatingListWithFilter_ValidFilterData_ShouldReturnSuccess() {
        String proName = "product2";
        String cusName = "Cuong Tran";
        String message = "comment 3";
        ZonedDateTime createdFrom = ZonedDateTime.now().minusDays(30);
        ZonedDateTime createdTo = ZonedDateTime.now().plusDays(30);
        int pageNo = 0;
        int pageSize = 10;
        
        Page<Rating> page = new PageImpl<>(List.of(ratingList.get(2)));
        when(ratingRepository.getRatingListWithFilter(anyString(), anyString(), anyString(), any(), any(), any(Pageable.class))).thenReturn(page);

        RatingListVm actualResponse = ratingService.getRatingListWithFilter(proName, cusName, message, createdFrom, createdTo, pageNo, pageSize);
        
        assertEquals(1, actualResponse.totalPages());
        assertEquals(1, actualResponse.totalElements());
        assertEquals(proName, actualResponse.ratingList().getFirst().productName());
        assertEquals(message, actualResponse.ratingList().getFirst().content());
        assertEquals("Cuong", actualResponse.ratingList().getFirst().firstName());
        assertEquals("Tran", actualResponse.ratingList().getFirst().lastName());
    }

    @Test
    void createRating_ValidRatingData_ShouldSuccess() {
        Jwt jwt = mock(Jwt.class);
        JwtAuthenticationToken authentication = mock(JwtAuthenticationToken.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        org.mockito.Mockito.lenient().when(authentication.getToken()).thenReturn(jwt);
        org.mockito.Mockito.lenient().when(authentication.getName()).thenReturn(userId);
        org.mockito.Mockito.lenient().when(jwt.getSubject()).thenReturn(userId);
        when(orderService.checkOrderExistsByProductAndUserWithStatus(anyLong())).
                thenReturn(new OrderExistsByProductAndUserGetVm(true));
        when(customerService.getCustomer()).thenReturn(new CustomerVm(userId, null, "Cuong", "Tran"));

        RatingPostVm ratingPostVm = RatingPostVm.builder().content("comment 4").productName("product3").star(4).productId(3L).build();
        
        when(ratingRepository.save(any(Rating.class))).thenAnswer(inv -> inv.getArgument(0));

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
        org.mockito.Mockito.lenient().when(authentication.getToken()).thenReturn(jwt);
        org.mockito.Mockito.lenient().when(authentication.getName()).thenReturn(userId);
        org.mockito.Mockito.lenient().when(jwt.getSubject()).thenReturn(userId);
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
        org.mockito.Mockito.lenient().when(authentication.getToken()).thenReturn(jwt);
        org.mockito.Mockito.lenient().when(authentication.getName()).thenReturn("");
        org.mockito.Mockito.lenient().when(jwt.getSubject()).thenReturn("");

        when(orderService.checkOrderExistsByProductAndUserWithStatus(anyLong())).thenReturn(new OrderExistsByProductAndUserGetVm(true));
        when(ratingRepository.existsByCreatedByAndProductId(anyString(), anyLong())).thenReturn(true);

        ResourceExistedException exception = assertThrows(ResourceExistedException.class,
                () -> ratingService.createRating(ratingPostVm));

        assertEquals("Resource already existed", exception.getMessage());
    }

    @Test
    void deleteRating_ValidRatingId_ShouldSuccess() {
        Long id = 1L;
        when(ratingRepository.findById(id)).thenReturn(Optional.of(ratingList.getFirst()));
        
        ratingService.deleteRating(id);
        
        verify(ratingRepository).delete(ratingList.getFirst());
    }

    @Test
    void deleteRating_InvalidRatingId_ShouldThrowNotFoundException() {
        when(ratingRepository.findById(0L)).thenReturn(Optional.empty());
        
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> ratingService.deleteRating(0L));
        assertEquals("RATING 0 is not found", exception.getMessage());
    }

    @Test
    void calculateAverageStar_ValidProductId_ShouldSuccess() {
        List<Object[]> mockResult = java.util.Collections.singletonList(new Object[]{6, 2});
        when(ratingRepository.getTotalStarsAndTotalRatings(1L)).thenReturn(mockResult);
        
        Double averageStar = ratingService.calculateAverageStar(1L);
        assertEquals(3.0, averageStar);
    }

    @Test
    void calculateAverageStar_InvalidProductId_ShouldReturnZero() {
        List<Object[]> mockResult = java.util.Collections.singletonList(new Object[]{null, 0});
        when(ratingRepository.getTotalStarsAndTotalRatings(0L)).thenReturn(mockResult);
        
        Double averageStar = ratingService.calculateAverageStar(0L);
        assertEquals(0.0, averageStar);
    }

    @Test
    void testGetLatestProducts_WhenHasListProductListVm_returnListProductListVm() {
        when(ratingRepository.getLatestRatings(any(Pageable.class))).thenReturn(ratingList.subList(0, 2));

        List<RatingVm> ratingListResult = ratingService.getLatestRatings(2);
        assertEquals(2, ratingListResult.size());
        assertEquals(ratingList.getFirst().getContent(), ratingListResult.getFirst().content());
        assertEquals(ratingList.get(1).getContent(), ratingListResult.get(1).content());
    }

    @Test
    void testGetLatestRatings_WhenCountLessThen1_returnEmpty() {
        List<RatingVm>  newResponse = ratingService.getLatestRatings(-1);
        assertEquals(0, newResponse.size());
    }

    @Test
    void testGetLatestRatings_WhenCountIs0_returnEmpty() {
        List<RatingVm>  newResponse = ratingService.getLatestRatings(0);
        assertEquals(0, newResponse.size());
    }

    @Test
    void testGetLatestRatings_WhenProductsEmpty_returnEmpty() {
        when(ratingRepository.getLatestRatings(any(Pageable.class))).thenReturn(List.of());
        List<RatingVm>  newResponse = ratingService.getLatestRatings(5);
        assertEquals(0, newResponse.size());
    }
}

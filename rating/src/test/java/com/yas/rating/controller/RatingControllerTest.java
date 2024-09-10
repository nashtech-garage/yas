package com.yas.rating.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.rating.RatingApplication;
import com.yas.rating.exception.NotFoundException;
import com.yas.rating.service.RatingService;
import com.yas.rating.utils.Constants;
import com.yas.rating.viewmodel.RatingListVm;
import com.yas.rating.viewmodel.RatingPostVm;
import com.yas.rating.viewmodel.RatingVm;
import com.yas.rating.viewmodel.ResponeStatusVm;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RatingController.class)
@ContextConfiguration(classes = RatingApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class RatingControllerTest {

    @MockBean
    private RatingService ratingService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;
    private RatingVm ratingVm;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        ratingVm = new RatingVm(1L, "rating1", 5, 1L, "product1", "nhat1", "Nhat", "Tran", ZonedDateTime.now());
    }

    @Test
    void testGetRatingListWithFilter() throws Exception {
        String productName = "product1";
        String customerName = "Nhat Tran";
        String message = "comment 1";
        ZonedDateTime createdFrom = ZonedDateTime.of(
                LocalDate.of(1970, 1, 1),
                LocalTime.of(0, 0),
                ZoneId.systemDefault());
        ZonedDateTime createdTo = ZonedDateTime.now();

        when(ratingService.getRatingListWithFilter(
                anyString(),
                anyString(),
                anyString(),
                any(ZonedDateTime.class),
                any(ZonedDateTime.class),
                anyInt(),
                anyInt())).thenReturn(new RatingListVm(List.of(), 0, 0));

        this.mockMvc.perform(get("/backoffice/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .param("productName", productName)
                .param("customerName", customerName)
                .param("message", message)
                .param("createdFrom", createdFrom.toString())
                .param("createdTo", createdTo.toString())
                .param("pageNo", "0")
                .param("pageSize", "10")
        )
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteRating_WhenValidId_ShouldReturn200() throws Exception {
        when(ratingService.deleteRating(anyLong())).thenReturn(new ResponeStatusVm(
                "Delete Rating",
                Constants.Message.SUCCESS_MESSAGE,
                HttpStatus.OK.toString())
        );

        this.mockMvc.perform(delete("/backoffice/ratings/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());

        verify(ratingService, times(1)).deleteRating(anyLong());
    }

    @Test
    void testDeleteRating_WhenInvalidId_ShouldReturn404() throws Exception {
        when(ratingService.deleteRating(1L))
                .thenThrow(new NotFoundException(Constants.ErrorCode.RATING_NOT_FOUND, 1L));
        this.mockMvc.perform(delete("/backoffice/ratings/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", Matchers.is("404 NOT_FOUND")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Not Found")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail", Matchers.is("RATING 1 is not found")));

        verify(ratingService, times(1)).deleteRating(anyLong());
    }

    @Test
    void testStorefrontGetRatingList_ShouldReturnList() throws Exception {
        when(ratingService.getRatingListByProductId(anyLong(), anyInt(), anyInt()))
                .thenReturn(new RatingListVm(List.of(), 0, 0));

        this.mockMvc.perform(get("/storefront/ratings/products/{productId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNo", "0")
                .param("pageSize", "10")
        )
                .andExpect(status().isOk());
    }

    @Test
    void TestCreateRating_WhenValid_ShouldSuccess() throws Exception {
        RatingPostVm vm = new RatingPostVm("rating1", 5, 1L, "product1");

        when(ratingService.createRating(any(RatingPostVm.class))).thenReturn(ratingVm);

        this.mockMvc.perform(post("/storefront/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(vm))
        )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)));
    }

    @Test
    void TestGetAverageStarOfProduct_ShouldReturnSuccess() throws Exception {
        when(ratingService.calculateAverageStar(anyLong())).thenReturn(0.0D);
        this.mockMvc.perform(get("/storefront/ratings/product/{productId}/average-star", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().string("0.0"));
    }

}

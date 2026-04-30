package com.yas.rating.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.rating.service.RatingService;
import com.yas.rating.viewmodel.RatingListVm;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @MockitoBean
    private RatingService ratingService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void storefrontEndpoints_shouldBeAccessibleWithoutAuth() throws Exception {
        when(ratingService.getRatingListByProductId(anyLong(), anyInt(), anyInt()))
                .thenReturn(new RatingListVm(List.of(), 0, 0));

        mockMvc.perform(get("/storefront/ratings/products/1")
                        .param("pageNo", "0")
                        .param("pageSize", "5"))
                .andExpect(status().isOk());
    }

    @Test
    void storefrontAverageStar_shouldBeAccessibleWithoutAuth() throws Exception {
        when(ratingService.calculateAverageStar(anyLong())).thenReturn(4.5);

        mockMvc.perform(get("/storefront/ratings/product/1/average-star"))
                .andExpect(status().isOk());
    }

    @Test
    void backofficeEndpoints_shouldReturn401WithoutAuth() throws Exception {
        mockMvc.perform(get("/backoffice/ratings"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void backofficeDeleteEndpoints_shouldReturn401WithoutAuth() throws Exception {
        mockMvc.perform(delete("/backoffice/ratings/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void backofficeEndpoints_withValidAdminJwt_shouldReturn200() throws Exception {
        Jwt jwt = new Jwt(
                "mock-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("realm_access", Map.of("roles", List.of("ADMIN")),
                        "sub", "admin-user")
        );
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        when(ratingService.getRatingListWithFilter(
                anyString(), anyString(), anyString(),
                any(ZonedDateTime.class), any(ZonedDateTime.class),
                anyInt(), anyInt()))
                .thenReturn(new RatingListVm(List.of(), 0, 0));

        mockMvc.perform(get("/backoffice/ratings")
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk());
    }

    @Test
    void backofficeEndpoints_withNonAdminJwt_shouldReturn403() throws Exception {
        Jwt jwt = new Jwt(
                "mock-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("realm_access", Map.of("roles", List.of("USER")),
                        "sub", "regular-user")
        );
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        mockMvc.perform(get("/backoffice/ratings")
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isForbidden());
    }
}

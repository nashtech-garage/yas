package com.yas.recommendation.controller;

import com.yas.recommendation.vector.common.query.VectorQuery;
import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.viewmodel.RelatedProductVm;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmbeddingQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmbeddingQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock the VectorQuery service
    @MockBean
    private VectorQuery<ProductDocument, RelatedProductVm> relatedProductSearch;

    @BeforeEach
    void setUp() {
        // Prepare some mock data to be returned by the relatedProductSearch
        RelatedProductVm product1 = new RelatedProductVm();
        product1.setName("Mock Product 1");

        RelatedProductVm product2 = new RelatedProductVm();
        product2.setName("Mock Product 2");

        List<RelatedProductVm> mockProducts = Arrays.asList(product1, product2);

        // Mock the behavior of relatedProductSearch
        Mockito.when(relatedProductSearch.similaritySearch(anyLong())).thenReturn(mockProducts);
    }

    @Test
    void shouldReturnSimilarProducts_whenValidProductIdProvided() throws Exception {
        // Perform a GET request to /embedding/product/{id}/similarity
        mockMvc.perform(get("/embedding/product/1/similarity"))
                .andExpect(status().isOk())  // Expect HTTP 200 OK status
                .andExpect(jsonPath("$", hasSize(2)))  // Expect 2 items in the JSON array
                .andExpect(jsonPath("$[0].name", is("Mock Product 1")))
                .andExpect(jsonPath("$[1].name", is("Mock Product 2")));
    }

    @Test
    void shouldReturnEmptyList_whenNoSimilarProductsFound() throws Exception {
        // Mock empty search results
        Mockito.when(relatedProductSearch.similaritySearch(anyLong())).thenReturn(Arrays.asList());

        // Perform a GET request to /embedding/product/{id}/similarity
        mockMvc.perform(get("/embedding/product/2/similarity"))
                .andExpect(status().isOk())  // Expect HTTP 200 OK status
                .andExpect(jsonPath("$", hasSize(0)));  // Expect empty JSON array
    }
}

package com.yas.product.controller;

import com.yas.product.exception.NotFoundException;
import com.yas.product.model.ProductOption;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.viewmodel.ProductOptionGetVm;
import com.yas.product.viewmodel.ProductOptionPostVm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class ProductOptionControllerTest {
    private ProductOptionController productOptionController;
    ProductOptionRepository productOptionRepository;
    private Principal principal;
    private UriComponentsBuilder uriComponentsBuilder;
    private ProductOption productOption;
    @BeforeEach
    public void setUp(){
        productOptionRepository = mock(ProductOptionRepository.class);
        principal = mock(Principal.class);
        uriComponentsBuilder = mock(UriComponentsBuilder.class);
        productOptionController = new ProductOptionController(productOptionRepository);
        productOption = new ProductOption();
        productOption.setId(1L);
        productOption.setName("hihi");
    }

    @Test
    public void listProductOption_ReturnListProductOption_Success(){
        List<ProductOption> productOptions = new ArrayList<>(Arrays.asList(productOption));
        when(productOptionRepository.findAll()).thenReturn(productOptions);
        ResponseEntity<List<ProductOptionGetVm>> result = productOptionController.listProductOption();
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody().size(), is(productOptions.size()));
    }

    @Test
    public void getProductOption_ReturnProductOptionGetVm_Success(){
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        ResponseEntity<ProductOptionGetVm> result = productOptionController.getProductOption(1L);
        assertThat(result.getBody().name(), is("hihi"));
    }

    @Test
    public void getProductOption_ProductOptionIdIsInvalid_ThrowNotFoundException(){
        when(productOptionRepository.findById(2L)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                ()->productOptionController.getProductOption(2L));
        assertThat(exception.getMessage(), is("Product option 2 is not found"));
    }

    @Test
    public void createProductOption_VaildProductOptionPostVm_Success(){
        ProductOptionPostVm productOptionPostVm = new ProductOptionPostVm("hihi");
        var productOptionCaptor = ArgumentCaptor.forClass(ProductOption.class);
        when(productOptionRepository.saveAndFlush(productOptionCaptor.capture())).thenReturn(productOption);
        UriComponentsBuilder newUriComponentsBuilder = mock(UriComponentsBuilder.class);
        UriComponents uriComponents = mock(UriComponents.class);
        when(uriComponentsBuilder.replacePath("/product-options/{id}")).thenReturn(newUriComponentsBuilder);
        when(newUriComponentsBuilder.buildAndExpand(productOption.getId())).thenReturn(uriComponents);
        ResponseEntity<ProductOptionGetVm> result = productOptionController.createProductOption(productOptionPostVm, principal, uriComponentsBuilder);
        assertThat(result.getBody().name(), is("hihi"));
    }

    @Test
    public void updateProductOption_ProductOptionIdIsValid_Success(){
        ProductOptionPostVm productOptionPostVm = new ProductOptionPostVm("hihi");
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        ResponseEntity<Void> result = productOptionController.updateProductOption(1L, productOptionPostVm, principal);
        assertThat(result.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }

    @Test
    public void updateProductOption_ProductOptionIdIsInvalid_ThrowNotFoundException(){
        ProductOptionPostVm productOptionPostVm = new ProductOptionPostVm("hihi");
        when(productOptionRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> productOptionController.updateProductOption(1L, productOptionPostVm, principal));
        assertThat(exception.getMessage(), is("Product option 1 is not found"));
    }

    @Test
    public void deleteProductOption_givenProductOptionIdValid_thenSuccess(){
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        ResponseEntity<Void> response = productOptionController.deleteProductOption(1L);
        verify(productOptionRepository).deleteById(1L);
        assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }

    @Test
    public void deleteProductOption_givenProductOptionIdInvalid_thenThrowNotFoundException(){
        when(productOptionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, ()->productOptionController.deleteProductOption(1L));
    }
}

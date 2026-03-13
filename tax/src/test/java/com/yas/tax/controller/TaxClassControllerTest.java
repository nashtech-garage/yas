package com.yas.tax.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.yas.tax.model.TaxClass;
import com.yas.tax.service.TaxClassService;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class TaxClassControllerTest {

    @Mock
    private TaxClassService taxClassService;

    @InjectMocks
    private TaxClassController taxClassController;

    private TaxClass taxClass;
    private TaxClassPostVm taxClassPostVm;

    @BeforeEach
    void setUp() {
        taxClass = TaxClass.builder().id(1L).name("Tax Class A").build();
        taxClassPostVm = mock(TaxClassPostVm.class);
    }

    @Test
    void getPageableTaxClasses_ShouldReturnOk() {
        TaxClassListGetVm mockList = mock(TaxClassListGetVm.class);
        when(taxClassService.getPageableTaxClasses(0, 10)).thenReturn(mockList);

        ResponseEntity<TaxClassListGetVm> response = taxClassController.getPageableTaxClasses(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void listTaxClasses_ShouldReturnOk() {
        when(taxClassService.findAllTaxClasses()).thenReturn(List.of(mock(TaxClassVm.class)));
        ResponseEntity<List<TaxClassVm>> response = taxClassController.listTaxClasses();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getTaxClass_ShouldReturnOk() {
        when(taxClassService.findById(1L)).thenReturn(mock(TaxClassVm.class));
        ResponseEntity<TaxClassVm> response = taxClassController.getTaxClass(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void createTaxClass_ShouldReturnCreated() {
        when(taxClassService.create(taxClassPostVm)).thenReturn(taxClass);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

        ResponseEntity<TaxClassVm> response = taxClassController.createTaxClass(taxClassPostVm, uriBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/tax-classes/1");
    }

    @Test
    void updateTaxClass_ShouldReturnNoContent() {
        doNothing().when(taxClassService).update(any(), eq(1L));
        ResponseEntity<Void> response = taxClassController.updateTaxClass(1L, taxClassPostVm);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteTaxClass_ShouldReturnNoContent() {
        doNothing().when(taxClassService).delete(1L);
        ResponseEntity<Void> response = taxClassController.deleteTaxClass(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
package com.yas.tax.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.tax.model.TaxClass;
import com.yas.tax.service.TaxClassService;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import java.net.URI;
import java.util.List;
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
    TaxClassService taxClassService;

    @InjectMocks
    TaxClassController taxClassController;

    @Test
    void getPageableTaxClassesShouldReturnServicePage() {
        TaxClassListGetVm page = new TaxClassListGetVm(List.of(new TaxClassVm(1L, "Standard")), 0, 10, 1, 1, true);
        when(taxClassService.getPageableTaxClasses(0, 10)).thenReturn(page);

        ResponseEntity<TaxClassListGetVm> response = taxClassController.getPageableTaxClasses(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(page);
    }

    @Test
    void listTaxClassesShouldReturnAllClasses() {
        List<TaxClassVm> classes = List.of(new TaxClassVm(1L, "Standard"));
        when(taxClassService.findAllTaxClasses()).thenReturn(classes);

        ResponseEntity<List<TaxClassVm>> response = taxClassController.listTaxClasses();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(classes);
    }

    @Test
    void getTaxClassShouldReturnServiceResult() {
        TaxClassVm taxClass = new TaxClassVm(1L, "Standard");
        when(taxClassService.findById(1L)).thenReturn(taxClass);

        ResponseEntity<TaxClassVm> response = taxClassController.getTaxClass(1L);

        assertThat(response.getBody()).isSameAs(taxClass);
    }

    @Test
    void createTaxClassShouldReturnCreatedLocation() {
        TaxClass created = TaxClass.builder().id(1L).name("Standard").build();
        TaxClassPostVm request = new TaxClassPostVm(null, "Standard");
        when(taxClassService.create(request)).thenReturn(created);

        ResponseEntity<TaxClassVm> response = taxClassController.createTaxClass(
            request,
            UriComponentsBuilder.fromUri(URI.create("http://localhost"))
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).hasPath("/tax-classes/1");
        assertThat(response.getBody()).isEqualTo(new TaxClassVm(1L, "Standard"));
    }

    @Test
    void updateTaxClassShouldReturnNoContent() {
        TaxClassPostVm request = new TaxClassPostVm(null, "Standard");

        ResponseEntity<Void> response = taxClassController.updateTaxClass(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxClassService).update(request, 1L);
    }

    @Test
    void deleteTaxClassShouldReturnNoContent() {
        ResponseEntity<Void> response = taxClassController.deleteTaxClass(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxClassService).delete(1L);
    }
}

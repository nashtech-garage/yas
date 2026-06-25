package com.yas.tax.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.tax.model.TaxClass;
import com.yas.tax.service.TaxClassService;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import java.util.List;
import org.instancio.Instancio;
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
public class TaxClassControllerTest {

    @Mock
    TaxClassService taxClassService;

    @InjectMocks
    TaxClassController taxClassController;

    TaxClass taxClass;
    TaxClassVm taxClassVm;

    @BeforeEach
    void setUp() {
        taxClass = Instancio.create(TaxClass.class);
        taxClassVm = TaxClassVm.fromModel(taxClass);
    }

    @Test
    void listTaxClasses_shouldReturn200WithList() {
        when(taxClassService.findAllTaxClasses()).thenReturn(List.of(taxClassVm));

        ResponseEntity<List<TaxClassVm>> response = taxClassController.listTaxClasses();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).id()).isEqualTo(taxClassVm.id());
    }

    @Test
    void getPageableTaxClasses_shouldReturn200() {
        TaxClassListGetVm listGetVm = new TaxClassListGetVm(
                List.of(taxClassVm), 0, 10, 1, 1, true);
        when(taxClassService.getPageableTaxClasses(0, 10)).thenReturn(listGetVm);

        ResponseEntity<TaxClassListGetVm> response = taxClassController.getPageableTaxClasses(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().taxClassContent()).hasSize(1);
        assertThat(response.getBody().taxClassContent().get(0).id()).isEqualTo(taxClassVm.id());
    }

    @Test
    void getTaxClassById_shouldReturn200() {
        when(taxClassService.findById(taxClass.getId())).thenReturn(taxClassVm);

        ResponseEntity<TaxClassVm> response = taxClassController.getTaxClass(taxClass.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(taxClassVm.id());
    }

    @Test
    void createTaxClass_shouldReturn201() {
        TaxClassPostVm postVm = new TaxClassPostVm("id-1", "Standard Tax");
        when(taxClassService.create(any())).thenReturn(taxClass);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        ResponseEntity<TaxClassVm> response = taxClassController.createTaxClass(postVm, uriComponentsBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().id()).isEqualTo(taxClassVm.id());
    }

    @Test
    void updateTaxClass_shouldReturn204() {
        TaxClassPostVm postVm = new TaxClassPostVm("id-1", "Updated Tax");
        doNothing().when(taxClassService).update(any(), anyLong());

        ResponseEntity<Void> response = taxClassController.updateTaxClass(taxClass.getId(), postVm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxClassService).update(postVm, taxClass.getId());
    }

    @Test
    void deleteTaxClass_shouldReturn204() {
        doNothing().when(taxClassService).delete(anyLong());

        ResponseEntity<Void> response = taxClassController.deleteTaxClass(taxClass.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxClassService).delete(taxClass.getId());
    }
}

package com.yas.tax.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import org.instancio.Instancio;
import static org.instancio.Select.field;
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
public class TaxRateControllerTest {

    @Mock
    TaxRateService taxRateService;

    @InjectMocks
    TaxRateController taxRateController;

    TaxRate taxRate;
    TaxRateVm taxRateVm;

    @BeforeEach
    void setUp() {
        TaxClass taxClass = Instancio.create(TaxClass.class);
        taxRate = Instancio.of(TaxRate.class)
                .set(field("taxClass"), taxClass)
                .create();
        taxRateVm = TaxRateVm.fromModel(taxRate);
    }

    @Test
    void getPageableTaxRates_shouldReturn200() {
        TaxRateListGetVm listVm = new TaxRateListGetVm(List.of(), 0, 10, 0, 0, true);
        when(taxRateService.getPageableTaxRates(0, 10)).thenReturn(listVm);

        ResponseEntity<TaxRateListGetVm> response = taxRateController.getPageableTaxRates(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getTaxRateById_shouldReturn200() {
        when(taxRateService.findById(taxRate.getId())).thenReturn(taxRateVm);

        ResponseEntity<TaxRateVm> response = taxRateController.getTaxRate(taxRate.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(taxRateVm.id());
    }

    @Test
    void createTaxRate_shouldReturn201() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        when(taxRateService.createTaxRate(any())).thenReturn(taxRate);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        ResponseEntity<TaxRateVm> response = taxRateController.createTaxRate(postVm, uriComponentsBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().id()).isEqualTo(taxRateVm.id());
    }

    @Test
    void updateTaxRate_shouldReturn204() {
        TaxRatePostVm postVm = new TaxRatePostVm(15.0, "54321", 1L, 1L, 1L);
        doNothing().when(taxRateService).updateTaxRate(any(), anyLong());

        ResponseEntity<Void> response = taxRateController.updateTaxRate(taxRate.getId(), postVm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxRateService).updateTaxRate(postVm, taxRate.getId());
    }

    @Test
    void deleteTaxRate_shouldReturn204() {
        doNothing().when(taxRateService).delete(anyLong());

        ResponseEntity<Void> response = taxRateController.deleteTaxRate(taxRate.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxRateService).delete(taxRate.getId());
    }

    @Test
    void getTaxPercentByAddress_shouldReturn200() {
        when(taxRateService.getTaxPercent(1L, 1L, 1L, "12345")).thenReturn(10.0);

        ResponseEntity<Double> response = taxRateController.getTaxPercentByAddress(1L, 1L, 1L, "12345");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(10.0);
    }

    @Test
    void getBatchTaxPercentsByAddress_shouldReturn200() {
        when(taxRateService.getBulkTaxRate(any(), any(), any(), any()))
                .thenReturn(List.of(taxRateVm));

        ResponseEntity<List<TaxRateVm>> response = taxRateController.getBatchTaxPercentsByAddress(List.of(1L, 2L), 1L, null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }
}
